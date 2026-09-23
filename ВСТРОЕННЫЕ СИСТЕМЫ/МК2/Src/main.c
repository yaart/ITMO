/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : SDK-1.1M Calculator (Variant 1)
  ******************************************************************************
  */
/* USER CODE END Header */

#include "main.h"
#include "i2c.h"
#include "usart.h"
#include "gpio.h"

/* USER CODE BEGIN Includes */
#include "kb.h"
#include "sdk_uart.h"
#include "pca9538.h"
#include "oled.h"
#include "fonts.h"
#include <string.h>
#include <stdio.h>
#include <limits.h>
/* USER CODE END Includes */

/* USER CODE BEGIN PV */
static int32_t g_n1 = 0, g_n2 = 0, g_res = 0;
static int  g_op = 0;        /* 0=+, 1=-, 2=* */
static int  g_state = 0;     /* 0=first, 1=second, 2=result */
static int  g_overflow = 0;  /* 1 если последняя операция дала переполнение */
/* USER CODE END PV */

void SystemClock_Config(void);

/* USER CODE BEGIN PFP */
static uint8_t ReadKey(void);
static void    ProcessKey(uint8_t k);
static void    UpdateDisplay(void);
static void    IntToStr(int32_t n, char* buf);
static void    CalcReset(void);
static int     SafeAdd(int32_t a, int32_t b, int32_t* r);
static int     SafeSub(int32_t a, int32_t b, int32_t* r);
static int     SafeMul(int32_t a, int32_t b, int32_t* r);
/* USER CODE END PFP */

int main(void)
{
  HAL_Init();
  SystemClock_Config();

  MX_GPIO_Init();
  MX_I2C1_Init();
  MX_USART6_UART_Init();

  /* USER CODE BEGIN 2 */
  oled_Init();
  CalcReset();
  UpdateDisplay();
  UART_Transmit((uint8_t*)"Calculator ready\r\n");

  uint8_t prev = 0xFF;
  /* USER CODE END 2 */

  while (1)
  {
    /* USER CODE BEGIN WHILE */
    uint8_t k = ReadKey();

    if (k != prev)
    {
      HAL_Delay(30);
      uint8_t k2 = ReadKey();
      if (k2 != prev && k2 != 0xFF)
      {
        /* --- ДИАГНОСТИКА: печатаем сырой код нажатой кнопки --- */
        char dbg[32];
        snprintf(dbg, sizeof(dbg), "raw k=%d\r\n", k2);
        UART_Transmit((uint8_t*)dbg);

        ProcessKey(k2);
        UpdateDisplay();
      }
      prev = k2;
    }
    HAL_Delay(20);
    /* USER CODE END WHILE */
  }
}

void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE1);

  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLM = 25;
  RCC_OscInitStruct.PLL.PLLN = 336;
  RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV2;
  RCC_OscInitStruct.PLL.PLLQ = 4;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK) Error_Handler();

  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV4;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV2;
  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_5) != HAL_OK) Error_Handler();
}

/* USER CODE BEGIN 4 */

static uint8_t ReadKey(void)
{
  uint8_t rows[4] = {ROW4, ROW3, ROW2, ROW1};
  for (int i = 0; i < 4; i++)
  {
    uint8_t k = Check_Row(rows[i]);
    if (k == 0x04) return (uint8_t)(3*i + 0);   /* left   */
    if (k == 0x02) return (uint8_t)(3*i + 1);   /* center */
    if (k == 0x01) return (uint8_t)(3*i + 2);   /* right  */
  }
  return 0xFF;
}

