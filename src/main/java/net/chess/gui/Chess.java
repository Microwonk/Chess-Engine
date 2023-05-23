package net.chess.gui;

import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.pieces.*;
import net.chess.engine.board.MoveTransition.MoveStatus;
import net.chess.gui.audio.AudioHandler;
import net.chess.gui.util.Variables;
import net.chess.parsing.FenParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static net.chess.engine.board.Move.PawnPromotion;
import static net.chess.gui.util.Variables.*;

/**
 * Container for all the GUI Content Management and Connection of all the Components -> Singleton type
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Chess {

    private final JFrame frame;
    private final ChessBoard chessBoard;
    private final TakenPieces takenPieces;
    private final Logger logger;
    private final RecentGamesPlayed leftPanel;
    private Board board;
    private final MoveLog moveLog;
    private final GameDialog gameDialog;
    private SettingsDialog settingsDialog;
    private final static Dimension FRAME_DIMENSION = new Dimension(800, 640);
    private final Dimension CHESS_BOARD_DIMENSION = new Dimension(400, 400);
    private final Dimension SQUARE_DIMENSION = new Dimension(50, 50);

    private final ArrayList <String> positionLog; // for repetition
    // for moving with mouse clicking

    private BoardDirection boardDirection;

    // instantiating the Singleton
    protected static final Chess CHESS = new Chess();

    private Chess () {
        this.frame = new JFrame("森 Mori Chess ~dev");
        this.frame.setMinimumSize(FRAME_DIMENSION);
        this.board = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.positionLog = new ArrayList <>();
        this.moveLog = new MoveLog();
        this.gameDialog = new GameDialog(this.frame);
        this.logger = new Logger();
        this.leftPanel = new RecentGamesPlayed();

        // TODO: make user choose own art
        this.frame.setLayout(new BorderLayout());
        this.frame.setFont(new Font("Minecraft", Font.BOLD, 13));
        this.chessBoard = new ChessBoard();
        this.frame.add(this.chessBoard, BorderLayout.CENTER);
        this.takenPieces = new TakenPieces();
        this.frame.add(this.takenPieces, BorderLayout.SOUTH);
        this.frame.add(logger, BorderLayout.EAST);
        this.frame.add(leftPanel, BorderLayout.WEST);
        this.frame.setJMenuBar(makeMenuBar());
        this.frame.setSize(FRAME_DIMENSION);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);

        // this is for responsive design, now the chessboard will always stay square
        this.frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth() - (chessBoard.getPreferredSize().width +14) + (chessBoard.getPreferredSize().height - chessBoard.getHeight());
                int height = frame.getHeight();
                logger.setPreferredSize(new Dimension(width / 2, height));
                leftPanel.setPreferredSize(new Dimension(width/2, height));
                logger.revalidate();
                leftPanel.revalidate();
            }
        });

        this.frame.addKeyListener(new HotKeys());
        this.frame.setVisible(true);
        Variables.init();
    }

    public void update() {
        chessBoard.drawBoard(board);
    }

    public static Chess get () {
        return CHESS;
    }

    private GameDialog getGame () {
        return this.gameDialog;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Board getGameBoard () {
        return this.board;
    }

    public List <String> getPositionLog () {
        return positionLog;
    }

    public JFrame getFrame () {
        return this.frame;
    }

    public void show () {
        Chess.get().getMoveLog().clear();
        Chess.get().getTakenPieces().refresh(Chess.get().getMoveLog());
        Chess.get().getChessBoard().drawBoard(Chess.get().getGameBoard());
    }

    public void draw() {
        takenPieces.refresh(CHESS.getMoveLog());
        chessBoard.drawBoard(board);
    }

    /**
     * @return GUI MenuBar
     */
    private JMenuBar makeMenuBar () {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.add(createFileMenu());
        menuBar.add(createSettingsMenu());
        menuBar.add(createOptionsMenu());
        return menuBar;
    }

    /**
     * @return GUI FileMenu
     */
    private JMenu createFileMenu () {
        final JMenu fileMenu = new JMenu("File");
        // implementing PGN files for game loading and saving

        final JMenuItem openFen = new JMenuItem("Load Fen");
        openFen.setFont(frame.getFont());
        openFen.addActionListener(e -> loadGame());

        final JMenuItem saveToFen = new JMenuItem("Save to Fen");
        saveToFen.setFont(frame.getFont());
        saveToFen.addActionListener(e -> saveGame(FenParser.parseFen(this.board)));

        final JMenuItem exitFrame = new JMenuItem("Exit");
        exitFrame.setFont(frame.getFont());
        exitFrame.addActionListener(e -> this.frame.dispose());

        final JMenuItem newFrame = new JMenuItem("Reset");
        newFrame.setFont(frame.getFont());
        newFrame.addActionListener(e -> reset());

        fileMenu.add(openFen);
        fileMenu.add(saveToFen);
        fileMenu.add(exitFrame);
        fileMenu.add(newFrame);
        fileMenu.setFont(frame.getFont());
        return fileMenu;
    }

    /**
     * load from .fen and later from .pgn or db
     */
    protected void loadGame () {
        final JFileChooser fileChooser = new JFileChooser();
        final File selectedFile;
        fileChooser.setCurrentDirectory(new File(savePath + "."));
        fileChooser.setFileFilter(new FileNameExtensionFilter("FEN-file", "*.fen"));
        int result = fileChooser.showOpenDialog(this.frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            logger.printLog("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            return;
        }

        try {
            final BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            this.board = FenParser.createGameFromFen(reader.readLine());
            Variables.store("savePath", selectedFile.getAbsolutePath());
            reader.close();

            this.chessBoard.drawBoard(this.board);
        } catch (Exception e) {
            logger.printLog(String.valueOf(e));
        }
    }

    /**
     * @param toSave Fen String or PGN Format Moves
     */
    protected void saveGame (final String toSave) {
        final JFileChooser fileChooser = new JFileChooser();
        File selectedFile;
        fileChooser.setCurrentDirectory(new File(savePath));
        fileChooser.setSelectedFile(new File(savePath +
                DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
                        .format(LocalDateTime.now()) + ".fen"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter
                ("FEN-file, PGN-file", "*.fen", "*.pgn"));

        int result = fileChooser.showSaveDialog(this.frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            logger.printLog("Selected file: " + selectedFile.getPath());
        } else {
            return;
        }
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(selectedFile));
            bw.write(toSave);
            Variables.store("savePath", selectedFile.getPath());
            bw.close();
        } catch (Exception e) {
            logger.printLog(String.valueOf(e));
        }

    }

    /**
     * @return GUI SettingsMenu
     */
    private JMenu createSettingsMenu () {
        final JMenu settingsMenu = new JMenu("Settings");
        final JMenuItem more = new JMenuItem("More...");
        more.setFont(frame.getFont());
        more.addActionListener(e -> {
            if (this.settingsDialog == null) {
                this.settingsDialog = new SettingsDialog(this.frame);
            }
            this.settingsDialog.setLocationRelativeTo(this.frame);
            this.settingsDialog.setVisible(true);
        });

        final JMenuItem flip = new JMenuItem("Flip Board");
        flip.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            chessBoard.drawBoard(board);
        });
        flip.setFont(frame.getFont());
        settingsMenu.add(flip);
        settingsMenu.addSeparator();

        final JCheckBoxMenuItem legalMovesToggle = new JCheckBoxMenuItem("Highlight Moves");
        legalMovesToggle.setSelected(highlightLegalMovesActive);
        legalMovesToggle.setFont(frame.getFont());
        legalMovesToggle.addActionListener(e -> {
            highlightLegalMovesActive = legalMovesToggle.isSelected();
            Variables.store("highlightLegalMovesActive", highlightLegalMovesActive ? "true" : "false");
            chessBoard.drawBoard(board);
        });

        final JCheckBoxMenuItem checksToggle = new JCheckBoxMenuItem("Signify Checks");
        checksToggle.setSelected(signifyChecksActive);
        checksToggle.setFont(frame.getFont());
        checksToggle.addActionListener(e -> {
            signifyChecksActive = checksToggle.isSelected();
            Variables.store("signifyChecksActive", signifyChecksActive ? "true" : "false");
            chessBoard.drawBoard(board);
        });

        final JCheckBoxMenuItem soundToggle = new JCheckBoxMenuItem("Toggle Sound");
        soundToggle.setSelected(soundOn);
        soundToggle.setFont(frame.getFont());
        soundToggle.addActionListener(e -> {
            soundOn = soundToggle.isSelected();
            Variables.store("soundOn", soundOn ? "true" : "false");
            chessBoard.drawBoard(board);
        });

        settingsMenu.add(more);
        settingsMenu.add(legalMovesToggle);
        settingsMenu.add(checksToggle);
        settingsMenu.add(soundToggle);
        settingsMenu.setFont(frame.getFont());
        return settingsMenu;
    }

    /**
     * @return GUI OptionsMenu
     */
    private JMenu createOptionsMenu () {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGame = new JMenuItem("Setup Game");
        setupGame.addActionListener(e -> {
            CHESS.getGame().promptUser(Chess.get().frame);
        });
        setupGame.setFont(frame.getFont());
        optionsMenu.add(setupGame);
        optionsMenu.setFont(frame.getFont());
        return optionsMenu;
    }

    public void setChessBoard (final Board board) {
        this.board = board;
    }


    ChessBoard getChessBoard () {
        return this.chessBoard;
    }

    TakenPieces getTakenPieces () {
        return this.takenPieces;
    }

    public MoveLog getMoveLog () {
        return this.moveLog;
    }

    /**
     * removes all the moves and creates new Standard Board
     */
    protected void reset () {
        if (moveLog.getMoves().isEmpty()) {
            return;
        }
        this.board = Board.createStandardBoard();
        this.moveLog.clear();
        this.positionLog.clear();
        this.takenPieces.refresh(this.moveLog);
        chessBoard.drawBoard(board);
        AudioHandler.playSound(2);
    }

    /**
     * visualizes the starting board with sound
     */
    protected void beginBoard () {
        if (this.moveLog.getMoves().isEmpty()) {
            return;
        }
        movingEnabled = false;
        currentMove = 0;
        chessBoard.drawBoard(this.moveLog.getMoves().get(0).getBoard());
        AudioHandler.playSound(0);
    }

    /**
     * visualizes the end board with sound
     */
    protected void endBoard () {
        movingEnabled = true;
        if (currentMove == this.moveLog.size() - 1
                || this.moveLog.getMoves().isEmpty()) {
            return;
        }
        currentMove = this.moveLog.size() - 1;
        chessBoard.drawBoard(this.board);
        AudioHandler.playSound(0);
    }

    /**
     * visualizes the previous move with sound
     */
    protected void prevMove () {
        if (currentMove > 0 && this.moveLog != null && this.moveLog.size() > 0) {
            currentMove--;
            chessBoard.drawBoard(this.moveLog.getMoves().get(currentMove).getBoard());
            movingEnabled = false;

            if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                AudioHandler.playSound(1);
            } else {
                AudioHandler.playSound(0);
            }

        } else {
            movingEnabled = true;
        }
    }

    /**
     * visualizes the next move with sound
     */
    protected void nextMove () {
        if (currentMove < moveLog.size()) {
            if (currentMove == moveLog.size() - 1) {
                if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                    AudioHandler.playSound(1);
                } else AudioHandler.playSound(0);
                currentMove++;
                movingEnabled = true;
                chessBoard.drawBoard(this.board);
            } else {
                if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                    AudioHandler.playSound(1);
                } else AudioHandler.playSound(0);
                currentMove++;
                chessBoard.drawBoard(this.moveLog.getMoves().get(currentMove).getBoard());
                movingEnabled = false;
            }

        }
    }

    /**
     * @return if there is a lack of material to mate someone
     */
    public boolean isDrawByLackOfMaterial () {
        if (this.board.getBlackPieces().size() <= 2 && this.board.getWhitePieces().size() <= 2) {
            if ((this.board.getBlackPieces().stream().anyMatch(piece -> piece instanceof Bishop || piece instanceof Knight) || this.board.getBlackPieces().size() == 1)
                    && (this.board.getWhitePieces().stream().anyMatch(piece -> piece instanceof Bishop || piece instanceof Knight) || this.board.getWhitePieces().size() == 1)) {
                movingEnabled = false;
                return true;
            } else if (this.board.getBlackPieces().size() == 1 && this.board.getWhitePieces().size() == 1) {
                movingEnabled = false;
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if it is a move resulting in a draw
     */
    public boolean isDrawByRepetition () {
        return positionLog.stream().collect(Collectors.groupingBy(c -> c, Collectors.counting())).containsValue(3L);
    }

    /**
     * Board Direction in accordance to being white or black player
     */
    public enum BoardDirection {
        NORMAL {
            @Override
            List <SquarePanel> traverse (List <SquarePanel> squares) {
                return squares;
            }

            @Override
            BoardDirection opposite () {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List <SquarePanel> traverse (List <SquarePanel> squares) {
                final List <SquarePanel> viewOnly = new ArrayList <>(squares);
                Collections.reverse(viewOnly);
                return Collections.unmodifiableList(viewOnly);
            }

            @Override
            BoardDirection opposite () {
                return NORMAL;
            }
        };

        abstract List <SquarePanel> traverse (final List <SquarePanel> squares);

        abstract BoardDirection opposite ();
    }

    /**
     * 8x8 JPanel filled with Squares
     */
    private class ChessBoard extends JPanel {

        final List <SquarePanel> boardSquares;

        ChessBoard () {
            super(new GridLayout(8, 8));
            this.boardSquares = new ArrayList <>();

            for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
                final SquarePanel square = new SquarePanel(i);
                this.boardSquares.add(square);
                this.add(square);
            }
            setPreferredSize(CHESS_BOARD_DIMENSION);
            setMinimumSize(new Dimension(400, 400));
            validate();
        }

        /**
         * @param board the Board being drawn to refresh the view
         */
        public void drawBoard (final Board board) {
            removeAll();
            for (final SquarePanel square : boardDirection.traverse(boardSquares)) {
                square.drawSquare(board);
                add(square);
            }
            validate();
            repaint();
        }
    }

    /**
     * ArrayList of all Moves
     */
    public static class MoveLog extends ArrayList<Move> {

        public List <Move> getMoves () {
            return this;
        }

        public void addMove (final Move move) {
            this.add(move);
        }
    }

    /**
     * Computer or Human Player enum
     */
    protected enum PlayerType {
        HUMAN,
        COMPUTER
    }

    /**
     * A single Square on the Board
     */
    private class SquarePanel extends JPanel {

        private final int squareID;

        SquarePanel (final int squareID) {
            super(new GridBagLayout());
            this.squareID = squareID;
            this.setPreferredSize(SQUARE_DIMENSION);
            this.setBackground(assignSquareColour());
            try {
                this.assignSquareIcon(board);
            } catch (Exception e) {
                logger.printLog(e.toString());
            }

            addMouseListener(new Controller(squareID));
            validate();
        }
        /**
         * @param board to get the Piece that is on the Square to generate an image
         */
        private void assignSquareIcon (final Board board) {
            this.removeAll();
            if (board.getSquare(this.squareID).isOccupied()) {
                add(new JLabel(artPack.load(board.getSquare(this.squareID)
                        .getPiece().getPieceTeam().toString()
                        .charAt(0) + board.getSquare(this.squareID)
                        .getPiece().toString())));
            }
        }

        /**
         * @param board to highlight all the legal moves on the board
         */
        private void highlightLegalMoves (final Board board) throws Exception {
            if (highlightLegalMovesActive) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() != squareID) {
                        continue;
                    }
                    add(new JLabel(new ImageIcon(ImageIO.read(new File(miscPath + "highlighting.png"))
                            .getScaledInstance(25, 25, 0))));
                }
            }
        }

        /**
         * @param board to filter out all the legal moves on the board, filtering out moves that leave in check and duplicate moves
         * @return all visually important legal moves
         */
        private Collection <Move> pieceLegalMoves (final Board board) {
            if (movedPiece != null && movedPiece.getPieceTeam() == board.currentPlayer().getTeam()) {
                return movedPiece.calcLegalMoves(board).stream()
                        .filter(move -> board.currentPlayer().makeMove(move).getMoveStatus() == MoveStatus.DONE)
                        .filter(move -> !(move instanceof PawnPromotion) || ((PawnPromotion) move).getPromotedToPiece() instanceof Queen)
                        .collect(Collectors.toList());
            }
            // if we are clicking on a piece that is not ours
            return Collections.emptyList();
        }

        /**
         * @param board passed in Board to check for mate, checks and stalemate
         */
        private void signifyCheck (final Board board) {
            if (!signifyChecksActive || moveLog.size() < 1) return;

            Color red = new Color(152, 40, 0);
            // note: this code is written this way because otherwise there would be method overloading, which makes it throw a no King error.:(
            if (board.blackPlayer().isInCheck()) {
                if (!board.blackPlayer().isInCheckmate()) {
                    red = red.brighter();
                }
                if (board.blackPlayer().getPlayerKing().getPosition() == this.squareID) {
                    this.setBackground(red);
                }
            } else if (board.whitePlayer().isInCheck()) {
                if (!board.whitePlayer().isInCheckmate()) {
                    red = red.brighter();
                }
                if (board.whitePlayer().getPlayerKing().getPosition() == this.squareID) {
                    this.setBackground(red);
                }
            } else if (board.blackPlayer().isInStalemate()
                    && board.blackPlayer().getPlayerKing().getPosition() == this.squareID) {
                this.setBackground(Color.GRAY);

            } else if (board.whitePlayer().isInStalemate()
                    && board.whitePlayer().getPlayerKing().getPosition() == this.squareID) {
                this.setBackground(Color.GRAY);

            } else if (isDrawByLackOfMaterial() || isDrawByRepetition()
                    && (board.whitePlayer().getPlayerKing().getPosition() == this.squareID
                    || board.blackPlayer().getPlayerKing().getPosition() == this.squareID)) {
                    this.setBackground(Color.GRAY);
                }
            }


        /**
         * @return the Color of the Square
         */
        private Color assignSquareColour () {
            return BoardUtilities.FIRST_ROW[this.squareID]
                    || BoardUtilities.THIRD_ROW[this.squareID]
                    || BoardUtilities.FIFTH_ROW[this.squareID]
                    || BoardUtilities.SEVENTH_ROW[this.squareID]
                    ? (this.squareID % 2 == 0 ? colorPack.dark() : colorPack.light())
                    : (this.squareID % 2 != 0 ? colorPack.dark() : colorPack.light());
        }

        /**
         * @param board to pass in all of the above methods -> draws one single Square
         */
        public void drawSquare (final Board board) {
            setBackground(assignSquareColour());
            try {
                assignSquareIcon(board);
                highlightLegalMoves(board);
            } catch (Exception e) {
                logger.printLog(e.toString());
            }
            signifyCheck(board);
            validate();
            repaint();
        }
    }
}