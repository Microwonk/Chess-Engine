package net.chess.gui;

import com.google.common.collect.Lists;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;
import net.chess.engine.pieces.Piece;
import net.chess.engine.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GUI_Contents {

    private final JFrame frame;
    private final ChessBoard chessBoard;
    private Board board;

    private Square sourceSquare;
    private Square destinationSquare;
    private Piece movedPiece;
    private BoardDirection boardDirection;

    private final static Dimension FRAME_DIMENSION = new Dimension(600, 600);
    private final Dimension CHESS_BOARD_DIMENSION = new Dimension(400, 350);
    private final Dimension SQUARE_DIMENSION = new Dimension(10, 10);
    private boolean highlightLegalMovesActive;
    private String path;
    private String misc;

    private final Color lightColour = new Color(196, 189, 175);
    private final Color darkColour = new Color(155, 132, 75);

    public GUI_Contents() {
        this.board = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMovesActive = false;
        // TODO: make user choose own art
        this.path = "assets/pieces/pixel_art/";
        this.misc = "assets/misc/";

        this.frame = new JFrame("Chess by Nicolas Frey");
        this.frame.setLayout(new BorderLayout());
        this.frame.setFont(new Font("Minecraft", Font.BOLD, 13));

        this.chessBoard = new ChessBoard();
        this.frame.add(this.chessBoard, BorderLayout.CENTER);

        this.frame.setJMenuBar(makeMenuBar());
        this.frame.setIconImage(new ImageIcon(path + "WR.png").getImage());
        this.frame.setSize(FRAME_DIMENSION);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
    }

    private JMenuBar makeMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(210, 37, 141));
        menuBar.add(createFileMenu());
        menuBar.add(createSettingsMenu());
        // menuBar.setBackground(new Color(102, 175, 107));
        return menuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        // implementing PGN files for game loading and saving
        final JMenuItem openPGN = new JMenuItem("Load PGN");
        openPGN.setFont(frame.getFont());
        openPGN.addActionListener(e -> System.out.println("PGN Action babyyy"));
        final JMenuItem exitFrame = new JMenuItem("Exit");
        exitFrame.setFont(frame.getFont());
        exitFrame.addActionListener(e -> this.frame.dispose());
        fileMenu.add(openPGN);
        fileMenu.add(exitFrame);
        fileMenu.setFont(frame.getFont());
        return fileMenu;
    }

    private JMenu createSettingsMenu() {
        final JMenu settingsMenu = new JMenu("Settings");
        final JMenuItem flip = new JMenuItem("Flip Board");
        flip.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            chessBoard.drawBoard(board);
        });
        flip.setFont(frame.getFont());
        settingsMenu.add(flip);
        settingsMenu.addSeparator();

        final JCheckBoxMenuItem highlightingLegalMovesToggle = new JCheckBoxMenuItem("Highlight Moves");
        highlightingLegalMovesToggle.setFont(frame.getFont());
        highlightingLegalMovesToggle.addActionListener(e -> {
            highlightLegalMovesActive = highlightingLegalMovesToggle.isSelected();
            chessBoard.drawBoard(board);
        });
        settingsMenu.add(highlightingLegalMovesToggle);
        settingsMenu.setFont(frame.getFont());
        return settingsMenu;
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<SquareGUI> traverse(List<SquareGUI> squares) {
                return squares;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<SquareGUI> traverse(List<SquareGUI> squares) {
                return Lists.reverse(squares);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<SquareGUI> traverse(final List<SquareGUI> squares);
        abstract BoardDirection opposite();
    }


    private class ChessBoard extends JPanel {
        final List<SquareGUI> boardSquares;

        ChessBoard() {
            super(new GridLayout(8, 8));
            this.boardSquares = new ArrayList<>();

            for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
                final SquareGUI square = new SquareGUI(this, i);
                this.boardSquares.add(square);
                this.add(square);
            }
            setPreferredSize(CHESS_BOARD_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final SquareGUI square: boardDirection.traverse(boardSquares)) {
                square.drawSquare(board);
                add(square);
            }
            validate();
            repaint();
        }
    }

    private class SquareGUI extends JPanel {

        private final int squareID;

        SquareGUI(final ChessBoard chessBoard
                , final int squareID) {
            super(new GridBagLayout());
            this.squareID = squareID;
            setPreferredSize(SQUARE_DIMENSION);
            setBackground(assignSquareColour());
            assignSquareIcon(board);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isLeftMouseButton(e)) {
                        // first Click
                        if (sourceSquare == null) {
                            sourceSquare = board.getSquare(squareID); // <- solution without using pixel measurements!
                            movedPiece = sourceSquare.getPiece();

                            // if clicked on empty square, do nothing
                            if (movedPiece == null) {
                                sourceSquare = null;
                            }
                        } else { // second Click
                            destinationSquare = board.getSquare(squareID);
                            // if the square that is clicked has the same color piece on it
                            // , it will jump into that square clicked -> quality of Life
                            if (!(destinationSquare instanceof Square.EmptySquare)
                                    && destinationSquare.getPiece().getPieceTeam()
                                    == movedPiece.getPieceTeam()) {

                                sourceSquare = board.getSquare(squareID);
                                movedPiece = sourceSquare.getPiece();
                                destinationSquare = null;
                                SwingUtilities.invokeLater(() -> chessBoard.drawBoard(board));
                                return;
                            }
                            // creates the move and brings it into transition board -> visualises with the updater
                            // invokelater swingutilies
                            final Move move = Move.MoveFactory.createMove(board
                                    , sourceSquare.getSquareCoordinate()
                                    , destinationSquare.getSquareCoordinate());
                            final MoveTransition transition = board.currentPlayer().makeMove(move);

                            if (transition.getMoveStatus().isDone()) {
                                board = transition.getTransitionBoard();
                            }
                            sourceSquare = null;
                            destinationSquare = null;
                            movedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> chessBoard.drawBoard(board));
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        private void assignSquareIcon(final Board board) {
            this.removeAll();
            if (board.getSquare(this.squareID).isOccupied()) {
                try {
                    final BufferedImage image = ImageIO.read(new File(
                            path + board.getSquare(this.squareID)
                                    .getPiece().getPieceTeam().toString()
                                    .charAt(0) + board.getSquare(this.squareID)
                                    .getPiece().toString() + ".png")
                            );
                    add(new JLabel(new ImageIcon(image.getScaledInstance(30, 60, 0))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void highlightLegalMoves(final Board board) {
            if (highlightLegalMovesActive) {
                for (final Move move: pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == squareID) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File(misc + "highlighting.png"))
                                    .getScaledInstance(25, 25, 0))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(Board board) {
            if (movedPiece != null && movedPiece.getPieceTeam() == board.currentPlayer().getTeam()) {
                return movedPiece.calcLegalMoves(board);
            }
            // if we are clicking on a piece that is not ours e.g.
            return Collections.emptyList();
        }


        // TODO make easier?
        private Color assignSquareColour() {
            if (BoardUtilities.FIRST_ROW[this.squareID]
                || BoardUtilities.THIRD_ROW[this.squareID]
                || BoardUtilities.FIFTH_ROW[this.squareID]
                || BoardUtilities.SEVENTH_ROW[this.squareID]) {
                return (this.squareID%2 == 0 ? lightColour : darkColour);
            } else if (BoardUtilities.SECOND_ROW[this.squareID]
                    || BoardUtilities.FOURTH_ROW[this.squareID]
                    || BoardUtilities.SIXTH_ROW[this.squareID]
                    || BoardUtilities.EIGHTH_ROW[this.squareID]) {
                return (this.squareID%2 != 0 ? lightColour : darkColour);
            }
            throw new RuntimeException("what");
        }

        public void drawSquare(final Board board) {
            setBackground(assignSquareColour());
            assignSquareIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }
    }
}
