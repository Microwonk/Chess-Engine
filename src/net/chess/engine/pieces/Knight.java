package net.chess.engine.pieces;

import com.google.common.collect.ImmutableList;
import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// for MajorMove and AttackingMove
import static net.chess.engine.board.Move.*;

public class Knight extends Piece{
    // 8 kandidaten der Moves eines Knights
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.KNIGHT, true);
    }

    public Knight(final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Collection<Move> calcLegalMoves(final Board board) {
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
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationSquare.getPiece();
                    final Team pieceTeam = candidateDestinationSquare.getPiece().getPieceTeam();

                    if (this.pieceTeam != pieceTeam) {
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getPiece().getPieceTeam());
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

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }
}
