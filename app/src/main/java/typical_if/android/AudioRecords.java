package typical_if.android;

import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * Created by LJ on 18.08.2014.
 */
public class AudioRecords {

    String audioUrl;
    boolean isFirstTimePlayed;
    boolean isPaused;
    boolean isPlayed;
    int progresBarposition = 0;
    int totalDuration = 0;

    AudioRecords(String audioUrl, boolean isFirstTimePlayed, boolean isPaused, boolean isPlayed){
        this.audioUrl = audioUrl;
        this.isFirstTimePlayed = isFirstTimePlayed;
        this.isPaused = isPaused;
        this.isPlayed = isPlayed;
    }

}
