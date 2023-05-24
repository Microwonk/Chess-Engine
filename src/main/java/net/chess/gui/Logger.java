package net.chess.gui;

import net.chess.parsing.PGNParser;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static net.chess.gui.Chess.CHESS;
import static net.chess.gui.util.Variables.TITLE;


//TODO: clean up this mess
public class Logger extends JPanel {

    private final JCheckBox toggleTerminal;
    private final JPanel body;
    private final Console console;
    private final JPanel heading;
    private final CardLayout cardLayout;

    public Logger() {
        super();
        this.cardLayout = new CardLayout();
        this.body = new JPanel(cardLayout);
        this.setLayout(new BorderLayout());
        Terminal terminal = new Terminal();
        this.console = new Console();
        JPanel terminalPanel = new JPanel(new GridBagLayout());
        JPanel consolePanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        terminalPanel.add(terminal, gbc);
        consolePanel.add(console, gbc);

        this.body.add(console, "Console");
        this.body.add(terminal, "Terminal");

        this.heading = new JPanel(new BorderLayout());
        this.heading.add(new JLabel(TITLE + " ➜ Console"), BorderLayout.CENTER);

        this.toggleTerminal = new JCheckBox("♙");
        this.toggleTerminal.setToolTipText("Toggle Terminal for Input \n Experimental!!");
        this.toggleTerminal.setSelected(false);

        toggleTerminal.addActionListener(e -> {
            cardLayout.show(body, toggleTerminal.isSelected() ? "Terminal" : "Console");
            ((JLabel) heading.getComponent(0)).setText(TITLE + (toggleTerminal.isSelected() ? " ➜ Terminal" : " ➜ Console"));
        });
        this.heading.add(toggleTerminal, BorderLayout.EAST);

        this.add(heading, BorderLayout.NORTH);
        this.add(body, BorderLayout.CENTER);
        this.setVisible(true);
    }

    @SafeVarargs
    public final <T> void printLog (final T... text) {
        for (T s: text) {
            this.console.textArea.append("> " + s + '\n');
        }
    }

    public void clear() {
        this.console.textArea.setText(null);
    }

    public JTextArea getTextArea () {
        return console.textArea;
    }

    public static class Terminal extends JScrollPane {

        private final JTextArea textArea;
        private int startPos;

        public Terminal () {
            this.setBorder(null);
            this.textArea = new JTextArea();
            ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            this.setPreferredSize(new Dimension(100, 640));
            this.textArea.setEditable(true);
            this.textArea.setLineWrap(true);
            this.textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            this.textArea.setBackground(new Color(70, 70, 70));
            this.textArea.setForeground(Color.GREEN);
            this.textArea.setText(">/ ");

            TerminalDocumentFilter documentFilter = new TerminalDocumentFilter();
            ((AbstractDocument) textArea.getDocument()).setDocumentFilter(documentFilter);

            documentFilter.setCommandPosition(textArea.getDocument().getLength());
            startPos = textArea.getDocument().getLength();

            textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enterAction");
            textArea.getActionMap().put("enterAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch (textArea.getText().substring(startPos)) {
                        case "cls" -> {
                            ((AbstractDocument) textArea.getDocument()).setDocumentFilter(null);
                            textArea.setText(">/ ");
                            ((AbstractDocument) textArea.getDocument()).setDocumentFilter(documentFilter);
                            documentFilter.setCommandPosition(textArea.getDocument().getLength());
                            startPos = textArea.getDocument().getLength();
                            return;
                        }
                        case "hello world", "hello" -> {
                            textArea.append("\n>/ Hello Chess Player!");
                        }
                        case "help", "?" -> {
                            textArea.append("\n>/ Here will be help");
                        }
                        default -> {
                            try {
                                // Chess.get().getLogger().printLog(Chess.get().getGameBoard().currentPlayer().makeMove(PGNParser.parseMove(Chess.get().getGameBoard(), stringBuilder.toString())).getTransitionBoard());
                                CHESS.update(CHESS.getGameBoard().currentPlayer().makeMove(PGNParser.parseMove(CHESS.getGameBoard(), textArea.getText().substring(startPos))).getTransitionBoard());
                            } catch (Exception ex) {
                                textArea.append("\n>/ " + ex.getMessage());
                            }
                        }
                    }
                    textArea.append("\n>/ ");
                    documentFilter.setAllowDelete(false);
                    documentFilter.setCommandPosition(textArea.getDocument().getLength());
                    startPos = textArea.getDocument().getLength();


                }
            });
            this.setViewportView(textArea);
        }
    }

    public static class Console extends JScrollPane {

        private final JTextArea textArea;

        public Console () {
            this.setBorder(null);
            this.textArea = new JTextArea();
            ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            this.setPreferredSize(new Dimension(100, 640));
            this.textArea.setEditable(false);
            this.textArea.setLineWrap(true);
            this.textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            this.textArea.setBackground(new Color(70, 70, 70));
            this.textArea.setDisabledTextColor(Color.WHITE);
            this.setViewportView(textArea);
        }
    }

    // class for terminal text behaviour
    public static class TerminalDocumentFilter extends DocumentFilter {
        private int commandPosition = 0;
        private boolean allowDelete = false;

        public void setCommandPosition(int commandPosition) {
            this.commandPosition = commandPosition;
        }

        public void setAllowDelete(boolean allowDelete) {
            this.allowDelete = allowDelete;
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (allowDelete || offset >= commandPosition) {
                super.remove(fb, offset, length);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (allowDelete || offset >= commandPosition) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
