package net.chess.ai;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;

/**
 * WIP
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public interface AI {
    Move execute (final Board board);
}
