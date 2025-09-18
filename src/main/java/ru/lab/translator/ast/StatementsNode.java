package ru.lab.translator.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatementsNode extends ASTNode {
    List<StatementNode> statements = new ArrayList<>();

    public StatementsNode(List<StatementNode> statements) {
        this.statements = statements;
    }

    @Override
    public String generateAssembly() {
        StringBuilder sb = new StringBuilder();
        for (StatementNode s : statements) sb.append(s.generateAssembly());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < statements.size(); i++) {
            StatementNode s = statements.get(i);
            sb.append((i == statements.size() - 1 ? "└─ " : "├─ ") + s.toString().replaceAll("(?m)^", "│  "));
            if (i != statements.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
