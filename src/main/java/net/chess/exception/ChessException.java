package net.chess.exception;

public class ChessException extends RuntimeException{

    public ChessException() {
        super();
    }

    public ChessException(final String exception) {
        super(exception);
    }

}
