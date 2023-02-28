package net.chess.gui;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;
import net.chess.engine.pieces.Piece;
import net.chess.engine.player.MoveStatus;
import net.chess.engine.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.swing.SwingUtilities.isLeftMouseButton;

public class GUI_Contents {

    private final JFrame frame;
    private final ChessBoard chessBoard;
    private final TakenPieces takenPieces;
    private Board board;
    private final MoveLog moveLog;
    private final AudioHandler audioHandler;

    private Square sourceSquare;
    private Square destinationSquare;
    private Piece movedPiece;
    private BoardDirection boardDirection;
    private int currentMove;
    private boolean movingEnabled;

    private final static Dimension FRAME_DIMENSION = new Dimension(600, 700);
    private final Dimension CHESS_BOARD_DIMENSION = new Dimension(400, 400);
    private final Dimension SQUARE_DIMENSION = new Dimension(10, 10);
    private boolean highlightLegalMovesActive;
    public final static String path = "assets/pieces/pixel_art/";
    public final static String misc = "assets/misc/";

    protected final static Color lightColour = new Color(196, 189, 175);
    protected final static Color darkColour = new Color(155, 132, 75);

    public GUI_Contents() {
        this.board = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMovesActive = true; // can turn off if needed
        this.moveLog = new MoveLog();
        this.audioHandler = new AudioHandler();
        this.currentMove = 0;
        this.movingEnabled = true;
        // TODO: make user choose own art

        this.frame = new JFrame("Chess by Nicolas Frey");
        this.frame.setLayout(new BorderLayout());
        this.frame.setFont(new Font("Minecraft", Font.BOLD, 13));

        this.chessBoard = new ChessBoard();
        this.frame.add(this.chessBoard, BorderLayout.CENTER);

        this.takenPieces = new TakenPieces();
        this.frame.add(this.takenPieces, BorderLayout.SOUTH);

        this.frame.setJMenuBar(makeMenuBar());
        this.frame.addKeyListener(addHotKeys());
        this.frame.setIconImage(new ImageIcon(path + "WR.png").getImage());
        this.frame.setSize(FRAME_DIMENSION);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setVisible(true);
    }

    // adds hot keys, can be made customizable in the future
    private KeyListener addHotKeys() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // System.out.println(e.getKeyCode()); // for finding out the hot keys KeyCode
                switch (e.getKeyCode()) {
                    case 37 ->  prevMove(); // left arrow
                    case 39 -> nextMove(); // right arrow
                    case 38 -> endBoard(); // up arrow
                    case 40 -> beginBoard(); // down arrow
                    case 82 -> reset(); // r key
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
    }

    private JMenuBar makeMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.add(createFileMenu());
        menuBar.add(createSettingsMenu());
        menuBar.add(createDebugMenu());
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

        final JMenuItem newFrame = new JMenuItem("Reset");
        newFrame.setFont(frame.getFont());
        newFrame.addActionListener(e -> reset());

