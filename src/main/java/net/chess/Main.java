package main.java.net.chess;

import com.formdev.flatlaf.FlatDarkLaf;
import main.java.net.chess.gui.GUI_Contents;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        GUI_Contents.get().show();
    }
}