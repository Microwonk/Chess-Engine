package net.chess.engine.board;

import java.util.HashMap;
import java.util.Map;

public class BoardUtilities {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] THIRD_COLUMN = initColumn(2);
    public static final boolean[] FOURTH_COLUMN = initColumn(3);
    public static final boolean[] FIFTH_COLUMN = initColumn(4);
    public static final boolean[] SIXTH_COLUMN = initColumn(5);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] EIGHTH_ROW = initRow(0);
    public static final boolean[] SEVENTH_ROW = initRow(8);
    public static final boolean[] SIXTH_ROW = initRow(16);
    public static final boolean[] FIFTH_ROW = initRow(24);
    public static final boolean[] FOURTH_ROW = initRow(32);
    public static final boolean[] THIRD_ROW = initRow(40);
    public static final boolean[] SECOND_ROW = initRow(48);
    public static final boolean[] FIRST_ROW = initRow(56);


    public static final int NUM_SQUARES = 64;
    public static final int NUM_SQUARES_PER_ROW = 8;

    public static final String[] RANK = initPosToRank();
    public static Map<String, Integer> FILE = initPosToFile();

    private static Map<String, Integer> initPosToFile() {
        final Map<String, Integer> toReturn = new HashMap<>();
        String file = "";
        for (int i = 0; i < NUM_SQUARES; i++) {
            switch (i/NUM_SQUARES_PER_ROW) {
                case 0: file = "A";
                case 1: file = "B";
                case 2: file = "C";
                case 3: file = "D";
                case 4: file = "E";
                case 5: file = "F";
                case 6: file = "G";
                case 7: file = "H";
            }
            toReturn.put(file, i);
        }
        return toReturn;
    }

    private static String[] initPosToRank() {
        return null;
    }

    private BoardUtilities() {
        throw new RuntimeException("dont do this");
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

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_SQUARES];

        do {
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % NUM_SQUARES_PER_ROW != 0);
        return row;
    }

    public static boolean isValidSquareCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }

    public static String getPositionCoordinate(final int position) {
        return FILE.keySet().toArray()[position].toString();
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return RANK[coordinate];
    }
}
