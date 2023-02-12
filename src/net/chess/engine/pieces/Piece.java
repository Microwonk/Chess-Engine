package net.chess.engine.pieces;

import net.chess.engine.Team;
import net.chess.engine.board.Board;
import net.chess.engine.board.Move;

import java.util.Collection;
import java.util.Objects;

public abstract class Piece {

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Team pieceTeam;
    protected final boolean isFirstMove;
    private final int cachedHashCode;

    Piece (final int piecePosition, final Team pieceTeam, final PieceType pieceType) {
        this.piecePosition = piecePosition;
        this.pieceTeam = pieceTeam;
        this.isFirstMove = false;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        return Objects.hash(pieceType, piecePosition, pieceTeam, isFirstMove);
    }

    public Team getPieceTeam() {
        return this.pieceTeam;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Piece oPiece)) {
            return false;
        }
        return piecePosition == oPiece.getPiecePosition()
                && pieceType == oPiece.getPieceType()
                && pieceTeam == oPiece.getPieceTeam()
                && isFirstMove == oPiece.isFirstMove;
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public abstract Collection<Move> calcLegalMoves(final Board board);

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public abstract Piece movePiece(final Move move);

    public enum PieceType {

        PAWN("P") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }
        };

        private String pieceName;

        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }
        public abstract boolean isKing();
    }
}
