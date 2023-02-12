package net.chess.engine.board.player;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.pieces.Piece;

import java.util.Collection;

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
}
