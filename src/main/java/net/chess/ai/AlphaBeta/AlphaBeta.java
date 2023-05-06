package net.chess.ai.AlphaBeta;

import net.chess.ai.AI;
import net.chess.ai.Evaluator;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.MoveTransition;
import net.chess.engine.player.Player;
import net.chess.gui.Chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * recursive algorithm for alpha beta pruning -> WIP
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class AlphaBeta implements AI {

    final int searchDepth;

    public AlphaBeta(final int searchDepth) {
        this.searchDepth = searchDepth;
    }

    @Override
    public Move execute(final Board board) {
        final Player currentPlayer = board.currentPlayer();
        Move bestMove = Move.NULL_MOVE;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = currentPlayer.getTeam().isWhite() ?
                        min(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                if (currentPlayer.getTeam().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitionBoard().blackPlayer().isInCheckmate()) {
                        break;
                    }
                } else if (currentPlayer.getTeam().isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitionBoard().whitePlayer().isInCheckmate()) {
                        break;
                    }
                }
            }
        }
        return bestMove;
    }

    private static String score(final Player currentPlayer,
                                final int highestSeenValue,
                                final int lowestSeenValue) {

        if (currentPlayer.getTeam().isWhite()) {
            return "[score: " + highestSeenValue + "]";
        } else if (currentPlayer.getTeam().isBlack()) {
            return "[score: " + lowestSeenValue + "]";
        }
        throw new RuntimeException("bad bad boy!");
    }

    public int max(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || BoardUtilities.isEndGame(board)) {
            return Evaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : (board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final Board toBoard = moveTransition.getTransitionBoard();
                currentHighest = Math.max(currentHighest, min(toBoard,
                        calculateQuiescenceDepth(toBoard, depth), currentHighest, lowest));
                if (currentHighest >= lowest) {
                    return lowest;
                }
            }
        }
        return currentHighest;
    }

    public int min(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || BoardUtilities.isEndGame(board)) {
            return Evaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;
        for (final Move move : (board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final Board toBoard = moveTransition.getTransitionBoard();
                currentLowest = Math.min(currentLowest, max(toBoard,
                        calculateQuiescenceDepth(toBoard, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    return highest;
                }
            }
        }
        return currentLowest;
    }

    private int calculateQuiescenceDepth(final Board toBoard,
                                         final int depth) {
        // todo: need to implement actual quiescence
        return depth - 1;
    }
}

