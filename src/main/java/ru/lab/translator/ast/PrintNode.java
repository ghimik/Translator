package ru.lab.translator.ast;

public class PrintNode extends StatementNode {
    String var;

    public PrintNode(String var) {
        this.var = var;
    }

    @Override
    public String generateAssembly() {
        return "";
    }

    @Override
    public String toString() {
        return "print " + var;
    }
}
