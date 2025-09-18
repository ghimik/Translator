package ru.lab.translator.ide;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;

public class PeriodicSyntaxHighlighter {
    private final JTextPane textPane;

    private static final Set<String> TYPES = Set.of("Integer", "Boolean");
    private static final Set<String> CONTROL = Set.of("Begin", "End", "IF", "THEN", "ELSE", "ENDIF",
            "WHILE", "DO", "ENDWHILE", "CASE", "OF", "ENDCASE", "Print");
    private static final Set<String> LOGIC = Set.of("true", "false", "NOT", "AND", "OR");

    private final StyleContext sc = StyleContext.getDefaultStyleContext();
    private final AttributeSet typeStyle = sc.addAttribute(sc.getEmptySet(), StyleConstants.Foreground, new Color(0xCC7832));
    private final AttributeSet controlStyle = sc.addAttribute(sc.getEmptySet(), StyleConstants.Foreground, new Color(0x6897BB));
    private final AttributeSet logicStyle = sc.addAttribute(sc.getEmptySet(), StyleConstants.Foreground, new Color(0x9876AA));
    private final AttributeSet normalStyle = sc.addAttribute(sc.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

    public PeriodicSyntaxHighlighter(JTextPane textPane, int intervalMs) {
        this.textPane = textPane;
        Timer timer = new Timer(intervalMs, e -> highlightAll());
        timer.start();
    }

    private void highlightAll() {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                String text = doc.getText(0, doc.getLength());
                doc.setCharacterAttributes(0, text.length(), normalStyle, true);

                int offset = 0;
                int len = text.length();
                int i = 0;

                while (i < len) {
                    if (!Character.isLetter(text.charAt(i))) { i++; continue; }
                    int j = i;
                    while (j < len && Character.isLetter(text.charAt(j))) j++;
                    String word = text.substring(i, j);

                    AttributeSet style = null;
                    if (TYPES.contains(word)) style = typeStyle;
                    else if (CONTROL.contains(word)) style = controlStyle;
                    else if (LOGIC.contains(word)) style = logicStyle;

                    if (style != null) {
                        doc.setCharacterAttributes(i, j - i, style, true);
                    }
                    i = j;
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
}
