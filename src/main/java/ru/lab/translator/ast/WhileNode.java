package ru.lab.translator.ast;

public class WhileNode extends StatementNode {
    public ExpressionNode condition;
    public StatementsNode body;

    public WhileNode(ExpressionNode cond, StatementsNode body) {
        this.condition = cond;
        this.body = body;
    }

    @Override
    public String generateAssembly() {
        return "";
    }
    @Override
    public String toString() {
        return "while\n└─ condition:\n   " + condition.toString().replaceAll("(?m)^", "   ") +
                "\n└─ body:\n   " + body.toString().replaceAll("(?m)^", "   ");
    }
}
