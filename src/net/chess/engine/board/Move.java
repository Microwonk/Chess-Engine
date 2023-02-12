package net.chess.engine.board;

import net.chess.engine.pieces.Pawn;
import net.chess.engine.pieces.Piece;
import net.chess.engine.pieces.Rook;

import java.util.Objects;

import static net.chess.engine.board.Board.*;

public abstract class Move {

    final Board board;
    final Piece piece;
    final int destinationCoordinate;

    // error??
    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board
            , final Piece piece
            , final int destinationCoordinate) {
        this.board = board;
        this.piece = piece;
        this.destinationCoordinate = destinationCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Move oMove)) {
            return false;
        }
        return getDestinationCoordinate() == oMove.getDestinationCoordinate()
                && getPiece().equals(oMove.getPiece());
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, piece, destinationCoordinate);
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
        final Builder builder = new Board.Builder();
        for (final Piece piece: this.board.currentPlayer().getActivePieces()) {
            // TODO: hashcode and equals for Pieces
            if (!this.piece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        // common -> moves the moved piece
        builder.setPiece(this.piece.movePiece(this));
        // if the ingoing Move was white, then the next player will be black -> getOpponent does this
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
        return builder.build();
    }

    public static final class MajorMove extends Move {

        public MajorMove(final Board board
                , final Piece piece
                , final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            return null;
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

    static abstract class CastleMove extends Move {

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
            builder.setPiece(new Rook(this.castleRook.getPieceTeam(), this.castleRookDestination, false));
            builder.setMoveMaker(this.board.currentPlayer().)
        }

        static final class KingSideCastleMove extends CastleMove {

            public KingSideCastleMove(final Board board
                    , final Piece piece
                    , final int destinationCoordinate) {
                super(board, piece, destinationCoordinate);
            }

            @Override
            public Board execute() {
                throw new RuntimeException("Should not be executable");
            }
        }

        public static final class QueenSideCastleMove extends CastleMove {

            public QueenSideCastleMove(final Board board
                    , final Piece piece
                    , final int destinationCoordinate) {
                super(board, piece, destinationCoordinate);
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
            return null;
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
