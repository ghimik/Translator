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
        String asmOp = switch (op) {
            case "AND" -> "and eax, ebx";
            case "OR" -> "or eax, ebx";
            default -> throw new RuntimeException("Unknown logical operator: " + op);
        };
        return left.generateAssembly() +
                "    mov ebx, eax\n" +
                right.generateAssembly() +
                "    " + asmOp + "\n";
    }

    @Override
    public String toString() {
        return op + "\n├─ " + left.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ " + right.toString().replaceAll("(?m)^", "   ");
    }
}