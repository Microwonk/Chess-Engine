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
        super(piecePosition, pieceTeam, PieceType.PAWN, true);
    }

    public Pawn(final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.ROOK, isFirstMove);
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
                // taking care of pawn promotions
                if (this.pieceTeam.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion
                            (new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove
                            (board ,this, candidateDestinationCoordinate));
                }
            } // two in front

            else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtilities.SEVENTH_ROW[this.piecePosition] && this.pieceTeam.isBlack()) ||
                    (BoardUtilities.SECOND_ROW[this.piecePosition] && this.pieceTeam.isWhite()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (8 * this.getPieceTeam().getDirection());

                if(!board.getSquare(behindCandidateDestinationCoordinate).isOccupied()
                        && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // moves taking a piece diagonally -> edge cases column 1 and 8
            else if (currentCandidateOffset == 7
                    && !((BoardUtilities.EIGHTH_COLUMN[this.piecePosition] && this.pieceTeam.isWhite())
                    || (BoardUtilities.FIRST_COLUMN[this.piecePosition] && this.pieceTeam.isBlack()))) {

                if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    final Piece pieceOnCandidate = board.getSquare(candidateDestinationCoordinate).getPiece();

                    if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) { // only adds the move if the color of the piece is different
                        // pawn promotion
                        if (this.pieceTeam.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove
                                    (board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnAttackMove
                                    (board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
                // en passant
                else if (board.getEnPassantPawn() != null
                        && board.getEnPassantPawn().getPiecePosition()
                        == (this.piecePosition + (-this.pieceTeam.getDirection()))) { // en passant
                    final Pawn pieceOnCandidate = board.getEnPassantPawn();

                    if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) {
                        legalMoves.add(new PawnEnPassantAttack(
                                board, this, candidateDestinationCoordinate, pieceOnCandidate
                        ));
                    }
                }
            }

            else if (currentCandidateOffset == 9 // refactored for taking piece on other diagonal
                    && !((BoardUtilities.FIRST_COLUMN[this.piecePosition] && this.pieceTeam.isWhite())
                    || (BoardUtilities.EIGHTH_COLUMN[this.piecePosition] && this.pieceTeam.isBlack()))) {

                if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    final Piece pieceOnCandidate = board.getSquare(candidateDestinationCoordinate).getPiece();

                    // pawn promotion
                    if (this.pieceTeam.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                        legalMoves.add(new PawnPromotion(new PawnAttackMove
                                (board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                    } else {
                        legalMoves.add(new PawnAttackMove
                                (board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    }
                }
                // en passant
                else if (board.getEnPassantPawn() != null
                        && board.getEnPassantPawn().getPiecePosition()
                        == (this.piecePosition - (-this.pieceTeam.getDirection()))) { // en passant
                    final Pawn pieceOnCandidate = board.getEnPassantPawn();

                    if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) {
                        legalMoves.add(new PawnEnPassantAttack(
                                board, this, candidateDestinationCoordinate, pieceOnCandidate
                        ));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getPiece().getPieceTeam());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece() {
        // for now only promotion to queen possible
        return new Queen(this.piecePosition, this.pieceTeam, false);
    }
}
