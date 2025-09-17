package ru.lab.translator.e2e;

import org.junit.jupiter.api.Test;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.parser.LL1Parser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerAndParserE2ETest {

    private void parse(String program) {
        Lexer lexer = new Lexer(program);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        parser.parseProgram();
    }

    // -------------------- POSITIVE TESTS --------------------

    @Test
    void testArithmeticAndPrint() {
        String code = """
            Integer x, y, z
            Begin
              x := 5;
              y := x + 3;
              z := y * 2;
            End
            Print z
            """;
        assertDoesNotThrow(() -> parse(code));
    }

    @Test
    void testCaseStatement() {
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
        assertDoesNotThrow(() -> parse(code));
    }

    @Test
    void testIfStatement() {
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
        assertDoesNotThrow(() -> parse(code));
    }

    @Test
    void testWhileLoop() {
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
        assertDoesNotThrow(() -> parse(code));
    }

    // -------------------- NEGATIVE TESTS --------------------

    @Test
    void testMissingSemicolon() {
        String code = """
            Integer x
            Begin
              x := 5
            End
            Print x
            """;
        assertThrows(RuntimeException.class, () -> parse(code));
    }

    @Test
    void testUnknownIdentifier() {
        String code = """
            Integer x
            Begin
              y := 10;
            End
            Print x
            """;
        // тут семантической проверки пока нет, но синтаксис неверен (y не объявлен),
        // парсер бросит исключение на неожиданном идентификаторе
        assertThrows(RuntimeException.class, () -> parse(code));
    }

    @Test
    void testIfWithoutElse() {
        String code = """
            Integer flag
            Begin
              IF 1 < 2 THEN
                flag := 1;
              ENDIF
            End
            Print flag
            """;
        // по нашей грамматике ELSE обязателен
        assertThrows(RuntimeException.class, () -> parse(code));
    }

    @Test
    void testWhileWithoutEnd() {
        String code = """
            Integer i
            Begin
              WHILE i < 5 DO
                i := i + 1;
            End
            Print i
            """;
        assertThrows(RuntimeException.class, () -> parse(code));
    }

    @Test
    void testCaseWithoutEndcase() {
        String code = """
            Integer a, b
            Begin
              CASE a OF
                1: b := 10;
            End
            Print b
            """;
        assertThrows(RuntimeException.class, () -> parse(code));
    }
}
