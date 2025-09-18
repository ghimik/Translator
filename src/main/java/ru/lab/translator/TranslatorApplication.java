package ru.lab.translator;

import ru.lab.translator.ast.ProgramNode;
import ru.lab.translator.parser.LL1Parser;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TranslatorApplication {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar translator.jar <input.src> <output.asm>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            String source = Files.readString(Paths.get(inputFile));

            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();

            LL1Parser parser = new LL1Parser(tokens);
            ProgramNode program = parser.parseProgram();

            String asm = program.generateAssembly();

            Files.writeString(Paths.get(outputFile), asm);

            System.out.println("Assembly generated successfully: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Error during translation: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }
}
