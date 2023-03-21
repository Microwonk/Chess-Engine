package net.chess.engine.player;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;


// from one move to another
// , changing information
// and board and everything is done through here
public class MoveTransition {

    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition (final Board transitionBoard
            , final Move move
            , final MoveStatus moveStatus) {

        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus () {
        return this.moveStatus;
    }

    public Board getTransitionBoard () {
        return this.transitionBoard;
    }

    public enum MoveStatus {
        DONE {
            @Override
            public boolean isDone () {
                return true;
            }
        },
        ILLEGAL_MOVE {
            @Override
            public boolean isDone () {
                return false;
            }
        },
        LEAVES_IN_CHECK {
            @Override
            public boolean isDone () {
                return false;
            }
        };

        public abstract boolean isDone ();
    }
}
