#include "main.h"
#include "tm1637.h"
#include "keyboard.h"

extern const uint8_t digit_codes[];

/* ---------- Глобальные переменные ---------- */
volatile uint32_t tickCount;
char lastKey;
uint32_t lastScanTime;

/* ---------- Состояние калькулятора ---------- */
typedef enum {
  CALC_INPUT_A,       // набираем первое число (всегда ≥ 0)
  CALC_INPUT_B,       // набираем второе число (всегда ≥ 0)
  CALC_SHOW_RESULT    // показан результат / Err
} CalcState;

CalcState calc_state = CALC_INPUT_A;
int  calc_a = 0;
int  calc_b = 0;
char calc_op = '+';
int  calc_result = 0;
char calc_last_key = '\0';

#define MAX_OPERAND  999       // максимум, который можно набрать с клавиатуры
#define MAX_DISPLAY  9999      // положительный максимум на индикаторе
#define MIN_DISPLAY  (-999)    // отрицательный минимум на индикаторе

/* ---------- Коды символов для 7-сегментника ---------- */
#define CH_BLANK 0x00
#define CH_MINUS 0x40
#define CH_A     0x77
#define CH_d     0x5E
#define CH_5     0x6D
#define CH_U     0x3E
#define CH_b     0x7C
#define CH_M     0x37
#define CH_L     0x38
#define CH_I     0x30
#define CH_V     0x3E
#define CH_E     0x79
#define CH_r     0x50

/* ---------- Служебное ---------- */
void osSystickHandler(void) { tickCount++; }

void initGPIO() {
  RCC->AHBENR |= RCC_AHBENR_GPIOAEN | RCC_AHBENR_GPIOBEN;
  GPIOA->MODER   = (GPIOA->MODER   & ~(3U << (5 * 2))) | (1U << (5 * 2));
  GPIOA->OTYPER &= ~(1U << 5);
  GPIOA->OSPEEDR |=  (1U << (5 * 2));
  GPIOA->BRR = (1U << 5);
}

void initUSART2() {
  RCC->APB1ENR |= RCC_APB1ENR_USART2EN;
  GPIOA->MODER = (GPIOA->MODER & ~(0xF << 4)) | (0xA << 4);
  GPIOA->AFR[0] = (GPIOA->AFR[0] & ~(0xFF << 8)) | (1 << 8) | (1 << 12);
  USART2->BRR = 417;
  USART2->CR1 = USART_CR1_TE | USART_CR1_UE;
}

void initSysTick() {
  SysTick->LOAD = 47999;
  SysTick->VAL = 0;
  SysTick->CTRL = (1 << 2) | (1 << 1) | (1 << 0);
}

int _write(int file, uint8_t *ptr, int len) {
  for (int i = 0; i < len; i++) {
    while (!(USART2->ISR & USART_ISR_TXE));
    USART2->TDR = ptr[i];
  }
  return len;
}

/* ---------- LED (PA5) ---------- */
static inline void led_result_on(void)  { GPIOA->BSRR = (1U << 5); }
static inline void led_result_off(void) { GPIOA->BRR  = (1U << 5); }

/* ---------- Запись 4 произвольных символов ---------- */
static void tm1637_display_4(uint8_t d3, uint8_t d2, uint8_t d1, uint8_t d0) {
  tm1637_start(); tm1637_write_byte(0x40); tm1637_stop();
  tm1637_start();
  tm1637_write_byte(0xC0);
  tm1637_write_byte(d3);
  tm1637_write_byte(d2);
  tm1637_write_byte(d1);
  tm1637_write_byte(d0);
  tm1637_stop();
  tm1637_start(); tm1637_write_byte(0x8F); tm1637_stop();
}

/* ---------- Вывод числа со знаком ---------- */
static void tm1637_display_signed(int number) {
  uint8_t d3, d2, d1, d0;

  if (number < 0) {
    int n = -number;
    if (n > 999) n = 999;
    d0 = digit_codes[n % 10];
    d1 = (n >= 10)  ? digit_codes[(n / 10) % 10]  : 0x00;
    d2 = (n >= 100) ? digit_codes[(n / 100) % 10] : 0x00;
    d3 = CH_MINUS;
  } else {
    if (number > 9999) number = 9999;
    d0 = digit_codes[ number % 10];
    d1 = (number >= 10)   ? digit_codes[(number / 10) % 10]   : 0x00;
    d2 = (number >= 100)  ? digit_codes[(number / 100) % 10]  : 0x00;
    d3 = (number >= 1000) ? digit_codes[(number / 1000) % 10] : 0x00;
  }

  tm1637_display_4(d3, d2, d1, d0);
}

/* ---------- Подписи операций: Add / Sub / Mul / dIV ---------- */
static void calc_show_operation(char op) {
  switch (op) {
    case '+': tm1637_display_4(CH_BLANK, CH_A, CH_d, CH_d); break;
    case '-': tm1637_display_4(CH_BLANK, CH_5, CH_U, CH_b); break;
    case '*': tm1637_display_4(CH_BLANK, CH_M, CH_U, CH_L); break;
    case '/': tm1637_display_4(CH_BLANK, CH_d, CH_I, CH_V); break;
  }
}

