package net.chess.gui.util;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public record ColorPack (Color light, Color dark, String name) implements Serializable {
    @Serial
    private final static long serialVersionUID = 10938102938012938L;
}
