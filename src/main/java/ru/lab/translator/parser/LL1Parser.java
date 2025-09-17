package ru.lab.translator.parser;

import ru.lab.translator.lexer.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LL1Parser {

    private final List<Token> tokens;
    private int pos = 0;

    private final Set<String> declaredVariables = new HashSet<>();


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

    // ------------------------------------------------
    // Declarations, Identifiers, Expressions, etc
    // ------------------------------------------------

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

    private void parseDeclaration() {
        parseType();
        parseIdentifierList(true); // true = добавляем переменные в таблицу
    }

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

    private void parseIdentifierList(boolean isDeclaration) {
        parseIdentifier(isDeclaration);
        while (peek().getType() == TokenType.COMMA) {
            advance();
            parseIdentifier(isDeclaration);
        }
    }

    private void parseIdentifier() {
        parseIdentifier(false);
    }

    private void parseIdentifier(boolean isDeclaration) {
        Token t = peek();
        if (t.getType() == TokenType.ID) {
            String name = t.getValue();
            if (isDeclaration) {
                if (!declaredVariables.add(name)) {
                    throw new RuntimeException("Semantic error: переменная '" + name + "' уже объявлена (позиция " + t.getPosition() + ")");
                }
            } else {
                if (!declaredVariables.contains(name)) {
                    throw new RuntimeException("Semantic error: переменная '" + name + "' не объявлена (позиция " + t.getPosition() + ")");
                }
            }
            advance();
        } else {
            throw new RuntimeException(
                    "Syntax error: ожидался идентификатор, но найден " + t.getValue() + " на позиции " + t.getPosition()
            );
        }
    }

    // <Выражение> ::= <АрифВыр> | <ЛогВыр>
    private void parseExpression() {
        // смотрим токен: число, идентификатор, true/false, NOT, скобка
        Token t = peek();
        if (t.getType() == TokenType.NUMBER || t.getType() == TokenType.ID || t.getType() == TokenType.LPAREN) {
            parseArithmeticExpression();
        } else if (t.getType() == TokenType.TRUE || t.getType() == TokenType.FALSE
                || t.getType() == TokenType.NOT) {
            parseLogicalExpression();
        } else {
            throw new RuntimeException("Syntax error: недопустимое начало выражения " +
                    t.getValue() + " на позиции " + t.getPosition());
        }
    }

    // <АрифВыр> ::= <Термин> | <Термин> <БинОп> <АрифВыр>
    private void parseArithmeticExpression() {
        parseTerm();
        while (peek().getType() == TokenType.PLUS
                || peek().getType() == TokenType.MINUS
                || peek().getType() == TokenType.MUL
                || peek().getType() == TokenType.DIV) {
            advance(); // оператор
            parseTerm();
        }
    }

    // <Термин> ::= <Идент> | <Const> | ( <АрифВыр> )
    private void parseTerm() {
        Token t = peek();
        if (t.getType() == TokenType.ID) {
            parseIdentifier(); // тут проверка на объявление
        } else if (t.getType() == TokenType.NUMBER) {
            advance();
        } else if (t.getType() == TokenType.LPAREN) {
            advance();
            parseArithmeticExpression();
            expect(TokenType.RPAREN);
        } else {
            throw new RuntimeException("Syntax error: ожидался идентификатор, число или (expr), но найден " +
                    t.getValue() + " на позиции " + t.getPosition());
        }
    }


    // <ЛогВыр>
    private void parseLogicalExpression() {
        parseLogicalTerm();
        while (peek().getType() == TokenType.AND || peek().getType() == TokenType.OR) {
            advance(); // AND/OR
            parseLogicalTerm();
        }
    }

    private void parseLogicalTerm() {
        Token t = peek();
        if (t.getType() == TokenType.TRUE || t.getType() == TokenType.FALSE) {
            advance();
        } else if (t.getType() == TokenType.NOT) {
            advance();
            parseLogicalTerm();
        } else if (t.getType() == TokenType.ID || t.getType() == TokenType.NUMBER || t.getType() == TokenType.LPAREN) {
            parseArithmeticExpression();
            if (isRelOp(peek().getType())) {
                advance();
                parseArithmeticExpression();
            }
        } else {
            throw new RuntimeException("Syntax error: некорректное логическое выражение на позиции " + t.getPosition());
        }
    }

    private boolean isRelOp(TokenType type) {
        return type == TokenType.LT || type == TokenType.GT ||
                type == TokenType.LE || type == TokenType.GE ||
                type == TokenType.EQ || type == TokenType.NEQ;
    }

    // ------------------------------------------------
    // Новые методы: итоговый парсер, блок вычислений, операторы, Print
    // ------------------------------------------------

    public void parseProgram() {
        parseDeclarations();
        parseComputationDescription();
        parsePrintStatement(); // Print <Идент>
        // ожидание конца входа
        if (peek().getType() != TokenType.EOF) {
            Token t = peek();
            throw new RuntimeException("Syntax error: ожидался конец программы, но найден " + t.getValue() + " на позиции " + t.getPosition());
        }
    }

    // <Описание вычислений> ::= Begin <СписокОператоров> End
    private void parseComputationDescription() {
        expect(TokenType.BEGIN);
        parseStatementList();
        expect(TokenType.END);
    }

    // <СписокОператоров> ::= <Оператор> | <Оператор> <СписокОператоров>
    private void parseStatementList() {
        while (isStatementStart(peek().getType())) {
            parseStatement();
        }
    }

    private boolean isStatementStart(TokenType t) {
        return t == TokenType.ID || t == TokenType.IF || t == TokenType.WHILE || t == TokenType.CASE;
    }

    // <Оператор> ::= <Присваивание> | <Case> | <If> | <While>
    private void parseStatement() {
        Token t = peek();
        switch (t.getType()) {
            case ID:
                parseAssignment();
                break;
            case IF:
                parseIf();
                break;
            case WHILE:
                parseWhile();
                break;
            case CASE:
                parseCase();
                break;
            default:
                throw new RuntimeException("Syntax error: неожиданный оператор " + t.getValue() + " на позиции " + t.getPosition());
        }
    }

    // <Присваивание> ::= <Идент> := <Выражение> ;
    private void parseAssignment() {
        parseIdentifier();
        // предполагаем токен ASSIGN для ':='
        expect(TokenType.ASSIGN);
        parseExpression();
        expect(TokenType.SEMICOLON);
    }

    // <If> ::= IF <ЛогВыр> THEN <СписокОператоров> ELSE <СписокОператоров> ENDIF
    private void parseIf() {
        expect(TokenType.IF);
        parseLogicalExpression();
        expect(TokenType.THEN);
        parseStatementList();
        expect(TokenType.ELSE);
        parseStatementList();
        expect(TokenType.ENDIF);
    }

    // <While> ::= WHILE <ЛогВыр> DO <СписокОператоров> ENDWHILE
    private void parseWhile() {
        expect(TokenType.WHILE);
        parseLogicalExpression();
        expect(TokenType.DO);
        parseStatementList();
        expect(TokenType.ENDWHILE);
    }

    // <Case> ::= CASE <Идент> OF <Ветви> ENDCASE
    private void parseCase() {
        expect(TokenType.CASE);
        parseIdentifier();
        expect(TokenType.OF);
        parseBranchList();
        expect(TokenType.ENDCASE);
    }

    // <Ветви> ::= <Ветвь> | <Ветвь> <Ветви>
    // <Ветвь> ::= <Const> : <СписокОператоров>
    private void parseBranchList() {
        while (peek().getType() == TokenType.NUMBER) {
            parseBranch();
        }
    }

    private void parseBranch() {
        expect(TokenType.NUMBER);
        expect(TokenType.COLON);
        parseStatementList();
    }

    // <Оператор печати> ::= Print <Идент>
    public void parsePrintStatement() {
        expect(TokenType.PRINT);
        parseIdentifier();
    }
}
