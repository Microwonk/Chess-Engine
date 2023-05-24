package net.chess.parsing;

import net.chess.engine.board.Board;
import net.chess.engine.board.Move;
import net.chess.exception.BadMoveException;
import net.chess.exception.ChessParsingException;
import net.chess.exception.InvalidCharacterSequenceException;
import net.chess.gui.Chess.MoveLog;

public class PGNParser {
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
        for (String move : moves) {
            Move add = parseMove(board, move);
            moveLog.add(add);
            board = board.currentPlayer().makeMove(add).getTransitionBoard();
        }
        return moveLog;
    }

    public static Move parseMove(final Board board, final String move) throws ChessParsingException {
        try {
            return board.getAllLegalMoves().stream().filter(m -> m.toString().equals(move)).findFirst().orElseThrow();
        } catch (Exception e) {
            if (!move.matches("^[a-h][1-8][a-h][1-8][qrbn]?[+#]?|^[a-h][1-8][+#]?$")) {
                throw new InvalidCharacterSequenceException("Invalid character sequence.");
            } else {
                throw new BadMoveException("Not a valid move.");
            }
        }
    }
}
