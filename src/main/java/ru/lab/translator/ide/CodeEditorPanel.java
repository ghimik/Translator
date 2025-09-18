package ru.lab.translator.ide;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;
import java.util.HashSet;

public class CodeEditorPanel extends JPanel {
    private final JTextPane textPane;
    private final JScrollPane scrollPane;
    private static final Set<String> KEYWORDS = new HashSet<>();

    static {
        String[] words = {"Integer", "Boolean", "Begin", "End", "IF", "THEN", "ELSE", "ENDIF",
                "WHILE", "DO", "ENDWHILE", "CASE", "OF", "ENDCASE", "Print", "true", "false", "NOT", "AND", "OR"};
        for (String w : words) KEYWORDS.add(w);
    }

    public CodeEditorPanel() {
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 14)); // IntelliJ шрифт
        textPane.setCaretColor(Color.BLACK);
        textPane.setBackground(Color.WHITE);

        new PeriodicSyntaxHighlighter(textPane, 500);

        scrollPane = new JScrollPane(textPane);

        scrollPane.setRowHeaderView(new LineNumberView(textPane));

        add(scrollPane, BorderLayout.CENTER);
    }

    public String getCode() {
        return textPane.getText();
    }

    public void setCode(String code) {
        textPane.setText(code);
    }

    static class LineNumberView extends JPanel {
        private final JTextPane textPane;
        private final Font font = new Font("JetBrains Mono", Font.PLAIN, 14);

        public LineNumberView(JTextPane textPane) {
            this.textPane = textPane;
            setPreferredSize(new Dimension(40, Integer.MAX_VALUE));
            textPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { repaint(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { repaint(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.GRAY);
            g.setFont(font);
            int lineHeight = textPane.getFontMetrics(textPane.getFont()).getHeight();
            int startOffset = textPane.viewToModel2D(new Point(0, 0));
            int endOffset = textPane.viewToModel2D(new Point(0, getHeight()));
            int startLine = textPane.getDocument().getDefaultRootElement().getElementIndex(startOffset);
            int endLine = textPane.getDocument().getDefaultRootElement().getElementIndex(endOffset) + 1;

            int y = lineHeight;
            for (int i = startLine; i < endLine; i++) {
                g.drawString(String.valueOf(i + 1), 5, y - 4);
                y += lineHeight;
            }
        }
    }
}
