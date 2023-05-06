package net.chess.engine.pieces;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;

import java.util.Collection;

public final class Bishop extends Piece implements SlidingPiece {

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -7, 7, 9};

    public Bishop (final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.BISHOP, true);
    }

    public Bishop (final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.BISHOP, isFirstMove);
    }

    @Override
    public Collection <Move> calcLegalMoves (final Board board) {
        return calcSliding(POSSIBLE_MOVE_COORDINATES, this, board);
    }

    @Override
    public Bishop movePiece (Move move) {
        return move.getPiece().getPieceTeam() == Team.WHITE ?
                BoardUtilities.cachedWhiteBishops[move.getDestinationCoordinate()]
                : BoardUtilities.cachedBlackBishops[move.getDestinationCoordinate()];
    }

    public boolean isFirstColumnExclusion (final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition]
                && (candidateOffset == -9 || candidateOffset == 7);
    }

    public boolean isEighthColumnExclusion (final int currentPosition, final int candidateOffset) {
        return BoardUtilities.EIGHTH_COLUMN[currentPosition]
                && (candidateOffset == 9 || candidateOffset == -7);
    }

    @Override
    public int locationBonus() {
        return this.pieceTeam.bishopBonus(this.piecePosition);
    }

    @Override
    public String toString () {
        return PieceType.BISHOP.toString();
    }
}
