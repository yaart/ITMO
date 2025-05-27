ORG 0x0
T1:	WORD 0x0; Тест 1 - Проверка корректного результата при С = 0
T2:	WORD 0x0; Тест 2 - Проверка на отсутствие изменения NZVC
T3:	WORD 0x0; Тест 3 - Проверка на корректный результат при С=1
T4:	WORD 0x0; Тест 4 - Крайний случай


ORG 0x0A4
START:	CALL $TEST1; Вызов тестов
 	LD $T1
	NOP
	CALL $TEST2
 	LD $T2
	NOP
	CALL $TEST3
	LD $T3
	NOP
	CALL $TEST4
	LD $T4
	HLT

ORG 0x100
A1: 	WORD 0xB345
B1: 	WORD 0x0135
RES1: 	WORD ?
TEST1:	CLA
	CLC
	LD A1
	ADC B1
       	ST RES1
        
	LD A1
	WORD 0x9101; Выполнение команды MADC
	LD B1
	CMP RES1; Проверка результатов
	BNE ERR1
	
	LD #0x1
	ST $T1
	RET
        
ERR1:	LD  #0x0
	ST $T1
	RET
        
ORG 0x200
A2: 	WORD 0xB345
B2: 	WORD 0xFFFF
TEST2:	CLA
	CLC
	LD A2
	WORD 0x9201; Выполнение команды MADC
	BHIS ERR2; CF выставляться не должен 
	
	LD #0x1
	ST $T2
	RET
        
ERR2: 	LD #0x0
	ST $T2
       	RET

ORG 0x300
A3: 	WORD 0xB345
B3: 	WORD 0xFFFF
RES3:	WORD ?

TEST3: 	CLA
	CLC
	CMC
	LD A3
	ADC B3
	ST $RES3
	
	LD A3
	WORD 0x9301; Выполнение MADC с выставленным флагом С
	LD B3
	BLO ERR3
	CMP RES3
	BNE ERR3

	LD #0x1
	ST $T3
	RET
        
ERR3: 	LD #0x0
	ST $T3
	RET
        
ORG 0x400
A4: 	WORD 0x0000
B4: 	WORD 0xFFFF
RES4:	WORD ?

TEST4:	CLA
	CLC
	CMC
	LD A4
	ADC B4
	ST $RES4

	LD A4
	WORD 0x9401; Выполнение MADC с выставленным флагом С
	LD B4
	BCC ERR4
	CMP RES4
	BNE ERR4

	LD #0x1
	ST $T4
	RET
        
ERR4: 	LD #0x0
	ST $T4
	RET
