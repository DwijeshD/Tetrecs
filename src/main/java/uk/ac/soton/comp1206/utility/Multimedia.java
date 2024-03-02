package uk.ac.soton.comp1206.utility;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Multimedia {
    private static final Logger logger = LogManager.getLogger(uk.ac.soton.comp1206.utility.Multimedia.class);
    private static final BooleanProperty audioEnabledProperty = new SimpleBooleanProperty(true);
    private static MediaPlayer audioPlayer;
    private static MediaPlayer backgroundMusicPlayer;
    /**
     * Play an audio file
     * @param file filename to play from resources
     */
    public static void playAudio(String file) {

        if (!getAudioEnabled()) return;
        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing music: " + toPlay.substring(toPlay.lastIndexOf("/") + 1));

        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            setAudioEnabled(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    public static void playBackgroundMusic(String file) {
        if (!getAudioEnabled()) return;
        String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing music: " + toPlay.substring(toPlay.lastIndexOf("/") + 1));

        try {
            Media play = new Media(toPlay);
            backgroundMusicPlayer = new MediaPlayer(play);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusicPlayer.play();

        } catch (Exception e) {
            setAudioEnabled(false);
            e.printStackTrace();
            logger.error("Unable to play music file, disabling audio");
        }
    }

    public static BooleanProperty audioEnabledProperty(){
        return audioEnabledProperty;
    }

    public static void setAudioEnabled(boolean enabled) {
        logger.info("Audio enabled set to: " + enabled);
        audioEnabledProperty().set(enabled);
    }

    public static boolean getAudioEnabled() {
        return audioEnabledProperty().get();
    }

    public static void stopMusic() {
        backgroundMusicPlayer.stop();
    }

    public static void playFileOnce(String file, String secondFile) {
        if (!getAudioEnabled()) return;
        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        logger.info("Playing music: " + toPlay.substring(toPlay.lastIndexOf("/") + 1));

        try {
            Media play = new Media(toPlay);
            backgroundMusicPlayer = new MediaPlayer(play);
            backgroundMusicPlayer.play();
            backgroundMusicPlayer.setOnEndOfMedia(() -> {
                playBackgroundMusic(secondFile);
            });

        } catch (Exception e) {
            setAudioEnabled(false);
            e.printStackTrace();
            logger.error("Unable to play music file, disabling audio");
        }
    }
}
