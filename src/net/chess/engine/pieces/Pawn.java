package net.chess.engine.pieces;

import com.google.common.collect.ImmutableList;
import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// for MajorMove and AttackingMove
import static net.chess.engine.board.Move.*;

public class Pawn extends Piece {

    private final static int[] POSSIBLE_MOVE_COORDINATE = {7, 8, 9, 16};

    public Pawn(final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.PAWN);
    }

    @Override
    public Collection<Move> calcLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: POSSIBLE_MOVE_COORDINATE) {
            int candidateDestinationCoordinate = this.piecePosition
                    + currentCandidateOffset * this.getPieceTeam().getDirection();

            if(!BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            // one in front
            if(currentCandidateOffset == 8 && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {

                legalMoves.add(new MajorMove(board ,this, candidateDestinationCoordinate));
            } // two in front
            else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && (BoardUtilities.SECOND_ROW[this.piecePosition] && this.pieceTeam.isBlack()) ||
                    (BoardUtilities.SEVENTH_ROW[this.piecePosition] && this.pieceTeam.isWhite())) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceTeam.getDirection() * 8);

                if(!board.getSquare(behindCandidateDestinationCoordinate).isOccupied()
                        && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {

                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }
            } // moves taking a piece diagonally -> edge cases column 1 and 8
            else if (currentCandidateOffset == 7
                    && !((BoardUtilities.EIGHTH_COLUMN[this.piecePosition] && this.pieceTeam.isWhite())
                    || (BoardUtilities.FIRST_COLUMN[this.piecePosition] && this.pieceTeam.isBlack()))) {
                if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    final Piece pieceOnCandidate = board.getSquare(candidateDestinationCoordinate).getPiece();

                    if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) { // only adds the move if the color of the piece is different
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    }
                }
            } else if (currentCandidateOffset == 9 // refactored for taking piece on other diagonal
                    && !((BoardUtilities.FIRST_COLUMN[this.piecePosition] && this.pieceTeam.isWhite())
                    || (BoardUtilities.EIGHTH_COLUMN[this.piecePosition] && this.pieceTeam.isBlack()))) {

                if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    final Piece pieceOnCandidate = board.getSquare(candidateDestinationCoordinate).getPiece();

                    if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) { // only adds the move if the color of the piece is different
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
