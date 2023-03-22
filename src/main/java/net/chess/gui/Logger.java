package net.chess.gui;

import javax.swing.*;
import java.awt.*;

public class Logger extends JTextArea {


    public Logger() {
        super();
        this.setText("Console --v1\n");
        this.setPreferredSize(new Dimension(100, 640));
        this.setForeground(Color.GREEN);
        this.setEnabled(false);
        this.setVisible(true);
    }

    public void printLog(final String... text) {
        for (String s: text) {
            this.append(s + '\n');
        }
    }
}
