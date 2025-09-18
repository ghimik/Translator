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
        StringBuilder asm = new StringBuilder();

        asm.append(left.generateAssembly());

        asm.append("    push rax\n");

        asm.append(right.generateAssembly());

        asm.append("    pop rbx\n");

        switch (op) {
            case "+" -> asm.append("    add rax, rbx\n");   // rax = right + left
            case "-" -> asm.append("    sub rbx, rax\n")    // rbx = left - right
                    .append("    mov rax, rbx\n");
            case "*" -> asm.append("    imul rax, rbx\n");  // rax = right * left
            case "/" -> asm.append("    mov rdx, 0\n")
                    .append("    xchg rax, rbx\n")     // rax = left, rbx = right
                    .append("    div rbx\n");          // rax = left / right
            default -> throw new RuntimeException("Unknown op: " + op);
        }

        return asm.toString();
    }

    @Override
    public String toString() {
        return op + "\n├─ " + left.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ " + right.toString().replaceAll("(?m)^", "   ");
    }
}
