package main.java.net.chess.pgn;

import main.java.net.chess.engine.Team;
import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Board.Builder;
import main.java.net.chess.engine.board.BoardUtilities;
import main.java.net.chess.engine.pieces.*;

public class FenParser {

    private FenParser () {
        throw new RuntimeException("NO");
    }

    public static Board createGameFromFen (final String fenString) {
        final Builder builder = new Builder();
        int emptyCount = 0;
        final String[] splitter = fenString.replace("/", "").split(" ");
        for (int i = 0; i < splitter[0].length(); i++) {
            final String current = Character.toString(splitter[0].charAt(i));
            try {
                // System.out.println(Integer.parseInt(Character.toString(splitter[0].charAt(i))));
                emptyCount += Integer.parseInt(current) - 1;
            } catch(Exception e) {
                final Piece p = createPieceFromFen(current.charAt(0), i, splitter, emptyCount);
                if (p instanceof Pawn && !splitter[3].equals("-")) {
                    if (p.getPiecePosition() == (BoardUtilities.MAPPING_TO_POS.get(splitter[3])))
                        builder.setEnPassantPawn((Pawn) p);
                }
                builder.setPiece(p);
            }
        }
        builder.setMoveMaker(splitter[1].equals("w") ? Team.WHITE : Team.BLACK);
        return builder.build();
    }

    private static Piece createPieceFromFen (final char piece, final int piecePosition, final String[] args0, final int count) {
        final int pos = piecePosition + count;
        final Piece p;
        final boolean QSide = args0[2].contains("K");
        final boolean KSide = args0[2].contains("Q");
        final boolean qSide = args0[2].contains("k");
        final boolean kSide = args0[2].contains("q");
        switch (piece) {
            case 'b' -> p = new Bishop(pos, Team.BLACK);
            case 'B' -> p = new Bishop(pos, Team.WHITE);
            case 'n' -> p = new Knight(pos, Team.BLACK);
            case 'N' -> p = new Knight(pos, Team.WHITE);
            case 'q' -> p = new Queen(pos, Team.BLACK);
            case 'Q' -> p = new Queen(pos, Team.WHITE);
            case 'k' -> p = new King(pos, Team.BLACK
                    , kSide || qSide, !(kSide || qSide), kSide, qSide);
            case 'K' -> p = new King(pos, Team.WHITE
                    , KSide || QSide, !(KSide || QSide), KSide, QSide);
            case 'r' -> p = new Rook(pos, Team.BLACK
                    , ((BoardUtilities.EIGHTH_COLUMN[pos] && kSide)
                    ||(BoardUtilities.FIRST_COLUMN[pos] && qSide)));
            case 'R' -> p = new Rook(pos, Team.WHITE
                    , ((BoardUtilities.EIGHTH_COLUMN[pos] && KSide)
                    ||(BoardUtilities.FIRST_COLUMN[pos] && QSide)));
            case 'p' -> p = new Pawn(pos, Team.BLACK, BoardUtilities.SEVENTH_ROW[pos]);
            case 'P' -> p = new Pawn(pos, Team.WHITE, BoardUtilities.SECOND_ROW[pos]);
            default -> p = null;
        }
        System.out.println(pos);
        System.out.println(p);
        System.out.println();
        return p;
    }

    public static String parseFen (final Board board) { // currently not worrying about the full moves -> 0 1
        return calculateFEN(board) + " " + currentPlayer(board) + " " + castleText(board) + " " + enPassantSquare(board) + " 0 1";
    }

    private static String enPassantSquare (final Board board) {
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null) {
            return BoardUtilities.getPositionAtCoordinate(enPassantPawn.getPiecePosition()
                    + (8) * (-enPassantPawn.getPieceTeam().getDirection()));
        }
        return "-";
    }

    private static String calculateFEN (final Board board) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            builder.append(board.getSquare(i).toString());
        }
        for (int i = 8; i < BoardUtilities.NUM_SQUARES; i += 9) {
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

    private static String castleText (final Board board) {
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

    private static String currentPlayer (final Board board) {
        return board.currentPlayer().toString();
    }

}
