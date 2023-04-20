package net.chess.gui.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;

public record ArtPack(String location, String name, java.util.Properties packInfo) implements Loadable<ImageIcon> {
    @Override
    public ImageIcon load (String filename) {
        try {
            return new ImageIcon(
                    ImageIO.read(
                            new File((location.isEmpty() ? Properties.artPath : location) + filename + ".png"
                    )).getScaledInstance(width(), height(), 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArtPack {
        try {
            packInfo.load(new FileReader(location + "/pack.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int width() {
        return Integer.parseInt(packInfo.getProperty("width"));
    }

    public int height() {
        return Integer.parseInt(packInfo.getProperty("height"));
    }

    public Point offset() {
        return new Point(
                Integer.parseInt(packInfo.getProperty("offset").split(",")[0])
                , Integer.parseInt(packInfo.getProperty("offset").split(",")[1]));
    }
}
