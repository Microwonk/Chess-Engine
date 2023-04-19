package net.chess.gui.audio;

public enum Sound {

    MOVE("move.mp3"),
    CAPTURE("capture.mp3"),
    MATE("mate.mp3");

    private final String file;

    Sound (final String file) {
        this.file = file;
    }

    public String getFile () {
        return this.file;
    }
}

