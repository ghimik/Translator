package ru.lab.translator.ast;

public class VarNode extends ExpressionNode {
    String name;
    public VarNode(String name) {
        this.name = name;
    }
    @Override
    public String generateAssembly() {
        return "    mov eax, [" + name + "]\n";
    }
    @Override
    public String toString() {
        return name;
    }
}
