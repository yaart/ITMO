#include "keyboard.h"

void initKeyboard() {
  // Строки R1-R4: PB7, PB6, PA10, PB3 — выходы с открытым стоком
  GPIOB->MODER = (GPIOB->MODER & ~(3U << (7 * 2))) | (1U << (7 * 2));
  GPIOB->MODER = (GPIOB->MODER & ~(3U << (6 * 2))) | (1U << (6 * 2));
  GPIOB->MODER = (GPIOB->MODER & ~(3U << (3 * 2))) | (1U << (3 * 2));
  GPIOA->MODER = (GPIOA->MODER & ~(3U << (10 * 2))) | (1U << (10 * 2));

  GPIOB->OTYPER |= (1 << 7) | (1 << 6) | (1 << 3);
  GPIOA->OTYPER |= (1 << 10);

  // Столбцы C1-C3: PB10, PB4, PB5 — входы с подтяжкой к питанию
  GPIOB->MODER &= ~(3U << (10 * 2) | 3U << (4 * 2) | 3U << (5 * 2));
  GPIOB->PUPDR = (GPIOB->PUPDR & ~(3U << (10 * 2))) | (1U << (10 * 2));
  GPIOB->PUPDR = (GPIOB->PUPDR & ~(3U << (4  * 2))) | (1U << (4  * 2));
  GPIOB->PUPDR = (GPIOB->PUPDR & ~(3U << (5  * 2))) | (1U << (5  * 2));

  // Столбец C4: PA15 — вход с подтяжкой к питанию
  GPIOA->MODER  = (GPIOA->MODER  & ~(3U << (15 * 2)));
  GPIOA->PUPDR  = (GPIOA->PUPDR  & ~(3U << (15 * 2))) | (1U << (15 * 2));
  GPIOA->OTYPER &= ~(1U << 15);
  GPIOA->OSPEEDR &= ~(3U << (15 * 2));

  lastKey = '\0';
  lastScanTime = 0;
}

char readKey() {
  const uint8_t rows[] = {7, 6, 10, 3}; 
  // строки: PB7, PB6, PA10, PB3
  // C1=PB10, C2=PB4, C3=PB5, C4=PA15
    const char keymap[4][4] = {
    {'1', '2', '3', '+'},
    {'4', '5', '6', '-'},
    {'7', '8', '9', '*'},
    {'C', '0', '=', '/'}
  };

  for (uint8_t i = 0; i < 4; i++) {
    if (rows[i] != 10) {
      GPIOB->BSRR = (1U << (rows[i] + 16));
    } else {
      GPIOA->BSRR = (1U << (rows[i] + 16));
    }

    for (volatile int d = 0; d < 100; d++);

    int pressed_col = -1;
    if      ((GPIOB->IDR & (1U << 10)) == 0) pressed_col = 0;
    else if ((GPIOB->IDR & (1U << 4 )) == 0) pressed_col = 1;
    else if ((GPIOB->IDR & (1U << 5 )) == 0) pressed_col = 2;
    else if ((GPIOA->IDR & (1U << 15)) == 0) pressed_col = 3;

    if (rows[i] != 10) {
      GPIOB->BSRR = (1U << rows[i]);
    } else {
      GPIOA->BSRR = (1U << rows[i]);
    }

    if (pressed_col >= 0) {
      return keymap[i][pressed_col];
    }
  }

  return '\0';
}

void scanKeyboard() {
  if (tickCount - lastScanTime > 100) {
    lastScanTime = tickCount;
    char currentKey = readKey();

    if (currentKey != '\0' && currentKey != lastKey) {
      lastKey = currentKey;
    } else if (currentKey == '\0') {
      lastKey = '\0';
    }
  }
}