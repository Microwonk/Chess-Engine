package net.chess.engine.board.player;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.pieces.Piece;

import java.util.Collection;

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
}
