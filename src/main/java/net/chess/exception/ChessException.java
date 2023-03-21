package net.chess.exception;

public class ChessException extends RuntimeException{

    private final long serialVersionUID = 10938102938012938L;

    public ChessException() {
        super();
    }

    public ChessException(final String exception) {
        super(exception);
    }

}
