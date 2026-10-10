#include "buzzer.h"
#include "tim.h"
#include "cmsis_os.h"

/* ARR large enough for clean ~50% square wave (buzzer needs AC, not DC) */
#define BUZZER_ARR  100U

void Buzzer_Init(void)
{
	/* Match SDK_Buzzer start sequence */
	HAL_TIM_OC_Start(&htim2, TIM_CHANNEL_1);
	HAL_TIM_PWM_Start(&htim2, TIM_CHANNEL_1);

	/* Override period for audible square wave */
	__HAL_TIM_DISABLE(&htim2);
	TIM2->ARR = BUZZER_ARR;
	TIM2->CCR1 = 0;
	TIM2->EGR = TIM_EGR_UG;
	__HAL_TIM_ENABLE(&htim2);

	Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
}

void Buzzer_Set_Freq(uint16_t freq)
{
	uint32_t pclk1 = HAL_RCC_GetPCLK1Freq();
	uint32_t timclk;
	uint32_t psc;

	/* Same formula as official SDK_Buzzer */
	/* TIM2CLK = 2*PCLK1 when APB1 prescaler != 1 */
	timclk = 2U * pclk1;

	if (freq < 50U) {
		freq = 50U;
	}

	/* f = TIMCLK / ((PSC+1) * (ARR+1))  →  PSC = TIMCLK/((ARR+1)*f) - 1 */
	psc = (timclk / ((BUZZER_ARR + 1U) * (uint32_t)freq));
	if (psc == 0U) {
		psc = 1U;
	}
	psc -= 1U;
	if (psc > 0xFFFFU) {
		psc = 0xFFFFU;
	}

	TIM2->PSC = (uint16_t)psc;
	TIM2->EGR = TIM_EGR_UG; /* latch PSC immediately */
}

void Buzzer_Set_Volume(uint16_t volume)
{
	if (volume == 0U) {
		TIM2->CCR1 = 0;
		return;
	}
	/* ~50% duty — maximal loudness for magnetic buzzer */
	TIM2->CCR1 = BUZZER_ARR / 2U;
}

void Buzzer_Beep(uint16_t freq, uint16_t ms)
{
	if (freq == 0U) {
		osDelay(ms);
		return;
	}
	Buzzer_Set_Freq(freq);
	Buzzer_Set_Volume(BUZZER_VOLUME_MAX);
	osDelay(ms);
	Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
	osDelay(5);
}

void Buzzer_Play(uint32_t *melody, uint32_t *delays, uint16_t len)
{
	uint16_t i;
	for (i = 0; i < len; i++) {
		uint16_t ms = (uint16_t)(1920U / delays[i]);
		if (ms < 20U) {
			ms = 20U;
		}
		Buzzer_Beep((uint16_t)melody[i], ms);
	}
}

/* Blocking beep before scheduler (uses HAL_Delay) */
void Buzzer_BootBeep(void)
{
	Buzzer_Set_Freq(N_A4);
	Buzzer_Set_Volume(BUZZER_VOLUME_MAX);
	HAL_Delay(200);
	Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
	HAL_Delay(80);
	Buzzer_Set_Freq(N_E5);
	Buzzer_Set_Volume(BUZZER_VOLUME_MAX);
	HAL_Delay(250);
	Buzzer_Set_Volume(BUZZER_VOLUME_MUTE);
}
