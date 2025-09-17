package ru.lab.translator.e2e;

import org.junit.jupiter.api.Test;
import ru.lab.translator.ast.*;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.parser.LL1Parser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerAndParserE2EASTTest {

    private void assertASTEquals(ASTNode expected, ASTNode actual) {
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void testArithmeticAssignmentAST() {
        String code = """
        Integer x, y
        Begin
          x := 5;
          y := x + 3;
        End
        Print y
        """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        // строим ожидаемое дерево
        DeclarationsNode decls = new DeclarationsNode(List.of(
                new DeclarationNode("x", "Integer"),
                new DeclarationNode("y", "Integer")
        ));

        StatementsNode stmts = new StatementsNode(List.of(
                new AssignmentNode("x", new ConstNode(5)),
                new AssignmentNode("y", new BinaryExpressionNode("+", new VarNode("x"), new ConstNode(3)))
        ));

        PrintNode printNode = new PrintNode("y");

        ProgramNode expected = new ProgramNode(decls, stmts, printNode);



        System.out.println(program.toString());
        System.out.println(expected.toString());

        assertASTEquals(expected, program);
    }

    @Test
    void testIfStatementAST() {
        String code = """
        Integer x
        Boolean flag
        Begin
          x := 10;
          flag := true;
          IF flag THEN
            x := x + 1;
          ELSE
            x := x - 1;
          ENDIF
        End
        Print x
        """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(
                        new DeclarationNode("x", "Integer"),
                        new DeclarationNode("flag", "Boolean")
                )),
                new StatementsNode(List.of(
                        new AssignmentNode("x", new ConstNode(10)),
                        new AssignmentNode("flag", new ConstNode(1)),
                        new IfNode(
                                new VarNode("flag"),
                                new StatementsNode(List.of(
                                        new AssignmentNode("x", new BinaryExpressionNode("+", new VarNode("x"), new ConstNode(1)))
                                )),
                                new StatementsNode(List.of(
                                        new AssignmentNode("x", new BinaryExpressionNode("-", new VarNode("x"), new ConstNode(1)))
                                ))
                        )
                )),
                new PrintNode("x")
        );

        assertASTEquals(expected, program);
    }

    @Test
    void testWhileLoopAST() {
        String code = """
        Integer i, sum
        Begin
          i := 0;
          sum := 0;
          WHILE i < 5 DO
            sum := sum + i;
            i := i + 1;
          ENDWHILE
        End
        Print sum
        """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(
                        new DeclarationNode("i", "Integer"),
                        new DeclarationNode("sum", "Integer")
                )),
                new StatementsNode(List.of(
                        new AssignmentNode("i", new ConstNode(0)),
                        new AssignmentNode("sum", new ConstNode(0)),
                        new WhileNode(
                                new LogicalExpressionNode(new VarNode("i"), "<", new ConstNode(5)),
                                new StatementsNode(List.of(
                                        new AssignmentNode("sum", new BinaryExpressionNode("+", new VarNode("sum"), new VarNode("i"))),
                                        new AssignmentNode("i", new BinaryExpressionNode("+", new VarNode("i"), new ConstNode(1)))
                                ))
                        )
                )),
                new PrintNode("sum")
        );

        assertASTEquals(expected, program);
    }

    @Test
    void testCaseStatementAST() {
        String code = """
        Integer a, b
        Begin
          a := 2;
          CASE a OF
            1: b := 10;
            2: b := 20;
            3: b := 30;
          ENDCASE
        End
        Print b
        """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(
                        new DeclarationNode("a", "Integer"),
                        new DeclarationNode("b", "Integer")
                )),
                new StatementsNode(List.of(
                        new AssignmentNode("a", new ConstNode(2)),
                        new CaseNode(
                                "a",
                                List.of(
                                        new CaseBranchNode(1, new StatementsNode(List.of(new AssignmentNode("b", new ConstNode(10))))),
                                        new CaseBranchNode(2, new StatementsNode(List.of(new AssignmentNode("b", new ConstNode(20))))),
                                        new CaseBranchNode(3, new StatementsNode(List.of(new AssignmentNode("b", new ConstNode(30)))))
                                )
                        )
                )),
                new PrintNode("b")
        );

        assertASTEquals(expected, program);
    }

    @Test
    void testNestedExpressionsAST() {
        String code = """
        Integer x
        Begin
          x := (5 + 3) * (2 - 1);
        End
        Print x
        """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(new DeclarationNode("x", "Integer"))),
                new StatementsNode(List.of(
                        new AssignmentNode(
                                "x",
                                new BinaryExpressionNode(
                                        "*",
                                        new BinaryExpressionNode("+", new ConstNode(5), new ConstNode(3)),
                                        new BinaryExpressionNode("-", new ConstNode(2), new ConstNode(1))
                                )
                        )
                )),
                new PrintNode("x")
        );

        assertASTEquals(expected, program);
    }





}
