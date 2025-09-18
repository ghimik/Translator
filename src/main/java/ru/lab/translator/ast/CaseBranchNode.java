package ru.lab.translator.ast;

public class CaseBranchNode extends ASTNode {
    public int value;
    public StatementsNode body;
    String asmLabel;

    public CaseBranchNode(int value, StatementsNode body) {
        this.value = value;
        this.body = body;
        this.asmLabel = String.valueOf(hashCode());
    }

    @Override
    public String generateAssembly() {
        return body.generateAssembly();
    }

    @Override
    public String toString() {
        return "case " + value + ":\n└─ " + body.toString().replaceAll("(?m)^", "   ");
    }
}
