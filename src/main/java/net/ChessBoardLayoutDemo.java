package net;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChessBoardLayoutDemo {
    public static void main(String[] args) {
        // create the main frame
        JFrame frame = new JFrame("Chess Board Layout Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create the top panel for the chessboard
        JPanel chessBoardPanel = new JPanel();
        chessBoardPanel.setPreferredSize(new Dimension(400, 400));
        chessBoardPanel.setBackground(Color.WHITE);

        // create the right panel for the logging information
        JPanel loggingPanel = new JPanel();
        loggingPanel.setBackground(Color.LIGHT_GRAY);

        // create the bottom panel for the general purpose panel
        JPanel generalPanel = new JPanel();
        generalPanel.setBackground(Color.DARK_GRAY);

        // create a main panel to hold the chess board panel and the logging panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chessBoardPanel, BorderLayout.CENTER);
        mainPanel.add(loggingPanel, BorderLayout.EAST);

        // create a panel to hold the main panel and the general purpose panel
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(generalPanel, BorderLayout.SOUTH);

        // set the content pane of the frame to the panel we created
        frame.setContentPane(contentPane);

        // set the size of the frame and center it on the screen
        frame.setSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);

        // add a component listener to the frame to handle resizing
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // get the new size of the frame
                Dimension size = evt.getComponent().getSize();

                // set the new size of the logging panel
                int loggingPanelWidth = (int) (size.getWidth() * 0.25);
                loggingPanel.setPreferredSize(new Dimension(loggingPanelWidth, size.height));
                loggingPanel.revalidate();
            }
        });

        // pack and show the frame
        frame.pack();
        frame.setVisible(true);
    }
}
