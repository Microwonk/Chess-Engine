package main.java.net.chess.ai;

import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;
import main.java.net.chess.engine.player.MoveTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** AI minimax algorithm type
 * @author Nicolas Frey
 * @version 1.0
 */
public class Minimax implements AI{

    private final ExecutorService executorService;
    private int searchDepth;

    public Minimax(final int searchDepth) {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.searchDepth = searchDepth;
    }

    public Move execute(final Board board) {
        List<Move> legalMoves = new ArrayList<>(board.currentPlayer().getLegalMoves());

        AtomicReference<Move> bestMove = new AtomicReference<>(Move.MoveFactory.getNullMove());
        AtomicInteger highestSeenValue = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger lowestSeenValue = new AtomicInteger(Integer.MAX_VALUE);

        for (final Move move : legalMoves) {
            executorService.submit(() -> {
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    final int currentValue = minimax(moveTransition.getTransitionBoard(), this.searchDepth - 1, true);
                    synchronized (this) {
                        if (board.currentPlayer().getTeam().isWhite() && currentValue >= highestSeenValue.get()) {
                            highestSeenValue.set(currentValue);
                            bestMove.set(move);
                        } else if (board.currentPlayer().getTeam().isBlack() && currentValue <= lowestSeenValue.get()) {
                            lowestSeenValue.set(currentValue);
                            bestMove.set(move);
                        }
                    }
                }
            });
        }

        // Wait for all threads to complete
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bestMove.get();
    }

    public int minimax(Board board, int depth, boolean maximizingPlayer) {
        if (depth == 0 || board.isGameOver()) {
            return Evaluator.evaluate(board);
        }
        if (maximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            for (Move move : board.currentPlayer().getLegalMoves()) {
                Board newBoard = board.currentPlayer().makeMove(move).getTransitionBoard();
                int currentValue = minimax(newBoard, depth - 1, false);
                bestValue = Math.max(bestValue, currentValue);
            }
            return bestValue;
        } else {
            int bestValue = Integer.MAX_VALUE;
            for (Move move : board.currentPlayer().getLegalMoves()) {
                Board newBoard = board.currentPlayer().makeMove(move).getTransitionBoard();
                int currentValue = minimax(newBoard, depth - 1, true);
                bestValue = Math.min(bestValue, currentValue);
            }
            return bestValue;
        }
    }

    @Override
    public String toString() {
        return "MiniMax";
    }
}


