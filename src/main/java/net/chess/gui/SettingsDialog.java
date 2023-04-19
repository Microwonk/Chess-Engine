package net.chess.gui;

import net.chess.gui.audio.AudioHandler;
import net.chess.gui.util.Properties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO : A LOT

public class SettingsDialog extends JDialog implements ActionListener, ChangeListener {

    private JButton chooseFileButton;
    private JTextField filePathField;
    private JComboBox <String> colorChooser;
    private JSlider volumeSlider;

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
        Properties.defaultColorPacks.forEach(c -> colorChooser.addItem(c.name()));
        JPanel colorChooserPanel = new JPanel(new FlowLayout());
        colorChooserPanel.add(new JLabel("Color: "));
        colorChooserPanel.add(colorChooser);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(Properties.volume * 100));
        volumeSlider.addChangeListener(this);

        this.add(fileChooserPanel, BorderLayout.EAST);
        this.add(colorChooserPanel, BorderLayout.NORTH);
        this.add(volumeSlider, BorderLayout.SOUTH);
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

    @Override
    public void stateChanged (ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            AudioHandler.setSystemVolume(source.getValue() / 100f);
            Properties.store("volume", String.valueOf(source.getValue() / 100f));
        }
    }
}
