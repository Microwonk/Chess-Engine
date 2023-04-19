package net.chess.gui;

import net.chess.ai.AI;
import net.chess.ai.AlphaBeta.AlphaBeta;
import net.chess.ai.AlphaBeta.AlphaBetaMultiThreaded;
import net.chess.engine.board.Board;
import net.chess.engine.board.BoardUtilities;
import net.chess.engine.board.Move;
import net.chess.engine.board.Square;
import net.chess.engine.pieces.*;
import net.chess.engine.player.MoveTransition.MoveStatus;
import net.chess.engine.player.MoveTransition;
import net.chess.gui.audio.AudioHandler;
import net.chess.gui.util.Properties;
import net.chess.parsing.FenParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Flow.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
import static net.chess.engine.board.Move.PawnMove;
import static net.chess.engine.board.Move.PawnPromotion;
import static net.chess.engine.pieces.Piece.PieceType;
import static net.chess.gui.util.Properties.*;

/**
 * Container for all the GUI Content Management and Connection of all the Components -> Singleton type
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class Chess implements Publisher <Object> {

    private final JFrame frame;
    private final ChessBoard chessBoard;
    private final TakenPieces takenPieces;
    private final Logger logger;
    private Board board;
    private final MoveLog moveLog;
    private final GameDialog gameDialog;
    private SettingsDialog settingsDialog;
    private final static Dimension FRAME_DIMENSION = new Dimension(800, 640);
    private final Dimension CHESS_BOARD_DIMENSION = new Dimension(400, 400);
    private final Dimension SQUARE_DIMENSION = new Dimension(50, 50);

    private Move computerMove; // not needed until later
    private final SubmissionPublisher <Object> publisher;
    private final ArrayList <String> positionLog; // for repetition
    // for moving with mouse clicking
    private Square sourceSquare;
    private Square destinationSquare;
    private Piece movedPiece;

    private BoardDirection boardDirection;
    private int currentMove;
    private boolean movingEnabled;


    // instantiating the Singleton
    private static final Chess INSTANCE = new Chess();

    private Chess () {
        this.frame = new JFrame("Chess by Nicolas Frey");
        this.frame.setMinimumSize(FRAME_DIMENSION);
        this.board = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.positionLog = new ArrayList <>();
        this.moveLog = new MoveLog();
        this.publisher = new SubmissionPublisher <>();
        this.addSubscriber(new GameSubscriber());
        this.gameDialog = new GameDialog(this.frame);
        this.logger = new Logger();
        this.currentMove = 0;
        this.movingEnabled = true;

        // TODO: make user choose own art
        this.frame.setLayout(new BorderLayout());
        this.frame.setFont(new Font("Minecraft", Font.BOLD, 13));
        this.chessBoard = new ChessBoard();
        this.frame.add(this.chessBoard, BorderLayout.CENTER);
        this.takenPieces = new TakenPieces();
        this.frame.add(this.takenPieces, BorderLayout.SOUTH);
        this.frame.add(logger, BorderLayout.EAST);
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
                logger.setPreferredSize(new Dimension(width, height));
                Font f = logger.getTextArea().getFont();
                logger.getTextArea().setFont(new Font(f.getFontName(), f.getStyle(), (logger.getWidth()/18)));
                logger.revalidate();
            }
        });

        this.frame.addKeyListener(addHotKeys());
        this.frame.setVisible(true);
        Properties.init();
    }

    public void addSubscriber (Subscriber <? super Object> subscriber) {
        publisher.subscribe(subscriber);
    }

    private void notifySubscribers (Object obj) {
        publisher.submit(obj);
    }

    public static Chess get () {
        return INSTANCE;
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

    public void show () {
        Chess.get().getMoveLog().clear();
        Chess.get().getTakenPieces().refresh(Chess.get().getMoveLog());
        Chess.get().getChessBoard().drawBoard(Chess.get().getGameBoard());
    }

    /**
     * @return the Configuration of the Hotkeys -> can be made into global variables so that user can customize
     */
    private KeyListener addHotKeys () {
        return new KeyListener() {
            @Override
            public void keyTyped (KeyEvent e) {}

            @Override
            public void keyPressed (KeyEvent e) {
                //GUI_Contents.get().logger.printLog("Key Pressed: " + e.getKeyCode()); // for finding out the hot keys KeyCode
                switch (e.getKeyCode()) {
                    case 37 -> prevMove(); // left arrow
                    case 39 -> nextMove(); // right arrow
                    case 38 -> endBoard(); // up arrow
                    case 40 -> beginBoard(); // down arrow
                    case 82 -> reset(); // r key
                }
            }

            @Override
            public void keyReleased (KeyEvent e) {}
        };
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
    private void loadGame() {
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
            Properties.store("savePath", selectedFile.getAbsolutePath());
            reader.close();

            this.chessBoard.drawBoard(this.board);
        } catch (Exception e) {
            logger.printLog(String.valueOf(e));
        }
    }

    /**
     * @param toSave Fen String or PGN Format Moves
     */
    private void saveGame (final String toSave) {
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
            Properties.store("savePath", selectedFile.getPath());
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
            Properties.store("highlightLegalMovesActive", highlightLegalMovesActive ? "true" : "false");
            chessBoard.drawBoard(board);
        });

        final JCheckBoxMenuItem checksToggle = new JCheckBoxMenuItem("Signify Checks");
        checksToggle.setSelected(signifyChecksActive);
        checksToggle.setFont(frame.getFont());
        checksToggle.addActionListener(e -> {
            signifyChecksActive = checksToggle.isSelected();
            Properties.store("signifyChecksActive", signifyChecksActive ? "true" : "false");
            chessBoard.drawBoard(board);
        });

        final JCheckBoxMenuItem soundToggle = new JCheckBoxMenuItem("Toggle Sound");
        soundToggle.setSelected(soundOn);
        soundToggle.setFont(frame.getFont());
        soundToggle.addActionListener(e -> {
            soundOn = soundToggle.isSelected();
            Properties.store("soundOn", soundOn ? "true" : "false");
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
            Chess.get().getGame().promptUser(Chess.get().frame);
            Chess.get().gameUpdate(Chess.get().getGame());
        });
        setupGame.setFont(frame.getFont());
        optionsMenu.add(setupGame);
        optionsMenu.setFont(frame.getFont());
        return optionsMenu;
    }

    /**
     * @param gameDialog to notify all Subscribers
     */
    private void gameUpdate (final GameDialog gameDialog) {
        notifySubscribers(gameDialog);
    }

    @Override
    public void subscribe (Subscriber <? super Object> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    /**
     * Game Subscriber
     */
    private static class GameSubscriber implements Subscriber <Object> {

        private Subscription subscription;

        @Override
        public void onSubscribe (Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1); // Request initial batch of 1 event
        }

        @Override
        public void onNext (Object item) {
            handleGameEvent(item);
            subscription.request(1); // Request next batch of 1 event
        }

        private void handleGameEvent (Object ignored) {
            if (Chess.get().getGame().isAIPlayer(Chess.get().getGameBoard().currentPlayer())
                    && !Chess.get().getGameBoard().currentPlayer().isInCheckmate()
                    && !Chess.get().isDrawByLackOfMaterial()
                    && !Chess.get().isDrawByRepetition()) {
                // execute the AI
                final backGroundThreadForAI thread = new backGroundThreadForAI();
                thread.execute();

                if (Chess.get().getGameBoard().currentPlayer().isInCheckmate()) {
                    Chess.get().logger.printLog("game over, " + Chess.get().getGameBoard().currentPlayer() + " is in checkmate!");
                }

                if (Chess.get().getGameBoard().currentPlayer().isInStalemate()) {
                    Chess.get().logger.printLog("game over, " + Chess.get().getGameBoard().currentPlayer() + " is in stalemate!");
                }
            }
        }

        @Override
        public void onError (Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete () {
            Chess.get().logger.printLog("Game over!");
        }
    }

    /**
     * SwingWorker using threads to do the work in the background instead of the thread of the gui
     */
    private static class backGroundThreadForAI extends SwingWorker <Move, String> {
        private backGroundThreadForAI () {}

        // what should be done in the background thread
        @Override
        protected Move doInBackground () {
            //final AI miniMax = new Minimax(GUI_Contents.get().gameDialog.getSearchDepth());
            final AI alphaBeta = new AlphaBetaMultiThreaded(Chess.get().gameDialog.getSearchDepth());
            final Move move = alphaBeta.execute(Chess.get().getGameBoard());
            if (move.isAttack()) {
                AudioHandler.playSound(1);
            } else {
                AudioHandler.playSound(0);
            }
            return move;
        }

        @Override
        protected void done () {
            try {
                final Move executedMove = get();
                Chess.get().updateComputerMove(executedMove);
                Chess.get().updateGameBoard(Chess.get().getGameBoard().currentPlayer().makeMove(executedMove).getTransitionBoard());
                Chess.get().getMoveLog().addMove(executedMove);
                Chess.get().getPositionLog().add(FenParser.parseFen(executedMove.getBoard()));
                Chess.get().getTakenPieces().refresh(Chess.get().getMoveLog());
                Chess.get().getChessBoard().drawBoard(Chess.get().getGameBoard());
                Chess.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (Exception e) {
                Chess.get().logger.printLog("Execution failed...");
            }
            super.done();
        }
    }

    /**
     * @param playerType to notify the subscriber of the new Player-type
     */
    private void moveMadeUpdate (final PlayerType playerType) {
        notifySubscribers(playerType);
    }

    ChessBoard getChessBoard () {
        return this.chessBoard;
    }

    private TakenPieces getTakenPieces () {
        return this.takenPieces;
    }

    private MoveLog getMoveLog () {
        return this.moveLog;
    }

    private ArrayList <String> getPositionLog () {
        return this.positionLog;
    }

    /**
     * @param executedMove to update the current Computer Move and keep track of it
     */
    public void updateComputerMove (final Move executedMove) {
        this.computerMove = executedMove;
    }

    /**
     * @param board Board to update Board outside of Singleton
     */
    public void updateGameBoard (final Board board) {
        this.board = board;
    }

    /**
     * removes all the moves and creates new Standard Board
     */
    private void reset () {
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
    private void beginBoard () {
        if (this.moveLog.getMoves().isEmpty()) {
            return;
        }
        this.movingEnabled = false;
        currentMove = 0;
        chessBoard.drawBoard(this.moveLog.getMoves().get(0).getBoard());
        AudioHandler.playSound(0);
    }

    /**
     * visualizes the end board with sound
     */
    private void endBoard () {
        this.movingEnabled = true;
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
    private void prevMove () {
        if (currentMove > 0 && this.moveLog != null && this.moveLog.size() > 0) {
            currentMove--;
            chessBoard.drawBoard(this.moveLog.getMoves().get(currentMove).getBoard());
            this.movingEnabled = false;

            if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                AudioHandler.playSound(1);
            } else {
                AudioHandler.playSound(0);
            }

        } else {
            this.movingEnabled = true;
        }
    }

    private void playMoveLog(MoveLog moveLog) {
        this.board = Board.createStandardBoard();
        chessBoard.drawBoard(board);
        moveLog.forEach(m -> {
            this.board = board.getCurrentPlayer().makeMove(m).getTransitionBoard();
            Thread t = new Thread(() -> {
                SwingUtilities.invokeLater(() -> chessBoard.drawBoard(board));
            });
            t.start();
            System.out.println("Hallo");
        });
    }

    /**
     * visualizes the next move with sound
     */
    private void nextMove () {
        if (currentMove < moveLog.size()) {
            if (currentMove == moveLog.size() - 1) {
                if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                    AudioHandler.playSound(1);
                } else AudioHandler.playSound(0);
                currentMove++;
                this.movingEnabled = true;
                chessBoard.drawBoard(this.board);
            } else {
                if (this.moveLog.getMoves().get(currentMove).isAttack()) {
                    AudioHandler.playSound(1);
                } else AudioHandler.playSound(0);
                currentMove++;
                chessBoard.drawBoard(this.moveLog.getMoves().get(currentMove).getBoard());
                this.movingEnabled = false;
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
                this.movingEnabled = false;
                return true;
            } else if (this.board.getBlackPieces().size() == 1 && this.board.getWhitePieces().size() == 1) {
                this.movingEnabled = false;
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

            addMouseListener(createMouseListener());
            validate();
        }

        /**
         * @return MouseListener for managing moves made by user
         */
        private MouseListener createMouseListener () {
            return new MouseAdapter() {
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
                        SwingUtilities.invokeLater(() -> {
                            takenPieces.refresh(moveLog);
                            chessBoard.drawBoard(board);
                            if (gameDialog.isAIPlayer(board.currentPlayer())) {
                                Chess.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                        });
                    } else if (isRightMouseButton(e)) {
                        sourceSquare = null;
                        destinationSquare = null;
                        movedPiece = null;
                        playMoveLog(FenParser.parseIntoLog(FenParser.parsePGN(moveLog)));
                        SwingUtilities.invokeLater(() -> chessBoard.drawBoard(board));
                    }
                }
                private void firstClick() {
                    sourceSquare = board.getSquare(squareID);
                    movedPiece = sourceSquare.getPiece();
                    // if clicked on empty square, do nothing
                    if (movedPiece == null) {
                        sourceSquare = null;
                    } else if (movedPiece.getPieceTeam() != board.currentPlayer().getTeam()) {
                        sourceSquare = null;
                    }
                }

                private void secondClick() {
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
                    if (!(destinationSquare instanceof Square.EmptySquare) && destinationSquare.getPiece().getPieceTeam() == movedPiece.getPieceTeam()) {
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
                    final Move move;
                    if (movedPiece.getPieceTeam().isPawnPromotionSquare(destinationSquare.getSquareCoordinate())
                            && movedPiece instanceof Pawn
                            && movedPiece.getPieceTeam().isAboutToPromoteSquare(movedPiece.getPosition())) {

                        final PromotionDialog pD = new PromotionDialog(frame, movedPiece.getPieceTeam());
                        final Piece promotionPiece;
                        if (pD.getSelectedPieceType() == PieceType.QUEEN) {
                            promotionPiece = new Queen(movedPiece.getPosition()
                                    , movedPiece.getPieceTeam()
                                    , false);
                        } else if (pD.getSelectedPieceType() == PieceType.ROOK) {
                            promotionPiece = new Rook(movedPiece.getPosition()
                                    , movedPiece.getPieceTeam()
                                    , false);
                        } else if (pD.getSelectedPieceType() == PieceType.BISHOP) {
                            promotionPiece = new Bishop(movedPiece.getPosition()
                                    , movedPiece.getPieceTeam()
                                    , false);
                        } else if (pD.getSelectedPieceType() == PieceType.KNIGHT) {
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
                        move = new PawnPromotion(
                                new PawnMove(board, movedPiece, destinationSquare.getSquareCoordinate()), promotionPiece);
                    } else {
                        move = Move.MoveFactory.createMove(board
                                , sourceSquare.getSquareCoordinate()
                                , destinationSquare.getSquareCoordinate());
                    }
                    final MoveTransition transition = board.currentPlayer().makeMove(move);
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
                        board = transition.getTransitionBoard();
                        moveLog.addMove(move);
                        logger.printLog(FenParser.parsePGN(moveLog));
                        positionLog.add(FenParser.parseFen(transition.getTransitionBoard()));
                    }
                    sourceSquare = null;
                    destinationSquare = null;
                    movedPiece = null;
                }
            };
        }

        /**
         * @param board to get the Piece that is on the Square to generate an image
         */
        private void assignSquareIcon (final Board board) throws Exception {
            this.removeAll();
            if (board.getSquare(this.squareID).isOccupied()) {
                final BufferedImage image = ImageIO.read(new File(
                        artPath + board.getSquare(this.squareID)
                                .getPiece().getPieceTeam().toString()
                                .charAt(0) + board.getSquare(this.squareID)
                                .getPiece().toString() + ".png")
                );
                add(new JLabel(new ImageIcon(image.getScaledInstance
                        (CHESS_BOARD_DIMENSION.width/15, CHESS_BOARD_DIMENSION.height/8, 0))));
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
                return movedPiece.calcLegalMoves(board).stream().filter
                        (move -> {
                            if (board.currentPlayer().makeMove(move).getMoveStatus() == MoveStatus.DONE) {
                                if (move instanceof PawnPromotion) {
                                    // Check if the move is a pawn promotion move
                                    // and if so, only allow one of the four possible promotion moves
                                    Piece promotedPiece = ((PawnPromotion) move).getPromotedToPiece();
                                    return promotedPiece instanceof Bishop;
                                }
                                return true;
                            }
                            return false;
                        }).collect(Collectors.toList());
            }
            // if we are clicking on a piece that is not ours e.g.
            return Collections.emptyList();
        }

        /**
         * @param board passed in Board to check for mate, checks and stalemate
         */
        private void signifyCheck (final Board board) {
            if (!signifyChecksActive) return;

            Color red = new Color(152, 40, 0);
            if (moveLog.size() < 1) {
                return;
            }
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
            } else if (board.blackPlayer().isInStalemate()) {
                if (board.blackPlayer().getPlayerKing().getPosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            } else if (board.whitePlayer().isInStalemate()) {
                if (board.whitePlayer().getPlayerKing().getPosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            } else if (isDrawByLackOfMaterial()) {
                if (board.whitePlayer().getPlayerKing().getPosition() == this.squareID
                        || board.blackPlayer().getPlayerKing().getPosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            } else if (isDrawByRepetition()) {
                if (board.whitePlayer().getPlayerKing().getPosition() == this.squareID
                        || board.blackPlayer().getPlayerKing().getPosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
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