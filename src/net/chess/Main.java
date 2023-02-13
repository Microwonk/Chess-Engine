package net.chess;

import net.chess.engine.board.Board;
import net.chess.gui.GUI_Contents;

public class Main {

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();

        System.out.println(board);

        GUI_Contents guiuiui = new GUI_Contents();
    }
}
