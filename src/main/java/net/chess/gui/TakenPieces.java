package net.chess.gui;

import net.chess.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.chess.gui.util.Variables.defArtPath;

/**
 * JPanel to represent the Taken Pieces in one Game
 */
public class TakenPieces extends JPanel {

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
    public void refresh (final Chess.MoveLog moveLog) {
        this.EAST.removeAll();
        this.WEST.removeAll();

        final java.util.List <Piece> whiteTakenPieces = new ArrayList <>();
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
                this.EAST.add(new JLabel(new ImageIcon(ImageIO.read(new File(defArtPath
                        + takenPiece.getPieceTeam().toString().charAt(0)
                        + takenPiece + ".png")))));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece : blackTakenPieces) {
            try {
                this.WEST.add(new JLabel(new ImageIcon(ImageIO.read(new File(defArtPath
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