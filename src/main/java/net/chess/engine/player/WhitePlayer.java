package main.java.net.chess.engine.player;

import main.java.net.chess.engine.Team;
import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;
import main.java.net.chess.engine.board.Square;
import main.java.net.chess.engine.pieces.Piece;
import main.java.net.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static main.java.net.chess.engine.board.Move.CastleMove.*;

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
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals
            , final Collection<Move> opponentLegals) {

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            if (!this.board.getSquare(61).isOccupied()
                    && !this.board.getSquare(62).isOccupied()) {
                final Square rookSquare = this.board.getSquare(63);
                if (rookSquare.isOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnSquare(61, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(62, opponentLegals).isEmpty()
                            && rookSquare.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing
                                , 62, (Rook) rookSquare.getPiece()
                                , rookSquare.getSquareCoordinate(), 61)); // <- castling King side
                    }
                }
            }
            if (!this.board.getSquare(59).isOccupied()
                    && !this.board.getSquare(58).isOccupied()
                    && !this.board.getSquare(57).isOccupied()) {
                final Square rookSquare = this.board.getSquare(56);

                if (rookSquare.isOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnSquare(58, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnSquare(59, opponentLegals).isEmpty()
                            && rookSquare.getPiece().getPieceType().isRook()) {

                        kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing
                                , 58, (Rook) rookSquare.getPiece()
                                , rookSquare.getSquareCoordinate(), 59)); // <- castling Queenside
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