static void ProcessKey(uint8_t k)
{
  static const int8_t kmap[12] = {
    0, -1, -2,   /* k=0,1,2  ->  0, ОП, = */
    1,  2,  3,   /* k=3,4,5  ->  1, 2, 3  */
    4,  5,  6,   /* k=6,7,8  ->  4, 5, 6  */
    7,  8,  9    /* k=9,10,11->  7, 8, 9  */
  };
  int8_t v = kmap[k];

  if (v >= 0)
  {
    if (g_state == 2) CalcReset();
    int32_t* target = (g_state == 0) ? &g_n1 : &g_n2;
    /* защита от переполнения при наборе: не даём превысить 8 цифр */
    if (*target <= 9999999) {
      *target = *target * 10 + v;
    }
  }
  else if (v == -1)
  {
    if (g_state == 2) { g_n1 = g_res; g_n2 = 0; g_state = 1; }
    else if (g_state == 0) { g_state = 1; g_n2 = 0; }
    else { g_op = (g_op + 1) % 3; }
  }
  else if (v == -2)
  {
    if (g_state == 1)
    {
      g_overflow = 0;
      switch (g_op)
      {
        case 0: if (!SafeAdd(g_n1, g_n2, &g_res)) g_overflow = 1; break;
        case 1: if (!SafeSub(g_n1, g_n2, &g_res)) g_overflow = 1; break;
        case 2: if (!SafeMul(g_n1, g_n2, &g_res)) g_overflow = 1; break;
      }
      g_state = 2;
    }
  }
}

static int SafeAdd(int32_t a, int32_t b, int32_t* r)
{
  if ((b > 0 && a > INT32_MAX - b) || (b < 0 && a < INT32_MIN - b)) return 0;
  *r = a + b;
  return 1;
}
static int SafeSub(int32_t a, int32_t b, int32_t* r)
{
  if ((b < 0 && a > INT32_MAX + b) || (b > 0 && a < INT32_MIN + b)) return 0;
  *r = a - b;
  return 1;
}
static int SafeMul(int32_t a, int32_t b, int32_t* r)
{
  int64_t t = (int64_t)a * (int64_t)b;
  if (t > INT32_MAX || t < INT32_MIN) return 0;
  *r = (int32_t)t;
  return 1;
}

static void UpdateDisplay(void)
{
  char buf[24];
  char line[32];
  char opch = "+-*"[g_op];

  oled_Fill(Black);

  IntToStr(g_n1, buf);
  strcpy(line, buf);
  int len = (int)strlen(line);
  line[len]   = ' ';
  line[len+1] = opch;
  line[len+2] = '\0';
  oled_SetCursor(0, 0);
  oled_WriteString(line, Font_7x10, White);

  if (g_state >= 1)
  {
    IntToStr(g_n2, buf);
    strcpy(line, buf);
    len = (int)strlen(line);
    line[len]   = ' ';
    line[len+1] = '=';
    line[len+2] = '\0';
    oled_SetCursor(0, 16);
    oled_WriteString(line, Font_7x10, White);
  }

  if (g_state == 2)
  {
    oled_SetCursor(0, 32);
    if (g_overflow) {
      oled_WriteString("OVF", Font_7x10, White);
    } else {
      IntToStr(g_res, buf);
      oled_WriteString(buf, Font_7x10, White);
    }
  }

  oled_UpdateScreen();
}

static void IntToStr(int32_t n, char* buf)
{
  char tmp[16];
  int i = 0, neg = 0;

  if (n == 0) { buf[0] = '0'; buf[1] = '\0'; return; }
  if (n < 0)  { neg = 1; n = -n; }

  while (n > 0) { tmp[i++] = (char)('0' + (n % 10)); n /= 10; }

  int j = 0;
  if (neg) buf[j++] = '-';
  while (i > 0) buf[j++] = tmp[--i];
  buf[j] = '\0';
}

static void CalcReset(void)
{
  g_n1 = 0; g_n2 = 0; g_res = 0;
  g_op = 0; g_state = 0; g_overflow = 0;
}

/* USER CODE END 4 */

void Error_Handler(void) { }

#ifdef  USE_FULL_ASSERT
void assert_failed(uint8_t *file, uint32_t line) { }
#endif
