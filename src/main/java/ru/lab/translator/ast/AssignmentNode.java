package ru.lab.translator.ast;

public class AssignmentNode extends StatementNode  {
    String var;
    ExpressionNode expr;

    public AssignmentNode(String var, ExpressionNode expr) {
        this.var = var;
        this.expr = expr;
    }

    @Override
    public String generateAssembly() {
        return expr.generateAssembly() + "    mov [" + var + "], eax\n";
    }

    @Override
    public String toString() {
        return var + " =\n├─ " + expr.toString().replaceAll("(?m)^", "│  ");
    }
}
