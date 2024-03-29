package net.chess.gui;

import net.chess.engine.Team;
import net.chess.engine.player.Player;

import javax.swing.*;
import java.awt.*;

import static net.chess.gui.Chess.PlayerType;

public class GameDialog extends JDialog {

    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private final JSpinner searchDepthSpinner;

    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";

    public GameDialog (final JFrame parent) {
        super(parent, "Game", true);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
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
            whitePlayerType = whiteComputerButton.isSelected() ? Chess.PlayerType.COMPUTER : Chess.PlayerType.HUMAN;
            blackPlayerType = blackComputerButton.isSelected() ? Chess.PlayerType.COMPUTER : Chess.PlayerType.HUMAN;
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
            return getWhitePlayerType() == Chess.PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == Chess.PlayerType.COMPUTER;
    }

    Chess.PlayerType getWhitePlayerType () {
        return this.whitePlayerType;
    }

    Chess.PlayerType getBlackPlayerType () {
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