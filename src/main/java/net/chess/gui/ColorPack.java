package net.chess.gui;

import java.awt.*;

public record ColorPack (Color light, Color dark, String name) {
    public ColorPack (int r1, int g1, int b1, int r2, int g2, int b2, String name) {
        this(new Color(r1, g1, b1), new Color(r2, g2, b2), name);
    }
}
