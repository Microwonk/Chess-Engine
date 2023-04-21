package net.chess.gui.util;

import java.io.File;

public record SoundPack(String location, String name) implements Loadable<File> {
    @Override
    public File load (String filename) {
        return new File(location.isEmpty() ? Properties.defArtPath : location);
    }
}
