package net.chess.engine.board;

// from package guava !!FIRST TIME WORKING WITH LIBRARIES
import com.google.common.collect.ImmutableMap;
import net.chess.engine.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public abstract class Square {
    // finals in this set of classes is used for immutability
    protected final int squareCoordinate;
    private static final Map<Integer, EmptySquare> EMPTY_SQUARES_CACHE = createAllPossibleEmptySquares();


    private static Map<Integer, EmptySquare> createAllPossibleEmptySquares() {
        final Map<Integer, EmptySquare> emptySquareMap = new HashMap<>();

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            emptySquareMap.put(i, new EmptySquare(i));
        }
        // I don't want anyone to change this map, so i will want to make it immutable (through guava)
        return ImmutableMap.copyOf(emptySquareMap);
    }

    // method like a Factory, all the empty square we are ever gonna need have been set in the above method
    public static Square createSquare(final int squareCoordinate, final Piece piece) {
        return piece != null ? new OccupiedSquare(squareCoordinate, piece)
                : EMPTY_SQUARES_CACHE.get(squareCoordinate);
    }

    // constructor
    private Square(final int squareCoordinate) {
        this.squareCoordinate = squareCoordinate;
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();

    //Polymorphic Approach to setting the Squares
    public static final class EmptySquare extends Square {

        private EmptySquare(final int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }
    // needs to be static in this case, so it behaves the same way as having it in another Class
    public static final class OccupiedSquare extends Square {

        private final Piece currentPiece;

        private OccupiedSquare(final int squareCoordinate, final Piece currentPiece) {
            super(squareCoordinate);
            this.currentPiece = currentPiece;
        }

        @Override
        public String toString() {
            return getPiece().getPieceTeam().isBlack() ? getPiece().toString().toLowerCase():
                    getPiece().toString();
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.currentPiece;
        }
    }
}
