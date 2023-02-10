package net.chess.engine.pieces;

import com.google.common.collect.ImmutableList;
import net.chess.engine.Alliance;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends Piece {

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    Queen(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calcLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset: POSSIBLE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)
                        || isEighthColumnExlusion(candidateCoordinateOffset, candidateCoordinateOffset)) {
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;

                if (BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                    final Square candidateDestinationSquare = board.getSquare(candidateDestinationCoordinate);

                    if(!candidateDestinationSquare.isOccupied()) {
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationSquare.getPiece();
                        final Alliance pieceAlliance = candidateDestinationSquare.getPiece().getPieceAlliance();

                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        // if a piece is there it will stop looping to the next diagonal square
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition]
                && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExlusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition]
                && (candidateOffset == 1 || candidateOffset == 9 || candidateOffset == -7);
    }
}
