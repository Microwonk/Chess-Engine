package net.chess.engine.board;

public class BoardUtilities {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);

    public static final int NUM_SQUARES = 64;
    public static final int NUM_SQUARES_PER_ROW = 8;

    private BoardUtilities() {
        throw new RuntimeException("This Class should not be Instantiated!");
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_SQUARES];
        // do not initialize the booleans to false, it is done automatically
        do {
            column[columnNumber] = true;
            columnNumber += NUM_SQUARES_PER_ROW;
        } while(columnNumber < NUM_SQUARES);
        return column;
    }

    public static boolean isValidSquareCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }
}
