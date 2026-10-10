#include "flappy.h"
#include "oled.h"
#include "fonts.h"
#include <stdio.h>

static uint16_t lfsr = 0xACE1u;

static uint16_t rnd(void)
{
	lfsr ^= (uint16_t)(lfsr << 7);
	lfsr ^= (uint16_t)(lfsr >> 9);
	lfsr ^= (uint16_t)(lfsr << 8);
	return lfsr;
}

static int16_t random_gap_y(void)
{
	/* gap top between 8 and SCREEN_H - PIPE_GAP - 10 */
	int16_t min_y = 8;
	int16_t max_y = SCREEN_H - PIPE_GAP - 10;
	return (int16_t)(min_y + (rnd() % (uint16_t)(max_y - min_y + 1)));
}

static void spawn_pipe(Pipe_t *p, int16_t x)
{
	p->x = x;
	p->gap_y = random_gap_y();
	p->scored = 0;
}

void Flappy_Reset(GameState_t *g)
{
	g->mode = GAME_READY;
	g->bird_y = 28 * 10;
	g->bird_vy = 0;
	g->score = 0;
	spawn_pipe(&g->pipes[0], 80);
	spawn_pipe(&g->pipes[1], 80 + 64);
}

void Flappy_Start(GameState_t *g)
{
	g->mode = GAME_PLAYING;
	g->bird_vy = FLAP_VELOCITY;
}

void Flappy_OnFlap(GameState_t *g)
{
	if (g->mode == GAME_READY) {
		Flappy_Start(g);
		return;
	}
	if (g->mode == GAME_PLAYING) {
		g->bird_vy = FLAP_VELOCITY;
		return;
	}
	if (g->mode == GAME_OVER) {
		Flappy_Reset(g);
		Flappy_Start(g);
	}
}

static uint8_t bird_hits_pipe(const GameState_t *g, const Pipe_t *p)
{
	int16_t by = g->bird_y / 10;
	int16_t bx0 = BIRD_X;
	int16_t bx1 = BIRD_X + BIRD_SIZE;
	int16_t by0 = by;
	int16_t by1 = by + BIRD_SIZE;
	int16_t px0 = p->x;
	int16_t px1 = p->x + PIPE_W;

	if (bx1 < px0 || bx0 > px1) {
		return 0;
	}
	/* hit if outside gap */
	if (by0 < p->gap_y || by1 > (p->gap_y + PIPE_GAP)) {
		return 1;
	}
	return 0;
}

void Flappy_Tick(GameState_t *g)
{
	uint8_t i;

	if (g->mode != GAME_PLAYING) {
		return;
	}

	g->bird_vy += GRAVITY;
	g->bird_y += g->bird_vy / 10;

	{
		int16_t by = g->bird_y / 10;
		if (by < 0 || by + BIRD_SIZE >= GROUND_Y) {
			g->mode = GAME_OVER;
			if (g->score > g->best) {
				g->best = g->score;
			}
			return;
		}
	}

	for (i = 0; i < PIPE_COUNT; i++) {
		g->pipes[i].x -= PIPE_SPEED;

		if (!g->pipes[i].scored && (g->pipes[i].x + PIPE_W) < BIRD_X) {
			g->pipes[i].scored = 1;
			g->score++;
		}

		if (g->pipes[i].x < -PIPE_W) {
			int16_t other = g->pipes[(i + 1) % PIPE_COUNT].x;
			spawn_pipe(&g->pipes[i], other + 64);
		}

		if (bird_hits_pipe(g, &g->pipes[i])) {
			g->mode = GAME_OVER;
			if (g->score > g->best) {
				g->best = g->score;
			}
			return;
		}
	}
}

/* SDK oled_DrawSquare is outline-only — fill solid rectangles ourselves */
static void fill_rect(int16_t x0, int16_t x1, int16_t y0, int16_t y1)
{
	int16_t x, y;
	if (x0 < 0) {
		x0 = 0;
	}
	if (y0 < 0) {
		y0 = 0;
	}
	if (x1 >= SCREEN_W) {
		x1 = SCREEN_W - 1;
	}
	if (y1 >= SCREEN_H) {
		y1 = SCREEN_H - 1;
	}
	if (x0 > x1 || y0 > y1) {
		return;
	}
	for (y = y0; y <= y1; y++) {
		for (x = x0; x <= x1; x++) {
			oled_DrawPixel((uint8_t)x, (uint8_t)y, White);
		}
	}
}

static void draw_pipe(const Pipe_t *p)
{
	int16_t x0 = p->x;
	int16_t x1 = p->x + PIPE_W - 1;
	int16_t gap_bot = p->gap_y + PIPE_GAP;

	if (x1 < 0 || x0 >= SCREEN_W) {
		return;
	}

	if (p->gap_y > 0) {
		fill_rect(x0, x1, 0, p->gap_y - 1);
	}
	if (gap_bot < GROUND_Y) {
		fill_rect(x0, x1, gap_bot, GROUND_Y - 1);
	}
}

void Flappy_Draw(const GameState_t *g)
{
	char buf[16];
	uint8_t i;
	int16_t by = g->bird_y / 10;

	oled_Fill(Black);

	/* ground strip */
	fill_rect(0, SCREEN_W - 1, GROUND_Y, SCREEN_H - 1);

	for (i = 0; i < PIPE_COUNT; i++) {
		draw_pipe(&g->pipes[i]);
	}

	/* bird body */
	if (by < 0) {
		by = 0;
	}
	if (by > SCREEN_H - BIRD_SIZE) {
		by = SCREEN_H - BIRD_SIZE;
	}
	fill_rect(BIRD_X, BIRD_X + BIRD_SIZE - 1, by, by + BIRD_SIZE - 1);
	/* eye */
	oled_DrawPixel(BIRD_X + 4, (uint8_t)(by + 1), Black);

	sprintf(buf, "%u", (unsigned)g->score);
	oled_SetCursor(56, 0);
	oled_WriteString(buf, Font_7x10, White);

	if (g->mode == GAME_READY) {
		oled_SetCursor(22, 26);
		oled_WriteString("FLAPPY", Font_11x18, White);
		oled_SetCursor(10, 48);
		oled_WriteString("press key", Font_7x10, White);
	} else if (g->mode == GAME_OVER) {
		oled_SetCursor(28, 22);
		oled_WriteString("OVER", Font_11x18, White);
		sprintf(buf, "best %u", (unsigned)g->best);
		oled_SetCursor(36, 48);
		oled_WriteString(buf, Font_7x10, White);
	}

	oled_UpdateScreen();
}
