package net.chess.gui.audio;

import javazoom.jl.decoder.*;
import javazoom.jl.player.Player;
import net.chess.exception.ChessException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import java.io.*;

import static net.chess.gui.util.Properties.soundOn;

/**
 * Audio Handler using JLayer library
 *
 * @author Nicolas Frey
 * @version 1.0
 */
public class AudioHandler {
    private static final Sound[] sounds;

    static {
        sounds = new Sound[]{Sound.MOVE, Sound.CAPTURE, Sound.MATE};
    }

    private AudioHandler () {
        throw new ChessException ("AudioHandler may not be initialized");
    }

    public static void playSound (final int soundType) {
        if (!soundOn || soundType > 2 || soundType < 0) return;
        try {
            FileInputStream fileInputStream = new FileInputStream("assets/sounds/chesscom/" + sounds[soundType].getFile());
            Player player = new Player(fileInputStream);
            Thread playerThread = new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });
            playerThread.start();
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    // works!
    public static void setSystemVolume(float volume) {
        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            for (Mixer.Info info : mixerInfo) {
                Mixer mixer = AudioSystem.getMixer(info);
                if (mixer.isLineSupported(Port.Info.SPEAKER)) {
                    Port port = (Port) mixer.getLine(Port.Info.SPEAKER);
                    port.open();
                    FloatControl volumeControl = (FloatControl) port.getControl(FloatControl.Type.VOLUME);
                    volumeControl.setValue(volume);
                    port.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
