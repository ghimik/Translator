package ru.lab.translator.ast;

import java.util.ArrayList;
import java.util.List;

public class CaseNode extends StatementNode {
    public String varName;
    public List<CaseBranchNode> branches = new ArrayList<>();

    public CaseNode(String varName, List<CaseBranchNode> branches) {
        this.varName = varName;
        this.branches = branches;
    }

    @Override
    public String generateAssembly() {
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("case ").append(varName).append(":\n");
        for (int i = 0; i < branches.size(); i++) {
            CaseBranchNode b = branches.get(i);
            String prefix = (i == branches.size() - 1) ? "└─ " : "├─ ";
            sb.append(prefix).append(b.toString().replaceAll("(?m)^", "│  ")).append("\n");
        }
        return sb.toString().stripTrailing();
    }
}
