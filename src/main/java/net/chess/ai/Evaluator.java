package main.java.net.chess.ai;

import main.java.net.chess.engine.board.Board;

public interface Evaluator {

    int evaluate(Board board, int depth);
}
