package ru.lab.translator.ast;

public class IfNode extends StatementNode {
    public ExpressionNode condition;
    public StatementsNode thenBranch;
    public StatementsNode elseBranch;

    public IfNode(ExpressionNode cond, StatementsNode thenBranch, StatementsNode elseBranch) {
        this.condition = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public String generateAssembly() {
        return "";
    }
    @Override
    public String toString() {
        return "if\n├─ condition:\n│  " + condition.toString().replaceAll("(?m)^", "│  ") +
                "\n├─ then:\n│  " + thenBranch.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ else:\n   " + elseBranch.toString().replaceAll("(?m)^", "   ");
    }
}
