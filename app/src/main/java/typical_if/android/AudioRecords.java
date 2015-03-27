package typical_if.android;

/**
 * Created by LJ on 18.08.2014.
 */
public class AudioRecords {

    public String audioUrl;
    boolean isFirstTimePlayed;
    public boolean isPaused;
    public boolean isPlayed;
    int progresBarposition = 0;
    int totalDuration = 0;

    AudioRecords(String audioUrl, boolean isFirstTimePlayed, boolean isPaused, boolean isPlayed){
        this.audioUrl = audioUrl;
        this.isFirstTimePlayed = isFirstTimePlayed;
        this.isPaused = isPaused;
        this.isPlayed = isPlayed;
    }

}
