package typical_if.android;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.vk.sdk.VKUIHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LJ on 12.08.2014.
 */
public class AudioPlayer {

    public static void getOwnMadiaPlayer(final Activity activity, final String stream, final CheckBox play, final SeekBar progress) {

        final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

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
                                        Log.d("PLAYER IS STOPED", "YES");
                                        Constants.mediaPlayer.release();
                                    }
                                }
                                Constants.mediaPlayer = new MediaPlayer();
                                Constants.mediaPlayer.setDataSource(stream);
                                Constants.previousCheckBoxState = play;
                                Constants.previousSeekBarState = progress;
                                progress.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Constants.mediaPlayer.setAudioStreamType(audioManager.STREAM_MUSIC);
                            Constants.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {

                                    Constants.mediaPlayer.start();
                                    Constants.tempThread = progressBar(progress);
                                    Constants.tempThread.start();
                                    Constants.playedPausedRecord.audioUrl = stream;
                                    Constants.playedPausedRecord.isPlayed = true;
                                    Constants.playedPausedRecord.isPaused = false;
                                    //VKUIHelper.getTopActivity().startService(new Intent(VKUIHelper.getTopActivity().getApplicationContext(), AudioPlayerService.class));

                                }
                            });
                            Constants.mediaPlayer.prepareAsync();
                        }
                        else if (Constants.playedPausedRecord.audioUrl == stream && Constants.playedPausedRecord.isPaused == true){
                            Constants.mediaPlayer.start();
                            Constants.playedPausedRecord.audioUrl = stream;
                            Constants.playedPausedRecord.isPlayed = true;
                            Constants.playedPausedRecord.isPaused = false;
                        }
                }
                else {
                    Constants.mediaPlayer.pause();
                    Constants.playedPausedRecord = new AudioRecords(stream, false, true, false);
                }
            }
        });

    }


    public static Thread progressBar(final SeekBar progress){
        Thread seekTo = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                int total = Constants.mediaPlayer.getDuration();
                progress.setMax(total);
                Constants.playedPausedRecord.totalDuration = total;
                while (Constants.mediaPlayer != null) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = Constants.mediaPlayer.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
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
