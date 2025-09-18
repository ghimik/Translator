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
        String leftAsm = left.generateAssembly();
        String rightAsm = right.generateAssembly();
        String opAsm = switch (op) {
            case "+" -> "add eax, ebx";
            case "-" -> "sub eax, ebx";
            case "*" -> "imul eax, ebx";
            case "/" -> "xor edx, edx\n    div ebx";
            default -> throw new RuntimeException("Unknown operator: " + op);
        };
        return leftAsm +
                "    mov ebx, eax\n" +
                rightAsm +
                "    " + opAsm + "\n";
    }

    @Override
    public String toString() {
        return op + "\n├─ " + left.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ " + right.toString().replaceAll("(?m)^", "   ");
    }
}
