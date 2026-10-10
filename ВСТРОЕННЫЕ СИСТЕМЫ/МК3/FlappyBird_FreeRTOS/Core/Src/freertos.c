/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * File Name          : freertos.c
  * Description        : Flappy Bird on FreeRTOS (SDK-1.1M)
  ******************************************************************************
  */
/* USER CODE END Header */

#include "FreeRTOS.h"
#include "task.h"
#include "main.h"
#include "cmsis_os.h"

#include "flappy.h"
#include "kb.h"
#include "oled.h"
#include "gpio.h"
#include "sound.h"
#include "buzzer.h"
#include <string.h>

#define INPUT_POLL_MS   30
#define GAME_TICK_MS    40
#define DISPLAY_MS      60

static GameState_t g_game;

osThreadId Task_InputHandle;
osThreadId Task_GameHandle;
osThreadId Task_DisplayHandle;
osThreadId Task_SoundHandle;
osMessageQId inputQueueHandle;
osMessageQId soundQueueHandle;
osMutexId gameMutexHandle;
osMutexId i2cMutexHandle;

void StartTaskInput(void const *argument);
void StartTaskGame(void const *argument);
void StartTaskDisplay(void const *argument);
void StartTaskSound(void const *argument);

void MX_FREERTOS_Init(void);

void vApplicationGetIdleTaskMemory(StaticTask_t **ppxIdleTaskTCBBuffer,
				   StackType_t **ppxIdleTaskStackBuffer,
				   uint32_t *pulIdleTaskStackSize);

void configureTimerForRunTimeStats(void);
unsigned long getRunTimeCounterValue(void);

__weak void configureTimerForRunTimeStats(void)
{
}

__weak unsigned long getRunTimeCounterValue(void)
{
	return 0;
}

static StaticTask_t xIdleTaskTCBBuffer;
static StackType_t xIdleStack[configMINIMAL_STACK_SIZE];

void vApplicationGetIdleTaskMemory(StaticTask_t **ppxIdleTaskTCBBuffer,
				   StackType_t **ppxIdleTaskStackBuffer,
				   uint32_t *pulIdleTaskStackSize)
{
	*ppxIdleTaskTCBBuffer = &xIdleTaskTCBBuffer;
	*ppxIdleTaskStackBuffer = &xIdleStack[0];
	*pulIdleTaskStackSize = configMINIMAL_STACK_SIZE;
}

static void Sound_Post(SoundEvt_t evt)
{
	osMessagePut(soundQueueHandle, (uint32_t)evt, 0);
}

void MX_FREERTOS_Init(void)
{
	Flappy_Reset(&g_game);
	g_game.best = 0;

	osMutexDef(gameMutex);
	gameMutexHandle = osMutexCreate(osMutex(gameMutex));

	osMutexDef(i2cMutex);
	i2cMutexHandle = osMutexCreate(osMutex(i2cMutex));

	osMessageQDef(inputQueue, 8, uint16_t);
	inputQueueHandle = osMessageCreate(osMessageQ(inputQueue), NULL);

	osMessageQDef(soundQueue, 8, uint16_t);
	soundQueueHandle = osMessageCreate(osMessageQ(soundQueue), NULL);

	vQueueAddToRegistry(inputQueueHandle, "inputQ");
	vQueueAddToRegistry(soundQueueHandle, "soundQ");

	osThreadDef(Task_Input, StartTaskInput, osPriorityAboveNormal, 0, 256);
	Task_InputHandle = osThreadCreate(osThread(Task_Input), NULL);

	osThreadDef(Task_Game, StartTaskGame, osPriorityNormal, 0, 256);
	Task_GameHandle = osThreadCreate(osThread(Task_Game), NULL);

	osThreadDef(Task_Display, StartTaskDisplay, osPriorityBelowNormal, 0, 768);
	Task_DisplayHandle = osThreadCreate(osThread(Task_Display), NULL);

	osThreadDef(Task_Sound, StartTaskSound, osPriorityAboveNormal, 0, 384);
	Task_SoundHandle = osThreadCreate(osThread(Task_Sound), NULL);
}

