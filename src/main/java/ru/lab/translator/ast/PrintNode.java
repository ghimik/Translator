package ru.lab.translator.ast;

public class PrintNode extends StatementNode {
    String var;

    public PrintNode(String var) {
        this.var = var;
    }

    @Override
    public String generateAssembly() {
        return "    mov rsi, [" + var + "]\n" +
                "    lea rdi, [rel fmt]\n" +
                "    xor eax, eax\n" +
                "    call printf\n";
    }

    @Override
    public String toString() {
        return "print " + var;
    }
}
