package ru.lab.translator.ast;

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
        if (op.equals("AND") || op.equals("OR")) {
            String asmOp = op.equals("AND") ? "and rax, rbx" : "or rax, rbx";
            return left.generateAssembly() +
                    "    mov rbx, rax\n" +
                    right.generateAssembly() +
                    "    " + asmOp + "\n";
        } else {
            String setOp = switch (op) {
                case "<" -> "setl al";
                case ">" -> "setg al";
                case "<=" -> "setle al";
                case ">=" -> "setge al";
                case "==" -> "sete al";
                case "!=" -> "setne al";
                default -> throw new RuntimeException("Unknown logical operator: " + op);
            };
            return left.generateAssembly() +
                    "    mov rbx, rax\n" +
                    right.generateAssembly() +
                    "    cmp rbx, rax\n" +
                    "    " + setOp + "\n" +
                    "    movzx rax, al\n";  // extend al -> rax
        }
    }

    @Override
    public String toString() {
        return op + "\n├─ " + left.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ " + right.toString().replaceAll("(?m)^", "   ");
    }
}
