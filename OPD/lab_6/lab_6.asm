ORG 0x000; Инициализация векторов прерывания
V0: 	WORD $default, 0x180
V1: 	WORD $default, 0x180
V2: 	WORD $int2, 0x180
V3: 	WORD $int3, 0x180
V4: 	WORD $default, 0x180
V5: 	WORD $default, 0x180
V6: 	WORD $default, 0x180
V7: 	WORD $default, 0x180

ORG 0x010
int3: DI
      LD X
      NOP
      ASL
      ADD X
      NEG
      SUB #7
      OUT 0x6
      EI
      IRET
int2: DI
      CLA
      IN 0x4
      AND #0x000F
      OR X
      ST X
      NOP
      EI
      IRET

ORG 0x03A
X: WORD ?
MAX: WORD 0x0028; правая граница ОДЗ = 40
MIN: WORD 0xFFD4; левая граница ОДЗ = -44
default: IRET; Обработка прерывания по умолчанию

start:
      DI
      CLA
      OUT 0x1
      OUT 0x3
      OUT 0xB
      OUT 0xE
      OUT 0x12
      OUT 0x16
      OUT 0x1A
      OUT 0x1E
      LD #0xB; Загрузка в аккумулятор MR (1000|0011=1011)
      OUT 0x7; Разрешение прерываний для ВУ-3
      LD #0xA ; Загрузка в аккумулятор MR (1000|0010=1010)
      OUT 0x5 ; Разрешение прерываний для ВУ-2
      EI

PROG: DI
      LD X
      SUB #0x3
      CALL CHECK
      ST X
      NOP
      EI
      JUMP PROG
      
CHECK: 
      CMP MAX
      BGE MAXLD
      CMP MIN
      BLT MAXLD
      JUMP return
      
MAXLD:  LD MAX
return: RET
