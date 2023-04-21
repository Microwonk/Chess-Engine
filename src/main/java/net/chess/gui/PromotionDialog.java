package net.chess.gui;

import net.chess.engine.Team;
import net.chess.engine.pieces.Piece;

import javax.swing.*;

import static net.chess.gui.util.Properties.defArtPath;

/**
 * Popup dialog for Promoting a Promotion Pawn
 */
public class PromotionDialog extends JDialog {

    private Piece.PieceType selectedPieceType;

    public PromotionDialog (JFrame parent, Team pieceColor) {
        super(parent, "Promotion", true);
        final String team = pieceColor.toString().substring(0, 1);

        // Create buttons for each promotion piece
        JButton queenButton = new JButton(new ImageIcon(defArtPath + team + "Q.png"));
        JButton rookButton = new JButton(new ImageIcon(defArtPath + team + "R.png"));
        JButton bishopButton = new JButton(new ImageIcon(defArtPath + team + "B.png"));
        JButton knightButton = new JButton(new ImageIcon(defArtPath + team + "N.png"));

        queenButton.setFocusable(false);
        rookButton.setFocusable(false);
        bishopButton.setFocusable(false);
        knightButton.setFocusable(false);

        // Add action listeners for each button
        queenButton.addActionListener(e -> {
            selectedPieceType = Piece.PieceType.QUEEN;
            dispose();
        });
        rookButton.addActionListener(e -> {
            selectedPieceType = Piece.PieceType.ROOK;
            dispose();
        });
        bishopButton.addActionListener(e -> {
            selectedPieceType = Piece.PieceType.BISHOP;
            dispose();
        });
        knightButton.addActionListener(e -> {
            selectedPieceType = Piece.PieceType.KNIGHT;
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
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    public Piece.PieceType getSelectedPieceType () {
        return this.selectedPieceType;
    }
}
