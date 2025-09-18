package ru.lab.translator;

import ru.lab.translator.ide.CodeEditorPanel;
import ru.lab.translator.ide.ControlPanel;

import javax.swing.*;

public class MainFrame extends JFrame {
    private final CodeEditorPanel editorPanel;
    private final ControlPanel controlPanel;

    public MainFrame() {
        super("Mini IDE");

        editorPanel = new CodeEditorPanel();

        controlPanel = new ControlPanel(editorPanel);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(editorPanel);
        add(controlPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

}
