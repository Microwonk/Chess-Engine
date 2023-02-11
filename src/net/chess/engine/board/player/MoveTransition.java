package net.chess.engine.board.player;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;


// from one move to another
// , changing information
// and board and everything is done through here
public class MoveTransition {

    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board transitionBoard
                , final Move move
                , final MoveStatus moveStatus) {

        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

}
