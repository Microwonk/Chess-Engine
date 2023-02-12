package net.chess.engine.board.player;

import net.chess.engine.board.Move;

public enum MoveStatus {
    DONE {
        @Override
        boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        boolean isDone() {
            return false;
        }
    },
    LEAVES_IN_CHECK {
        @Override
        boolean isDone() {
            return false;
        }
    };

    abstract boolean isDone();
}