void StartTaskInput(void const *argument)
{
	uint8_t prev = 0;

	(void)argument;

	for (;;) {
		uint8_t pressed = 0;

		osMutexWait(i2cMutexHandle, osWaitForever);
		pressed = KB_AnyPressed();
		osMutexRelease(i2cMutexHandle);

		if (pressed && !prev) {
			osMessagePut(inputQueueHandle, (uint32_t)EVT_FLAP, 0);
			HAL_GPIO_TogglePin(GPIOD, GPIO_PIN_15);
		}
		prev = pressed;
		osDelay(INPUT_POLL_MS);
	}
}

void StartTaskGame(void const *argument)
{
	osEvent event;
	GameMode_t prev_mode = GAME_READY;
	uint16_t prev_score = 0;

	(void)argument;

	for (;;) {
		uint8_t did_flap = 0;

		do {
			event = osMessageGet(inputQueueHandle, 0);
			if (event.status == osEventMessage) {
				osMutexWait(gameMutexHandle, osWaitForever);
				Flappy_OnFlap(&g_game);
				did_flap = 1;
				osMutexRelease(gameMutexHandle);
			}
		} while (event.status == osEventMessage);

		osMutexWait(gameMutexHandle, osWaitForever);
		Flappy_Tick(&g_game);

		if (did_flap) {
			if (prev_mode == GAME_READY && g_game.mode == GAME_PLAYING) {
				Sound_Post(SND_START);
			} else if (g_game.mode == GAME_PLAYING) {
				Sound_Post(SND_FLAP);
			} else if (prev_mode == GAME_OVER && g_game.mode == GAME_PLAYING) {
				Sound_Post(SND_START);
			}
		}

		if (g_game.mode == GAME_PLAYING && g_game.score > prev_score) {
			Sound_Post(SND_SCORE);
		}

		if (prev_mode != GAME_OVER && g_game.mode == GAME_OVER) {
			Sound_Post(SND_DIE);
		}

		prev_mode = g_game.mode;
		prev_score = g_game.score;
		osMutexRelease(gameMutexHandle);

		osDelay(GAME_TICK_MS);
	}
}

void StartTaskDisplay(void const *argument)
{
	GameState_t snap;

	(void)argument;

	for (;;) {
		osMutexWait(gameMutexHandle, osWaitForever);
		snap = g_game;
		osMutexRelease(gameMutexHandle);

		osMutexWait(i2cMutexHandle, osWaitForever);
		Flappy_Draw(&snap);
		osMutexRelease(i2cMutexHandle);

		HAL_GPIO_TogglePin(GPIOD, GPIO_PIN_13);
		osDelay(DISPLAY_MS);
	}
}

void StartTaskSound(void const *argument)
{
	osEvent event;
	uint16_t bg_idx = 0;
	GameMode_t mode;

	(void)argument;

	for (;;) {
		/* Prefer SFX from queue; otherwise play background while alive/menu */
		event = osMessageGet(soundQueueHandle, 30);
		if (event.status == osEventMessage) {
			switch ((SoundEvt_t)event.value.v) {
			case SND_FLAP:
				Sound_PlayFlap();
				break;
			case SND_SCORE:
				Sound_PlayScore();
				break;
			case SND_DIE:
				Sound_PlayDie();
				break;
			case SND_START:
				Sound_PlayStart();
				break;
			default:
				break;
			}
			continue;
		}

		osMutexWait(gameMutexHandle, osWaitForever);
		mode = g_game.mode;
		osMutexRelease(gameMutexHandle);

		if (mode == GAME_READY || mode == GAME_PLAYING) {
			Sound_PlayBgNote(bg_idx++);
		} else {
			Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
			osDelay(80);
		}
	}
}
