package net.chess.gui.util;
@FunctionalInterface
public interface Loadable<T> {
    T load(String filename);
}
