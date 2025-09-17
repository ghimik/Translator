package ru.lab.translator.ast;

public class CaseBranchNode extends ASTNode {
    public int value;
    public StatementsNode body;

    public CaseBranchNode(int value, StatementsNode body) {
        this.value = value;
        this.body = body;
    }

    @Override
    public String generateAssembly() {
        return "";
    }

    @Override
    public String toString() {
        return "case " + value + ":\n└─ " + body.toString().replaceAll("(?m)^", "   ");
    }
}
