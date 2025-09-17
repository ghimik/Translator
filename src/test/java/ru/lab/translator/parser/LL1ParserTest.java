package ru.lab.translator.parser;

import org.junit.jupiter.api.Test;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.lexer.TokenType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LL1ParserTest {

    private void parseProgram(List<Token> tokens) {
        LL1Parser parser = new LL1Parser(tokens);
        parser.parseProgram();
    }

    private Token t(TokenType type, String value, int pos) {
        return new Token(type, value, pos);
    }

    // ------------------- POSITIVE TESTS -------------------

    @Test
    void testSingleDeclarationAndPrint() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "x", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.END, "End", 3),
                t(TokenType.PRINT, "Print", 4),
                t(TokenType.ID, "x", 5),
                t(TokenType.EOF, "", 6)
        );
        assertDoesNotThrow(() -> parseProgram(tokens));
    }

    @Test
    void testMultipleDeclarations() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.BOOLEAN, "Boolean", 0),
                t(TokenType.ID, "a", 1),
                t(TokenType.COMMA, ",", 2),
                t(TokenType.ID, "b", 3),
                t(TokenType.INTEGER, "Integer", 4),
                t(TokenType.ID, "n", 5),
                t(TokenType.BEGIN, "Begin", 6),
                t(TokenType.END, "End", 7),
                t(TokenType.PRINT, "Print", 8),
                t(TokenType.ID, "n", 9),
                t(TokenType.EOF, "", 10)
        );
        assertDoesNotThrow(() -> parseProgram(tokens));
    }

    @Test
    void testAssignmentWithExpression() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "x", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.ID, "x", 3),
                t(TokenType.ASSIGN, ":=", 4),
                t(TokenType.NUMBER, "5", 5),
                t(TokenType.PLUS, "+", 6),
                t(TokenType.NUMBER, "7", 7),
                t(TokenType.SEMICOLON, ";", 8),
                t(TokenType.END, "End", 9),
                t(TokenType.PRINT, "Print", 10),
                t(TokenType.ID, "x", 11),
                t(TokenType.EOF, "", 12)
        );
        assertDoesNotThrow(() -> parseProgram(tokens));
    }

    @Test
    void testIfElse() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.BOOLEAN, "Boolean", 0),
                t(TokenType.ID, "flag", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.IF, "IF", 3),
                t(TokenType.TRUE, "true", 4),
                t(TokenType.THEN, "THEN", 5),
                t(TokenType.ID, "flag", 6),
                t(TokenType.ASSIGN, ":=", 7),
                t(TokenType.FALSE, "false", 8),
                t(TokenType.SEMICOLON, ";", 9),
                t(TokenType.ELSE, "ELSE", 10),
                t(TokenType.ID, "flag", 11),
                t(TokenType.ASSIGN, ":=", 12),
                t(TokenType.TRUE, "true", 13),
                t(TokenType.SEMICOLON, ";", 14),
                t(TokenType.ENDIF, "ENDIF", 15),
                t(TokenType.END, "End", 16),
                t(TokenType.PRINT, "Print", 17),
                t(TokenType.ID, "flag", 18),
                t(TokenType.EOF, "", 19)
        );
        assertDoesNotThrow(() -> parseProgram(tokens));
    }

    @Test
    void testWhileLoop() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "i", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.WHILE, "WHILE", 3),
                t(TokenType.NUMBER, "1", 4),
                t(TokenType.LT, "<", 5),
                t(TokenType.NUMBER, "10", 6),
                t(TokenType.DO, "DO", 7),
                t(TokenType.ID, "i", 8),
                t(TokenType.ASSIGN, ":=", 9),
                t(TokenType.ID, "i", 10),
                t(TokenType.PLUS, "+", 11),
                t(TokenType.NUMBER, "1", 12),
                t(TokenType.SEMICOLON, ";", 13),
                t(TokenType.ENDWHILE, "ENDWHILE", 14),
                t(TokenType.END, "End", 15),
                t(TokenType.PRINT, "Print", 16),
                t(TokenType.ID, "i", 17),
                t(TokenType.EOF, "", 18)
        );
        assertDoesNotThrow(() -> parseProgram(tokens));
    }

    @Test
    void testCaseStatement() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "n", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.CASE, "CASE", 3),
                t(TokenType.ID, "n", 4),
                t(TokenType.OF, "OF", 5),
                t(TokenType.NUMBER, "1", 6),
                t(TokenType.COLON, ":", 7),
                t(TokenType.ID, "n", 8),
                t(TokenType.ASSIGN, ":=", 9),
                t(TokenType.NUMBER, "42", 10),
                t(TokenType.SEMICOLON, ";", 11),
                t(TokenType.ENDCASE, "ENDCASE", 12),
                t(TokenType.END, "End", 13),
                t(TokenType.PRINT, "Print", 14),
                t(TokenType.ID, "n", 15),
                t(TokenType.EOF, "", 16)
        );
        assertDoesNotThrow(() -> parseProgram(tokens));
    }

    // ------------------- NEGATIVE TESTS -------------------

    @Test
    void testMissingType() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.ID, "x", 0),
                t(TokenType.EOF, "", 1)
        );
        assertThrows(RuntimeException.class, () -> parseProgram(tokens));
    }

    @Test
    void testUnexpectedTokenInDeclaration() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.NUMBER, "123", 1), // вместо ID
                t(TokenType.EOF, "", 2)
        );
        assertThrows(RuntimeException.class, () -> parseProgram(tokens));
    }

    @Test
    void testMissingSemicolonInAssignment() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "x", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.ID, "x", 3),
                t(TokenType.ASSIGN, ":=", 4),
                t(TokenType.NUMBER, "5", 5),
                t(TokenType.END, "End", 6),
                t(TokenType.PRINT, "Print", 7),
                t(TokenType.ID, "x", 8),
                t(TokenType.EOF, "", 9)
        );
        assertThrows(RuntimeException.class, () -> parseProgram(tokens));
    }

    @Test
    void testIfWithoutElse() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.BOOLEAN, "Boolean", 0),
                t(TokenType.ID, "flag", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.IF, "IF", 3),
                t(TokenType.TRUE, "true", 4),
                t(TokenType.THEN, "THEN", 5),
                t(TokenType.END, "End", 6),
                t(TokenType.PRINT, "Print", 7),
                t(TokenType.ID, "flag", 8),
                t(TokenType.EOF, "", 9)
        );
        assertThrows(RuntimeException.class, () -> parseProgram(tokens));
    }

    @Test
    void testWhileWithoutEnd() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "i", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.WHILE, "WHILE", 3),
                t(TokenType.TRUE, "true", 4),
                t(TokenType.DO, "DO", 5),
                t(TokenType.END, "End", 6),
                t(TokenType.PRINT, "Print", 7),
                t(TokenType.ID, "i", 8),
                t(TokenType.EOF, "", 9)
        );
        assertThrows(RuntimeException.class, () -> parseProgram(tokens));
    }

    @Test
    void testUnexpectedTokenAfterProgram() {
        List<Token> tokens = Arrays.asList(
                t(TokenType.INTEGER, "Integer", 0),
                t(TokenType.ID, "x", 1),
                t(TokenType.BEGIN, "Begin", 2),
                t(TokenType.END, "End", 3),
                t(TokenType.PRINT, "Print", 4),
                t(TokenType.ID, "x", 5),
                t(TokenType.NUMBER, "999", 6), // лишний токен
                t(TokenType.EOF, "", 7)
        );
        assertThrows(RuntimeException.class, () -> parseProgram(tokens));
    }
}
