package net.chess.engine.board;

public class BoardUtilities {

    public static final boolean[] FIRST_COLUMN = null;
    public static final boolean[] SECOND_COLUMN = null;
    public static final boolean[] EIGHTH_COLUMN = null;
    public static final boolean[] SEVENTH_COLUMN = null;

    private BoardUtilities() {
        throw new RuntimeException("This Class should not be Instantiated!");
    }

    public static boolean isValidSquareCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }
}
