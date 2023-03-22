package net.chess.gui;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class PropertyVars {

    //TODO: let user change .properties in settings menu -> properties profiles and so on

    //config variables
    protected static final java.util.List <Color[]> colorPacks = List.of(new Color[]{new Color(196, 189, 175), new Color(155, 132, 75)}, new Color[]{}); // more color packs
    protected static Properties properties = new Properties();
    protected static String artPath;
    protected static String miscPath;
    protected static boolean highlightLegalMovesActive;
    protected static String savePath;
    protected static boolean signifyChecksActive;
    protected static boolean soundOn;
    protected static Color lightColour;
    protected static Color darkColour;

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
        lightColour = colorPacks.get(Integer.parseInt(properties.getProperty("colorPack")))[0];
        darkColour = colorPacks.get(Integer.parseInt(properties.getProperty("colorPack")))[1];
    }

}
