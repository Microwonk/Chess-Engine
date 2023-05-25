package net.chess.network.test;

import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.network.NetworkPlayer;
import net.chess.network.NetworkUtils;

import java.util.Arrays;
import java.util.Random;

public class Server {
    public static void main(String[] args) throws Exception {
        NetworkPlayer server = new NetworkPlayer();
        for (int i = 0; i < 20; i++) {
            server.send(new Move.PawnJump(Board.createStandardBoard(), BoardUtilities.cachedBlackPawns[new Random().nextInt(0, 64)], new Random().nextInt(0, 64)));
            Thread.sleep(500);
        }
        server.close();
    }
}