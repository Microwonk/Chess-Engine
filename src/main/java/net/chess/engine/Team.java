package main.java.net.chess.engine;

import main.java.net.chess.engine.board.BoardUtilities;
import main.java.net.chess.engine.player.BlackPlayer;
import main.java.net.chess.engine.player.Player;
import main.java.net.chess.engine.player.WhitePlayer;

public enum Team {
    WHITE {
        @Override
        public int getDirection () {
            return -1;
        }

        @Override
        public boolean isWhite () {
            return true;
        }

        @Override
        public boolean isBlack () {
            return false;
        }

        @Override
        public Player choosePlayer (final WhitePlayer whitePlayer
                , final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public boolean isPawnPromotionSquare (final int position) {
            return BoardUtilities.EIGHTH_ROW[position];
        }

        @Override
        public boolean isAboutToPromoteSquare (int position) {
            return BoardUtilities.SEVENTH_ROW[position];
        }
    },
    BLACK {
        @Override
        public int getDirection () {
            return 1;
        }

        @Override
        public boolean isWhite () {
            return false;
        }

        @Override
        public boolean isBlack () {
            return true;
        }

        @Override
        public Player choosePlayer (final WhitePlayer whitePlayer
                , final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public boolean isPawnPromotionSquare (final int position) {
            return BoardUtilities.FIRST_ROW[position];
        }

        @Override
        public boolean isAboutToPromoteSquare (int position) {
            return BoardUtilities.SECOND_ROW[position];
        }
    };

    public abstract int getDirection ();

    public abstract boolean isWhite ();

    public abstract boolean isBlack ();

    public abstract Player choosePlayer (final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

    public abstract boolean isPawnPromotionSquare (final int position);

    public abstract boolean isAboutToPromoteSquare (final int position);
}
