package ru.lab.translator.lexer;


public enum TokenType {

// основная база
    INTEGER, BOOLEAN, BEGIN, END, PRINT,
    IF, THEN, ELSE, ENDIF,
    WHILE, DO, ENDWHILE,
    CASE, OF, ENDCASE,
    TRUE, FALSE,
// присваивание, арифметика, логика
    ASSIGN,
    PLUS, MINUS, MUL, DIV,
    LT, GT, LE, GE, EQ, NEQ,
    AND, OR, NOT,
// скобочки, точка с запятой, запятая
    LPAREN, RPAREN, COMMA, SEMICOLON, COLON,
// идент и константные числа
    ID, NUMBER,
// хз может понадобится в лабах было
    EOF
}