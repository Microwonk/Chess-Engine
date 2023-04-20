package net.chess.gui;

import net.chess.parsing.FenParser;
import net.chess.parsing.PGNParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HotKeys implements KeyListener {

    private static final StringBuilder stringBuilder = new StringBuilder();
    private static boolean controlPressed = false;

    @Override
    public void keyTyped (KeyEvent e) {}

    @Override
    public void keyPressed (KeyEvent e) {
        // Chess.get().getLogger().printLog("Key Pressed: " + e.getKeyCode()); // for finding out the hot keys KeyCode
        switch (e.getKeyCode()) {
            case 37 -> Chess.get().prevMove(); // left arrow
            case 39 -> Chess.get().nextMove(); // right arrow
            case 38 -> Chess.get().endBoard(); // up arrow
            case 40 -> Chess.get().beginBoard(); // down arrow
            case 82 -> Chess.get().reset(); // r key
            case 17 -> controlPressed = true; // ctrl
            case 521 -> { // plus
                if (controlPressed) {
                    Font f = Chess.get().getLogger().getTextArea().getFont();
                    Chess.get().getLogger().getTextArea().setFont(new Font(f.getFontName(), f.getStyle(), f.getSize() + 1));
                }
            }
            case 45 -> { // minus
                if (controlPressed) {
                    Font f = Chess.get().getLogger().getTextArea().getFont();
                    Chess.get().getLogger().getTextArea().setFont(new Font(f.getFontName(), f.getStyle(), f.getSize() - 1));
                }
            }
            case 83 -> { // s key
                if (controlPressed) {
                    Chess.get().saveGame(FenParser.parseFen(Chess.get().getGameBoard()));
                }
            }
            case 76 -> { // l key
                if (controlPressed) {
                    Chess.get().loadGame();
                }
            }
            case 10 -> {
                Chess.get().getLogger().printLog(stringBuilder);
                try {
                    // Chess.get().getLogger().printLog(Chess.get().getGameBoard().currentPlayer().makeMove(PGNParser.parseMove(Chess.get().getGameBoard(), stringBuilder.toString())).getTransitionBoard());
                    Chess.get().setChessBoard(Chess.get().getGameBoard().currentPlayer().makeMove(PGNParser.parseMove(Chess.get().getGameBoard(), stringBuilder.toString())).getTransitionBoard());
                } catch (Exception ex) {
                    Chess.get().getLogger().printLog(ex);
                }
                stringBuilder.delete(0, stringBuilder.length());
                Chess.get().update();
            }
            default -> {
                if (String.valueOf(e.getKeyChar()).matches("[A-Za-z0-9]+")) {
                    stringBuilder.append(e.getKeyChar());
                }
            }
        }
    }

    @Override
    public void keyReleased (KeyEvent e) {
        switch (e.getKeyCode()) {
            case 17 -> controlPressed = false; // ctrl
        }
    }
}
