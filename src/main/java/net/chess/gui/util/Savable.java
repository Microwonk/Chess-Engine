package net.chess.gui.util;

@FunctionalInterface
public interface Savable<T> {
    void save(T toSave, String path);
}
