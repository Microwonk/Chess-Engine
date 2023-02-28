package net.chess.engine.pieces;

import com.google.common.collect.ImmutableList;
import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;
import net.chess.engine.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// for MajorMove and AttackingMove
import static net.chess.engine.board.Move.*;
import static net.chess.engine.board.Move.CastleMove.*;

public class King extends Piece{

    private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    private final boolean isCastled;
    private final boolean isQueenSideCapable;
    private final boolean isKingSideCapable;

    public King(final int piecePosition, final Team pieceTeam) {
        super(piecePosition, pieceTeam, PieceType.KING, true);
        this.isCastled = false;
        this.isKingSideCapable = true;
        this.isQueenSideCapable = true;
    }

    public King(final int piecePosition, final Team pieceTeam, final boolean isFirstMove) {
        super(piecePosition, pieceTeam, PieceType.ROOK, isFirstMove);
        this.isKingSideCapable = false;
        this.isCastled = false;
        this.isQueenSideCapable = false;
    }

    public King(final int piecePosition, final Team pieceTeam
            , final boolean isFirstMove
            , final boolean isCastled
            , final boolean isKingSideCapable
            , final boolean isQueenSideCapable) {
        super(piecePosition, pieceTeam, PieceType.ROOK, isFirstMove);
        this.isKingSideCapable = isKingSideCapable;
        this.isCastled = isCastled;
        this.isQueenSideCapable = isQueenSideCapable;
    }

    @Override
    public Collection<Move> calcLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: POSSIBLE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset)
                    || isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }

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
                }
            }
        }
        if (board != null && board.whitePlayer() != null && board.blackPlayer() != null) {
            final Player player = pieceTeam == board.whitePlayer().getTeam() ? board.whitePlayer() : board.blackPlayer();
            legalMoves.addAll(player.calculateKingCastles(player.getLegalMoves(), player.getOpponent().getLegalMoves()));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(final Move move) {
        final King previousKing = (King) move.getPiece();
        if (move instanceof QueenSideCastleMove || move instanceof KingSideCastleMove) {
            return new King(move.getDestinationCoordinate()
                    , previousKing.getPieceTeam()
                    , false
                    , true
                    , false
                    , false);
        }
        return new King(move.getDestinationCoordinate()
                , move.getPiece().getPieceTeam()
                , false
                , move.isCastlingMove() || previousKing.isCastled
                , previousKing.isKingSideCapable()
                , previousKing.isQueenSideCapable());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9)
                || (candidateOffset == -1)
                || (candidateOffset == 7));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtilities.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == -7)
                || (candidateOffset == 1)
                || (candidateOffset == 9));
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    public boolean isKingSideCapable() {
        return this.isKingSideCapable;
    }

    public boolean isQueenSideCapable() {
        return isQueenSideCapable;
    }

    public boolean isCastled() {
        return isCastled;
    }
}