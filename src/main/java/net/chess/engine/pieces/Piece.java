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

    Piece (final int piecePosition, final Team pieceTeam, final PieceType pieceType, final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceTeam = pieceTeam;
        this.isFirstMove = isFirstMove;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode () {
        return Objects.hash(pieceType, piecePosition, pieceTeam, isFirstMove);
    }

    public Team getPieceTeam () {
        return this.pieceTeam;
    }

    @Override
    public boolean equals (final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Piece oPiece)) {
            return false;
        }
        return piecePosition == oPiece.getPosition()
                && pieceType == oPiece.getPieceType()
                && pieceTeam == oPiece.getPieceTeam()
                && isFirstMove == oPiece.isFirstMove;
    }

    @Override
    public int hashCode () {
        return this.cachedHashCode;
    }

    public boolean isFirstMove () {
        return this.isFirstMove;
    }

    public int getPosition () {
        return this.piecePosition;
    }

    public abstract Collection <Move> calcLegalMoves (final Board board);

    public PieceType getPieceType () {
        return this.pieceType;
    }

    public abstract Piece movePiece (final Move move);

    public int getPieceValue () {
        return this.pieceType.getValue();
    }

    public abstract int locationBonus();

    public enum PieceType {

        PAWN("P", 100) {
            @Override
            public boolean isKing () {
                return false;
            }

            @Override
            public boolean isRook () {
                return false;
            }
        },
        KNIGHT("N", 320) {
            @Override
            public boolean isKing () {
                return false;
            }

            @Override
            public boolean isRook () {
                return false;
            }
        },
        BISHOP("B", 330) {
            @Override
            public boolean isKing () {
                return false;
            }

            @Override
            public boolean isRook () {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing () {
                return false;
            }

            @Override
            public boolean isRook () {
                return true;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing () {
                return false;
            }

            @Override
            public boolean isRook () {
                return false;
            }
        },
        KING("K", 20000) {
            @Override
            public boolean isKing () {
                return true;
            }

            @Override
            public boolean isRook () {
                return false;
            }
        },
        NULL("0", 0) {
            @Override
            public boolean isKing () {
                return false;
            }

            @Override
            public boolean isRook () {
                return false;
            }
        };

        private final String pieceName;
        private final int pieceValue;

        PieceType (final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString () {
            return this.pieceName;
        }

        public abstract boolean isKing ();

        public abstract boolean isRook ();

        public int getValue () {
            return this.pieceValue;
        }

    }

    public static class NullPiece extends Piece {

        private static final NullPiece NULL_PIECE = new NullPiece();

        private NullPiece () {
            super(65, null, PieceType.NULL, false);
        }

        @Override
        public Collection <Move> calcLegalMoves (Board board) {
            return null;
        }

        @Override
        public Piece movePiece (Move move) {
            return null;
        }

        @Override
        public int locationBonus() {
            return 0;
        }

        public static Piece getNullPiece () {
            return NULL_PIECE;
        }
    }
}
