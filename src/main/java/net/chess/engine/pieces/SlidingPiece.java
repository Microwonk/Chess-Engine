package net.chess.engine.pieces;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface SlidingPiece {
    boolean isEighthColumnExclusion (int currentPosition, int candidateOffset);

    boolean isFirstColumnExclusion (int currentPosition, int candidateOffset);

    default Collection <Move> calcSliding (final int[] possibleMoves, final Piece p, final Board board) {
        final List <Move> legalMoves = new ArrayList <>();

        for (final int candidateCoordinateOffset : possibleMoves) {
            int candidateDestinationCoordinate = p.piecePosition;

            while (BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)
                        || isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;

                if (BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                    final Square candidateDestinationSquare = board.getSquare(candidateDestinationCoordinate);

                    if (!candidateDestinationSquare.isOccupied()) {
                        legalMoves.add(new Move.MajorMove(board, p, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationSquare.getPiece();
                        final Team pieceTeam = candidateDestinationSquare.getPiece().getPieceTeam();

                        if (p.pieceTeam != pieceTeam) {
                            legalMoves.add(new Move.MajorAttackMove(board, p, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        // if a piece is there it will stop looping to the next diagonal square
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
}
