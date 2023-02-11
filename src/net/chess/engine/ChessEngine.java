package net.chess.engine;

import net.chess.engine.board.Board;

public class ChessEngine {

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();

        System.out.println(board);
    }
}
