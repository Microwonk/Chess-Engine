package net.chess.engine.board.player;

import com.google.common.collect.ImmutableList;
import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;
import net.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board
            , final Collection<Move> whiteStandardLegalMoves
            , final Collection<Move> blackStandardLegalMoves) {

        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Team getTeam() {
        return Team.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            if (!this.board.getSquare(61).isOccupied()
                    && !this.board.getSquare(62).isOccupied()) {
                final Square rookSquare = this.board.getSquare(63);

                if (rookSquare.isOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnSquare(61, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(62, opponentLegals).isEmpty()
                            && rookSquare.getPiece().getPieceType().isRook()) {
                        kingCastles.add(null); // <- castling Kingside
                    }
                }
            }
            if (!this.board.getSquare(59).isOccupied()
                    && !this.board.getSquare(58).isOccupied()
                    && !this.board.getSquare(57).isOccupied()) {
                final Square rookSquare = this.board.getSquare(56);

                if (rookSquare.isOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnSquare(59, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(58, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(57, opponentLegals).isEmpty()
                            && rookSquare.getPiece().getPieceType().isRook()) {
                        kingCastles.add(null); // <- castling Queenside
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
