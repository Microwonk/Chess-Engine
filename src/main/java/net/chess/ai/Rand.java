package net.chess.ai;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.engine.player.MoveTransition.MoveStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * AI random algorithm type
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Rand implements AI {
    @Override
    public Move execute (Board board) {
        Random r = new Random();
        List <Move> legalMoves = new ArrayList <>(board.currentPlayer().getLegalMoves())
                .stream().filter
                        (move -> board.currentPlayer().makeMove(move).getMoveStatus() == MoveStatus.DONE)
                .collect(Collectors.toList());
        int move = r.nextInt(0, legalMoves.size());
        return legalMoves.get(move);
    }
}
