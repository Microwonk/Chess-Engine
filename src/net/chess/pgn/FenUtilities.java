package net.chess.pgn;

import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.pieces.Pawn;

public class FenUtilities {

    private FenUtilities() {
        throw new RuntimeException("NO");
    }

    public static Board createGameFromFen(final String fenString) {
        return null;
    }

    public static String parseFen(final Board board) { // currently not worrying about the full moves -> 0 1
        return calculateFEN(board) + " " + currentPlayer(board) + " " + castleText(board) + " " + enPassantSquare(board) + " 0 1";
    }

    private static String enPassantSquare(final Board board) {
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null) {
            return BoardUtilities.getPositionAtCoordinate(enPassantPawn.getPiecePosition()
                    + (8) * (-enPassantPawn.getPieceTeam().getDirection()));
        }
        return "-";
    }

    private static String calculateFEN(final Board board) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            builder.append(board.getSquare(i).toString());
        }
        for (int i = 8; i < BoardUtilities.NUM_SQUARES; i+=9) {
            builder.insert(i, "/");
        }
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String castleText(final Board board) {
        final StringBuilder builder = new StringBuilder();

        if (board.whitePlayer().isKingSideCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCapable()) {
            builder.append("q");
        }
        return builder.toString().isEmpty() ? "-" : builder.toString();
    }

    private static String currentPlayer(final Board board) {
        return board.currentPlayer().toString().substring(0, 1).toLowerCase();
    }

}
