package ru.lab.translator.parser;

import ru.lab.translator.ast.*;
import ru.lab.translator.lexer.*;

import java.util.ArrayList;
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


    // <Объявления> ::= <Объявление> | <Объявление> <Объявления>
    private DeclarationsNode parseDeclarations() {
        List<DeclarationNode> declList = new ArrayList<>();
        while (peek().getType() == TokenType.INTEGER || peek().getType() == TokenType.BOOLEAN) {
            declList.addAll(parseDeclaration());
        }
        return new DeclarationsNode(declList);
    }

    private List<DeclarationNode> parseDeclaration() {
        Token typeToken = peek();
        parseType();
        String type = typeToken.getValue();
        List<String> identifiers = parseIdentifierList(true);
        return identifiers.stream().map(id -> new DeclarationNode(id, type)).toList();
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


    private List<String> parseIdentifierList(boolean isDeclaration) {
        List<String> ids = new ArrayList<>();
        ids.add(parseIdentifier(isDeclaration));
        while (peek().getType() == TokenType.COMMA) {
            advance();
            ids.add(parseIdentifier(isDeclaration));
        }
        return ids;
    }

    private String parseIdentifier(boolean isDeclaration) {
        Token t = peek();
        if (t.getType() == TokenType.ID) {
            String name = t.getValue();
            if (isDeclaration) {
                if (!declaredVariables.add(name)) {
                    throw new RuntimeException("Semantic error: переменная '" + name + "' уже объявлена");
                }
            } else {
                if (!declaredVariables.contains(name)) {
                    throw new RuntimeException("Semantic error: переменная '" + name + "' не объявлена");
                }
            }
            advance();
            return name;
        } else {
            throw new RuntimeException("Ожидался идентификатор");
        }
    }



    // <Выражение> ::= <АрифВыр> | <ЛогВыр>
    private ExpressionNode parseExpression() {
        return parseOr();
    }

    private ExpressionNode parseOr() {
        ExpressionNode left = parseAnd();
        while (peek().getType() == TokenType.OR) {
            String op = advance().getValue();
            ExpressionNode right = parseAnd();
            left = new LogicalExpressionNode(left, op, right);
        }
        return left;
    }

    private ExpressionNode parseAnd() {
        ExpressionNode left = parseNot();
        while (peek().getType() == TokenType.AND) {
            String op = advance().getValue();
            ExpressionNode right = parseNot();
            left = new LogicalExpressionNode(left, op, right);
        }
        return left;
    }

    private ExpressionNode parseNot() {
        if (peek().getType() == TokenType.NOT) {
            advance();
            ExpressionNode expr = parseNot();
            return new LogicalExpressionNode(expr, "NOT", null);
        }
        return parseComparison();
    }

    private ExpressionNode parseComparison() {
        ExpressionNode left = parseArithmeticExpression();
        if (isRelOp(peek().getType())) {
            String op = advance().getValue();
            ExpressionNode right = parseArithmeticExpression();
            left = new LogicalExpressionNode(left, op, right);
        }
        return left;
    }

    private ExpressionNode parseArithmeticExpression() {
        ExpressionNode left = parseMulDiv();
        while (peek().getType() == TokenType.PLUS || peek().getType() == TokenType.MINUS) {
            String op = advance().getValue();
            ExpressionNode right = parseMulDiv();
            left = new BinaryExpressionNode(op, left, right);
        }
        return left;
    }

    private ExpressionNode parseMulDiv() {
        ExpressionNode left = parseTerm();
        while (peek().getType() == TokenType.MUL || peek().getType() == TokenType.DIV) {
            String op = advance().getValue();
            ExpressionNode right = parseTerm();
            left = new BinaryExpressionNode(op, left, right);
        }
        return left;
    }

    private ExpressionNode parseTerm() {
        Token t = peek();
        if (t.getType() == TokenType.NUMBER) return parseConst();
        if (t.getType() == TokenType.ID) return parseVar();
        if (t.getType() == TokenType.LPAREN) {
            advance();
            ExpressionNode expr = parseExpression();
            expect(TokenType.RPAREN);
            return expr;
        }
        if (t.getType() == TokenType.TRUE) { advance(); return new ConstNode(1); }
        if (t.getType() == TokenType.FALSE) { advance(); return new ConstNode(0); }

        throw new RuntimeException("Syntax error: неожиданный токен " + t.getValue() + " на позиции " + t.getPosition());
    }

    private boolean isRelOp(TokenType type) {
        return type == TokenType.LT || type == TokenType.GT ||
                type == TokenType.LE || type == TokenType.GE ||
                type == TokenType.EQ || type == TokenType.NEQ;
    }



    private ConstNode parseConst() {
        int val = Integer.parseInt(peek().getValue());
        advance();
        return new ConstNode(val);
    }

    private VarNode parseVar() {
        String name = parseIdentifier(false);
        return new VarNode(name);
    }




    // <ЛогВыр>
    private ExpressionNode parseLogicalExpression() {
        ExpressionNode left = parseLogicalTerm();
        while (peek().getType() == TokenType.AND || peek().getType() == TokenType.OR) {
            String op = advance().getValue(); // AND/OR
            ExpressionNode right = parseLogicalTerm();
            left = new LogicalExpressionNode(left, op, right);
        }
        return left;
    }

    private ExpressionNode parseLogicalTerm() {
        Token t = peek();
        if (t.getType() == TokenType.TRUE) {
            advance();
            return new ConstNode(1);
        } else if (t.getType() == TokenType.FALSE) {
            advance();
            return new ConstNode(0);
        } else if (t.getType() == TokenType.NOT) {
            advance();
            ExpressionNode term = parseLogicalTerm();
            return new LogicalExpressionNode(term, "NOT", null); // для NOT можно обрабатывать null
        } else if (t.getType() == TokenType.ID || t.getType() == TokenType.NUMBER || t.getType() == TokenType.LPAREN) {
            ExpressionNode left = parseArithmeticExpression();
            if (isRelOp(peek().getType())) {
                String op = advance().getValue();
                ExpressionNode right = parseArithmeticExpression();
                return new LogicalExpressionNode(left, op, right);
            }
            return left;
        } else {
            throw new RuntimeException("Syntax error: некорректное логическое выражение на позиции " + t.getPosition());
        }
    }


    public ProgramNode parseProgram() {
        DeclarationsNode decls = parseDeclarations();
        StatementsNode stmts = parseComputationDescription();
        PrintNode printNode = parsePrintStatement();
        expect(TokenType.EOF);
        return new ProgramNode(decls, stmts, printNode);
    }


    // <Описание вычислений> ::= Begin <СписокОператоров> End
    private StatementsNode parseComputationDescription() {
        expect(TokenType.BEGIN);
        StatementsNode stmts = parseStatementList();
        expect(TokenType.END);
        return stmts;
    }


    // <СписокОператоров> ::= <Оператор> | <Оператор> <СписокОператоров>
    private StatementsNode parseStatementList() {
        List<StatementNode> list = new ArrayList<>();
        while (isStatementStart(peek().getType())) {
            list.add(parseStatement());
        }
        return new StatementsNode(list);
    }


    private boolean isStatementStart(TokenType t) {
        return t == TokenType.ID || t == TokenType.IF || t == TokenType.WHILE || t == TokenType.CASE || t == TokenType.PRINT;
    }

    // <Оператор> ::= <Присваивание> | <Case> | <If> | <While>

    private StatementNode parseStatement() {
        switch (peek().getType()) {
            case ID -> { return parseAssignment(); }
            case IF -> { return parseIf(); }
            case WHILE -> { return parseWhile(); }
            case CASE -> { return parseCase(); }
            case PRINT -> {
                PrintNode printNode = parsePrintStatement();
                expect(TokenType.SEMICOLON);
                return printNode;
            }
            default -> throw new RuntimeException("Unexpected token " + peek().getValue());
        }
    }

    // <Присваивание> ::= <Идент> := <Выражение> ;
    private AssignmentNode parseAssignment() {
        String varName = parseIdentifier(false);
        expect(TokenType.ASSIGN);
        ExpressionNode expr = parseExpression();
        expect(TokenType.SEMICOLON);
        return new AssignmentNode(varName, expr);
    }


    // <If> ::= IF <ЛогВыр> THEN <СписокОператоров> ELSE <СписокОператоров> ENDIF
    private IfNode parseIf() {
        expect(TokenType.IF);
        ExpressionNode cond = parseLogicalExpression();
        expect(TokenType.THEN);
        StatementsNode thenBranch = parseStatementList();

        StatementsNode elseBranch = new StatementsNode(new ArrayList<>());
        if (peek().getType() == TokenType.ELSE) {
            advance();
            elseBranch = parseStatementList();
        }

        expect(TokenType.ENDIF);
        return new IfNode(cond, thenBranch, elseBranch);
    }


    // <While> ::= WHILE <ЛогВыр> DO <СписокОператоров> ENDWHILE
    private WhileNode parseWhile() {
        expect(TokenType.WHILE);
        ExpressionNode cond = parseLogicalExpression();
        expect(TokenType.DO);
        StatementsNode body = parseStatementList();
        expect(TokenType.ENDWHILE);
        return new WhileNode(cond, body);
    }

    // <Case> ::= CASE <Идент> OF <Ветви> ENDCASE
    private CaseNode parseCase() {
        expect(TokenType.CASE);
        String varName = parseIdentifier(false);
        expect(TokenType.OF);
        List<CaseBranchNode> branches = parseBranchList();
        expect(TokenType.ENDCASE);
        return new CaseNode(varName, branches);
    }

    // <Ветви> ::= <Ветвь> | <Ветвь> <Ветви>
    // <Ветвь> ::= <Const> : <СписокОператоров>
    private List<CaseBranchNode> parseBranchList() {
        List<CaseBranchNode> list = new ArrayList<>();
        while (peek().getType() == TokenType.NUMBER) {
            list.add(parseBranch());
        }
        return list;
    }


    private CaseBranchNode parseBranch() {
        int val = Integer.parseInt(peek().getValue());
        advance();
        expect(TokenType.COLON);
        StatementsNode body = parseStatementList();
        return new CaseBranchNode(val, body);
    }

    // <Оператор печати> ::= Print <Идент>
    private PrintNode parsePrintStatement() {
        expect(TokenType.PRINT);
        String varName = parseIdentifier(false);
        return new PrintNode(varName);
    }

}
