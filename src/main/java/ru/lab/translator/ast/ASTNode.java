package ru.lab.translator.ast;

public abstract class ASTNode {
    public abstract String generateAssembly();
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