        fileMenu.add(openPGN);
        fileMenu.add(exitFrame);
        fileMenu.add(newFrame);
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
        highlightingLegalMovesToggle.setSelected(true);
        highlightingLegalMovesToggle.setFont(frame.getFont());
        highlightingLegalMovesToggle.addActionListener(e -> {
            highlightLegalMovesActive = highlightingLegalMovesToggle.isSelected();
            chessBoard.drawBoard(board);
        });
        settingsMenu.add(highlightingLegalMovesToggle);
        settingsMenu.setFont(frame.getFont());
        return settingsMenu;
    }

    private JMenu createDebugMenu() {
        final JMenu debugMenu = new JMenu("Debug");
        debugMenu.setFont(frame.getFont());

        final JMenuItem before = new JMenuItem("<<");
        before.addActionListener(e -> prevMove());
        before.setFont(frame.getFont());

        final JMenuItem after = new JMenuItem(">>");
        after.addActionListener(e -> nextMove());
        after.setFont(frame.getFont());

        final JMenuItem currentBoard = new JMenuItem("Current Playing Board");
        currentBoard.addActionListener(e -> endBoard());
        currentBoard.setFont(frame.getFont());

        final JMenuItem beginningBoard = new JMenuItem("Back to Beginning");
        beginningBoard.addActionListener(e -> beginBoard());
        beginningBoard.setFont(frame.getFont());

        debugMenu.add(before);
        debugMenu.add(after);
        debugMenu.add(currentBoard);
        debugMenu.add(beginningBoard);

        return debugMenu;
    }

    // resets everything
    private void reset() {
        if (moveLog.getMoves().isEmpty()) {
            return;
        }
        this.board = Board.createStandardBoard();
        this.moveLog.clear();
        this.takenPieces.redo(this.moveLog);
        chessBoard.drawBoard(board);
        audioHandler.playSound(2);
    }

    // visualizes the starting board with sound
    private void beginBoard() {
        if (this.moveLog.getMoves().isEmpty()) {
            return;
        }
        this.movingEnabled = false;
        currentMove = 0;
        chessBoard.drawBoard(this.moveLog.getMoves().get(0).getBoard());
        audioHandler.playSound(0);
    }

    // visualizes the end board with sound
    private void endBoard() {
        this.movingEnabled = true;
        if (currentMove == this.moveLog.size() - 1
                || this.moveLog.getMoves().isEmpty()) {
            return;
        }
        currentMove = this.moveLog.size() - 1;
        chessBoard.drawBoard(this.board);
        audioHandler.playSound(0);
    }

    // visualizes the previous move with sound
    private void prevMove() {
        if (currentMove > 0) {
            currentMove--;
            chessBoard.drawBoard(this.moveLog.getMoves().get(currentMove).getBoard());
            this.movingEnabled = false;
            if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                audioHandler.playSound(1);
            } else audioHandler.playSound(0);
        }
    }

    // visualizes the next move with sound
    private void nextMove() {
        if (currentMove < moveLog.size()) {
            if (currentMove == moveLog.size() - 1) {
                if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                    audioHandler.playSound(1);
                } else audioHandler.playSound(0);
                currentMove++;
                this.movingEnabled = true;
                chessBoard.drawBoard(this.board);
            } else {
                if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                    audioHandler.playSound(1);
                } else audioHandler.playSound(0);
                currentMove++;
                chessBoard.drawBoard(this.moveLog.getMoves().get(currentMove).getBoard());
                this.movingEnabled = false;
            }

        }
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

    //TODO cleanup
    public static class MoveLog {
        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(final int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
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
                    if (!movingEnabled) {
                        return;
                    }

                    if (isLeftMouseButton(e)) {
                        // first Click
                        if (sourceSquare == null) {
                            sourceSquare = board.getSquare(squareID); // <- solution without using pixel measurements!
                            movedPiece = sourceSquare.getPiece();

                            // if clicked on empty square, do nothing
                            if (movedPiece == null) {
                                sourceSquare = null;
                            } else if (movedPiece.getPieceTeam() != board.currentPlayer().getTeam()) {
                                sourceSquare = null;
                            }
                        }

                        else { // second Click
                            destinationSquare = board.getSquare(squareID);

                            // if same square is clicked, reset
                            if (sourceSquare == destinationSquare) {
                                sourceSquare = null;
                                destinationSquare = null;
                                movedPiece = null;
                                SwingUtilities.invokeLater(() -> chessBoard.drawBoard(board));
                                return;
                            }

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

                            if (destinationSquare.equals(sourceSquare)) {
                                sourceSquare = null;
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
                                if (move.isAttack()) {
                                    audioHandler.playSound(1);
                                } else if (transition.getTransitionBoard().blackPlayer().isInCheckMate()
                                        || transition.getTransitionBoard().whitePlayer().isInCheckMate()) {
                                    audioHandler.playSound(2);
                                } else {
                                    audioHandler.playSound(0);
                                }
                                currentMove++;
                                board = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceSquare = null;
                            destinationSquare = null;
                            movedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                            takenPieces.redo(moveLog);
                            chessBoard.drawBoard(board);
                        });
                    } else if (isLeftMouseButton(e)) {
                        sourceSquare = null;
                        destinationSquare = null;
                        movedPiece = null;
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
                return movedPiece.calcLegalMoves(board).stream().filter
                                (move -> board.currentPlayer().makeMove(move).getMoveStatus() == MoveStatus.DONE)
                        .collect(Collectors.toList());
            }
            // if we are clicking on a piece that is not ours e.g.
            return Collections.emptyList();
        }

        private void signifyCheck(final Board board) {
            Color red = new Color(152, 40, 0);
            /*if (board.blackPlayer().isInStalemate()) {
                if (board.blackPlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                    return;
                }
            }
            if (board.whitePlayer().isInStalemate()) {
                if (board.whitePlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                    return;
                }
            }*/
            if (board.blackPlayer().isInCheck()) {
                if (!board.blackPlayer().isInCheckMate()) {
                    red = red.brighter();
                }
                if (board.blackPlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(red);
                    return;
                }
            }
            if (board.whitePlayer().isInCheck()) {
                if (!board.whitePlayer().isInCheckMate()) {
                    red = red.brighter();
                }
                if (board.whitePlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(red);
                }
            }
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
            signifyCheck(board);
            validate();
            repaint();
        }
    }

    private static class TakenPieces extends JPanel {

        private final JPanel WEST;
        private final JPanel EAST;
        private static final Dimension DIMENSION = new Dimension(600, 70);

        public TakenPieces() {
            super(new BorderLayout());
            JPanel bottom = new JPanel();
            bottom.setPreferredSize(new Dimension(600, 5));
            this.setPreferredSize(DIMENSION);
            this.WEST = new JPanel(new GridLayout(2,8)); // 8 * 2 -> 16 pieces
            this.EAST = new JPanel(new GridLayout(2,8)); // 8 * 2 -> 16 pieces

            this.add(this.WEST, BorderLayout.WEST);
            this.add(this.EAST, BorderLayout.EAST);
            this.add(bottom, BorderLayout.SOUTH);
        }

        public void redo(final MoveLog moveLog) {
            this.EAST.removeAll();
            this.WEST.removeAll();

            final List<Piece> whiteTakenPieces = new ArrayList<>();
            final List<Piece> blackTakenPieces = new ArrayList<>();

            moveLog.getMoves().forEach(move -> {
                if (move.isAttack()) {
                    final Piece takenPiece = move.getAttackedPiece();
                    if (takenPiece.getPieceTeam().isWhite()) {
                        whiteTakenPieces.add(takenPiece);
                    } else {
                        blackTakenPieces.add(takenPiece);
                    }
                }
            });

            whiteTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));
            blackTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));

            for (final Piece takenPiece: whiteTakenPieces) {
                try {
                    final BufferedImage image = ImageIO.read(new File(path
                            + takenPiece.getPieceTeam().toString() .charAt(0)
                            + takenPiece.getPieceType().toString().charAt(0) + ".png"));

                    final ImageIcon icon = new ImageIcon(image);
                    final JLabel imageLabel = new JLabel(icon);
                    this.EAST.add(imageLabel);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (final Piece takenPiece: blackTakenPieces) {
                try {
                    final BufferedImage image = ImageIO.read(new File(path
                            + takenPiece.getPieceTeam().toString().charAt(0)
                            + takenPiece.getPieceType().toString().charAt(0) + ".png"));
                    final ImageIcon icon = new ImageIcon(image);
                    final JLabel imageLabel = new JLabel(icon);
                    this.WEST.add(imageLabel);

                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            validate();
            repaint();
        }
    }
}
