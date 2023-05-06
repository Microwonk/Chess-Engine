package net.chess.ai.AlphaBeta;

import net.chess.ai.AI;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.board.MoveTransition;
import net.chess.engine.player.Player;
import net.chess.gui.Chess;

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
public class AlphaBetaMultiThreaded extends AlphaBeta implements AI {

    private final ExecutorService executorService;

    public AlphaBetaMultiThreaded (final int searchDepth) {
        super(searchDepth);
        this.executorService = Executors.newFixedThreadPool (Runtime.getRuntime ().availableProcessors () - 1);
    }

    @Override
    public Move execute(final Board board) {
        final Player currentPlayer = board.currentPlayer();
        AtomicReference <Move> bestMove = new AtomicReference <> (Move.MoveFactory.getNullMove ());
        AtomicReference<AtomicInteger> highestSeenValue = new AtomicReference<>(new AtomicInteger(Integer.MIN_VALUE));
        final AtomicInteger[] lowestSeenValue = {new AtomicInteger(Integer.MAX_VALUE)};
        AtomicInteger currentValue = new AtomicInteger();
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            executorService.submit (() -> {
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    currentValue.set(currentPlayer.getTeam().isWhite() ?
                            min(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue.get().get(), lowestSeenValue[0].get()) :
                            max(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue.get().get(), lowestSeenValue[0].get()));
                    if (currentPlayer.getTeam().isWhite() && currentValue.get() > highestSeenValue.get().get()) {
                        highestSeenValue.set(currentValue);
                        bestMove.set(move);
                        if (moveTransition.getTransitionBoard().blackPlayer().isInCheckmate()) {
                            return;
                        }
                    } else if (currentPlayer.getTeam().isBlack() && currentValue.get() < lowestSeenValue[0].get()) {
                        lowestSeenValue[0] = currentValue;
                        bestMove.set(move);
                        if (moveTransition.getTransitionBoard().whitePlayer().isInCheckmate()) {
                            return;
                        }
                    }
                }
            });
        }

        executorService.shutdown ();
        try {
            executorService.awaitTermination (Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }

        Chess.get().getLogger().printLog("Best Move: " + bestMove
                , "Evaluation: " + highestSeenValue
                , "Color: " + board.currentPlayer());

        return bestMove.get();
    }
}


