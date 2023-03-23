package net.chess.ai;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.pieces.Pawn;
import net.chess.engine.pieces.Piece;
import net.chess.gui.GUI_Contents;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.chess.engine.pieces.Piece.PieceType;

/**
 * Evaluator for the Chess Board -> WIP VERRRY MUCH (so no Documentation yet)
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Evaluator {
    private static final int WINNING_SCORE = 10000;
    private static final int DRAW_SCORE = 0;
    private static final int MOBILITY_BONUS = 5;
    private static final int KING_SAFETY_BONUS = 700;
    private static final int PAWN_CHAIN_BONUS = 25;
    private static final int CONNECTED_PAWN_CHAIN_BONUS = 50;
    private static final int ADVANCED_PAWN_CHAIN_BONUS = 25;
    private static final int ISOLATED_PAWN_PENALTY = -30;
    private static final int DOUBLED_PAWN_PENALTY = -20;
    private static final int[] PASSED_PAWN_BONUS = {0, 5, 10, 15, 25, 50, 100};
    private static final int CENTRAL_PAWN_BONUS = 200;
    private static final int CENTRAL_PIECE_BONUS = 500;

    public static int evaluate (Board board) {
        int score = evaluateMaterial(board)
                + evaluateMobility(board)
                + evaluateKingSafety(board)
                + evaluateCenterControl(board);
        //evaluatePawnStructure(board)
        if (isCheckmate(board)) {
            if (board.currentPlayer().isInCheck()) {
                score -= WINNING_SCORE;
            } else {
                score += WINNING_SCORE;
            }
        } else if (GUI_Contents.get().isDrawByLackOfMaterial() || GUI_Contents.get().isDrawByRepetition()) {
            if (GUI_Contents.get().getGameBoard().isGameOverStaleMate()) {
                score = DRAW_SCORE;
            }
        }
        return score;
    }

    private static int evaluateMaterial (Board board) {
        int whiteScore = 0;
        int blackScore = 0;
        for (Piece piece : Stream.concat(board.getBlackPieces().stream(), board.getWhitePieces().stream()).toList()) {
            if (piece.getPieceTeam() == Team.WHITE) {
                whiteScore += piece.getPieceValue();
            } else {
                blackScore += piece.getPieceValue();
            }
        }
        return whiteScore - blackScore;
    }

    private static int evaluateMobility (final Board board) {
        int whiteMoves = board.currentPlayer().getLegalMoves().size();
        int blackMoves = board.currentPlayer().getOpponent().getLegalMoves().size();
        return MOBILITY_BONUS * (whiteMoves - blackMoves);
    }

    private static int evaluateKingSafety (final Board board) {
        final int currentPlayerKingPosition = board.currentPlayer().getPlayerKing().getPiecePosition();

        // Check if the king is in check
        if (board.currentPlayer().isInCheck()) {
            return -KING_SAFETY_BONUS;
        }

        // Check if there are enemy pieces attacking the king's current position
        final int kingAttackCount = BoardUtilities.getAttackCount(board.currentPlayer().getOpponent().getLegalMoves(), currentPlayerKingPosition);
        if (kingAttackCount > 0) {
            // Calculate the safety score based on the number of attacks and the position of the king
            final int kingDistanceFromEdge = BoardUtilities.distanceFromEdge(currentPlayerKingPosition);
            return (KING_SAFETY_BONUS * kingDistanceFromEdge) / kingAttackCount;
        }

        // Return a positive score if the king is safe
        return KING_SAFETY_BONUS;
    }

    private static int evaluateCenterControl (final Board board) {
        int centerControlBonus = 0;

        // Count number of pieces on central squares
        for (final Piece piece : board.currentPlayer().getActivePieces()) {
            if (piece instanceof Pawn) {
                if (BoardUtilities.IS_CENTRAL[piece.getPiecePosition()]) {
                    centerControlBonus += CENTRAL_PAWN_BONUS;
                }
            } else {
                if (BoardUtilities.IS_CENTRAL[piece.getPiecePosition()]) {
                    centerControlBonus += CENTRAL_PIECE_BONUS;
                }
            }
        }
        return centerControlBonus;
    }

    private static int evaluatePawnStructure (final Board board) {
        int pawnStructEval = 0;

        pawnStructEval += evaluatePawnChain(board) + evaluateIsolatedPawn(board) + evaluateDoubledPawns(board);
        //pawnStructEval += evaluateBackWardsPawns(); // both need to be implemented later
        //pawnStructEval += evaluatePawnIslands();
        return pawnStructEval;
    }

    private static int evaluatePassedPawns (final Board board) {
        int whitePassedPawns = 0;
        int blackPassedPawns = 0;

        for (final Piece pawn : board.currentPlayer().getActivePawns()) {
            if (isPassedPawn(pawn)) {
                if (BoardUtilities.getAttackCount(board.currentPlayer().getOpponent().getLegalMoves(), pawn.getPiecePosition()) == 0) {
                    // Passed pawn is not under attack
                    if (pawn.getPieceTeam().isWhite()) {
                        whitePassedPawns++;
                    } else {
                        blackPassedPawns++;
                    }
                }
            }
        }
        return 0;
    }

    private static boolean isPassedPawn (final Piece pawn) {
        // TODO: implement a method to find out passed pawns
        return false;
    }

    private static int evaluateIsolatedPawn (final Board board) {
        int isolatedPawns = 0;

        for (final Piece pawn : board.currentPlayer().getActivePawns()) {
            if (!isConnectedPawnChain(board, pawn.getPiecePosition(), pawn.getPieceTeam()) && !hasNeighboringPawns(pawn, board)) {
                isolatedPawns++;
            }
        }
        return ISOLATED_PAWN_PENALTY * isolatedPawns;
    }

    private static boolean hasNeighboringPawns (final Piece pawn, final Board board) {
        final int pawnFile = pawn.getPiecePosition() % 8;

        // Check for neighboring pawns on the same file
        for (final Piece otherPawn : board.currentPlayer().getActivePawns()) {
            if (otherPawn.getPiecePosition() % 8 == pawnFile && otherPawn != pawn) {
                return true;
            }
        }

        // Check for neighboring pawns on adjacent files
        final int[] adjacentFiles = {pawnFile - 1, pawnFile + 1};
        for (final int file : adjacentFiles) {
            if (file >= 0 && file < 8) {
                for (final Piece otherPawn : board.currentPlayer().getActivePawns()) {
                    if (otherPawn.getPiecePosition() % 8 == file) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static int evaluateDoubledPawns (final Board board) {
        int doubledPawnCount = 0;
        final int[] fileCount = new int[8];
        for (final Piece pawn : board.currentPlayer().getActivePawns()) {
            if (BoardUtilities.FIRST_COLUMN[pawn.getPiecePosition()]) {
                fileCount[0]++;
            } else if (BoardUtilities.SECOND_COLUMN[pawn.getPiecePosition()]) {
                fileCount[1]++;
            } else if (BoardUtilities.THIRD_COLUMN[pawn.getPiecePosition()]) {
                fileCount[2]++;
            } else if (BoardUtilities.FOURTH_COLUMN[pawn.getPiecePosition()]) {
                fileCount[3]++;
            } else if (BoardUtilities.FIFTH_COLUMN[pawn.getPiecePosition()]) {
                fileCount[4]++;
            } else if (BoardUtilities.SIXTH_COLUMN[pawn.getPiecePosition()]) {
                fileCount[5]++;
            } else if (BoardUtilities.SEVENTH_COLUMN[pawn.getPiecePosition()]) {
                fileCount[6]++;
            } else if (BoardUtilities.EIGHTH_COLUMN[pawn.getPiecePosition()]) {
                fileCount[7]++;
            }
        }
        for (final int count : fileCount) {
            if (count > 1) {
                doubledPawnCount += count;
            }
        }
        return doubledPawnCount * DOUBLED_PAWN_PENALTY;
    }

    private static int evaluatePawnChain (final Board board) {
        int pawnChainCount = 0;
        int connectedPawnChainCount = 0;
        int advancedPawnChainCount = 0;

        for (final Piece pawn : board.currentPlayer().getActivePawns()) {
            final int pawnRank = pawn.getPiecePosition() / 8;

            // Check for pawn chains
            if (isPawnChain(board, pawn.getPiecePosition(), pawn.getPieceTeam())) {
                pawnChainCount++;
                if (pawnRank >= 4) {
                    advancedPawnChainCount++;
                }
            }

            // Check for connected pawn chains
            if (isConnectedPawnChain(board, pawn.getPiecePosition(), pawn.getPieceTeam())) {
                connectedPawnChainCount++;
            }
        }

        // Calculate points based on the pawn chains
        int pawnChainPoints = PAWN_CHAIN_BONUS * pawnChainCount;
        int connectedPawnChainPoints = CONNECTED_PAWN_CHAIN_BONUS * connectedPawnChainCount;
        int advancedPawnChainPoints = ADVANCED_PAWN_CHAIN_BONUS * advancedPawnChainCount;

        return pawnChainPoints + connectedPawnChainPoints + advancedPawnChainPoints;
    }

    private static boolean isPawnChain (final Board board, final int pawnPosition, final Team pawnTeam) {
        final Piece pawn = board.getPiece(pawnPosition);
        if (pawn.getPieceType() != PieceType.PAWN || pawn.getPieceTeam() != pawnTeam) {
            return false;
        }

        // Check if the pawn is connected to a pawn of the same color either diagonally or vertically
        final int[] candidateOffsets = {7, 8, 9};
        for (final int candidateOffset : candidateOffsets) {
            final int candidateDestination = pawnPosition + (pawnTeam.getDirection() * candidateOffset);
            if (BoardUtilities.isValidSquareCoordinate(candidateDestination)) {
                final Piece candidatePawn = board.getPiece(candidateDestination);
                if (candidatePawn.getPieceType() == PieceType.PAWN && candidatePawn.getPieceTeam() == pawnTeam) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isConnectedPawnChain (final Board board, final int pawnPosition, final Team pawnTeam) {
        if (!isPawnChain(board, pawnPosition, pawnTeam)) {
            return false;
        }

        // Check if the pawn chain is connected to another pawn chain of the same color on either side
        final int[] candidateOffsets = {1, -1};
        for (final int candidateOffset : candidateOffsets) {
            int candidateDestination = pawnPosition + (pawnTeam.getDirection() * candidateOffset);
            while (BoardUtilities.isValidSquareCoordinate(candidateDestination)) {
                final Piece candidatePawn = board.getPiece(candidateDestination);
                if (candidatePawn.getPieceType() == PieceType.PAWN && candidatePawn.getPieceTeam() == pawnTeam) {
                    if (isPawnChain(board, candidateDestination, pawnTeam)) {
                        return true;
                    }
                } else {
                    break;
                }
                candidateDestination += (pawnTeam.getDirection() * candidateOffset);
            }
        }
        return false;
    }

    private static boolean isCheckmate (final Board board) {
        return board.currentPlayer().isInCheckmate() || board.currentPlayer().getOpponent().isInCheckmate();
    }
}

