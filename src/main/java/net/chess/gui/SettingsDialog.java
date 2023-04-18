package net.chess.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import static net.chess.gui.PropertyVars.*;

// TODO : A LOT

public class SettingsDialog extends JDialog implements ActionListener {

    private JButton chooseFileButton;
    private JTextField filePathField;
    private JComboBox <String> colorChooser;

    public SettingsDialog (JFrame parent) {
        super(parent, "Settings", true);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Create file chooser components
        chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(this);
        filePathField = new JTextField(20);
        JPanel fileChooserPanel = new JPanel(new FlowLayout());
        fileChooserPanel.add(new JLabel("File: "));
        fileChooserPanel.add(filePathField);
        fileChooserPanel.add(chooseFileButton);

        // Create color chooser components
        colorChooser = new JComboBox<>();
        PropertyVars.defaultColorPacks.forEach(c -> colorChooser.addItem(c.name()));
        JPanel colorChooserPanel = new JPanel(new FlowLayout());
        colorChooserPanel.add(new JLabel("Color: "));
        colorChooserPanel.add(colorChooser);

        // Create checkbox components
        final JCheckBoxMenuItem highlightingLegalMovesToggle = new JCheckBoxMenuItem("Highlight Moves");
        highlightingLegalMovesToggle.setSelected(highlightLegalMovesActive);
        highlightingLegalMovesToggle.addActionListener(GUI_Contents.get().highLightLegalMovesAction(highlightingLegalMovesToggle));


        final JCheckBoxMenuItem signifyChecksToggle = new JCheckBoxMenuItem("Signify Checks");
        signifyChecksToggle.setSelected(signifyChecksActive);
        signifyChecksToggle.addActionListener(GUI_Contents.get().signifyChecksAction(signifyChecksToggle));


        final JCheckBoxMenuItem soundToggle = new JCheckBoxMenuItem("Toggle Sound");
        soundToggle.setSelected(soundOn);
        soundToggle.addActionListener(GUI_Contents.get().soundToggleChecksAction(signifyChecksToggle));
        JPanel checkBoxPanel = new JPanel(new GridLayout(1, 3));
        checkBoxPanel.add(highlightingLegalMovesToggle);
        checkBoxPanel.add(signifyChecksToggle);
        checkBoxPanel.add(soundToggle);

        // Add components to dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(fileChooserPanel, BorderLayout.NORTH);
        mainPanel.add(colorChooserPanel, BorderLayout.CENTER);
        mainPanel.add(checkBoxPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == chooseFileButton) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getPath());
            }
        }
    }
}
