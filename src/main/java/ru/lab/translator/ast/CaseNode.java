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
        String endLabel = "ENDCASE_" + uniqueId();
        String tableLabel = "JUMP_TABLE_" + uniqueId();
        String defaultLabel = "CASE_DEFAULT_" + uniqueId();

        int min = branches.stream().mapToInt(b -> b.value).min().getAsInt();
        int max = branches.stream().mapToInt(b -> b.value).max().getAsInt();

        // переменная -> индекс
        sb.append("    mov rax, [").append(varName).append("]\n");
        sb.append("    sub rax, ").append(min).append("\n");
        sb.append("    cmp rax, ").append(max - min).append("\n");
        sb.append("    ja ").append(defaultLabel).append("\n");
        sb.append("    mov rdx, [rel ").append(tableLabel).append(" + rax*8]\n");
        sb.append("    jmp rdx\n\n");

        // тела кейсов
        for (CaseBranchNode branch : branches) {
            String label = "CASE_" + branch.value + "_" + uniqueId();
            branch.asmLabel = label; // сохраняем метку
            sb.append(label).append(":\n");
            sb.append(branch.body.generateAssembly());
            sb.append("    jmp ").append(endLabel).append("\n\n");
        }

        // default case
        sb.append(defaultLabel).append(":\n");
        sb.append("    ; default case (ничего не делаем)\n");
        sb.append("    jmp ").append(endLabel).append("\n\n");

        // jump table dq
        sb.append(tableLabel).append(":\n");
        for (int i = min; i <= max; i++) {
            int finalI = i;
            CaseBranchNode b = branches.stream().filter(br -> br.value == finalI).findFirst().orElse(null);
            if (b != null) {
                sb.append("    dq ").append(b.asmLabel).append("\n");
            } else {
                sb.append("    dq ").append(defaultLabel).append("\n");
            }
        }

        sb.append(endLabel).append(":\n");
        return sb.toString();
    }

    private static int caseCounter = 0;
    private static synchronized int uniqueId() { return caseCounter++; }



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
