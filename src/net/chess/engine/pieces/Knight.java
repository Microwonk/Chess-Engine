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

public class Knight extends Piece{
    // 8 kandidaten der Moves eines Knights
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calcLegalMoves(Board board) {
        int candidateDestinationCoordinate;
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentOffset: POSSIBLE_MOVE_COORDINATES) {
            candidateDestinationCoordinate = this.piecePosition + currentOffset;

            if (BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {

                if (isFirstColumnExclusion(this.piecePosition, currentOffset)
                        || isSecondColumnExclusion(this.piecePosition, currentOffset)
                        || isSeventhColumnExclusion(this.piecePosition, currentOffset)
                        || isEighthColumnExclusion(this.piecePosition, currentOffset)) {
                    continue;
                }

                final Square candidateDestinationSquare = board.getSquare(candidateDestinationCoordinate);

                if(!candidateDestinationSquare.isOccupied()) {
                    legalMoves.add(new Move());
                } else {
                    final Piece pieceAtDestination = candidateDestinationSquare.getPiece();
                    final Alliance pieceAlliance = candidateDestinationSquare.getPiece().getPieceAlliance();

                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move());
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17)
                || (candidateOffset == -10)
                || (candidateOffset == 6)
                || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.SECOND_COLUMN[currentPosition] && ((candidateOffset == - 10)
                || (candidateOffset == 6));
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.SEVENTH_COLUMN[currentPosition] && ((candidateOffset == 10)
                || (candidateOffset == - 6));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 17)
                || (candidateOffset == 10)
                || (candidateOffset == -6)
                || (candidateOffset == -15));
    }

}
