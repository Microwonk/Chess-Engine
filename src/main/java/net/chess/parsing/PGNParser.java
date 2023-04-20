package net.chess.parsing;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.exception.ChessParsingException;
import net.chess.gui.util.Loadable;
import net.chess.gui.Chess.MoveLog;
import net.chess.gui.util.Savable;

public class PGNParser implements Loadable<MoveLog>, Savable <String> {
    public static String parsePGN(final MoveLog moveLog) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < moveLog.size(); i++) {
            builder.append(i + 1).append('.').append(moveLog.get(i).toString()).append('\n');
        }
        return builder.toString();
    }

    // TODO: implement in "Chess" class
    public static MoveLog parseIntoLog(final String pgn) {
        final MoveLog moveLog = new MoveLog();
        String[] moves = pgn.replaceAll("\\d{1,3}\\.", "").split("\n");
        Board board = Board.createStandardBoard();
        for (int i = 0; i < moves.length; i++) {
            Move add = parseMove(board, moves[i]);
            moveLog.add(add);
            board = board.currentPlayer().makeMove(add).getTransitionBoard();
        }
        return moveLog;
    }

    public static Move parseMove(final Board board, final String move) throws ChessParsingException {
        try {
            return board.getAllLegalMoves().stream().filter(m -> m.toString().equals(move)).toList().get(0);
        } catch (Exception e) {
            throw new ChessParsingException("Invalid Move or Null given");
        }
    }

    @Override
    public MoveLog load (String filename) {
        return null;
    }

    @Override
    public void save (String toSave, String path) {

    }
}
