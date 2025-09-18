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

        // 1. Сначала вычисляем левое выражение, результат в rax
        asm.append(left.generateAssembly());

        // 2. Сохраняем левый результат во временную переменную (stack)
        asm.append("    push rax\n");

        // 3. Вычисляем правое выражение, результат в rax
        asm.append(right.generateAssembly());

        // 4. Вытаскиваем левый результат в rbx
        asm.append("    pop rbx\n");

        // 5. Применяем операцию: rbx = left, rax = right
        switch (op) {
            case "+" -> asm.append("    add rax, rbx\n");   // rax = right + left
            case "-" -> asm.append("    sub rbx, rax\n")    // rbx = left - right
                    .append("    mov rax, rbx\n");
            case "*" -> asm.append("    imul rax, rbx\n");  // rax = right * left
            case "/" -> asm.append("    mov rdx, 0\n")      // обнуляем старший регистр для div
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
