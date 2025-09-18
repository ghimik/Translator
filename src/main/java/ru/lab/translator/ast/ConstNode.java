package ru.lab.translator.ast;

public class ConstNode extends ExpressionNode {
    int value;
    public ConstNode(int value) {
        this.value = value;
    }

    @Override
    public String generateAssembly() {
        return "    mov eax, " + value + "\n"; // результат в eax
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
