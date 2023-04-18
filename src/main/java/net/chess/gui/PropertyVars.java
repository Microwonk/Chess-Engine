package net.chess.gui;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class PropertyVars {

    //TODO: let user change .properties in settings menu -> properties profiles and so on

    //config variables
    protected static final List <ColorPack> defaultColorPacks = List.of(
            new ColorPack(new Color(196, 189, 175), new Color(155, 132, 75), "Default"),
            new ColorPack(new Color(255, 100, 100), new Color(100, 20, 20), "Red"),
            new ColorPack(new Color(200, 10, 200), new Color(90, 10, 90), "Purple")
    );
    protected static Properties properties = new Properties();
    protected static String artPath;
    protected static String miscPath;
    protected static boolean highlightLegalMovesActive;
    protected static String savePath;
    protected static boolean signifyChecksActive;
    public static boolean soundOn;
    protected static ColorPack colorPack;

    static {
        try {
            properties.load(new FileReader("config/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        artPath = properties.getProperty("artPath");
        miscPath = properties.getProperty("miscPath");
        savePath = properties.getProperty("savePath");
        signifyChecksActive = properties.getProperty("signifyChecksActive").equals("true");
        highlightLegalMovesActive = properties.getProperty("highlightLegalMovesActive").equals("true");
        soundOn = properties.getProperty("soundOn").equals("true");
        colorPack = getPackByName(properties.getProperty("colorPack"));
    }

    public static void init () {
        // not inited yet GUI_Contents.get().getLogger().printLog("Initialized variables");
    }

    public static ColorPack getPackByName(String name) {
        return defaultColorPacks.stream().filter(c -> c.name().equals(name)).toList().get(0);
    }

}
