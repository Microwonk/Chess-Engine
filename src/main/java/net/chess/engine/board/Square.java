package main.java.net.chess.engine.board;

import main.java.net.chess.engine.pieces.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * represents a single square on the chessboard
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public abstract class Square {
    protected final int squareCoordinate;
    private static final Map <Integer, EmptySquare> EMPTY_SQUARES_CACHE = createAllPossibleEmptySquares ();

    public int getSquareCoordinate () {
        return this.squareCoordinate;
    }

    private static Map <Integer, EmptySquare> createAllPossibleEmptySquares () {
        final Map <Integer, EmptySquare> emptySquareMap = new HashMap <> ();

        for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
            emptySquareMap.put (i, new EmptySquare (i));
        }
        return Collections.unmodifiableMap (emptySquareMap);
    }

    /**
     * @param squareCoordinate coordinate of the square
     * @param piece            piece on the square
     * @return new Occupied or empty square
     */
    public static Square createSquare (final int squareCoordinate, final Piece piece) {
        return piece != null ? new OccupiedSquare (squareCoordinate, piece)
                : EMPTY_SQUARES_CACHE.get (squareCoordinate);
    }

    private Square (final int squareCoordinate) {
        this.squareCoordinate = squareCoordinate;
    }

    public abstract boolean isOccupied ();

    public abstract Piece getPiece ();

    /**
     *
     */
    public static final class EmptySquare extends Square {

        private EmptySquare (final int coordinate) {
            super (coordinate);
        }

        @Override
        public String toString () {
            return "-";
        }

        @Override
        public boolean isOccupied () {
            return false;
        }

        @Override
        public Piece getPiece () {
            return null;
        }
    }

    // needs to be static in this case, so it behaves the same way as having it in another Class
    public static final class OccupiedSquare extends Square {

        private final Piece currentPiece;

        private OccupiedSquare (final int squareCoordinate, final Piece currentPiece) {
            super (squareCoordinate);
            this.currentPiece = currentPiece;
        }

        @Override
        public String toString () {
            return getPiece ().getPieceTeam ().isBlack () ? getPiece ().toString ().toLowerCase () :
                    getPiece ().toString ();
        }

        @Override
        public boolean isOccupied () {
            return true;
        }

        @Override
        public Piece getPiece () {
            return this.currentPiece;
        }
    }
}
