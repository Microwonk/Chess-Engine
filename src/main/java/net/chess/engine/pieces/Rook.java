package net.chess.engine.pieces;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;

import java.util.Collection;


public final class Rook extends Piece implements SlidingPiece {

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-8, -1, 1, 8};

    public Rook (final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.ROOK, true);
    }

    public Rook (final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Collection <Move> calcLegalMoves (final Board board) {
        return calcSliding(POSSIBLE_MOVE_COORDINATES, this, board);
    }

    public boolean isFirstColumnExclusion (final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition] && candidateOffset == -1;
    }

    public boolean isEighthColumnExclusion (final int currentPosition, final int candidateOffset) {
        return BoardUtilities.EIGHTH_COLUMN[currentPosition] && candidateOffset == 1;
    }

    @Override
    public Rook movePiece (Move move) {
        return move.getPiece().getPieceTeam() == Team.WHITE ?
                BoardUtilities.cachedWhiteRooks[move.getDestinationCoordinate()]
                : BoardUtilities.cachedBlackRooks[move.getDestinationCoordinate()];
    }

    @Override
    public String toString () {
        return PieceType.ROOK.toString();
    }
}