/* ---------- Err. ---------- */
static void calc_show_error(void) {
  tm1637_display_4(CH_E, CH_r, CH_r, CH_BLANK);
  led_result_off();
  calc_state = CALC_SHOW_RESULT;
  printf("Error: вне диапазона или деление на 0\n");
}

/* ---------- Сброс ---------- */
static void calc_reset(void) {
  calc_state  = CALC_INPUT_A;
  calc_a      = 0;
  calc_b      = 0;
  calc_op     = '+';
  calc_result = 0;
  led_result_off();
  tm1637_display_number(0);
  printf("Сброс калькулятора\n");
}

/* ---------- Лимит на B для умножения ---------- */
static int max_b(void) {
  int m = MAX_OPERAND;
  if (calc_op == '*') {
    int a_abs = (calc_a < 0) ? -calc_a : calc_a;
    if (a_abs == 0) {
      m = MAX_OPERAND;
    } else if (calc_a > 0) {
      m = MAX_DISPLAY / a_abs;
    } else {
      m = (-MIN_DISPLAY) / a_abs;
    }
    if (m > MAX_OPERAND) m = MAX_OPERAND;
    if (m < 0) m = 0;
  }
  return m;
}

/* ---------- Вычисление + проверка диапазона ---------- */
static int calc_compute(long *out) {
  long r;
  switch (calc_op) {
    case '+': r = (long)calc_a + calc_b;  break;
    case '-': r = (long)calc_a - calc_b;  break;
    case '*': r = (long)calc_a * calc_b;  break;
    case '/':
      if (calc_b == 0) return -1;
      r = calc_a / calc_b;
      break;
    default: return -1;
  }
  if (r < MIN_DISPLAY || r > MAX_DISPLAY) return -1;
  *out = r;
  return 0;
}

/* ---------- Обработка клавиши ---------- */
static void calc_process_key(char key) {
  if (key == '\0') return;
  printf("Нажато: %c (state=%d)\n", key, calc_state);

  /* Reset */
  if (key == 'C') { calc_reset(); return; }

  /* Если показан результат — решаем, что делать */
  if (calc_state == CALC_SHOW_RESULT) {
    if (key >= '0' && key <= '9') {
      /* начинаем новый расчёт с нуля */
      calc_a = 0;
      calc_state = CALC_INPUT_A;
      led_result_off();
    } else if (key == '+' || key == '-' || key == '*' || key == '/') {
      /* цепочка: результат становится A, и сразу ждём B */
      calc_a   = calc_result;
      calc_op  = key;
      calc_b   = 0;
      calc_state = CALC_INPUT_B;
      led_result_off();
      calc_show_operation(key);
      printf("Chained: A = %d, op = %c\n", calc_a, calc_op);
      return;
    } else if (key == '=') {
      return;
    }
  }

  /* ---------- Цифры ---------- */
  if (key >= '0' && key <= '9') {
    int digit = key - '0';

    if (calc_state == CALC_INPUT_A) {
      if (calc_a <= (MAX_OPERAND - digit) / 10) {
        calc_a = calc_a * 10 + digit;
        tm1637_display_number(calc_a);
        printf("A = %d\n", calc_a);
      }
    } else if (calc_state == CALC_INPUT_B) {
      int lim = max_b();
      if (calc_b <= (lim - digit) / 10) {
        calc_b = calc_b * 10 + digit;
        tm1637_display_number(calc_b);
        printf("B = %d\n", calc_b);
      } else {
        printf("B limit reached (max %d)\n", lim);
      }
    }
    return;
  }

  /* ---------- Операции ---------- */
  if (key == '+' || key == '-' || key == '*' || key == '/') {
    if (calc_state == CALC_INPUT_A) {
      calc_op = key;
      calc_b  = 0;
      calc_state = CALC_INPUT_B;
      calc_show_operation(key);
      printf("Операция = %c\n", calc_op);
    } else if (calc_state == CALC_INPUT_B) {
      /* смена операции до ввода B */
      calc_op = key;
      calc_show_operation(key);
      printf("Операция сменилась на %c\n", calc_op);
    }
    return;
  }

  /* ---------- '=' = вычислить ---------- */
  if (key == '=') {
    if (calc_state == CALC_INPUT_B) {
      long r;
      if (calc_compute(&r) == 0) {
        calc_result = (int)r;
        tm1637_display_signed(calc_result);
        led_result_on();
        calc_state = CALC_SHOW_RESULT;
        printf("Результат: %d %c %d = %d\n",
               calc_a, calc_op, calc_b, calc_result);
      } else {
        calc_show_error();
      }
    }
    return;
  }
}

/* ---------- main ---------- */
int main(void) {
  initGPIO();
  initUSART2();
  initSysTick();
  initKeyboard();
  tm1637_init();

  printf("Калькулятор готов. Диапазон операндов 0..%d, Результат в диапазоне %d..%d\n",
         MAX_OPERAND, MIN_DISPLAY, MAX_DISPLAY);

  tm1637_display_number(0);

  while (1) {
    scanKeyboard();

    if (lastKey != '\0' && lastKey != calc_last_key) {
      calc_last_key = lastKey;
      calc_process_key(lastKey);
    }
    if (lastKey == '\0') {
      calc_last_key = '\0';
    }
  }

  return 0;
}