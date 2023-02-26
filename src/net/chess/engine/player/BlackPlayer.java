package net.chess.engine.player;

import com.google.common.collect.ImmutableList;
import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;
import net.chess.engine.pieces.Piece;
import net.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.chess.engine.board.Move.CastleMove.*;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board
            , final Collection<Move> whiteStandardLegalMoves
            , final Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Team getTeam() {
        return Team.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals
            , final Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {

            if (!this.board.getSquare(5).isOccupied()
                    && !this.board.getSquare(6).isOccupied()) {
                final Square rookSquare = this.board.getSquare(7);

                if (rookSquare.isOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnSquare(5, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(6, opponentLegals).isEmpty()
                            && rookSquare.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing
                                , 6, (Rook) rookSquare.getPiece()
                                , rookSquare.getSquareCoordinate(), 5)); // <- castling King side
                    }
                }
            }
            if (!this.board.getSquare(3).isOccupied()
                    && !this.board.getSquare(2).isOccupied()
                    && !this.board.getSquare(1).isOccupied()) {
                final Square rookSquare = this.board.getSquare(0);

                if (rookSquare.isOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnSquare(3, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(2, opponentLegals).isEmpty()
                            && rookSquare.getPiece().getPieceType().isRook()) {

                        kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing
                                , 2, (Rook) rookSquare.getPiece()
                                , rookSquare.getSquareCoordinate(), 3)); // <- castling Queenside
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
