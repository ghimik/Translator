package ru.lab.translator.parser;

import ru.lab.translator.lexer.*;

import java.util.List;

public class LL1Parser {

    private final List<Token> tokens;
    private int pos = 0;

    public LL1Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        if (pos < tokens.size()) return tokens.get(pos);
        return new Token(TokenType.EOF, "", pos);
    }

    private Token advance() {
        return tokens.get(pos++);
    }

    private void expect(TokenType type) {
        Token t = peek();
        if (t.getType() != type) {
            throw new RuntimeException(
                    "Syntax error: ожидался " + type + ", но найден " + t.getType() + " на позиции " + t.getPosition()
            );
        }
        advance();
    }
//

    // <Объявления> ::= <Объявление> | <Объявление> <Объявления>
    public void parseDeclarations() {
        if (peek().getType() != TokenType.INTEGER && peek().getType() != TokenType.BOOLEAN) {
            throw new RuntimeException(
                    "Syntax error: ожидался тип Integer или Boolean, но найден " + peek().getValue() + " на позиции " + peek().getPosition()
            );
        }

        while (peek().getType() == TokenType.INTEGER || peek().getType() == TokenType.BOOLEAN) {
            parseDeclaration();
        }
    }


    // <Объявление> ::= <Тип> <СписокПеременных>
    private void parseDeclaration() {
        parseType();
        parseIdentifierList();
    }

    // <Тип> ::= Integer | Boolean
    private void parseType() {
        Token t = peek();
        if (t.getType() == TokenType.INTEGER || t.getType() == TokenType.BOOLEAN) {
            advance();
        } else {
            throw new RuntimeException(
                    "Syntax error: ожидался тип Integer или Boolean, но найден " + t.getValue() + " на позиции " + t.getPosition()
            );
        }
    }

    // <СписокПеременных> ::= <Идент> | <Идент> , <СписокПеременных>
    private void parseIdentifierList() {
        parseIdentifier();
        while (peek().getType() == TokenType.COMMA) {
            advance(); // пропускаем ','
            parseIdentifier();
        }
    }

    // <Идент> ::= <Буква> | <Буква><Идент>
    private void parseIdentifier() {
        Token t = peek();
        if (t.getType() == TokenType.ID) {
            advance();
        } else {
            throw new RuntimeException(
                    "Syntax error: ожидался идентификатор, но найден " + t.getValue() + " на позиции " + t.getPosition()
            );
        }
    }
}
