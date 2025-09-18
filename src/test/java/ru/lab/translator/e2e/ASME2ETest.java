package ru.lab.translator.e2e;

import org.junit.jupiter.api.Test;
import ru.lab.translator.ast.ProgramNode;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.parser.LL1Parser;

import java.util.List;

public class ASME2ETest {

    private void runTest(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        String asm = program.generateAssembly();
        System.out.println("Assembly generated successfully[" + Thread.currentThread().getStackTrace()[2].getMethodName() + "]:\n" + asm);
    }

    @Test
    public void testArithmeticPrint() {
        String source = """
                Integer x, y, z
                Begin
                x := 5;
                y := x + 3;
                z := y * 2;
                End
                Print z
                """;
        runTest(source);
    }

    @Test
    public void testCaseStatement() {
        String source = """
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
        runTest(source);
    }

    @Test
    public void testIfElse() {
        String source = """
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
        runTest(source);
    }

    @Test
    public void testWhileLoop() {
        String source = """
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
        runTest(source);
    }

    @Test
    public void testNestedIfAndArithmetic() {
        String source = """
                Integer x, y
                Boolean flag
                Begin
                x := 3;
                y := 0;
                flag := false;
                IF x > 2 THEN
                  IF flag THEN
                    y := 100;
                  ELSE
                    y := 50;
                  ENDIF
                ELSE
                  y := 10;
                ENDIF
                End
                Print y
                """;
        runTest(source);
    }

    @Test
    public void testWhileWithCase() {
        String source = """
                Integer i, result
                Begin
                i := 1;
                result := 0;
                WHILE i <= 3 DO
                  CASE i OF
                  1: result := result + 10;
                  2: result := result + 20;
                  3: result := result + 30;
                  ENDCASE
                  i := i + 1;
                ENDWHILE
                End
                Print result
                """;
        runTest(source);
    }

    @Test
    public void testLogicalExpressions() {
        String source = """
                Integer x, y
                Boolean flag
                Begin
                x := 5;
                y := 0;
                flag := x > 3 AND x < 10;
                IF flag THEN
                  y := x * 2;
                ELSE
                  y := x / 2;
                ENDIF
                End
                Print y
                """;
        runTest(source);
    }
}
