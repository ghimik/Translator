package ru.lab.translator.parser;

import org.junit.jupiter.api.Test;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.lexer.TokenType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LL1ParserTest {

    @Test
    void testSingleDeclarationInteger() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "Integer", 0),
                new Token(TokenType.ID, "x", 8),
                new Token(TokenType.EOF, "", 9)
        );

        LL1Parser parser = new LL1Parser(tokens);
        assertDoesNotThrow(parser::parseDeclarations);
    }

    @Test
    void testMultipleDeclarations() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "Integer", 0),
                new Token(TokenType.ID, "x", 8),
                new Token(TokenType.COMMA, ",", 9),
                new Token(TokenType.ID, "y", 11),
                new Token(TokenType.BOOLEAN, "Boolean", 13),
                new Token(TokenType.ID, "flag", 21),
                new Token(TokenType.EOF, "", 25)
        );

        LL1Parser parser = new LL1Parser(tokens);
        assertDoesNotThrow(parser::parseDeclarations);
    }

    @Test
    void testMissingIdentifier() {
        List<Token> tokens = List.of(
                new Token(TokenType.INTEGER, "Integer", 0),
                new Token(TokenType.COMMA, ",", 8),
                new Token(TokenType.EOF, "", 9)
        );

        LL1Parser parser = new LL1Parser(tokens);
        RuntimeException ex = assertThrows(RuntimeException.class, parser::parseDeclarations);
        assertTrue(ex.getMessage().contains("ожидался идентификатор"));
    }

    @Test
    void testInvalidType() {
        List<Token> tokens = List.of(
                new Token(TokenType.ID, "x", 0),
                new Token(TokenType.ID, "y", 2),
                new Token(TokenType.EOF, "", 3)
        );

        LL1Parser parser = new LL1Parser(tokens);
        RuntimeException ex = assertThrows(RuntimeException.class, parser::parseDeclarations);
        assertTrue(ex.getMessage().contains("ожидался тип"));
    }
}
