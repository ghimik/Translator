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

        // left -> rax
        asm.append(left.generateAssembly());
        // сохрани left на стек
        asm.append("    push rax\n");

        // right -> rax
        asm.append(right.generateAssembly());

        // верни left в rbx
        asm.append("    pop rbx\n");

        switch (op) {
            case "+" -> {
                // rax = right, rbx = left -> rax = right + left (коммутативно OK)
                asm.append("    add rax, rbx\n");
            }
            case "-" -> {
                // нужно left - right
                // rax = right, rbx = left
                asm.append("    sub rbx, rax\n");   // rbx = left - right
                asm.append("    mov rax, rbx\n");   // результат в rax
            }
            case "*" -> {
                // rax = right, rbx = left ; imul rax, rbx => rax = rax * rbx = right * left
                asm.append("    imul rax, rbx\n");
            }
            case "/" -> {
                // нужно left / right
                // rax = right, rbx = left
                asm.append("    mov rdx, 0\n");
                asm.append("    xchg rax, rbx\n"); // rax = left, rbx = right
                asm.append("    div rbx\n");        // rax = left / right
            }
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
