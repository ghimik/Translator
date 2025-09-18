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
        String start = "WHILE_START_" + hashCode();
        String end = "WHILE_END_" + hashCode();
        return start + ":\n" +
                condition.generateAssembly() +
                "    cmp rax, 0\n" +
                "    je " + end + "\n" +
                body.generateAssembly() +
                "    jmp " + start + "\n" +
                end + ":\n";
    }

    @Override
    public String toString() {
        return "while\n└─ condition:\n   " + condition.toString().replaceAll("(?m)^", "   ") +
                "\n└─ body:\n   " + body.toString().replaceAll("(?m)^", "   ");
    }
}
