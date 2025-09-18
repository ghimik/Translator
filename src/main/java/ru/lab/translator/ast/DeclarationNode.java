package ru.lab.translator.ast;

public class DeclarationNode extends ASTNode {
    String name;
    String type;

    public DeclarationNode(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String generateAssembly() {
        return name + " dd 0\n"; // 64-bit переменная
    }

    @Override
    public String toString() {
        return type + " " + name;
    }

}
