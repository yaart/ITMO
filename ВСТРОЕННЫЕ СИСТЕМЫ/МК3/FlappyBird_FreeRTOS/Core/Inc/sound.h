#ifndef SOUND_H_
#define SOUND_H_

#include <stdint.h>

typedef enum {
	SND_FLAP = 1,
	SND_SCORE = 2,
	SND_DIE = 3,
	SND_START = 4
} SoundEvt_t;

void Sound_PlayFlap(void);
void Sound_PlayScore(void);
void Sound_PlayDie(void);
void Sound_PlayStart(void);
void Sound_PlayBgNote(uint16_t index);

#endif /* SOUND_H_ */
