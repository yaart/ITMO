/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Flappy Bird + FreeRTOS on SDK-1.1M
  ******************************************************************************
  */
/* USER CODE END Header */

#include "main.h"
#include "cmsis_os.h"
#include "i2c.h"
#include "usart.h"
#include "gpio.h"
#include "tim.h"

/* USER CODE BEGIN Includes */
#include "oled.h"
#include "buzzer.h"
/* USER CODE END Includes */

/* USER CODE BEGIN PTD */
/* USER CODE END PTD */

/* USER CODE BEGIN PD */
/* USER CODE END PD */

/* USER CODE BEGIN PM */
/* USER CODE END PM */

/* USER CODE BEGIN PV */
/* USER CODE END PV */

void SystemClock_Config(void);
void MX_FREERTOS_Init(void);
/* USER CODE BEGIN PFP */
/* USER CODE END PFP */

/* USER CODE BEGIN 0 */
/* USER CODE END 0 */

int main(void)
{
	/* USER CODE BEGIN 1 */
	/* USER CODE END 1 */

	HAL_Init();

	/* USER CODE BEGIN Init */
	/* USER CODE END Init */

	SystemClock_Config();

	/* USER CODE BEGIN SysInit */
	/* USER CODE END SysInit */

	MX_GPIO_Init();
	MX_I2C1_Init();
	MX_USART1_UART_Init();
	MX_TIM2_Init();

	/* USER CODE BEGIN 2 */
	oled_Init();
	oled_Fill(Black);
	oled_UpdateScreen();
	Buzzer_Init();
	/* Two beeps at power-up — if silent, hardware/pin issue */
	Buzzer_BootBeep();
	/* USER CODE END 2 */

	MX_FREERTOS_Init();
	osKernelStart();

	/* USER CODE BEGIN WHILE */
	while (1) {
		/* USER CODE END WHILE */
		/* USER CODE BEGIN 3 */
	}
	/* USER CODE END 3 */
}

void SystemClock_Config(void)
{
	RCC_OscInitTypeDef RCC_OscInitStruct = {0};
	RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

	__HAL_RCC_PWR_CLK_ENABLE();
	__HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE1);

	/* Same clock as SDK_Keyboard — HSE 25 MHz → 168 MHz (OLED + keypad) */
	RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE;
	RCC_OscInitStruct.HSEState = RCC_HSE_ON;
	RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
	RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
	RCC_OscInitStruct.PLL.PLLM = 25;
	RCC_OscInitStruct.PLL.PLLN = 336;
	RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV2;
	RCC_OscInitStruct.PLL.PLLQ = 4;
	if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK) {
		Error_Handler();
	}

	RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK | RCC_CLOCKTYPE_SYSCLK
				      | RCC_CLOCKTYPE_PCLK1 | RCC_CLOCKTYPE_PCLK2;
	RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
	RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
	RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV4;
	RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV2;

	if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_5) != HAL_OK) {
		Error_Handler();
	}
}

/* USER CODE BEGIN 4 */
/* USER CODE END 4 */

void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim)
{
	/* USER CODE BEGIN Callback 0 */
	/* USER CODE END Callback 0 */
	if (htim->Instance == TIM1) {
		HAL_IncTick();
	}
	/* USER CODE BEGIN Callback 1 */
	/* USER CODE END Callback 1 */
}

void Error_Handler(void)
{
	/* USER CODE BEGIN Error_Handler_Debug */
	/* USER CODE END Error_Handler_Debug */
}

#ifdef USE_FULL_ASSERT
void assert_failed(uint8_t *file, uint32_t line)
{
	/* USER CODE BEGIN 6 */
	(void)file;
	(void)line;
	/* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */
