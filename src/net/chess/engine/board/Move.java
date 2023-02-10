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

    public static final class MajorMove extends Move {

        public MajorMove(final Board board
                , final Piece piece
                , final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
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
    }
}
