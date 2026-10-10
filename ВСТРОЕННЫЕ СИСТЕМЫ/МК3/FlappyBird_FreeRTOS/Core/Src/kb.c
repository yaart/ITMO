#include "main.h"
#include "pca9538.h"
#include "kb.h"

#define KBRD_ADDR 0xE2

HAL_StatusTypeDef Set_Keyboard(void)
{
	HAL_StatusTypeDef ret;
	uint8_t buf = 0;

	ret = PCA9538_Write_Register(KBRD_ADDR, POLARITY_INVERSION, &buf);
	if (ret != HAL_OK) {
		return ret;
	}

	buf = 0;
	return PCA9538_Write_Register(KBRD_ADDR, OUTPUT_PORT, &buf);
}

uint8_t Check_Row(uint8_t Nrow)
{
	uint8_t buf = 0xFF;
	uint8_t kbd_in;

	if (Set_Keyboard() != HAL_OK) {
		return 0x00;
	}

	buf = Nrow;
	if (PCA9538_Write_Register(KBRD_ADDR, CONFIG, &buf) != HAL_OK) {
		return 0x00;
	}

	buf = 0xFF;
	if (PCA9538_Read_Inputs(KBRD_ADDR, &buf) != HAL_OK) {
		return 0x00;
	}

	kbd_in = buf & 0x70;
	if (kbd_in == 0x70) {
		return 0x00;
	}

	/* columns: bit4=left, bit5=center, bit6=right (active low) */
	if (!(kbd_in & 0x10)) {
		return 0x04;
	}
	if (!(kbd_in & 0x20)) {
		return 0x02;
	}
	if (!(kbd_in & 0x40)) {
		return 0x01;
	}

	return 0x00;
}

/* Returns 1 if any of 12 keys is pressed */
uint8_t KB_AnyPressed(void)
{
	static const uint8_t rows[4] = { ROW1, ROW2, ROW3, ROW4 };
	uint8_t i;

	for (i = 0; i < 4; i++) {
		if (Check_Row(rows[i]) != 0x00) {
			return 1;
		}
	}
	return 0;
}
