package net.chess.gui;

import javax.sound.sampled.*;
import java.io.File;

public class AudioHandler {
    private final Sound[] sounds;

    public AudioHandler() {
        sounds = new Sound[]{Sound.MOVE, Sound.CAPTURE, Sound.MATE};
    }

    void playSound(int soundType) {
        try {
            File f = new File("assets/pieces/sounds/" + sounds[soundType].getFile());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(f);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            audioClip.close();
            audioStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Sound {

        MOVE("move.wav"),
        CAPTURE("capture.wav"),
        MATE("mate.wav");

        private final String file;

        Sound(final String file) {
            this.file = file;
        }

        public String getFile() {
            return this.file;
        }

    }

}
