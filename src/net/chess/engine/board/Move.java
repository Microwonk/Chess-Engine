package net.chess.engine.board;

import net.chess.engine.pieces.Pawn;
import net.chess.engine.pieces.Piece;
import net.chess.engine.pieces.Rook;

import java.util.Objects;

import static net.chess.engine.board.Board.*;

public abstract class Move {

    protected final Board board;
    protected final Piece piece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    // error??
    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board
            , final Piece piece
            , final int destinationCoordinate) {
        this.board = board;
        this.piece = piece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = piece.isFirstMove();
    }

    private Move(final Board board
            , final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate =destinationCoordinate;
        this.piece = null;
        this.isFirstMove = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Move oMove)) {
            return false;
        }
        return getCurrentCoordinate() == oMove.getCurrentCoordinate()
               && getDestinationCoordinate() == oMove.getDestinationCoordinate()
               && getPiece().equals(oMove.getPiece());
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, piece, destinationCoordinate, isFirstMove);
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public int getCurrentCoordinate() {
        return this.piece.getPiecePosition();
    }

    public Piece getPiece() {
        return this.piece;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Board execute() {
        final Board.Builder builder = new Builder();
        this.board.currentPlayer().getActivePieces().stream().filter(piece -> !this.piece.equals(piece)).forEach(builder::setPiece);
        this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
        builder.setPiece(this.piece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
        // builder.setMoveTransition(this);
        return builder.build();
    }

    public static final class MajorMove extends Move {

        public MajorMove(final Board board
                , final Piece piece
                , final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof MajorMove && super.equals(o);
        }

        @Override
        public String toString() {
            return piece.getPieceType().toString() + BoardUtilities.getPositionCoordinate(this.destinationCoordinate);
        }
    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board
                , final Piece piece
                , final int destinationCoordinate
                , final Piece attackedPiece) {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof final Move oMove)) {
                return false;
            }
            return super.equals(oMove) && getAttackedPiece().equals(oMove.getAttackedPiece());
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    public static final class PawnMove extends Move {

        public PawnMove(final Board board
                , final Piece piece
                , final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board
                , final Piece piece
                , final int destinationCoordinate
                , final Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class PawnEnPassantAttack extends PawnAttackMove {
        public PawnEnPassantAttack(final Board board
                , final Piece piece
                , final int destinationCoordinate
                , final Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class PawnJump extends Move {

        public PawnJump(final Board board
                , final Piece piece
                , final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece: this.board.currentPlayer().getActivePieces()) {
                if (!this.piece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.piece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }
    }

    public static abstract class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board
                , final Piece piece
                , final int destinationCoordinate
                , final Rook castleRook
                , final int castleRookStart
                , final int castleRookDestination) {

            super(board, piece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece: this.board.currentPlayer().getActivePieces()) {
                if (!this.piece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.piece.movePiece(this));
            // two pieces being moved via the same execute method
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceTeam()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        public static final class KingSideCastleMove extends CastleMove {

            public KingSideCastleMove(final Board board
                    , final Piece piece
                    , final int destinationCoordinate
                    , final Rook castleRook
                    , final int castleRookStart
                    , final int castleRookDestination) {
                super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
            }

            @Override
            public String toString() {
                return "O-O";
            }
        }

        public static final class QueenSideCastleMove extends CastleMove {

            public QueenSideCastleMove(final Board board
                    , final Piece piece
                    , final int destinationCoordinate
                    , final Rook castleRook
                    , final int castleRookStart
                    , final int castleRookDestination) {
                super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
            }

            @Override
            public String toString() {
                return "O-O-O";
            }

            @Override
            public Board execute() {
                return null;
            }
        }
    }

    public static final class NullMove extends Move {

        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Should not be executable");
        }
    }


    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Not instantiable");
        }

        public static Move createMove(final Board board
                , final int currentCoordinate
                , final int destinationCoordinate) {

            for (final Move move: board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate
                        && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
