#ifndef FLAPPY_H_
#define FLAPPY_H_

#include <stdint.h>

#define SCREEN_W           128
#define SCREEN_H           64
#define BIRD_X             24
#define BIRD_SIZE          6
#define PIPE_W             14
#define PIPE_GAP           22
#define PIPE_COUNT         2
#define GRAVITY            18   /* fixed-point: /100 per tick */
#define FLAP_VELOCITY     (-90)
#define PIPE_SPEED         2
#define GROUND_Y           60

typedef enum {
	GAME_READY = 0,
	GAME_PLAYING,
	GAME_OVER
} GameMode_t;

typedef struct {
	int16_t x;
	int16_t gap_y; /* top of gap */
	uint8_t scored;
} Pipe_t;

typedef struct {
	GameMode_t mode;
	int16_t bird_y;      /* pixel * 10 for subpixel */
	int16_t bird_vy;
	Pipe_t pipes[PIPE_COUNT];
	uint16_t score;
	uint16_t best;
} GameState_t;

typedef enum {
	EVT_NONE = 0,
	EVT_FLAP = 1,
	EVT_RESTART = 2
} InputEvent_t;

void Flappy_Reset(GameState_t *g);
void Flappy_Start(GameState_t *g);
void Flappy_OnFlap(GameState_t *g);
void Flappy_Tick(GameState_t *g);
void Flappy_Draw(const GameState_t *g);

#endif /* FLAPPY_H_ */
