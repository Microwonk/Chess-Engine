package net.chess.gui;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class Logger extends JPanel {

    private final JScrollPane scrollPane;
    private final JTextArea textArea;

    public Logger() {
        super();
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        this.textArea = new JTextArea();
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.textArea.setText("<------ Logger ------>\n");
        this.setPreferredSize(new Dimension(100, 640));
        this.textArea.setEditable(false);
        this.textArea.setEnabled(false);
        this.textArea.setName("Logging");
        this.textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        this.textArea.setBackground(new Color(70, 70, 70));
        this.textArea.setDisabledTextColor(new Color(124, 201, 87));
        this.scrollPane = new JScrollPane(textArea);
        this.scrollPane.setBorder(null);
        this.add(scrollPane, gbc);
        this.setVisible(true);
    }

    public void printLog(final String... text) {
        for (String s: text) {
            this.textArea.append(">\\ " + s + '\n');
        }
    }

    public void clear() {
        this.textArea.setText(null);
    }
}
