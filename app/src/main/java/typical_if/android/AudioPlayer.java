package typical_if.android;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import java.io.IOException;

import typical_if.android.util.StoppableThread;


/**
 * Created by LJ on 12.08.2014.
 */
public class AudioPlayer {

    static final long PROGRESS_UPDATE_TIME = 1000;
    static MediaPlayer mediaPlayer;
    public static StoppableThread tempThread;
    public static AudioRecords playedPausedRecord = new AudioRecords(null, false, false, false);

    public static void stop(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
            playedPausedRecord.isPaused = true;
            playedPausedRecord.isPlayed = false;
            Constants.previousSeekBarState.setVisibility(View.INVISIBLE);
            AudioPlayer.progressBar(Constants.previousSeekBarState).interrupt();
        }
        if (tempThread!=null) {
            tempThread.stopThread();
            tempThread = null;
        }
    }

    public static void getOwnMediaPlayer(final String stream, final CheckBox play, final SeekBar progress, final String songTitle, final String singer) {

        //final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        play.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {

                    if (play != Constants.previousCheckBoxState && Constants.previousCheckBoxState != null){
                        Constants.previousCheckBoxState.setChecked(false);
                    }
                    if (progress == Constants.previousSeekBarState){
                        progress.setVisibility(View.VISIBLE);
                    }
                    if (progress != Constants.previousSeekBarState && Constants.previousSeekBarState != null){
                        Constants.previousSeekBarState.setVisibility(View.INVISIBLE);
                    }
                    if (playedPausedRecord.audioUrl == null || playedPausedRecord.audioUrl != stream) {
                        try {
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                    //Log.d("PLAYER IS STOPED", "YES");
                                    mediaPlayer.release();
                                }
                            }
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(stream);
                            play.setClickable(false);
                            Constants.previousCheckBoxState = play;
                            Constants.previousSeekBarState = progress;
                            Constants.title = songTitle;
                            Constants.artist = singer;

                            progress.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                mediaPlayer.start();
                                play.setClickable(true);
                                tempThread = progressBar(progress);
                                tempThread.start();
                                playedPausedRecord.audioUrl = stream;
                                playedPausedRecord.isPlayed = true;
                                playedPausedRecord.isPaused = false;
                                TIFApp.getAppContext().stopService(Constants.myIntent);
                                TIFApp.getAppContext().startService(Constants.myIntent);
                            }
                        });
                        mediaPlayer.prepareAsync();
//                        Constants.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mp) {
//                                FragmentWall.refresh();
//                            }
//                        });

                    }
                    else if (playedPausedRecord.audioUrl == stream && playedPausedRecord.isPaused == true){
                        mediaPlayer.start();
                        playedPausedRecord.audioUrl = stream;
                        playedPausedRecord.isPlayed = true;
                        playedPausedRecord.isPaused = false;
                        TIFApp.getAppContext().stopService(Constants.myIntent);
                        TIFApp.getAppContext().startService(Constants.myIntent);
                    }
                }
                else {
                    mediaPlayer.pause();
                    playedPausedRecord = new AudioRecords(stream, false, true, false);
                    TIFApp.getAppContext().stopService(Constants.myIntent);
                    TIFApp.getAppContext().startService(Constants.myIntent);
                    Constants.timerForNotif = System.currentTimeMillis();
                    //AudioPlayerService.cancelNotification(Constants.mainActivity.getApplicationContext() , Constants.notifID);
                }
            }
        });
    }


    public static StoppableThread progressBar(final SeekBar progress){
        return new StoppableThread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    int total = mediaPlayer.getDuration();
                    progress.setMax(total);
                    playedPausedRecord.totalDuration = total;
                }
                catch (IllegalStateException e){}
                while (mediaPlayer != null) {
                    if (((StoppableThread)Thread.currentThread()).isStopped)
                        return;
                    try {
                        Thread.sleep(PROGRESS_UPDATE_TIME);
                        currentPosition = mediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }

                    if (((StoppableThread)Thread.currentThread()).isStopped)
                        return;

                    if (System.currentTimeMillis() >= (Constants.timerForNotif + 10000) && playedPausedRecord.isPaused){
                        TIFApp.getAppContext().stopService(Constants.myIntent);
                        AudioPlayerService.cancelNotification(TIFApp.getAppContext(), Constants.notifID);
                        Constants.timerForNotif = 0;
                    }

                    progress.setProgress(currentPosition);
                    playedPausedRecord.progresBarposition = currentPosition;
                    progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                mediaPlayer.seekTo(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {}

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {}
                    });
                }
            }
        });
    }
}
