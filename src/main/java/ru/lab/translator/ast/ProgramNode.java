package ru.lab.translator.ast;

public class ProgramNode extends ASTNode {
    DeclarationsNode declarations;
    StatementsNode statements;
    PrintNode printNode;

    public ProgramNode(DeclarationsNode declarations, StatementsNode statements, PrintNode printNode) {
        this.declarations = declarations;
        this.statements = statements;
        this.printNode = printNode;
    }

    @Override
    public String generateAssembly() {
        StringBuilder sb = new StringBuilder();
        sb.append("global _start\nsection .data\n");
        sb.append("buffer times 21 db 0\n");
        sb.append("newline db 10\n");
        sb.append(declarations.generateAssembly());
        sb.append("\nsection .text\n_start:\n");
        sb.append(statements.generateAssembly());
        sb.append(printNode.generateAssembly());
        sb.append("    mov rax, 60\n    xor rdi, rdi\n    syscall\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Program\n├─ Declarations:\n│  " + declarations.toString().replaceAll("(?m)^", "│  ") +
                "\n├─ Statements:\n│  " + statements.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ Print:\n   " + printNode.toString().replaceAll("(?m)^", "   ");
    }
}
