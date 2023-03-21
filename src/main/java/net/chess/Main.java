package net.chess;

import com.formdev.flatlaf.FlatDarkLaf;
import net.chess.gui.GUI_Contents;

import javax.swing.*;

/**
 * runner Code Class, sets GUI look and feel to Laf preset (lib)
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Main {

    public static void main (String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        GUI_Contents.get().show();
    }
}