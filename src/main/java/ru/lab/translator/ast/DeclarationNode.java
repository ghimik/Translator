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
        return "";
    }

    @Override
    public String toString() {
        return type + " " + name;
    }

}
