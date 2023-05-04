package net.chess.gui;

import net.chess.gui.audio.AudioHandler;
import net.chess.gui.util.ColorPack;
import net.chess.gui.util.Properties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

// TODO : A LOT

public class SettingsDialog extends JDialog {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final GeneralPanel generalPanel;
    private final ArtPacksPanel artpacksPanel;
    private final ColorPacksPanel colorPacksPanel;
    private final SoundPacksPanel soundPacksPanel;

    public SettingsDialog (JFrame parent) {
        super(parent, "Settings", true);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(parent.getWidth() - 20,  parent.getHeight() - 20);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        generalPanel = new GeneralPanel(new BorderLayout());
        artpacksPanel = new ArtPacksPanel(new BorderLayout());
        colorPacksPanel = new ColorPacksPanel(new BorderLayout());
        soundPacksPanel = new SoundPacksPanel();

        contentPanel.add(generalPanel, "General");
        contentPanel.add(artpacksPanel, "Art Packs");
        contentPanel.add(colorPacksPanel, "Color Packs");
        contentPanel.add(soundPacksPanel, "Sound Packs");

        this.add(contentPanel, BorderLayout.CENTER);
        this.add(sideBarPanel(this), BorderLayout.WEST);
        //this.add(colorChooserPanel, BorderLayout.NORTH);
        this.add(bottomBarPanel(), BorderLayout.SOUTH);
    }

    private JPanel bottomBarPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.setBackground(new Color(43, 43, 43));

        JButton okButton = new JButton("OK");
        okButton.setSize(new Dimension(50, 20));
        okButton.addActionListener(e -> {
            setVisible(false);
        });

        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(okButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        return bottomPanel;
    }

    private JPanel sideBarPanel(JDialog parent) {
        // Create a panel for the sidebar menu
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(43, 43, 43));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));

        // Create a label for the title of the sidebar menu
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(title);

        // Add menu items to the sidebar panel
        String[] menuItems = {"General", "Color Packs", "Art Packs", "Sound Packs"};
        for (String item : menuItems) {
            JButton menuItem = new JButton(item);
            menuItem.setBackground(new Color(43, 43, 43));
            menuItem.setBorder(BorderFactory.createEmptyBorder(10,0, 10, 0));
            menuItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            // to go to the subSettings
            menuItem.addActionListener(e -> {
                String command = e.getActionCommand();
                cardLayout.show(contentPanel, command);
            });
            sidebarPanel.add(menuItem);
        }

        return sidebarPanel;
    }

    public static class GeneralPanel extends JPanel implements ChangeListener{

        public GeneralPanel(LayoutManager layout) {
            super(layout);
            JSlider volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, (int) (Properties.volume * 100));
            volumeSlider.addChangeListener(this);
            volumeSlider.setBorder(BorderFactory.createEmptyBorder(0,5,0,10));
            add(volumeSlider, BorderLayout.EAST);

            JPanel header = new JPanel(new BorderLayout());
            JLabel heading = new JLabel("General");
            heading.setFont(new Font("Helvetica Neue", Font.BOLD, 15));
            header.add(heading, BorderLayout.CENTER);
            header.add(new JLabel("Volume"), BorderLayout.EAST);
            header.setBorder(BorderFactory.createEmptyBorder(5, 10, 0,0));
            add(header, BorderLayout.NORTH);

            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));
            body.add(new Setting("Font Size", new JTextArea()));
            add(body);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                AudioHandler.setSystemVolume(source.getValue() / 100f);
                Properties.store("volume", String.valueOf(source.getValue() / 100f));
            }
        }
    }

    public static class Setting extends JPanel {
        public Setting(String heading, Component content) {
            JLabel header = new JLabel(heading);
            setLayout(new BorderLayout());
            add(header, BorderLayout.NORTH);
            add(content, BorderLayout.SOUTH);
        }
    }

    public static class ArtPacksPanel extends JPanel {
        public ArtPacksPanel(LayoutManager layout) {
            super(layout);
            add(new JLabel("Hallo"));
        }
    }

    public static class ColorPacksPanel extends JPanel {
        public ColorPacksPanel(LayoutManager layout) {
            super(layout);

            JPanel sidePanel = new JPanel();
            sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
            sidePanel.setPreferredSize(new Dimension(100, getHeight()));
            sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));
            for (ColorPack cp: Properties.defaultColorPacks) {
                JButton menuItem = new JButton(cp.name());
                menuItem.setSize(new Dimension(sidePanel.getPreferredSize().width / 2, 20));
                menuItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                menuItem.addActionListener(e -> {
                    System.out.println(cp.name());
                });
                sidePanel.add(menuItem);
            }
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.add(sidePanel);
            add(sidePanel, BorderLayout.WEST);
            add(new JPanel(), BorderLayout.CENTER);
        }
    }

    public class SoundPacksPanel extends JPanel {

    }

}
