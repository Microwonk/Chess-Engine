package net.chess.gui;

import com.google.common.primitives.Ints;
import net.chess.engine.board.Move;
import net.chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static net.chess.gui.GUI_Contents.MoveLog;

public class TakenPieces extends JPanel {

    private final JPanel NORTH;
    private final JPanel SOUTH;
    private static final Color PANEL_COLOUR = new Color(255, 255, 255);
    private static final EtchedBorder BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Dimension DIMENSION = new Dimension(40, 80);

    public TakenPieces() {
        super(new BorderLayout());
        this.setBackground(PANEL_COLOUR);
        this.setBorder(BORDER);
        this.NORTH = new JPanel(new GridLayout(8,2)); // 8 * 2 -> 16 pieces
        this.SOUTH = new JPanel(new GridLayout(8,2)); // 8 * 2 -> 16 pieces
        this.NORTH.setBackground(PANEL_COLOUR);
        this.SOUTH.setBackground(PANEL_COLOUR);
        this.add(this.NORTH, BorderLayout.NORTH);
        this.add(this.SOUTH, BorderLayout.SOUTH);
        this.setPreferredSize(DIMENSION);
    }

    // need to implement MoveLog
    public void redo(final MoveLog moveLog) {
        this.SOUTH.removeAll();
        this.NORTH.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move: moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();

                if (takenPiece.getPieceTeam().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else {
                    blackTakenPieces.add(takenPiece);
                }
            }
        }

        whiteTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));


        for (final Move move: moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();

                if (takenPiece.getPieceTeam().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else {
                    blackTakenPieces.add(takenPiece);
                }
            }
        }

        blackTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));

        for (final Piece takenPiece: whiteTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File(GUI_Contents.path
                        + takenPiece.getPieceTeam().toString() .charAt(0)
                        + takenPiece.getPieceType().toString().charAt(0) + ".png"));

                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                this.SOUTH.add(imageLabel);

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece: blackTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File(GUI_Contents.path
                        + takenPiece.getPieceTeam().toString().charAt(0)
                        + takenPiece.getPieceType().toString().charAt(0) + ".png"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                this.SOUTH.add(imageLabel);

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        validate();
    }
}
