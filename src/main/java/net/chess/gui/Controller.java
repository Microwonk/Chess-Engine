package net.chess.gui;

import net.chess.engine.board.Move;
import net.chess.engine.board.MoveTransition;
import net.chess.engine.board.Square;
import net.chess.engine.pieces.*;
import net.chess.gui.audio.AudioHandler;
import net.chess.parsing.FenParser;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
import static net.chess.gui.Chess.CHESS;
import static net.chess.gui.util.Variables.*;

public class Controller extends MouseAdapter {

    final int squareID;
    public Controller (final int squareID) {
        this.squareID = squareID;
    }

    @Override
    public void mouseClicked (MouseEvent e) {
        if (!movingEnabled) {
            return;
        }
        if (isLeftMouseButton(e)) {
            if (sourceSquare == null) {
                firstClick();
            } else {
                secondClick();
            }
            SwingUtilities.invokeLater(CHESS::draw);
        } else if (isRightMouseButton(e)) {
            sourceSquare = null;
            destinationSquare = null;
            movedPiece = null;
            SwingUtilities.invokeLater(CHESS::draw);
        }
    }
    private void firstClick() {
        sourceSquare = CHESS.getGameBoard().getSquare(squareID);
        movedPiece = sourceSquare.getPiece();
        // if clicked on empty square, do nothing
        if (movedPiece == null) {
            sourceSquare = null;
        } else if (movedPiece.getPieceTeam() != CHESS.getGameBoard().currentPlayer().getTeam()) {
            sourceSquare = null;
        }
    }

    private void secondClick() {
        destinationSquare = CHESS.getGameBoard().getSquare(squareID);
        // if same square is clicked, reset
        if (sourceSquare == destinationSquare) {
            sourceSquare = null;
            destinationSquare = null;
            movedPiece = null;
            SwingUtilities.invokeLater(CHESS::draw);
            return;
        }
        // if the square that is clicked has the same color piece on it
        // , it will jump into that square clicked -> quality of Life
        if (!(destinationSquare instanceof Square.EmptySquare) && destinationSquare.getPiece().getPieceTeam() == movedPiece.getPieceTeam()) {
            sourceSquare = CHESS.getGameBoard().getSquare(squareID);
            movedPiece = sourceSquare.getPiece();
            destinationSquare = null;
            SwingUtilities.invokeLater(CHESS::draw);
            return;
        }
        if (destinationSquare.equals(sourceSquare)) {
            sourceSquare = null;
            destinationSquare = null;
            SwingUtilities.invokeLater(CHESS::draw);
            return;
        }
        final Move move;
        if (movedPiece.getPieceTeam().isPawnPromotionSquare(destinationSquare.getSquareCoordinate())
                && movedPiece instanceof Pawn
                && movedPiece.getPieceTeam().isAboutToPromoteSquare(movedPiece.getPosition())) {

            final PromotionDialog pD = new PromotionDialog(CHESS.getFrame(), movedPiece.getPieceTeam());
            final Piece promotionPiece;
            if (pD.getSelectedPieceType() == Piece.PieceType.QUEEN) {
                promotionPiece = new Queen(movedPiece.getPosition()
                        , movedPiece.getPieceTeam()
                        , false);
            } else if (pD.getSelectedPieceType() == Piece.PieceType.ROOK) {
                promotionPiece = new Rook(movedPiece.getPosition()
                        , movedPiece.getPieceTeam()
                        , false);
            } else if (pD.getSelectedPieceType() == Piece.PieceType.BISHOP) {
                promotionPiece = new Bishop(movedPiece.getPosition()
                        , movedPiece.getPieceTeam()
                        , false);
            } else if (pD.getSelectedPieceType() == Piece.PieceType.KNIGHT) {
                promotionPiece = new Knight(movedPiece.getPosition()
                        , movedPiece.getPieceTeam()
                        , false);
            } else {
                // if it is "X"d away
                sourceSquare = null;
                destinationSquare = null;
                movedPiece = null;
                return;
            }
            move = new Move.PawnPromotion(
                    new Move.PawnMove(CHESS.getGameBoard(), movedPiece, destinationSquare.getSquareCoordinate()), promotionPiece);
        } else {
            move = Move.MoveFactory.createMove(CHESS.getGameBoard()
                    , sourceSquare.getSquareCoordinate()
                    , destinationSquare.getSquareCoordinate());
        }
        final MoveTransition transition = CHESS.getGameBoard().currentPlayer().makeMove(move);
        if (transition.getMoveStatus().isDone()) {
            if (move.isAttack()) {
                AudioHandler.playSound(1);
            } else if (transition.getTransitionBoard().blackPlayer().isInCheckmate()
                    || transition.getTransitionBoard().whitePlayer().isInCheckmate()) {
                AudioHandler.playSound(2);
            } else {
                AudioHandler.playSound(0);
            }
            currentMove++;
            CHESS.setChessBoard(transition.getTransitionBoard());
            CHESS.getMoveLog().addMove(move);
            CHESS.getLogger().printLog(move.toString());
            CHESS.getPositionLog().add(FenParser.parseFen(transition.getTransitionBoard()));
        }
        sourceSquare = null;
        destinationSquare = null;
        movedPiece = null;
    }
}