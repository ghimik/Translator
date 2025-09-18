package ru.lab.translator.e2e;

import org.junit.jupiter.api.Test;
import ru.lab.translator.ast.ProgramNode;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.parser.LL1Parser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ASME2ETest {

    @Test
    public void test() {
        String source = """
                Integer x, y, z
                Begin
                x := 5;
                y := x + 3;
                z := y * 2;
                End
                Print z
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();

        String asm = program.generateAssembly();

        System.out.println("Assembly generated successfully: " + asm);
    }
}
