package ru.lab.translator.lexer;

import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("Integer", TokenType.INTEGER),
            Map.entry("Boolean", TokenType.BOOLEAN),
            Map.entry("Begin", TokenType.BEGIN),
            Map.entry("End", TokenType.END),
            Map.entry("Print", TokenType.PRINT),
            Map.entry("IF", TokenType.IF),
            Map.entry("THEN", TokenType.THEN),
            Map.entry("ELSE", TokenType.ELSE),
            Map.entry("ENDIF", TokenType.ENDIF),
            Map.entry("WHILE", TokenType.WHILE),
            Map.entry("DO", TokenType.DO),
            Map.entry("ENDWHILE", TokenType.ENDWHILE),
            Map.entry("CASE", TokenType.CASE),
            Map.entry("OF", TokenType.OF),
            Map.entry("ENDCASE", TokenType.ENDCASE),
            Map.entry("true", TokenType.TRUE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("AND", TokenType.AND),
            Map.entry("OR", TokenType.OR),
            Map.entry("NOT", TokenType.NOT)
    );

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = peek();

            if (Character.isWhitespace(c)) {
                advance();
            } else if (Character.isLetter(c)) {
                tokens.add(readIdentifierOrKeyword());
            } else if (Character.isDigit(c)) {
                tokens.add(readNumber());
            } else {
                tokens.add(readSymbol());
            }
        }
        tokens.add(new Token(TokenType.EOF, "", pos));
        return tokens;
    }

    private Token readIdentifierOrKeyword() {
        int start = pos;
        while (pos < input.length() && (Character.isLetterOrDigit(peek()))) {
            advance();
        }
        String word = input.substring(start, pos);
        TokenType type = KEYWORDS.getOrDefault(word, TokenType.ID);
        return new Token(type, word, start);
    }

    private Token readNumber() {
        int start = pos;
        while (pos < input.length() && Character.isDigit(peek())) {
            advance();
        }
        String number = input.substring(start, pos);
        return new Token(TokenType.NUMBER, number, start);
    }

    private Token readSymbol() {
        int start = pos;
        char c = advance();

        switch (c) {
            case '+': return new Token(TokenType.PLUS, "+", start);
            case '-': return new Token(TokenType.MINUS, "-", start);
            case '*': return new Token(TokenType.MUL, "*", start);
            case '/': return new Token(TokenType.DIV, "/", start);
            case '(': return new Token(TokenType.LPAREN, "(", start);
            case ')': return new Token(TokenType.RPAREN, ")", start);
            case ',': return new Token(TokenType.COMMA, ",", start);
            case ';': return new Token(TokenType.SEMICOLON, ";", start);
            case ':':
                if (peek() == '=') {
                    advance();
                    return new Token(TokenType.ASSIGN, ":=", start);
                }
                return new Token(TokenType.COLON, ":", start);
            case '<':
                if (peek() == '=') { advance(); return new Token(TokenType.LE, "<=", start); }
                return new Token(TokenType.LT, "<", start);
            case '>':
                if (peek() == '=') { advance(); return new Token(TokenType.GE, ">=", start); }
                return new Token(TokenType.GT, ">", start);
            case '=':
                if (peek() == '=') { advance(); return new Token(TokenType.EQ, "==", start); }
                break;
            case '!':
                if (peek() == '=') { advance(); return new Token(TokenType.NEQ, "!=", start); }
                break;
        }
        throw new RuntimeException("Неожиданный символ: " + c + " на позиции " + start);
    }

    private char peek() {
        return input.charAt(pos);
    }

    private char advance() {
        return input.charAt(pos++);
    }
}
