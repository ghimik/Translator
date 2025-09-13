package ru.lab.translator.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void testSimpleProgram() {
        String source = """
                Integer x, y
                Begin
                x := 5;
                y := x + 3;
                End
                Print y
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        assertEquals(19, tokens.size());

        assertEquals(TokenType.INTEGER, tokens.get(0).getType());
        assertEquals("Integer", tokens.get(0).getValue());

        assertEquals(TokenType.ID, tokens.get(1).getType());
        assertEquals("x", tokens.get(1).getValue());

        assertEquals(TokenType.COMMA, tokens.get(2).getType());
        assertEquals(",", tokens.get(2).getValue());

        assertEquals(TokenType.ID, tokens.get(3).getType());
        assertEquals("y", tokens.get(3).getValue());

        assertEquals(TokenType.BEGIN, tokens.get(4).getType());
        assertEquals("Begin", tokens.get(4).getValue());

        assertEquals(TokenType.ID, tokens.get(5).getType());
        assertEquals("x", tokens.get(5).getValue());

        assertEquals(TokenType.ASSIGN, tokens.get(6).getType());
        assertEquals(":=", tokens.get(6).getValue());

        assertEquals(TokenType.NUMBER, tokens.get(7).getType());
        assertEquals("5", tokens.get(7).getValue());

        assertEquals(TokenType.SEMICOLON, tokens.get(8).getType());

        Token eof = tokens.get(tokens.size() - 1);
        assertEquals(TokenType.EOF, eof.getType());
    }

    @Test
    void testBooleanTokens() {
        String source = "Boolean flag\nflag := true;";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        assertEquals(TokenType.BOOLEAN, tokens.get(0).getType());
        assertEquals("Boolean", tokens.get(0).getValue());

        assertEquals(TokenType.ID, tokens.get(1).getType());
        assertEquals("flag", tokens.get(1).getValue());

        assertEquals(TokenType.ID, tokens.get(2).getType());
        assertEquals("flag", tokens.get(2).getValue());

        assertEquals(TokenType.ASSIGN, tokens.get(3).getType());
        assertEquals(":=", tokens.get(3).getValue());

        assertEquals(TokenType.TRUE, tokens.get(4).getType());
        assertEquals("true", tokens.get(4).getValue());

        assertEquals(TokenType.SEMICOLON, tokens.get(5).getType());
    }
}
