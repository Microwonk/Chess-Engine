package main.java.net.chess.ai;

import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;

public interface AI {
    Move execute(final Board board, final int depth);
}
