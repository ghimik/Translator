package ru.lab.translator.ast;

public class PrintNode extends StatementNode {
    String var;

    public PrintNode(String var) {
        this.var = var;
    }

    @Override
    public String generateAssembly() {
        String loopLabel = "PRINT_CONVERT_" + hashCode();
        String doneLabel = "PRINT_DONE_" + hashCode();
        String negLabel = "PRINT_NEG_" + hashCode();

        return
                "    mov rax, [" + var + "]\n" +
                        "    mov rbx, 10\n" +
                        "    lea rsi, [rel buffer+20]\n" +
                        "    mov rcx, 0\n" +
                        "    mov r8, 0          ; флаг отрицательности\n" +

                        "    cmp rax, 0\n" +
                        "    jge " + loopLabel + "\n" +
                        "    neg rax\n" +
                        "    mov r8, 1\n" +

                        loopLabel + ":\n" +
                        "    xor rdx, rdx\n" +
                        "    div rbx\n" +
                        "    add dl, '0'\n" +
                        "    dec rsi\n" +
                        "    mov [rsi], dl\n" +
                        "    inc rcx\n" +
                        "    test rax, rax\n" +
                        "    jnz " + loopLabel + "\n" +

                        "    cmp r8, 0\n" +
                        "    je " + doneLabel + "\n" +
                        "    dec rsi\n" +
                        "    mov byte [rsi], '-'\n" +
                        "    inc rcx\n" +

                        doneLabel + ":\n" +
                        "    mov rax, 1\n" +
                        "    mov rdi, 1\n" +
                        "    mov rdx, rcx\n" +
                        "    syscall\n" +

                        "    mov rax, 1\n" +
                        "    mov rdi, 1\n" +
                        "    mov rsi, newline\n" +
                        "    mov rdx, 1\n" +
                        "    syscall\n";
    }

    @Override
    public String toString() {
        return "print " + var;
    }
}
