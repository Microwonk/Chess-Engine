package main.java.net.chess.ai;

import main.java.net.chess.engine.Team;
import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.pieces.Piece;
import main.java.net.chess.gui.GUI_Contents;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Evaluator {
    private static final int WINNING_SCORE = 10000;
    private static final int DRAW_SCORE = 0;
    private static final int MOBILITY_BONUS = 5;

    public static int evaluate(Board board) {
        int score = evaluateMaterial(board) + evaluateMobility(board);
        if (isCheckmate(board)) {
            if (board.currentPlayer().isInCheck()) {
                score -= WINNING_SCORE;
            } else {
                score += WINNING_SCORE;
            }
        } else if (GUI_Contents.get().isDrawByLackOfMaterial()) {
            score = DRAW_SCORE;
        }
        return score;
    }

    private static int evaluateMaterial(Board board) {
        int whiteScore = 0;
        int blackScore = 0;
        for (Piece piece : Stream.concat(board.getBlackPieces().stream(), board.getWhitePieces().stream()).collect(Collectors.toList())) {
            if (piece.getPieceTeam() == Team.WHITE) {
                whiteScore += piece.getPieceValue();
            } else {
                blackScore += piece.getPieceValue();
            }
        }
        return whiteScore - blackScore;
    }

    private static int evaluateMobility(Board board) {
        int whiteMoves = board.currentPlayer().getLegalMoves().size();
        int blackMoves = board.currentPlayer().getOpponent().getLegalMoves().size();
        return MOBILITY_BONUS * (whiteMoves - blackMoves);
    }

    private static boolean isCheckmate(Board board) {
        return board.currentPlayer().isInCheckmate() || board.currentPlayer().getOpponent().isInCheckmate();
    }
}

