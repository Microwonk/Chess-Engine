package net.chess.gui.util;

import net.chess.engine.board.Square;
import net.chess.engine.pieces.Piece;
import net.chess.gui.audio.AudioHandler;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Variables {

    //TODO: let user change .properties in settings menu -> properties profiles and so on

    //config variables
    public static final Set <ColorPack> defaultColorPacks = new HashSet <>(List.of(
            new ColorPack(new Color(196, 189, 175), new Color(155, 132, 75), "Default"),
            new ColorPack(new Color(255, 100, 100), new Color(100, 20, 20), "Red"),
            new ColorPack(new Color(200, 10, 200), new Color(90, 10, 90), "Purple")
    ));
    public static final Set <ArtPack> defaultArtPacks = new HashSet <>(List.of(
            new ArtPack("assets/art/default/pixel_art/", "PixelArt", new java.util.Properties())
    ));
    public static java.util.Properties properties = new java.util.Properties();
    public static String defArtPath;
    public static String miscPath; // TODO: move into artPacks?
    public static String soundsPath;
    public static boolean highlightLegalMovesActive;
    public static String savePath;
    public static boolean signifyChecksActive;
    public static boolean soundOn;
    public static ColorPack colorPack;
    public static ArtPack artPack;
    public static float volume;

    // running game Variables
    public static boolean movingEnabled;
    public static Square sourceSquare;
    public static Square destinationSquare;
    public static Piece movedPiece;
    public static int currentMove;

    static {
        try {
            properties.load(new FileReader("config/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        defArtPath = properties.getProperty("defArtPath");
        miscPath = properties.getProperty("miscPath");
        savePath = properties.getProperty("savePath");
        soundsPath = properties.getProperty("soundsPath");
        signifyChecksActive = properties.getProperty("signifyChecksActive").equals("true");
        highlightLegalMovesActive = properties.getProperty("highlightLegalMovesActive").equals("true");
        soundOn = properties.getProperty("soundOn").equals("true");
        colorPack = getColorPackByName(properties.getProperty("colorPack"));
        artPack = getArtPackByName(properties.getProperty("artPack"));
        volume = Float.parseFloat(properties.getProperty("volume"));
        movingEnabled = true;
        currentMove = 0;
    }

    public static void store(String key, String value) {
        try {
            properties.setProperty(key, value);
            properties.store(new FileWriter("config/config.properties"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init () {
        AudioHandler.setSystemVolume(volume);
    }

    public static ColorPack getColorPackByName(String name) {
        return defaultColorPacks.stream().filter(c -> c.name().equals(name)).toList().get(0);
    }

    public static ArtPack getArtPackByName(String name) {
        return defaultArtPacks.stream().filter(c -> c.name().equals(name)).toList().get(0);
    }
}
