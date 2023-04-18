package net.chess.gui.audio;

import jaco.mp3.player.MP3Player;
import net.chess.exception.ChessException;
import net.chess.gui.GUI_Contents;

import javax.sound.sampled.*;
import java.io.*;
import javax.sound.sampled.*;
import java.io.InputStream;

import static net.chess.gui.PropertyVars.soundOn;

/**
 * Audio Handler using "jaco mp3 library"
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
        throw new ChessException("AudioHandler may not be initialized");
    }

    public static void playSound (final int soundType) {
        if (!soundOn) return;

        if (soundType > 2 || soundType < 0) return;
        try {
            // create input and output files
            File oggFile = new File("my_song.ogg");

            // read MP3 file using JLayer
            FileInputStream fis = new FileInputStream(mp3File);
            BufferedInputStream bis = new BufferedInputStream(fis);
            Decoder decoder = new Decoder();
            AudioInputStream ais = null;
            try {
                Bitstream bs = new Bitstream(bis);
                Header h = bs.readFrame();
                decoder = new Decoder(h);
                ais = new AudioInputStream(decoder.decodeFrame(h, bs), h.mode() == Mode.MONO ? new AudioFormat(Encoding.PCM_SIGNED, h.getSampleRate(), 16, 1, 2, h.getSampleRate(), false) : new AudioFormat(Encoding.PCM_SIGNED, h.getSampleRate(), 16, 2, 4, h.getSampleRate(), false), -1);
            } catch (BitstreamException ex) {
                ex.printStackTrace();
            }

            // convert to Ogg Vorbis format
            AudioInputStream encodedAIS = AudioSystem.getAudioInputStream(new OggVorbisEncoding(), ais);
            AudioSystem.write(encodedAIS, AudioFileFormat.Type.WAVE, oggFile);

            // play back Ogg Vorbis file
            AudioInputStream oggAIS = AudioSystem.getAudioInputStream(oggFile);
            AudioFormat oggFormat = oggAIS.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, oggFormat);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(oggAIS);
            clip.start();

            // wait for clip to finish playing
            while (clip.isRunning()) {
                Thread.sleep(10);
            }

            // close streams and clip
            clip.close();
            oggAIS.close();
            encodedAIS.close();
            ais.close();
            bis.close();
            fis.close();
            oggFile.delete(); // delete the temporary Ogg file
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

}
