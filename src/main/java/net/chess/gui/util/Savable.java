package net.chess.gui.util;

import java.io.Serializable;

@FunctionalInterface
public interface Savable<T> extends Serializable {
    void save(T toSave, String path);
}
