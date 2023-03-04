package main.java.net.chess.engine.board;

import main.java.net.chess.engine.Team;
import main.java.net.chess.engine.player.BlackPlayer;
import main.java.net.chess.engine.player.Player;
import main.java.net.chess.engine.player.WhitePlayer;
import main.java.net.chess.engine.pieces.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

    private final List<Square> chessBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

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
            if ((i+1) % BoardUtilities.NUM_SQUARES_PER_ROW == 0) {
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


    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece: pieces) {
            legalMoves.addAll(piece.calcLegalMoves(this));
        }
        return Collections.unmodifiableList(legalMoves);
    }

    private static Collection<Piece> calculateActivePieces(final List<Square> chessBoard, final Team team) {
        final List<Piece> activePieces = new ArrayList<>();

        for (final Square square: chessBoard) {
            if (square.isOccupied()) {
                final Piece piece = square.getPiece();

                if (piece.getPieceTeam() == team) {
                    activePieces.add(piece);
                }
            }
        }
        return Collections.unmodifiableList(activePieces);
    }

    // get an immutable List of Squares -> chessBoard
    private static List<Square> createChessBoard(Builder builder) {
        final Square[] squares = new Square[BoardUtilities.NUM_SQUARES];

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            squares[i] = Square.createSquare(i, builder.boardConfiguration.get(i));
        }
        return Arrays.stream(squares).toList();
    }

    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }

    // creates normal Board
    public static Board createStandardBoard() {
        final Builder builder = new Builder();

        // BLACK
        builder.setPiece(new Rook(0, Team.BLACK));
        builder.setPiece(new Knight(1, Team.BLACK));
        builder.setPiece(new Bishop(2, Team.BLACK));
        builder.setPiece(new Queen(3, Team.BLACK));
        builder.setPiece(new King(4, Team.BLACK));
        builder.setPiece(new Bishop(5, Team.BLACK));
        builder.setPiece(new Knight(6, Team.BLACK));
        builder.setPiece(new Rook(7, Team.BLACK));
        builder.setPiece(new Pawn(8, Team.BLACK));
        builder.setPiece(new Pawn(9, Team.BLACK));
        builder.setPiece(new Pawn(10, Team.BLACK));
        builder.setPiece(new Pawn(11, Team.BLACK));
        builder.setPiece(new Pawn(12, Team.BLACK));
        builder.setPiece(new Pawn(13, Team.BLACK));
        builder.setPiece(new Pawn(14, Team.BLACK));
        builder.setPiece(new Pawn(15, Team.BLACK));
        // WHITE
        builder.setPiece(new Pawn(48, Team.WHITE));
        builder.setPiece(new Pawn(49, Team.WHITE));
        builder.setPiece(new Pawn(50, Team.WHITE));
        builder.setPiece(new Pawn(51, Team.WHITE));
        builder.setPiece(new Pawn(52, Team.WHITE));
        builder.setPiece(new Pawn(53, Team.WHITE));
        builder.setPiece(new Pawn(54, Team.WHITE));
        builder.setPiece(new Pawn(55, Team.WHITE));
        builder.setPiece(new Rook(56, Team.WHITE));
        builder.setPiece(new Knight(57, Team.WHITE));
        builder.setPiece(new Bishop(58, Team.WHITE));
        builder.setPiece(new Queen(59, Team.WHITE));
        builder.setPiece(new King(60, Team.WHITE));
        builder.setPiece(new Bishop(61, Team.WHITE));
        builder.setPiece(new Knight(62, Team.WHITE));
        builder.setPiece(new Rook(63, Team.WHITE));
        // white to Move first
        builder.setMoveMaker(Team.WHITE);
        return builder.build();
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
        return isGameOverCheckMate() || isGameOverCheckMate();
    }

    public Piece getPiece(final int destinationCoordinate) {
        return this.getSquare(destinationCoordinate).getPiece();
    }

    // inner builder class -> directly stolen from design patterns
    public static class Builder {
        Map<Integer, Piece> boardConfiguration;
        Team nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfiguration = new HashMap<>();
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfiguration.put(piece.getPiecePosition(), piece);
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
