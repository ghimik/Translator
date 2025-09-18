package ru.lab.translator.ide;

import ru.lab.translator.ast.ProgramNode;
import ru.lab.translator.lexer.Lexer;
import ru.lab.translator.lexer.Token;
import ru.lab.translator.parser.LL1Parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ControlPanel extends JPanel {
    private final CodeEditorPanel editorPanel;

    public ControlPanel(CodeEditorPanel editorPanel) {
        this.editorPanel = editorPanel;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton newBtn = new JButton("New");
        JButton openBtn = new JButton("Open");
        JButton saveBtn = new JButton("Save");
        JButton runBtn = new JButton("Run");
        JButton showAsmBtn = new JButton("Show Assembly");

        add(newBtn);
        add(openBtn);
        add(saveBtn);
        add(runBtn);
        add(showAsmBtn);

        newBtn.addActionListener(e -> editorPanel.setCode(""));

        openBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    editorPanel.setCode(sb.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(editorPanel.getCode());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        runBtn.addActionListener(e -> {
            String source = editorPanel.getCode();

            // Генерируем ассемблер
            String asmCode;
            try {
                asmCode = generateAssembly(source);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating assembly:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextArea outputArea = new JTextArea(20, 60);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);

            JFrame runFrame = new JFrame("Run Output");
            runFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            runFrame.getContentPane().add(scrollPane);
            runFrame.pack();
            runFrame.setLocationRelativeTo(null);
            runFrame.setVisible(true);

            new Thread(() -> {
                try {
                    String jsonPayload = "{\"asm\": " + jsonEscape(asmCode) + "}";

                    URL url = new URL("http://localhost:8877/run_asm");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonPayload.getBytes());
                    }

                    String json = getString(conn);
                    String stdout = "";
                    int exitCode = -1;
                    if (json.contains("\"stdout\"")) {
                        int start = json.indexOf("\"stdout\"") + 10;
                        int end = json.indexOf("\"", start);
                        stdout = json.substring(start, end).replace("\\n", "\n").replace("\\r", "\r");
                    }
                    if (json.contains("\"exitCode\"")) {
                        int start = json.indexOf("\"exitCode\"") + 11;
                        int end = json.indexOf("}", start);
                        exitCode = Integer.parseInt(json.substring(start, end).trim());
                    }

                    final String finalStdout = stdout;
                    final int finalExitCode = exitCode;
                    SwingUtilities.invokeLater(() -> outputArea.setText(
                            "Exit code: " + finalExitCode + "\n--------------------\n" +
                                    finalStdout
                    ));

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> outputArea.setText("Error sending request:\n" + ex.getMessage()));
                }
            }).start();

        });

        showAsmBtn.addActionListener(e -> {
            String source = editorPanel.getCode();
            JTextArea outputArea = new JTextArea(30, 80);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);

            JFrame asmFrame = new JFrame("Assembly Output");
            asmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            asmFrame.getContentPane().add(scrollPane);
            asmFrame.pack();
            asmFrame.setLocationRelativeTo(null);
            asmFrame.setVisible(true);

            try {
                outputArea.setText(generateAssembly(source));
            } catch (Exception ex) {
                outputArea.setText("Error:\n" + ex.getMessage());
            }
        });
    }

    private static String generateAssembly(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        ProgramNode program = parser.parseProgram();
        return program.generateAssembly();
    }

    private static String getString(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream is = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // Простейший парсинг JSON ответа
        return response.toString();
    }

    private static String jsonEscape(String s) {
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

}
