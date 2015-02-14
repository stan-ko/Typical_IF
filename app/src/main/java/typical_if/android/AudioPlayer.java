package typical_if.android;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import java.io.IOException;


/**
 * Created by LJ on 12.08.2014.
 */
public class AudioPlayer {

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
                    if (Constants.playedPausedRecord.audioUrl == null || Constants.playedPausedRecord.audioUrl != stream) {
                        try {
                            if (Constants.mediaPlayer != null) {
                                if (Constants.mediaPlayer.isPlaying()) {
                                    Constants.mediaPlayer.stop();
                                    //Log.d("PLAYER IS STOPED", "YES");
                                    Constants.mediaPlayer.release();
                                }
                            }
                            Constants.mediaPlayer = new MediaPlayer();
                            Constants.mediaPlayer.setDataSource(stream);
                            play.setClickable(false);
                            Constants.previousCheckBoxState = play;
                            Constants.previousSeekBarState = progress;
                            Constants.title = songTitle;
                            Constants.artist = singer;

                            progress.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Constants.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        Constants.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                Constants.mediaPlayer.start();
                                play.setClickable(true);
                                Constants.tempThread = progressBar(progress);
                                Constants.tempThread.start();
                                Constants.playedPausedRecord.audioUrl = stream;
                                Constants.playedPausedRecord.isPlayed = true;
                                Constants.playedPausedRecord.isPaused = false;
                                Constants.mainActivity.stopService(Constants.myIntent);
                                Constants.mainActivity.startService(Constants.myIntent);
                            }
                        });
                        Constants.mediaPlayer.prepareAsync();
//                        Constants.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mp) {
//                                FragmentWall.refresh();
//                            }
//                        });

                    }
                    else if (Constants.playedPausedRecord.audioUrl == stream && Constants.playedPausedRecord.isPaused == true){
                        Constants.mediaPlayer.start();
                        Constants.playedPausedRecord.audioUrl = stream;
                        Constants.playedPausedRecord.isPlayed = true;
                        Constants.playedPausedRecord.isPaused = false;
                        Constants.mainActivity.stopService(Constants.myIntent);
                        Constants.mainActivity.startService(Constants.myIntent);
                    }
                }
                else {
                    Constants.mediaPlayer.pause();
                    Constants.playedPausedRecord = new AudioRecords(stream, false, true, false);
                    Constants.mainActivity.stopService(Constants.myIntent);
                    Constants.mainActivity.startService(Constants.myIntent);
                    Constants.timerForNotif = System.currentTimeMillis();
                    //AudioPlayerService.cancelNotification(Constants.mainActivity.getApplicationContext() , Constants.notifID);
                }
            }
        });
    }


    public static Thread progressBar(final SeekBar progress){
        Thread seekTo = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    int total = Constants.mediaPlayer.getDuration();
                    progress.setMax(total);
                    Constants.playedPausedRecord.totalDuration = total;
                }
                catch (IllegalStateException e){

                }
                while (Constants.mediaPlayer != null) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = Constants.mediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }

                    if (System.currentTimeMillis() >= (Constants.timerForNotif + 10000) && Constants.playedPausedRecord.isPaused == true){
                        Constants.mainActivity.stopService(Constants.myIntent);
                        AudioPlayerService.cancelNotification(Constants.mainActivity.getApplicationContext(), Constants.notifID);
                        Constants.timerForNotif = 0;
                    }

                    progress.setProgress(currentPosition);
                    Constants.playedPausedRecord.progresBarposition = currentPosition;
                    progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                Constants.mediaPlayer.seekTo(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                }
            }
        });
        return seekTo;
    }
}
