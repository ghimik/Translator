package ru.lab.translator.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class LogicalExpressionNode extends ExpressionNode {
    public ExpressionNode left, right;
    public String op;

    public LogicalExpressionNode(ExpressionNode left, String op, ExpressionNode right) {
        this.left = left;
        this.op = op;
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