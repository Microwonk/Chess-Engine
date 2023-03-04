package main.java.net.chess.engine.player;

import main.java.net.chess.engine.Team;
import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.Move;
import main.java.net.chess.engine.pieces.King;
import main.java.net.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board
            , final Collection<Move> legalMoves
            , final Collection<Move> opponentsMoves) {

        this.board = board;
        this.playerKing = establishKing();
        // concatenating the list of legal moves AND the castling moves
        this.legalMoves = Stream.concat(legalMoves.stream(), calculateKingCastles
                (legalMoves, opponentsMoves).stream()).collect(Collectors.toUnmodifiableList());
        this.isInCheck = !Player.calculateAttacksOnSquare(this.playerKing.getPiecePosition(), opponentsMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnSquare(final int piecePosition
            , final Collection<Move> opponentsMoves) {
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move: opponentsMoves) {
            if(piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    private King establishKing() {
        for (final Piece piece: getActivePieces()) {
            if (piece instanceof King) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid Board, no King");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckmate() {
        return this.isInCheck && !hasEscapeMoves();
    }


    public boolean isInStalemate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isKingSideCapable() {
        return this.playerKing.isKingSideCapable();
    }
    public boolean isQueenSideCapable() {
        return this.playerKing.isQueenSideCapable();
    }

    public boolean isCastled() {
        return this.playerKing.isCastled();
    }

    protected boolean hasEscapeMoves() {
        for (final Move move: this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnSquare(
                transitionBoard.currentPlayer()
                        .getOpponent().getPlayerKing().getPiecePosition()
                , transitionBoard.currentPlayer().getLegalMoves()
                );

        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Team getTeam();
    public abstract Player getOpponent();
    public abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
