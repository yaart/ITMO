#include "sound.h"
#include "buzzer.h"
#include "cmsis_os.h"

/* Short Flappy-style motif for background */
static const uint16_t bg_notes[] = {
	N_C5, N_E5, N_G5, N_E5,
	N_C5, N_E5, N_G5, N_C6,
	N_A5, N_G5, N_E5, N_C5,
	N_D5, N_F5, N_A5, N_G5
};
static const uint16_t bg_len = (uint16_t)(sizeof(bg_notes) / sizeof(bg_notes[0]));

void Sound_PlayFlap(void)
{
	Buzzer_Beep(N_C6, 40);
	Buzzer_Beep(N_E6, 50);
}

void Sound_PlayScore(void)
{
	Buzzer_Beep(N_E5, 50);
	Buzzer_Beep(N_G5, 50);
	Buzzer_Beep(N_C6, 80);
}

void Sound_PlayStart(void)
{
	Buzzer_Beep(N_C5, 60);
	Buzzer_Beep(N_E5, 60);
	Buzzer_Beep(N_G5, 60);
	Buzzer_Beep(N_C6, 100);
}

void Sound_PlayDie(void)
{
	/* sad descending game-over jingle */
	Buzzer_Beep(N_G5, 90);
	Buzzer_Beep(N_E5, 90);
	Buzzer_Beep(N_C5, 110);
	Buzzer_Beep(N_A4, 130);
	Buzzer_Beep(N_F4, 160);
	Buzzer_Beep(N_D4, 220);
	osDelay(80);
	Buzzer_Beep(N_G3, 280);
}

void Sound_PlayBgNote(uint16_t index)
{
	uint16_t i = (uint16_t)(index % bg_len);
	Buzzer_Beep(bg_notes[i], 140);
}
