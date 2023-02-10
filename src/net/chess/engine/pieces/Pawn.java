package net.chess.engine.pieces;

import net.chess.engine.Alliance;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] POSSIBLE_MOVE_COORDINATE = {8};

    Pawn(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calcLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: POSSIBLE_MOVE_COORDINATE) {
            int candidateDestinationCoordinate = this.piecePosition
                    + currentCandidateOffset * this.getPieceAlliance().getDirection();

            if(!BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if(currentCandidateOffset == 8 && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                legalMoves.add(new Move.MajorMove(board ,this, candidateDestinationCoordinate))
            }

        }

        return null;
    }
}
