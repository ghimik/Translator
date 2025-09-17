package ru.lab.translator.ast;

import java.util.List;

// Объявления
public class DeclarationsNode extends ASTNode {
    List<DeclarationNode> declarations;

    public DeclarationsNode(List<DeclarationNode> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String generateAssembly() {
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < declarations.size(); i++) {
            sb.append(declarations.get(i).toString());
            if (i != declarations.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
