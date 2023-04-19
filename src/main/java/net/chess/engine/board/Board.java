package net.chess.engine.board;

import net.chess.engine.Team;
import net.chess.engine.pieces.*;
import net.chess.engine.player.BlackPlayer;
import net.chess.engine.player.Player;
import net.chess.engine.player.WhitePlayer;
import net.chess.gui.Chess;
import net.chess.parsing.FenParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Chessboard filled with squares
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Board {

    private final List<Square> chessBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    // can only be instantiated using the Board Builder
    private Board(final Builder builder) {
        this.chessBoard = createChessBoard(builder);
        this.whitePieces = calculateActivePieces(this.chessBoard, Team.WHITE);
        this.blackPieces = calculateActivePieces(this.chessBoard, Team.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            final String squareText = this.chessBoard.get(i).toString() + " ";

            builder.append(squareText);
            if ((i + 1) % BoardUtilities.NUM_SQUARES_PER_ROW == 0) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }


    /**
     * @param pieces passed in for which the legal-moves should be calculated for
     * @return all legal Moves on the Board
     */
    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calcLegalMoves(this));
        }
        return Collections.unmodifiableList(legalMoves);
    }

    public Player getCurrentPlayer () {
        return currentPlayer;
    }

    /**
     * @param chessBoard for the board that should be calculated for
     * @param team       for which team it should be calculated for
     * @return all Pieces of said Team on said Board
     */
    public static Collection<Piece> calculateActivePieces(final List<Square> chessBoard, final Team team) {
        final List<Piece> activePieces = new ArrayList<>();

        for (final Square square : chessBoard) {
            if (square.isOccupied()) {
                final Piece piece = square.getPiece();

                if (piece.getPieceTeam() == team) {
                    activePieces.add(piece);
                }
            }
        }
        return Collections.unmodifiableList(activePieces);
    }

    /**
     * @param builder builder can be set to set a certain board, which will return a new Board
     * @return new Board (List of Squares)
     */
    private static List<Square> createChessBoard(Builder builder) {
        final Square[] squares = new Square[BoardUtilities.NUM_SQUARES];

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            squares[i] = Square.createSquare(i, builder.boardConfiguration.get(i));
        }
        return Arrays.stream(squares).toList();
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    /**
     * @return a standard Chessboard
     */
    public static Board createStandardBoard() {
        return FenParser.createGameFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Square getSquare(final int squareCoordinate) {
        return chessBoard.get(squareCoordinate);
    }

    public Collection<Move> getAllLegalMoves() {
        return Stream.concat(this.whitePlayer.getLegalMoves().stream()
                , this.blackPlayer.getLegalMoves().stream()).collect(Collectors.toList());
    }

    public boolean isGameOverCheckMate() {
        return this.blackPlayer.isInCheckmate() || this.whitePlayer.isInCheckmate();
    }

    public boolean isGameOverStaleMate() {
        return this.blackPlayer.isInStalemate() || this.whitePlayer.isInStalemate();
    }

    public boolean isGameOver() {
        return isGameOverCheckMate() || isGameOverStaleMate() || Chess.get().isDrawByLackOfMaterial() || Chess.get().isDrawByRepetition();
    }

    public Piece getPiece(final int destinationCoordinate) {
        return this.getSquare(destinationCoordinate).getPiece();
    }

    /**
     * Board Builder Class
     *
     * @author Nicolas Frey
     * @version 1.0
     */
    public static class Builder {
        Map<Integer, Piece> boardConfiguration;
        Team nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfiguration = new HashMap<>();
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfiguration.put(piece.getPosition(), piece);
            return this;
        }

        public void setMoveMaker(final Team nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }

}
