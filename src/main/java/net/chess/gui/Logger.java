package net.chess.gui;

import javax.swing.*;
import java.awt.*;

public class Logger extends JPanel {

    private final JScrollPane scrollPane;
    private final JTextArea textArea;

    public Logger() {
        super();
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();
        this.textArea.setText("Console --v1\n");
        this.setPreferredSize(new Dimension(100, 640));
        this.textArea.setForeground(Color.GREEN);
        this.scrollPane.add(textArea);
        this.add(scrollPane, gbc);
        this.setEnabled(false);
        this.setVisible(true);
    }

    public void printLog(final String... text) {
        for (String s: text) {
            this.textArea.append(s + '\n');
        }
    }

    public void clear() {
        this.textArea.setText("");
    }
}
