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
        String startLabel = "WHILE_START_" + hashCode();
        String endLabel = "WHILE_END_" + hashCode();
        return startLabel + ":\n" +
                condition.generateAssembly() +
                "    cmp eax, 0\n" +
                "    je " + endLabel + "\n" +
                body.generateAssembly() +
                "    jmp " + startLabel + "\n" +
                endLabel + ":\n";
    }

    @Override
    public String toString() {
        return "while\n└─ condition:\n   " + condition.toString().replaceAll("(?m)^", "   ") +
                "\n└─ body:\n   " + body.toString().replaceAll("(?m)^", "   ");
    }
}
