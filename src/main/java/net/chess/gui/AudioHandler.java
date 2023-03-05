package main.java.net.chess.gui;

import jaco.mp3.player.MP3Player;
import java.io.File;

/** Audio Handler using jaco mp3 library
 * @author Nicolas Frey
 * @version 1.0
 */
public class AudioHandler {
    private final Sound[] sounds;

    public AudioHandler() {
        sounds = new Sound[]{Sound.MOVE, Sound.CAPTURE, Sound.MATE};
    }

    void playSound(final int soundType) {
        if (soundType > 2 || soundType < 0) return;
        try {
            new MP3Player(new File("assets/pieces/sounds/" + sounds[soundType].getFile())).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Sound {

        MOVE("move.mp3"),
        CAPTURE("capture.mp3"),
        MATE("mate.mp3");

        private final String file;

        Sound(final String file) {
            this.file = file;
        }

        public String getFile() {
            return this.file;
        }
    }

}
