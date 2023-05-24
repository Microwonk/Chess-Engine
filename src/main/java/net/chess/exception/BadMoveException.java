package net.chess.exception;

public class BadMoveException extends ChessParsingException {
    public BadMoveException(String message) {
        super(message);
    }
}
