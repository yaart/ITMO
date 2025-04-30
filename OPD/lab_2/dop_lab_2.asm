;---------------------------переменные------------------------------------------
ORG 0x0
A: WORD 0xFFFF
B: WORD 0x1
C: WORD 0xFFE6
D: WORD 0x0

divident: WORD 0x0
divisor: WORD 0x0
divisionresult: WORD 0x0
divisioncounter: WORD 0x0
MAXVALUE: WORD 0xFFFF

multiplier_1: WORD 0x0
multiplier_2: WORD 0x0
mulresult: WORD 0x0
mulcounter: WORD 0x0

X0: WORD 0x0
X_result: WORD 0x0

KOEF: WORD 0x0; koef*(x-x1(=x0))(x+x2(=x0)*x+x3(=x0^2))=0
X1: WORD 0x0
X2: WORD 0x0
X3: WORD 0x0

;------------------------------основная часть---------------------------
LD $C
SUB $B
ST $divident
LD $A
ST $divisor

CALL $CHECK

CALL $DIV
LD $divisionresult
ST $D
CALL $FIND_X
LD $A
ST $KOEF
LD $X0
ST $X1
LD $X0
ST $X2
CALL $POW_2
LD $mulresult
ST $X3
HLT


;----------------------------деление--------------------------------------

DIV: CLA
ST $divisionresult
LD $divident
ST $divisioncounter

LD $divisor
BEQ DIV_IF_ZERO

DIVLOOP: LD $divisioncounter ;Цикл вычитания
BEQ DIVEND ; Проверка что делимое не равно нулю

LD $divisioncounter ;вычитание
SUB $divisor
ST $divisioncounter

LD $divisionresult ;уменьшение счетчика
INC
ST $divisionresult

JUMP $DIVLOOP

DIV_IF_ZERO:
LD $MAXVALUE
ST $divisionresult
RET

DIVEND: RET

;----------------------------умножение----------------------------------

MUL: CLA
ST $mulresult
LD $multiplier_1
BEQ MUL_IF_ZERO
LD $multiplier_2
BEQ MUL_IF_ZERO
LD $multiplier_2
ST $mulcounter

MULLOOP: LD $mulcounter
BEQ MULEND

LD $mulcounter
DEC
ST $mulcounter

LD $mulresult
ADD $multiplier_1
ST $mulresult

JUMP $MULLOOP

MUL_IF_ZERO: CLA
ST $mulresult
RET

MULEND: RET

;----------------------------возведение в куб-------------------------
POW_3: CLA
ST $X_result

LD $X0
ST $multiplier_1
LD $X0
ST $multiplier_2

CALL MUL
LD $mulresult
ST $multiplier_1

CALL MUL
LD $mulresult
ST $X_result
RET

;----------------------------возведение в квадрат-------------------
POW_2: CLA
ST $X_result

LD $X0
ST $multiplier_1
LD $X0
ST $multiplier_2

CALL MUL
RET

;----------------------------поиск корня---------------------------------
FIND_X: CLA
ST $X_result
LD $X0
CALL $POW_3

LD $X_result
CMP $D

BEQ GAME_OVER

LD $X0
INC
ST $X0

CALL FIND_X

GAME_OVER: RET

;----------------------------проверка знаков--------------------------
CHECK: CLA
LD $divident
BMI SIGN_divisor
LD $divisor
BMI BREAK
RET

SIGN_divisor: CLA
LD $divisor
BPL BREAK
CALL $DO_POS
RET

DO_POS: CLA
LD $divident
NEG
ST $divident
LD $divisor
NEG
ST $divisor
RET

BREAK: HLT

