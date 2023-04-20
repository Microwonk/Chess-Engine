package net.chess.ai.AlphaBeta;

import net.chess.ai.AI;
import net.chess.ai.Evaluator;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.board.MoveTransition;
import net.chess.gui.Chess;

import java.util.ArrayList;
import java.util.List;

/**
 * recursive algorithm for alpha beta pruning -> WIP
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class AlphaBeta implements AI {

    private final int searchDepth;

    public AlphaBeta (final int searchDepth) {
        this.searchDepth = searchDepth;
    }

    @Override
    public Move execute (final Board board) {
        List <Move> legalMoves = new ArrayList <> (board.currentPlayer ().getLegalMoves ());

        Move bestMove = Move.MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;

        for (final Move move : legalMoves) {
            final MoveTransition moveTransition = board.currentPlayer ().makeMove (move);
            if (moveTransition.getMoveStatus ().isDone ()) {
                final int currentValue = alphaBeta(moveTransition.getTransitionBoard (), this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                synchronized (this) {
                    if (board.currentPlayer ().getTeam ().isWhite () && currentValue >= highestSeenValue) {
                        highestSeenValue = currentValue;
                        bestMove = move;
                    } else if (board.currentPlayer ().getTeam ().isBlack () && currentValue <= lowestSeenValue) {
                        lowestSeenValue = currentValue;
                        bestMove = move;
                    }
                }
            }
        }

        Chess.get().getLogger().printLog("Best Move: " + bestMove
                , "Evaluation: " + highestSeenValue
                , "Color: " + board.currentPlayer());
        return bestMove;
    }

    private int alphaBeta (Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0) {
            return Evaluator.evaluate (board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (final Move move : board.currentPlayer ().getLegalMoves ()) {
                final MoveTransition moveTransition = board.currentPlayer ().makeMove (move);
                if (moveTransition.getMoveStatus ().isDone ()) {
                    int eval = alphaBeta(moveTransition.getTransitionBoard (), depth - 1, alpha, beta, false);
                    maxEval = Math.max (maxEval, eval);
                    alpha = Math.max (alpha, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (final Move move : board.currentPlayer ().getLegalMoves ()) {
                final MoveTransition moveTransition = board.currentPlayer ().makeMove (move);
                if (moveTransition.getMoveStatus ().isDone ()) {
                    int eval = alphaBeta(moveTransition.getTransitionBoard (), depth - 1, alpha, beta, true);
                    minEval = Math.min (minEval, eval);
                    beta = Math.min (beta, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return minEval;
        }
    }
}


