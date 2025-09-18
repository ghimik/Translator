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
        if (branches.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        String endLabel = "ENDCASE_" + hashCode();
        String tableLabel = "JUMP_TABLE_" + hashCode();
        String defaultLabel = "CASE_DEFAULT_" + hashCode();

        // находим min и max значения кейсов
        int min = branches.stream().mapToInt(b -> b.value).min().getAsInt();
        int max = branches.stream().mapToInt(b -> b.value).max().getAsInt();

        // загружаем переменную и вычисляем индекс
        sb.append("    mov eax, [").append(varName).append("]\n");
        sb.append("    sub eax, ").append(min).append("\n");
        sb.append("    cmp eax, ").append(max - min).append("\n");
        sb.append("    ja ").append(defaultLabel).append("\n");
        sb.append("    mov edx, [rel ").append(tableLabel).append(" + eax*8]\n");
        sb.append("    jmp edx\n\n");

        // генерируем тела кейсов
        for (CaseBranchNode branch : branches) {
            String branchLabel = "CASE_" + branch.hashCode();
            sb.append(branchLabel).append(":\n");
            sb.append(branch.body.generateAssembly());
            sb.append("    jmp ").append(endLabel).append("\n\n");
        }

        // default (если нужен)
        sb.append(defaultLabel).append(":\n");
        sb.append("    ; default case (ничего не делаем)\n");
        sb.append("    jmp ").append(endLabel).append("\n\n");

        // jump table
        sb.append(tableLabel).append(":\n");
        for (int i = min; i <= max; i++) {
            int finalI = i;
            CaseBranchNode b = branches.stream().filter(br -> br.value == finalI).findFirst().orElse(null);
            if (b != null) {
                sb.append("    dd CASE_").append(b.hashCode()).append("\n");
            } else {
                sb.append("    dd ").append(defaultLabel).append("\n");
            }
        }

        sb.append(endLabel).append(":\n");
        return sb.toString();
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
