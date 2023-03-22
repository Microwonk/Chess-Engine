package net.chess.engine.board;

import net.chess.exception.ChessException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for Comfort
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class BoardUtilities {

    private BoardUtilities() {
        throw new RuntimeException("don't do this");
    }

    // to see if a coordinate is on a specific column/file
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] THIRD_COLUMN = initColumn(2);
    public static final boolean[] FOURTH_COLUMN = initColumn(3);
    public static final boolean[] FIFTH_COLUMN = initColumn(4);
    public static final boolean[] SIXTH_COLUMN = initColumn(5);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    // to see if a coordinate is on a specific
    public static final boolean[] EIGHTH_ROW = initRow(0);
    public static final boolean[] SEVENTH_ROW = initRow(8);
    public static final boolean[] SIXTH_ROW = initRow(16);
    public static final boolean[] FIFTH_ROW = initRow(24);
    public static final boolean[] FOURTH_ROW = initRow(32);
    public static final boolean[] THIRD_ROW = initRow(40);
    public static final boolean[] SECOND_ROW = initRow(48);
    public static final boolean[] FIRST_ROW = initRow(56);

    // to see if a specified coordinate is central or not
    public static final boolean[] IS_CENTRAL = initCentral();

    public static final int NUM_SQUARES = 64;
    public static final int NUM_SQUARES_PER_ROW = 8;

    // for pgn format
    public static final String[] NOTATION = initNotation();
    public static Map<String, Integer> MAPPING_TO_POS = initMapping();

    // following are just initializers
    private static Map<String, Integer> initMapping() {
        final Map<String, Integer> res = new HashMap<>();

        for (int i = 0; i < NUM_SQUARES; i++) {
            res.put(NOTATION[i], i);
        }
        return res;
    }

    private static String[] initNotation() {
        return new String[]{
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_SQUARES];
        do {
            column[columnNumber] = true;
            columnNumber += NUM_SQUARES_PER_ROW;
        } while (columnNumber < NUM_SQUARES);
        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_SQUARES];

        do {
            row[rowNumber] = true;
            rowNumber++;
        } while (rowNumber % NUM_SQUARES_PER_ROW != 0);
        return row;
    }

    private static boolean[] initCentral() {
        return new boolean[]{
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, true, true, true, true, false, false,
                false, false, true, true, true, true, false, false,
                false, false, true, true, true, true, false, false,
                false, false, true, true, true, true, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false
        };
    }

    /**
     * @param coordinate given to see if it is on the board
     * @return true or false
     */
    public static boolean isValidSquareCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }

    public static int getPositionCoordinate(final String position) {
        return MAPPING_TO_POS.get(position);
    }

    /**
     * @param coordinate given for notation
     * @return notation
     */
    public static String getPositionAtCoordinate(final int coordinate) {
        return NOTATION[coordinate];
    }

    /**
     * @param legalMoves    opponents legal-moves
     * @param piecePosition position of the square being attacked
     * @return amount of attacks on the square
     */
    public static int getAttackCount(final Collection<Move> legalMoves, final int piecePosition) {
        return legalMoves.stream().filter(move -> move.getDestinationCoordinate()
                == piecePosition && move.isAttack()).toList().size();
    }

    /**
     * @param piecePosition position of the piece
     * @return the distance the piece is from the edge
     */
    public static int distanceFromEdge(final int piecePosition) {
        if (FIRST_COLUMN[piecePosition] || EIGHTH_COLUMN[piecePosition]) {
            return 0;
        } else if (SECOND_COLUMN[piecePosition] || SEVENTH_COLUMN[piecePosition]) {
            return 1;
        } else if (THIRD_COLUMN[piecePosition] || SIXTH_COLUMN[piecePosition]) {
            return 2;
        } else if (FOURTH_COLUMN[piecePosition] || FIFTH_COLUMN[piecePosition]) {
            return 3;
        } else {
            throw new ChessException("Piece is Off Board");
        }
    }
}
