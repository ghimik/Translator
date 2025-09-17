package ru.lab.translator.ast;

public class BinaryExpressionNode extends ExpressionNode {
    ExpressionNode left, right;
    String op;

    public BinaryExpressionNode(String op, ExpressionNode left, ExpressionNode right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public String generateAssembly() {
        return "";
    }

    @Override
    public String toString() {
        return op + "\n├─ " + left.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ " + right.toString().replaceAll("(?m)^", "   ");
    }
}
