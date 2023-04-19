package net.chess.engine.pieces;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;

import java.util.Collection;

public final class Queen extends Piece implements SlidingPiece {

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public Queen (final int piecePosition, final Team pieceTeam) {
        super (piecePosition, pieceTeam, PieceType.QUEEN, true);
    }

    public Queen (final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super (piecePosition, pieceTeam, PieceType.QUEEN, isFirstMove);
    }

    @Override
    public Collection <Move> calcLegalMoves (final Board board) {
         return calcSliding(POSSIBLE_MOVE_COORDINATES, this, board);
    }

    public boolean isFirstColumnExclusion (final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition]
                && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    public boolean isEighthColumnExclusion (final int currentPosition, final int candidateOffset) {
        return BoardUtilities.EIGHTH_COLUMN[currentPosition]
                && (candidateOffset == 1 || candidateOffset == 9 || candidateOffset == -7);
    }

    @Override
    public Queen movePiece (Move move) {
        return move.getPiece().getPieceTeam() == Team.WHITE ?
                BoardUtilities.cachedWhiteQueens[move.getDestinationCoordinate()]
                : BoardUtilities.cachedBlackQueens[move.getDestinationCoordinate()];
    }

    @Override
    public String toString () {
        return PieceType.QUEEN.toString ();
    }
}
