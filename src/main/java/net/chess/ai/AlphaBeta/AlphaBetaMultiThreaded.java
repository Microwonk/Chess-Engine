package net.chess.ai.AlphaBeta;

import net.chess.ai.AI;
import net.chess.ai.Evaluator;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.player.MoveTransition;
import net.chess.gui.GUI_Contents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * recursive algorithm for alpha beta pruning -> WIP
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class AlphaBetaMultiThreaded implements AI {

    private final ExecutorService executorService;
    private final int searchDepth;

    public AlphaBetaMultiThreaded (final int searchDepth) {
        this.executorService = Executors.newFixedThreadPool (Runtime.getRuntime ().availableProcessors () - 1);
        this.searchDepth = searchDepth;
    }

    @Override
    public Move execute (final Board board) {
        List <Move> legalMoves = new ArrayList <> (board.currentPlayer ().getLegalMoves ());

        AtomicReference <Move> bestMove = new AtomicReference <> (Move.MoveFactory.getNullMove ());
        AtomicInteger highestSeenValue = new AtomicInteger (Integer.MIN_VALUE);
        AtomicInteger lowestSeenValue = new AtomicInteger (Integer.MAX_VALUE);

        for (final Move move : legalMoves) {
            executorService.submit (() -> {
                final MoveTransition moveTransition = board.currentPlayer ().makeMove (move);
                if (moveTransition.getMoveStatus ().isDone ()) {
                    final int currentValue = alphaBeta(moveTransition.getTransitionBoard (), this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                    synchronized (this) {
                        if (board.currentPlayer ().getTeam ().isWhite () && currentValue >= highestSeenValue.get ()) {
                            highestSeenValue.set (currentValue);
                            bestMove.set (move);
                        } else if (board.currentPlayer ().getTeam ().isBlack () && currentValue <= lowestSeenValue.get ()) {
                            lowestSeenValue.set (currentValue);
                            bestMove.set (move);
                        }
                    }
                }
            });
        }

        // Wait for all threads to complete
        executorService.shutdown ();
        try {
            executorService.awaitTermination (Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }

        GUI_Contents.get().getLogger().printLog("Best Move: " + bestMove
                , "Evaluation: " + highestSeenValue
                , "Color: " + board.currentPlayer());
        return bestMove.get ();
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


