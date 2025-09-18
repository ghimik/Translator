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
        sb.append("global main\nextern printf\n\nsection .data\n");
        sb.append("fmt: db \"Result: %d\",10,0\n"); // формат для printf
        sb.append(declarations.generateAssembly());
        sb.append("\nsection .text\nmain:\n");
        sb.append(statements.generateAssembly());
        sb.append(printNode.generateAssembly());
        sb.append("    ret\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Program\n├─ Declarations:\n│  " + declarations.toString().replaceAll("(?m)^", "│  ") +
                "\n├─ Statements:\n│  " + statements.toString().replaceAll("(?m)^", "│  ") +
                "\n└─ Print:\n   " + printNode.toString().replaceAll("(?m)^", "   ");
    }
}
