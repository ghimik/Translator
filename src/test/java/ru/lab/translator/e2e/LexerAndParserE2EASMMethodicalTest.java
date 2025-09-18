package ru.lab.translator.e2e;

import org.junit.jupiter.api.Test;
import ru.lab.translator.ast.*;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.parser.LL1Parser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerAndParserE2EASMMethodicalTest {

    private void assertASTEquals(ASTNode expected, ASTNode actual) {
        assertEquals(expected.toString(), actual.toString());
    }

    private void runTest(String source, ASTNode expected) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();
        assertASTEquals(program, expected);

        String asm = program.generateAssembly();
        System.out.println("Assembly generated successfully[" + Thread.currentThread().getStackTrace()[2].getMethodName() + "]:\n" + asm);
    }

    // --------------------------
    // Тест 01: арифметика со скобками
    // --------------------------
    @Test
    void testArithmeticExpressionFullAST() {
        String code = """
            Integer a, n, res
            Begin
              a := 2;
              n := 5;
              res := a + 3 * (n + 1) / a - 2;
            End
            Print res
            """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(
                        new DeclarationNode("a", "Integer"),
                        new DeclarationNode("n", "Integer"),
                        new DeclarationNode("res", "Integer")
                )),
                new StatementsNode(List.of(
                        new AssignmentNode("a", new ConstNode(2)),
                        new AssignmentNode("n", new ConstNode(5)),
                        new AssignmentNode(
                                "res",
                                new BinaryExpressionNode(
                                        "-",
                                        new BinaryExpressionNode(
                                                "+",
                                                new VarNode("a"),
                                                new BinaryExpressionNode(
                                                        "/",
                                                        new BinaryExpressionNode("*", new ConstNode(3), new BinaryExpressionNode("+", new VarNode("n"), new ConstNode(1))),
                                                        new VarNode("a")
                                                )
                                        ),
                                        new ConstNode(2)
                                )
                        )
                )),
                new PrintNode("res")
        );

        runTest(code, program);
    }

    // --------------------------
    // Тест 02: if/while
    // --------------------------
    @Test
    void testIfStatementAST() {
        String code = """
            Integer a, b, c
            Begin
              a := 77;
              c := 2;
              IF a > 20 THEN
                  Print a;
              ELSE
                  Print c;
              ENDIF
            End
            Print a
            """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(
                        new DeclarationNode("a", "Integer"),
                        new DeclarationNode("b", "Integer"),
                        new DeclarationNode("c", "Integer")
                )),
                new StatementsNode(List.of(
                        new AssignmentNode("a", new ConstNode(77)),
                        new AssignmentNode("c", new ConstNode(2)),
                        new IfNode(
                                new BinaryExpressionNode(">", new VarNode("a"), new ConstNode(20)),
                                new StatementsNode(List.of(new PrintNode("a"))),
                                new StatementsNode(List.of(new PrintNode("c")))
                        )
                )),
                new PrintNode("a")
        );

        runTest(code, program);
    }

    @Test
    void testWhileStatementAST() {
        String code = """
            Integer a, b, c, temp, othertemp, newtemp
            Begin
              a := 7;
              WHILE a < 14 DO
                  a := a + 1;
              ENDWHILE
            End
            Print a
            """;

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        ProgramNode expected = new ProgramNode(
                new DeclarationsNode(List.of(
                        new DeclarationNode("a", "Integer"),
                        new DeclarationNode("b", "Integer"),
                        new DeclarationNode("c", "Integer"),
                        new DeclarationNode("temp", "Integer"),
                        new DeclarationNode("othertemp", "Integer"),
                        new DeclarationNode("newtemp", "Integer")
                )),
                new StatementsNode(List.of(
                        new AssignmentNode("a", new ConstNode(7)),
                        new WhileNode(
                                new BinaryExpressionNode("<", new VarNode("a"), new ConstNode(14)),
                                new StatementsNode(List.of(
                                        new AssignmentNode("a", new BinaryExpressionNode("+", new VarNode("a"), new ConstNode(1)))
                                ))
                        )
                )),
                new PrintNode("a")
        );

        runTest(code, program);
    }

    // --------------------------
    // Тесты на ошибки лексера
    // --------------------------
    @Test
    void testLexerError() {
        String code = "Integer 2a, n, res\nBegin\na := 2;\nEnd\nPrint a";

        Lexer lexer = new Lexer(code);
        assertThrows(RuntimeException.class, lexer::tokenize);
    }

    // --------------------------
    // Тесты на ошибки парсера
    // --------------------------
    @Test
    void testParserErrorBeginKeyword() {
        String code = "Integer a, n, res\nBegin1\na := 2;\nEnd\nPrint a";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        assertThrows(RuntimeException.class, parser::parseProgram);
    }

    @Test
    void testParserErrorIncompleteExpression() {
        String code = "Integer a, n, res\nBegin\na := 2 + ;\nEnd\nPrint a";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        assertThrows(RuntimeException.class, parser::parseProgram);
    }

    @Test
    void testParserErrorInvalidExpression() {
        String code = "Integer a, n, res\nBegin\na := *2;\nEnd\nPrint a";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        assertThrows(RuntimeException.class, parser::parseProgram);
    }

    @Test
    void testParserErrorMissingOperator() {
        String code = "Integer a, n, res\nBegin\na := 2(;\nEnd\nPrint a";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        assertThrows(RuntimeException.class, parser::parseProgram);
    }

}
