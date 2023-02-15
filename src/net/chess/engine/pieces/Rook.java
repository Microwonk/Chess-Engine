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

public class Rook extends Piece {

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-8, -1, 1, 8};

    public Rook(final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.ROOK, false);
    }

    public Rook(final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Collection<Move> calcLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset: POSSIBLE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)
                        || isEighthColumnExlusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;

                if (BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                    final Square candidateDestinationSquare = board.getSquare(candidateDestinationCoordinate);

                    if(!candidateDestinationSquare.isOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationSquare.getPiece();
                        final Team pieceTeam = candidateDestinationSquare.getPiece().getPieceTeam();

                        if (this.pieceTeam != pieceTeam) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
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
        return BoardUtilities.FIRST_COLUMN[currentPosition] && candidateOffset == -1;
    }

    private static boolean isEighthColumnExlusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.EIGHTH_COLUMN[currentPosition] && candidateOffset == 1;
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getPiece().getPieceTeam());
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}
