package net.chess.engine.board;

import net.chess.engine.pieces.Piece;

public abstract class Move {

    final Board board;
    final Piece piece;
    final int destinationCoordinate;

    private Move(final Board board
            , final Piece piece
            , final int destinationCoordinate) {
        this.board = board;
        this.piece = piece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public abstract Board execute();

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

    public static final class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board
                , final Piece piece
                , final int destinationCoordinate
                , final Piece attackedPiece) {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
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
    }
}
