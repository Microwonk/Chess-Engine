package net.chess.engine.pieces;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.chess.engine.board.Move.*;

public final class Pawn extends Piece {

    private final static int[] POSSIBLE_MOVE_COORDINATE = {7, 8, 9, 16};

    public Pawn (final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.PAWN, true);
    }

    public Pawn (final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.PAWN, isFirstMove);
    }

    @Override
    public Collection <Move> calcLegalMoves (final Board board) {
        final List <Move> legalMoves = new ArrayList <>();

        for (final int currentCandidateOffset : POSSIBLE_MOVE_COORDINATE) {
            int candidateDestinationCoordinate = this.piecePosition
                    + currentCandidateOffset * this.getPieceTeam().getDirection();

            if (!BoardUtilities.isValidSquareCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            // one in front
            if (currentCandidateOffset == 8 && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                // taking care of pawn promotions
                if (this.pieceTeam.isPawnPromotionSquare(candidateDestinationCoordinate)) {

                    legalMoves.add(new PawnPromotion
                            (new PawnMove(board, this, candidateDestinationCoordinate)
                                    , this.getPieceTeam() == Team.WHITE
                                    ? BoardUtilities.cachedWhiteQueens[this.piecePosition]
                                    : BoardUtilities.cachedBlackQueens[this.piecePosition]));

                    legalMoves.add(new PawnPromotion
                            (new PawnMove(board, this, candidateDestinationCoordinate)
                                    , this.getPieceTeam() == Team.WHITE
                                    ? BoardUtilities.cachedWhiteRooks[this.piecePosition]
                                    : BoardUtilities.cachedBlackRooks[this.piecePosition]));

                    legalMoves.add(new PawnPromotion
                            (new PawnMove(board, this, candidateDestinationCoordinate)
                                    , this.getPieceTeam() == Team.WHITE
                                    ? BoardUtilities.cachedWhiteBishops[this.piecePosition]
                                    : BoardUtilities.cachedBlackBishops[this.piecePosition]));

                    legalMoves.add(new PawnPromotion
                            (new PawnMove(board, this, candidateDestinationCoordinate)
                                    , this.getPieceTeam() == Team.WHITE
                                    ? BoardUtilities.cachedWhiteKnights[this.piecePosition]
                                    : BoardUtilities.cachedBlackKnights[this.piecePosition]));
                } else {
                    legalMoves.add(new PawnMove
                            (board, this, candidateDestinationCoordinate));
                }
            } // two in front

            else if (currentCandidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtilities.SEVENTH_ROW[this.piecePosition] && this.pieceTeam.isBlack()) ||
                    (BoardUtilities.SECOND_ROW[this.piecePosition] && this.pieceTeam.isWhite()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (8 * this.getPieceTeam().getDirection());

                if (!board.getSquare(behindCandidateDestinationCoordinate).isOccupied()
                        && !board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // moves taking a piece diagonally -> edge cases column 1 and 8
            else if (currentCandidateOffset == 7
                    && !((BoardUtilities.EIGHTH_COLUMN[this.piecePosition] && this.pieceTeam.isWhite())
                    || (BoardUtilities.FIRST_COLUMN[this.piecePosition] && this.pieceTeam.isBlack()))) {

                if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    promotionHelper(board, legalMoves, candidateDestinationCoordinate);
                }
                // en passant
                else if (board.getEnPassantPawn() != null
                        && board.getEnPassantPawn().getPosition()
                        == (this.piecePosition + (-this.pieceTeam.getDirection()))) { // en passant
                    final Pawn pieceOnCandidate = board.getEnPassantPawn();

                    if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) {
                        legalMoves.add(new PawnEnPassantAttack(
                                board, this, candidateDestinationCoordinate, pieceOnCandidate
                        ));
                    }
                }
            } else if (currentCandidateOffset == 9 // refactored for taking piece on other diagonal
                    && !((BoardUtilities.FIRST_COLUMN[this.piecePosition] && this.pieceTeam.isWhite())
                    || (BoardUtilities.EIGHTH_COLUMN[this.piecePosition] && this.pieceTeam.isBlack()))) {

                if (board.getSquare(candidateDestinationCoordinate).isOccupied()) {
                    promotionHelper(board, legalMoves, candidateDestinationCoordinate);
                }
                // en passant
                else if (board.getEnPassantPawn() != null
                        && board.getEnPassantPawn().getPosition()
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
        return Collections.unmodifiableList(legalMoves);
    }

    private void promotionHelper (Board board, List <Move> legalMoves, int candidateDestinationCoordinate) {
        final Piece pieceOnCandidate = board.getSquare(candidateDestinationCoordinate).getPiece();

        if (this.pieceTeam != pieceOnCandidate.getPieceTeam()) { // only adds the move if the color of the piece is different
            // pawn promotion
            if (this.pieceTeam.isPawnPromotionSquare(candidateDestinationCoordinate)) {

                legalMoves.add(new PawnPromotion(new PawnAttackMove
                        (board, this, candidateDestinationCoordinate, pieceOnCandidate)
                        , this.getPieceTeam() == Team.WHITE
                        ? BoardUtilities.cachedWhiteQueens[this.piecePosition]
                        : BoardUtilities.cachedBlackQueens[this.piecePosition]));

                legalMoves.add(new PawnPromotion(new PawnAttackMove
                        (board, this, candidateDestinationCoordinate, pieceOnCandidate)
                        , this.getPieceTeam() == Team.WHITE
                        ? BoardUtilities.cachedWhiteRooks[this.piecePosition]
                        : BoardUtilities.cachedBlackRooks[this.piecePosition]));

                legalMoves.add(new PawnPromotion(new PawnAttackMove
                        (board, this, candidateDestinationCoordinate, pieceOnCandidate)
                        , this.getPieceTeam() == Team.WHITE
                        ? BoardUtilities.cachedWhiteBishops[this.piecePosition]
                        : BoardUtilities.cachedBlackBishops[this.piecePosition]));


                legalMoves.add(new PawnPromotion(new PawnAttackMove
                        (board, this, candidateDestinationCoordinate, pieceOnCandidate)
                        , this.getPieceTeam() == Team.WHITE
                        ? BoardUtilities.cachedWhiteKnights[this.piecePosition]
                        : BoardUtilities.cachedBlackKnights[this.piecePosition]));

            } else {
                legalMoves.add(new PawnAttackMove
                        (board, this, candidateDestinationCoordinate, pieceOnCandidate));
            }
        }
    }

    @Override
    public Pawn movePiece (Move move) {
        return move.getPiece().getPieceTeam() == Team.WHITE ?
                BoardUtilities.cachedWhitePawns[move.getDestinationCoordinate()]
                : BoardUtilities.cachedBlackPawns[move.getDestinationCoordinate()];
    }

    @Override
    public int locationBonus() {
        return this.pieceTeam.pawnBonus(this.piecePosition);
    }

    @Override
    public String toString () {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece () {
        // for now only promotion to queen possible
        return new Queen(this.piecePosition, this.pieceTeam, false);
    }
}
