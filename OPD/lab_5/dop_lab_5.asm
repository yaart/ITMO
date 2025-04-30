ORG 0x0

RES:        WORD 0            
TEMP:       WORD 0            
MUL_10:     WORD 0            

END_:       LD    #0xA
            OUT   0xC
            HLT

START:      CLA               

NUMBER:     IN    0x1D
            AND   #0x40
            BEQ   NUMBER
            IN    0x1C
            CMP   #0x0F         
            BEQ   SHOW
            CMP   #0x0A       
            BGE   END_
            ST    TEMP
            ADD   #0x30
            OUT   0xC      
            LD    TEMP
            LD    RES
            ASL
            ST    MUL_10
            ASL
            ASL
            ADD   MUL_10
            ADD   TEMP          
            ST    RES
            JUMP  NUMBER

; СТАРШАЯ ЧАСТЬ РИМСКИХ ЦИФР
MAX_VALUE:  WORD  0x1389
V_5000:     WORD  0x1388
MV_4000:    WORD  0xFA0
M_1000:     WORD  0x3E8
CM_900:     WORD  0x384
D_500:      WORD  0x1F4
CD_400:     WORD  0x190

SHOW:       LD    RES
            ST    TEMP_NUM
            CMP   MAX_VALUE     
            BGE   END_
            LD    #0x20
            OUT   0xC
            LD    TEMP_NUM

CONVERT:    CMP   V_5000
            BLT   CHECK_MV
            SUB   V_5000 
            ST    TEMP_NUM
            LD    #0x56 
            OUT   0xC
            LD    #0x7E
            OUT   0xC
            LD    TEMP_NUM
            JUMP  END_

CHECK_MV:   CMP   MV_4000
            BLT   CHECK_M
            SUB   MV_4000      
            ST    TEMP_NUM 
            LD    #0x4D
            OUT   0xC
            LD    #0x56  
            OUT   0xC
            LD    #0x7E
            OUT   0xC
            LD    TEMP_NUM
            JUMP  CHECK_CM
            
CHECK_M:    CMP   M_1000
            BLT   CHECK_CM
            SUB   M_1000
            ST    TEMP_NUM
            LD    #0x4D
            OUT   0xC
            LD    TEMP_NUM
            JUMP  CHECK_M

CHECK_CM:   CMP   CM_900
            BLT   CHECK_D
            SUB   CM_900
            ST    TEMP_NUM
            LD    #0x43
            OUT   0xC
            LD    #0x4D
            OUT   0xC
            LD    TEMP_NUM
            JUMP  CHECK_XC

CHECK_D:    CMP   D_500        
            BLT   CHECK_CD      
            SUB   D_500
            ST    TEMP_NUM  
            LD    #0x44       
            OUT   0xC
            LD    TEMP_NUM         
            JUMP  CHECK_C      

CHECK_CD:   CMP   CD_400        
            BLT   CHECK_C       
            SUB   CD_400 
            ST    TEMP_NUM       
            LD    #0x43          
            OUT   0xC          
            LD    #0x44         
            OUT   0xC
            LD    TEMP_NUM          
            JUMP  CHECK_XC     

; ВРЕМЕННАЯ ПЕРЕМЕННАЯ ДЛЯ СОХРАНЕНИЯ ТЕКУЩЕГО ЗНАЧЕНИЯ ЧИСЛА
TEMP_NUM:   WORD  0
; МЛАДШАЯ ЧАСТЬ РИМСКИХ ЦИФР
C_100:      WORD  0x64
XC_90:      WORD  0x5A
L_50:       WORD  0x32
XL_40:      WORD  0x28
X_10:       WORD  0xA
IX_9:       WORD  0x9
V_5:        WORD  0x5
IV_4:       WORD  0x4
I_1:        WORD  0x1

CHECK_C:    CMP   C_100        
            BLT   CHECK_XC      
            SUB   C_100  
            ST    TEMP_NUM      
            LD    #0x43          
            OUT   0xC
            LD    TEMP_NUM          
            JUMP  CHECK_C      
            
CHECK_XC:   CMP   XC_90        
            BLT   CHECK_L      
            SUB   XC_90 
            ST    TEMP_NUM       
            LD    #0x58         
            OUT   0xC         
            LD    #0x43         
            OUT   0xC 
            LD    TEMP_NUM        
            JUMP  CHECK_IX     

CHECK_L:    CMP   L_50         
            BLT   CHECK_XL     
            SUB   L_50  
            ST    TEMP_NUM       
            LD    #0x4C         
            OUT   0xC 
            LD    TEMP_NUM        
            JUMP  CHECK_X      

CHECK_XL:   CMP   XL_40        
            BLT   CHECK_X      
            SUB   XL_40 
            ST    TEMP_NUM       
            LD    #0x58         
            OUT   0xC         
            LD    #0x4C         
            OUT   0xC
            LD    TEMP_NUM         
            JUMP  CHECK_IX     

CHECK_X:    CMP   X_10         
            BLT   CHECK_IX     
            SUB   X_10
            ST    TEMP_NUM         
            LD    #0x58         
            OUT   0xC
            LD    TEMP_NUM         
            JUMP  CHECK_X     

CHECK_IX:   CMP   IX_9         
            BLT   CHECK_V      
            SUB   IX_9  
            ST    TEMP_NUM       
            LD    #0x49         
            OUT   0xC         
            LD    #0x58         
            OUT   0xC 
            LD    TEMP_NUM        
            JUMP  END_CONVERT  

CHECK_V:    CMP   V_5          
            BLT   CHECK_IV     
            SUB   V_5
            ST    TEMP_NUM          
            LD    #0x56         
            OUT   0xC 
            LD    TEMP_NUM   
            JUMP  CHECK_I     

CHECK_IV:   CMP   IV_4         
            BLT   CHECK_I      
            SUB   IV_4   
            ST    TEMP_NUM      
            LD    #0x49         
            OUT   0xC         
            LD    #0x56         
            OUT   0xC
            LD    TEMP_NUM         
            JUMP  END_CONVERT  

CHECK_I:    CMP   I_1          
            BLT   END_CONVERT  
            SUB   I_1
            ST    TEMP_NUM          
            LD    #0x49      
            OUT   0xC 
            LD    TEMP_NUM        
            JUMP  CHECK_I     

END_CONVERT: LD   #0xA
             OUT  0xC
             HLT
