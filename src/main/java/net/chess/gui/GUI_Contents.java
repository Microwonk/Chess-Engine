package main.java.net.chess.gui;

import main.java.net.chess.ai.AI;
import main.java.net.chess.ai.AlphaBeta.AlphaBetaPruning;
import main.java.net.chess.engine.Team;
import main.java.net.chess.engine.board.Board;
import main.java.net.chess.engine.board.BoardUtilities;
import main.java.net.chess.engine.board.Move;
import main.java.net.chess.engine.board.Square;
import main.java.net.chess.engine.pieces.*;
import main.java.net.chess.engine.player.MoveStatus;
import main.java.net.chess.engine.player.MoveTransition;
import main.java.net.chess.engine.player.Player;
import main.java.net.chess.pgn.FenParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import static java.util.concurrent.Flow.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static main.java.net.chess.engine.board.Move.PawnMove;
import static main.java.net.chess.engine.board.Move.PawnPromotion;
import static main.java.net.chess.engine.pieces.Piece.PieceType;

/**
 * Container for all the GUI Content Management and Connection of all the Components -> Singleton type
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class GUI_Contents implements Publisher <Object> {


    // TODO: shuffling moves results in draw

    private final JFrame frame;
    private final ChessBoard chessBoard;
    private final TakenPieces takenPieces;
    private Board board;
    private final MoveLog moveLog;
    private final GameDialog gameDialog;
    private final static Dimension FRAME_DIMENSION = new Dimension(640, 720);
    private final Dimension CHESS_BOARD_DIMENSION = new Dimension(400, 400);
    private final Dimension SQUARE_DIMENSION = new Dimension(10, 10);

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
    private boolean highlightLegalMovesActive;

    public final static String path = "assets/pieces/pixel_art/";
    public final static String misc = "assets/misc/";
    protected final static Color lightColour = new Color(196, 189, 175);
    protected final static Color darkColour = new Color(155, 132, 75);


    // instantiating the Singleton
    private static final GUI_Contents GUI_INSTANCE = new GUI_Contents();

    private GUI_Contents () {
        this.frame = new JFrame("Chess by Nicolas Frey");
        this.board = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMovesActive = true;
        this.positionLog = new ArrayList <>();
        this.moveLog = new MoveLog();
        this.publisher = new SubmissionPublisher <>();
        this.addSubscriber(new GameSubscriber());
        this.gameDialog = new GameDialog(this.frame);
        this.currentMove = 0;
        this.movingEnabled = true;
        // TODO: make user choose own art
        this.frame.setLayout(new BorderLayout());
        this.frame.setFont(new Font("Minecraft", Font.BOLD, 13));
        this.chessBoard = new ChessBoard();
        this.frame.add(this.chessBoard, BorderLayout.CENTER);
        this.takenPieces = new TakenPieces();
        this.frame.add(this.takenPieces, BorderLayout.SOUTH);
        this.frame.setJMenuBar(makeMenuBar());
        this.frame.addKeyListener(addHotKeys());
        this.frame.setSize(FRAME_DIMENSION);
        this.frame.setMinimumSize(new Dimension(720, 820));
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    public void addSubscriber (Subscriber <? super Object> subscriber) {
        publisher.subscribe(subscriber);
    }

    private void notifySubscribers (Object obj) {
        publisher.submit(obj);
    }

    public static GUI_Contents get () {
        return GUI_INSTANCE;
    }

    private GameDialog getGame () {
        return this.gameDialog;
    }

    public Board getGameBoard () {
        return this.board;
    }

    public void show () {
        GUI_Contents.get().getMoveLog().clear();
        GUI_Contents.get().getTakenPieces().refresh(GUI_Contents.get().getMoveLog());
        GUI_Contents.get().getChessBoard().drawBoard(GUI_Contents.get().getGameBoard());
    }

    /**
     * @return the Configuration of the Hotkeys -> can be made into global variables so that user can customize
     */
    private KeyListener addHotKeys () {
        return new KeyListener() {
            @Override
            public void keyTyped (KeyEvent e) {
            }

            @Override
            public void keyPressed (KeyEvent e) {
                // System.out.println(e.getKeyCode()); // for finding out the hot keys KeyCode
                switch (e.getKeyCode()) {
                    case 37 -> prevMove(); // left arrow
                    case 39 -> nextMove(); // right arrow
                    case 38 -> endBoard(); // up arrow
                    case 40 -> beginBoard(); // down arrow
                    case 82 -> reset(); // r key
                }
            }

            @Override
            public void keyReleased (KeyEvent e) {
            }
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
        openFen.addActionListener(e -> loadGame("save/save.txt"));

        final JMenuItem saveToFen = new JMenuItem("Save to Fen");
        saveToFen.setFont(frame.getFont());
        saveToFen.addActionListener(e -> saveGame(FenParser.parseFen(this.board), "save/", ".txt"));

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

    private void loadGame (final String toLoad) {
        final File loadFile = new File(toLoad);
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(loadFile));
            this.board = FenParser.createGameFromFen(reader.readLine());
            reader.close();
            this.chessBoard.drawBoard(this.board);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param toSave   Fen String or PGN Format Moves
     * @param savePath path that will be saved to -> will be obsolete once database is established
     * @param fileType .txt or .pgn depending on FEN or PGN -> will be obsolete once database is established
     */
    private void saveGame (final String toSave, final String savePath, final String fileType) {
        final File saveFile = new File(savePath +
                DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
                        .format(LocalDateTime.now()) + fileType);
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
            bw.write(toSave);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @return GUI SettingsMenu
     */
    private JMenu createSettingsMenu () {
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

    /**
     * @return GUI OptionsMenu
     */
    private JMenu createOptionsMenu () {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGame = new JMenuItem("Setup Game");
        setupGame.addActionListener(e -> {
            GUI_Contents.get().getGame().promptUser(GUI_Contents.get().frame);
            GUI_Contents.get().gameUpdate(GUI_Contents.get().getGame());
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

    }

    // TODO: Observer is deprecated -> look into PropertyChangeListener ::
    //  https://stackoverflow.com/questions/46380073/observer-is-deprecated-in-java-9-what-should-we-use-instead-of-it

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
            if (GUI_Contents.get().getGame().isAIPlayer(GUI_Contents.get().getGameBoard().currentPlayer())
                    && !GUI_Contents.get().getGameBoard().currentPlayer().isInCheckmate()
                    && !GUI_Contents.get().getGameBoard().currentPlayer().isInStalemate()
                    && !GUI_Contents.get().isDrawByLackOfMaterial()
                    && !GUI_Contents.get().isDrawByRepetition()) {
                // execute the AI
                final backGroundThreadForAI thread = new backGroundThreadForAI();
                thread.execute();

                if (GUI_Contents.get().getGameBoard().currentPlayer().isInCheckmate()) {
                    System.out.println("game over, " + GUI_Contents.get().getGameBoard().currentPlayer() + " is in checkmate!");
                }

                if (GUI_Contents.get().getGameBoard().currentPlayer().isInStalemate()) {
                    System.out.println("game over, " + GUI_Contents.get().getGameBoard().currentPlayer() + " is in stalemate!");
                }
            }
        }

        @Override
        public void onError (Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete () {
            System.out.println("Game over!");
        }
    }

    /**
     * SwingWorker using threads to do the work in the background instead of the thread of the gui
     */
    private static class backGroundThreadForAI extends SwingWorker <Move, String> {
        private backGroundThreadForAI () {
        }

        // what should be done in the background thread
        @Override
        protected Move doInBackground () {
            // TODO: implement difference between random and actual algorithm
            final AI alphaBeta = new AlphaBetaPruning(GUI_Contents.get().gameDialog.getSearchDepth());
            final Move move = alphaBeta.execute(GUI_Contents.get().getGameBoard());
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
                GUI_Contents.get().updateComputerMove(executedMove);
                GUI_Contents.get().updateGameBoard(GUI_Contents.get().getGameBoard().currentPlayer().makeMove(executedMove).getTransitionBoard());
                GUI_Contents.get().getMoveLog().addMove(executedMove);
                GUI_Contents.get().getPositionLog().add(FenParser.parseFen(executedMove.getBoard()));
                GUI_Contents.get().getTakenPieces().refresh(GUI_Contents.get().getMoveLog());
                GUI_Contents.get().getChessBoard().drawBoard(GUI_Contents.get().getGameBoard());
                GUI_Contents.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
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

    private ChessBoard getChessBoard () {
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
        if (this.positionLog.size() < 6) {
            return false;
        }
        final Map <String, Long> amount = positionLog.stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        amount.forEach((k, v) -> System.out.println(v > 2 ? v : ""));
        return amount.containsValue(3L);
    }

    /**
     * Board Direction in accordance to being white or black player
     */
    public enum BoardDirection {
        NORMAL {
            @Override
            List <SquareGUI> traverse (List <SquareGUI> squares) {
                return squares;
            }

            @Override
            BoardDirection opposite () {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List <SquareGUI> traverse (List <SquareGUI> squares) {
                final List <SquareGUI> viewOnly = new ArrayList <>(squares);
                Collections.reverse(viewOnly);
                return Collections.unmodifiableList(viewOnly);
            }

            @Override
            BoardDirection opposite () {
                return NORMAL;
            }
        };

        abstract List <SquareGUI> traverse (final List <SquareGUI> squares);

        abstract BoardDirection opposite ();
    }

    /**
     * 8x8 JPanel filled with Squares
     */
    private class ChessBoard extends JPanel {

        final List <SquareGUI> boardSquares;

        ChessBoard () {
            super(new GridLayout(8, 8));
            this.boardSquares = new ArrayList <>();

            for (int i = 0; i < BoardUtilities.NUM_SQUARES; i++) {
                final SquareGUI square = new SquareGUI(i);
                this.boardSquares.add(square);
                this.add(square);
            }
            setPreferredSize(CHESS_BOARD_DIMENSION);
            validate();
        }

        /**
         * @param board the Board being drawn to refresh the view
         */
        public void drawBoard (final Board board) {
            removeAll();
            for (final SquareGUI square : boardDirection.traverse(boardSquares)) {
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
    public static class MoveLog {
        private final List <Move> moves;

        MoveLog () {
            this.moves = new ArrayList <>();
        }

        public List <Move> getMoves () {
            return this.moves;
        }

        public void addMove (final Move move) {
            this.moves.add(move);
        }

        public int size () {
            return this.moves.size();
        }

        public void clear () {
            this.moves.clear();
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
    private class SquareGUI extends JPanel {

        private final int squareID;

        SquareGUI (final int squareID) {
            super(new GridBagLayout());
            this.squareID = squareID;
            this.setPreferredSize(SQUARE_DIMENSION);
            this.setBackground(assignSquareColour());
            this.assignSquareIcon(board);

            addMouseListener(createMouseListener());
            validate();
        }

        /**
         * @return MouseListener for managing moves made by user
         */
        private MouseListener createMouseListener () {
            return new MouseListener() {
                @Override
                public void mouseClicked (final MouseEvent e) {
                    if (!movingEnabled) {
                        return;
                    }
                    if (isLeftMouseButton(e)) {
                        // first Click
                        if (sourceSquare == null) {
                            sourceSquare = board.getSquare(squareID);
                            movedPiece = sourceSquare.getPiece();
                            // if clicked on empty square, do nothing
                            if (movedPiece == null) {
                                sourceSquare = null;
                            } else if (movedPiece.getPieceTeam() != board.currentPlayer().getTeam()) {
                                sourceSquare = null;
                            }
                        } else { // second Click
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
                                    && movedPiece.getPieceTeam().isAboutToPromoteSquare(movedPiece.getPiecePosition())) {

                                final PromotionDialog pD = new PromotionDialog(frame, movedPiece.getPieceTeam());
                                final Piece promotionPiece;
                                if (pD.getSelectedPieceType() == PieceType.QUEEN) {
                                    promotionPiece = new Queen(movedPiece.getPiecePosition()
                                            , movedPiece.getPieceTeam()
                                            , false);
                                } else if (pD.selectedPieceType == PieceType.ROOK) {
                                    promotionPiece = new Rook(movedPiece.getPiecePosition()
                                            , movedPiece.getPieceTeam()
                                            , false);
                                } else if (pD.selectedPieceType == PieceType.BISHOP) {
                                    promotionPiece = new Bishop(movedPiece.getPiecePosition()
                                            , movedPiece.getPieceTeam()
                                            , false);
                                } else if (pD.selectedPieceType == PieceType.KNIGHT) {
                                    promotionPiece = new Knight(movedPiece.getPiecePosition()
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
                                positionLog.add(FenParser.parseFen(transition.getTransitionBoard()));
                            }
                            sourceSquare = null;
                            destinationSquare = null;
                            movedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                            takenPieces.refresh(moveLog);
                            chessBoard.drawBoard(board);
                            if (gameDialog.isAIPlayer(board.currentPlayer())) {
                                GUI_Contents.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                        });
                    } else if (isLeftMouseButton(e)) {
                        sourceSquare = null;
                        destinationSquare = null;
                        movedPiece = null;
                        SwingUtilities.invokeLater(() -> chessBoard.drawBoard(board));
                    }
                }

                @Override
                public void mousePressed (final MouseEvent e) {
                }

                @Override
                public void mouseReleased (final MouseEvent e) {
                }

                @Override
                public void mouseEntered (final MouseEvent e) {
                }

                @Override
                public void mouseExited (final MouseEvent e) {
                }
            };
        }

        /**
         * @param board to get the Piece that is on the Square to generate an image
         */
        private void assignSquareIcon (final Board board) {
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

        /**
         * @param board to highlight all the legal moves on the board
         */
        private void highlightLegalMoves (final Board board) {
            if (highlightLegalMovesActive) {
                for (final Move move : pieceLegalMoves(board)) {
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
            Color red = new Color(152, 40, 0);
            if (moveLog.size() < 1) {
                return;
            }
            if (board.blackPlayer().isInCheck()) {
                if (!board.blackPlayer().isInCheckmate()) {
                    red = red.brighter();
                }
                if (board.blackPlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(red);
                }
            } else if (board.whitePlayer().isInCheck()) {
                if (!board.whitePlayer().isInCheckmate()) {
                    red = red.brighter();
                }
                if (board.whitePlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(red);
                }
            } else if (board.blackPlayer().isInStalemate()) {
                if (board.blackPlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            } else if (board.whitePlayer().isInStalemate()) {
                if (board.whitePlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            } else if (isDrawByLackOfMaterial()) {
                if (board.whitePlayer().getPlayerKing().getPiecePosition() == this.squareID
                        || board.blackPlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            } else if (isDrawByRepetition()) {
                if (board.whitePlayer().getPlayerKing().getPiecePosition() == this.squareID
                        || board.blackPlayer().getPlayerKing().getPiecePosition() == this.squareID) {
                    this.setBackground(Color.GRAY);
                }
            }
        }

        /**
         * @return the Color of the Square
         */
        private Color assignSquareColour () {
            if (BoardUtilities.FIRST_ROW[this.squareID]
                    || BoardUtilities.THIRD_ROW[this.squareID]
                    || BoardUtilities.FIFTH_ROW[this.squareID]
                    || BoardUtilities.SEVENTH_ROW[this.squareID]) {
                return (this.squareID % 2 == 0 ? lightColour : darkColour);
            } else if (BoardUtilities.SECOND_ROW[this.squareID]
                    || BoardUtilities.FOURTH_ROW[this.squareID]
                    || BoardUtilities.SIXTH_ROW[this.squareID]
                    || BoardUtilities.EIGHTH_ROW[this.squareID]) {
                return (this.squareID % 2 != 0 ? lightColour : darkColour);
            }
            throw new RuntimeException("Square is off the bounds of the board.");
        }

        /**
         * @param board to pass in all of the above methods -> draws one single Square
         */
        public void drawSquare (final Board board) {
            setBackground(assignSquareColour());
            assignSquareIcon(board);
            highlightLegalMoves(board);
            signifyCheck(board);
            validate();
            repaint();
        }
    }

    /**
     * JPanel to represent the Taken Pieces in one Game
     */
    private static class TakenPieces extends JPanel {

        private final JPanel WEST;
        private final JPanel EAST;
        private static final Dimension DIMENSION = new Dimension(600, 70);

        public TakenPieces () {
            super(new BorderLayout());
            JPanel bottom = new JPanel();
            bottom.setPreferredSize(new Dimension(600, 5));
            this.setPreferredSize(DIMENSION);
            this.WEST = new JPanel(new GridLayout(2, 8)); // 8 * 2 -> 16 pieces
            this.EAST = new JPanel(new GridLayout(2, 8)); // 8 * 2 -> 16 pieces

            this.add(this.WEST, BorderLayout.WEST);
            this.add(this.EAST, BorderLayout.EAST);
            this.add(bottom, BorderLayout.SOUTH);
        }

        /**
         * @param moveLog passed in to look for Attacking Moves
         *                , which have an attacked Piece -> so it will be a "Taken Piece"
         */
        public void refresh (final MoveLog moveLog) {
            this.EAST.removeAll();
            this.WEST.removeAll();

            final List <Piece> whiteTakenPieces = new ArrayList <>();
            final List <Piece> blackTakenPieces = new ArrayList <>();

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

            whiteTakenPieces.sort(Comparator.comparingInt(Piece::getPieceValue));
            blackTakenPieces.sort(Comparator.comparingInt(Piece::getPieceValue));

            for (final Piece takenPiece : whiteTakenPieces) {
                try {
                    this.EAST.add(new JLabel(new ImageIcon(ImageIO.read(new File(path
                            + takenPiece.getPieceTeam().toString().charAt(0)
                            + takenPiece + ".png")))));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (final Piece takenPiece : blackTakenPieces) {
                try {
                    this.WEST.add(new JLabel(new ImageIcon(ImageIO.read(new File(path
                            + takenPiece.getPieceTeam().toString().charAt(0)
                            + takenPiece + ".png")))));

                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            validate();
            repaint();
        }
    }

    /**
     * Popup dialog for Promoting a Promotion Pawn
     */
    private static class PromotionDialog extends JDialog {

        private PieceType selectedPieceType;

        public PromotionDialog (JFrame parent, Team pieceColor) {
            super(parent, "Promotion", true);
            final String team = pieceColor.toString().substring(0, 1);

            // Create buttons for each promotion piece
            JButton queenButton = new JButton(new ImageIcon(path + team + "Q.png"));
            JButton rookButton = new JButton(new ImageIcon(path + team + "R.png"));
            JButton bishopButton = new JButton(new ImageIcon(path + team + "B.png"));
            JButton knightButton = new JButton(new ImageIcon(path + team + "N.png"));

            queenButton.setFocusable(false);
            rookButton.setFocusable(false);
            bishopButton.setFocusable(false);
            knightButton.setFocusable(false);

            // Add action listeners for each button
            queenButton.addActionListener(e -> {
                selectedPieceType = PieceType.QUEEN;
                dispose();
            });
            rookButton.addActionListener(e -> {
                selectedPieceType = PieceType.ROOK;
                dispose();
            });
            bishopButton.addActionListener(e -> {
                selectedPieceType = PieceType.BISHOP;
                dispose();
            });
            knightButton.addActionListener(e -> {
                selectedPieceType = PieceType.KNIGHT;
                dispose();
            });
            // Create a panel to hold the buttons
            JPanel panel = new JPanel();
            panel.add(queenButton);
            panel.add(rookButton);
            panel.add(bishopButton);
            panel.add(knightButton);
            this.add(panel);
            // Set size and visibility
            this.pack();
            this.setLocation(new Point(parent.getMousePosition(true).x + 385, parent.getMousePosition(true).y));
            this.setVisible(true);
        }

        public PieceType getSelectedPieceType () {
            return this.selectedPieceType;
        }
    }

    /**
     * Popup dialog for Computer and Human settings
     */
    private static class GameDialog extends JDialog {

        private GUI_Contents.PlayerType whitePlayerType;
        private GUI_Contents.PlayerType blackPlayerType;
        private final JSpinner searchDepthSpinner;

        private static final String HUMAN_TEXT = "Human";
        private static final String COMPUTER_TEXT = "Computer";

        public GameDialog (final JFrame parent) {
            super(parent, "Game", true);
            final JPanel myPanel = new JPanel(new GridLayout(0, 1));
            final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
            final JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
            final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
            final JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);

            whiteHumanButton.setActionCommand(HUMAN_TEXT);

            final ButtonGroup whiteGroup = new ButtonGroup();
            whiteGroup.add(whiteHumanButton);
            whiteGroup.add(whiteComputerButton);
            whiteHumanButton.setSelected(true);

            final ButtonGroup blackGroup = new ButtonGroup();
            blackGroup.add(blackHumanButton);
            blackGroup.add(blackComputerButton);
            blackHumanButton.setSelected(true);

            getContentPane().add(myPanel);

            myPanel.add(new JLabel("White"));
            myPanel.add(whiteHumanButton);
            myPanel.add(whiteComputerButton);
            myPanel.add(new JLabel("Black"));
            myPanel.add(blackHumanButton);
            myPanel.add(blackComputerButton);
            myPanel.add(new JLabel("Search"));

            this.searchDepthSpinner = addLabeledSpinner(myPanel, new SpinnerNumberModel(6, 0, Integer.MAX_VALUE, 1));

            final JButton cancelButton = new JButton("Cancel");
            final JButton okButton = new JButton("OK");

            okButton.addActionListener(e -> {
                whitePlayerType = whiteComputerButton.isSelected() ? GUI_Contents.PlayerType.COMPUTER : GUI_Contents.PlayerType.HUMAN;
                blackPlayerType = blackComputerButton.isSelected() ? GUI_Contents.PlayerType.COMPUTER : GUI_Contents.PlayerType.HUMAN;
                GameDialog.this.setVisible(false);
            });
            cancelButton.addActionListener(e -> {
                System.out.println("Cancel");
                GameDialog.this.setVisible(false);
            });

            myPanel.add(cancelButton);
            myPanel.add(okButton);

            pack();
            setVisible(false);
        }

        void promptUser (JFrame parent) {
            setLocationRelativeTo(parent);
            setVisible(true);
            repaint();
        }

        boolean isAIPlayer (final Player player) {
            if (player.getTeam() == Team.WHITE) {
                return getWhitePlayerType() == GUI_Contents.PlayerType.COMPUTER;
            }
            return getBlackPlayerType() == GUI_Contents.PlayerType.COMPUTER;
        }

        GUI_Contents.PlayerType getWhitePlayerType () {
            return this.whitePlayerType;
        }

        GUI_Contents.PlayerType getBlackPlayerType () {
            return this.blackPlayerType;
        }

        private static JSpinner addLabeledSpinner (final Container c, final SpinnerModel model) {
            final JLabel l = new JLabel("Search Depth");
            c.add(l);
            final JSpinner spinner = new JSpinner(model);
            l.setLabelFor(spinner);
            c.add(spinner);
            return spinner;
        }

        int getSearchDepth () {
            return (Integer) this.searchDepthSpinner.getValue();
        }
    }
}