package net.chess.engine.board;

import com.google.common.collect.ImmutableMap;

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

    public static final String[] NOTATION = initNotation();
    public static Map<String, Integer> MAPPING_TO_POS = initMapping();

    private static Map<String, Integer> initMapping() {
        final Map<String, Integer> res = new HashMap<>();

        for (int i = 0; i < NUM_SQUARES; i++) {
            res.put(NOTATION[i], i);
        }
        return res;
    }

    private static String[] initNotation() {
        final String[] res = new String[64];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < NUM_SQUARES_PER_ROW; i++) {
            for (int j = NUM_SQUARES_PER_ROW; j > 0; j--) {
                builder.append((char)(97 + (NUM_SQUARES_PER_ROW - j)));
                builder.append(NUM_SQUARES_PER_ROW - i);
                res[i*j] = builder.toString();
                builder.delete(0, builder.length());
            }
        }
        return res;
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

    public static int getPositionCoordinate(final String position) {
        return MAPPING_TO_POS.get(position);
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return NOTATION[coordinate];
    }
}
