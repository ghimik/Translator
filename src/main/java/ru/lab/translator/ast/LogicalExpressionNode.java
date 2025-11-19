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
        // логика: сохраняем left на стек, генерируем right, потом восстанавливаем left и делаем cmp / логическую операцию
        if ("AND".equals(op) || "OR".equals(op)) {
            StringBuilder sb = new StringBuilder();
            sb.append(left.generateAssembly());
            sb.append("    push rax\n");              // сохранить left
            sb.append(right.generateAssembly());      // right -> rax
            sb.append("    pop rbx\n");               // rbx = left
            if ("AND".equals(op)) {
                // хотим булевое AND: rax и rbx могут быть 0/1 или числа -> результат неплохо получить как (rax & rbx) != 0
                sb.append("    and rax, rbx\n");     // rax = right & left
            } else {
                sb.append("    or rax, rbx\n");      // rax = right | left
            }
            return sb.toString();
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

            StringBuilder sb = new StringBuilder();
            sb.append(left.generateAssembly());     // left -> rax
            sb.append("    push rax\n");            // сохранить left
            sb.append(right.generateAssembly());    // right -> rax
            sb.append("    pop rbx\n");             // rbx = left
            // хотим cmp left, right
            sb.append("    cmp rbx, rax\n");        // cmp left, right
            sb.append("    ").append(setOp).append("\n");
            sb.append("    movzx rax, al\n");      // result 0/1 in rax
            return sb.toString();
        }
    }


    @Override
    public String toString() {
        return op + "\n├─ " + left.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ " + right.toString().replaceAll("(?m)^", "   ");
    }
}
