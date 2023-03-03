package main.java.net.chess.ai;

import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;
import main.java.net.chess.engine.player.MoveStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Rand implements AI{
    @Override
    public Move execute(Board board) {
        Random r = new Random();
        List<Move> legalMoves = new ArrayList<>(board.currentPlayer().getLegalMoves())
                .stream().filter
                        (move -> board.currentPlayer().makeMove(move).getMoveStatus() == MoveStatus.DONE)
                .collect(Collectors.toList());
        int move = r.nextInt(0, legalMoves.size());
        return legalMoves.get(move);
    }
}
