package net.chess.ai;

import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.pieces.Piece;
import net.chess.engine.player.Player;

import java.util.Collection;

public final class KingSafetyAnalyzer {

    private static final KingSafetyAnalyzer INSTANCE = new KingSafetyAnalyzer();

    private KingSafetyAnalyzer() {
    }

    public static KingSafetyAnalyzer get() {
        return INSTANCE;
    }
    public KingDistance calculateKingTropism(final Player player) {
        final int playerKingSquare = player.getPlayerKing().getPosition();
        final Collection<Move> enemyMoves = player.getOpponent().getLegalMoves();
        Piece closestPiece = null;
        int closestDistance = Integer.MAX_VALUE;
        for(final Move move : enemyMoves) {
            final int currentDistance = calculateChebyshevDistance(playerKingSquare, move.getDestinationCoordinate());
            if(currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestPiece = move.getPiece();
            }
        }
        return new KingDistance(closestPiece, closestDistance);
    }

    private static int calculateChebyshevDistance(final int kingTileId,
                                                  final int enemyAttackTileId) {
        final int rankDistance = Math.abs(getRank(enemyAttackTileId) - getRank(kingTileId));
        final int fileDistance = Math.abs(getFile(enemyAttackTileId) - getFile(kingTileId));
        return Math.max(rankDistance, fileDistance);
    }

    private static int getFile(final int coordinate) {
        if(BoardUtilities.FIRST_COLUMN[coordinate]) {
            return 1;
        } else if(BoardUtilities.SECOND_COLUMN[coordinate]) {
            return 2;
        } else if(BoardUtilities.THIRD_COLUMN[coordinate]) {
            return 3;
        } else if(BoardUtilities.FOURTH_COLUMN[coordinate]) {
            return 4;
        } else if(BoardUtilities.FIFTH_COLUMN[coordinate]) {
            return 5;
        } else if(BoardUtilities.SIXTH_COLUMN[coordinate]) {
            return 6;
        } else if(BoardUtilities.SEVENTH_COLUMN[coordinate]) {
            return 7;
        } else if(BoardUtilities.EIGHTH_COLUMN[coordinate]) {
            return 8;
        }
        throw new RuntimeException("should not reach here!");
    }

    private static int getRank(final int coordinate) {
        if(BoardUtilities.FIRST_ROW[coordinate]) {
            return 1;
        } else if(BoardUtilities.SECOND_ROW[coordinate]) {
            return 2;
        } else if(BoardUtilities.THIRD_ROW[coordinate]) {
            return 3;
        } else if(BoardUtilities.FOURTH_ROW[coordinate]) {
            return 4;
        } else if(BoardUtilities.FIFTH_ROW[coordinate]) {
            return 5;
        } else if(BoardUtilities.SIXTH_ROW[coordinate]) {
            return 6;
        } else if(BoardUtilities.SEVENTH_ROW[coordinate]) {
            return 7;
        } else if(BoardUtilities.EIGHTH_ROW[coordinate]) {
            return 8;
        }
        throw new RuntimeException("should not reach here!");
    }

    static class KingDistance {

        final Piece enemyPiece;
        final int distance;

        KingDistance(final Piece enemyDistance,
                     final int distance) {
            this.enemyPiece = enemyDistance;
            this.distance = distance;
        }

        public Piece getEnemyPiece() {
            return enemyPiece;
        }

        public int getDistance() {
            return distance;
        }

        public int tropismScore() {
            return (enemyPiece.getPieceValue()/10) * distance;
        }

    }

}
