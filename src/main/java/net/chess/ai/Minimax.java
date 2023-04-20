package net.chess.ai;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.board.MoveTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * AI minimax algorithm type
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Minimax implements AI {

    private int searchDepth;

    public Minimax (final int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public Move execute (final Board board) {
        List <Move> legalMoves = new ArrayList <>(board.currentPlayer().getLegalMoves());

        Move bestMove = Move.MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;

        for (final Move move : legalMoves) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = minimax(moveTransition.getTransitionBoard(), this.searchDepth - 1, true);

                if (board.currentPlayer().getTeam().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getTeam().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    public int minimax (Board board, int depth, boolean maximizingPlayer) {
        if (depth == 0 || board.isGameOver()) {
            return Evaluator.evaluate(board);
        }
        int bestValue;
        if (maximizingPlayer) {
            bestValue = Integer.MIN_VALUE;
            for (Move move : board.currentPlayer().getLegalMoves()) {
                Board newBoard = board.currentPlayer().makeMove(move).getTransitionBoard();
                int currentValue = minimax(newBoard, depth - 1, false);
                bestValue = Math.max(bestValue, currentValue);
            }
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Move move : board.currentPlayer().getLegalMoves()) {
                Board newBoard = board.currentPlayer().makeMove(move).getTransitionBoard();
                int currentValue = minimax(newBoard, depth - 1, true);
                bestValue = Math.min(bestValue, currentValue);
            }
        }
        return bestValue;
    }

    @Override
    public String toString () {
        return "MiniMax";
    }
}


