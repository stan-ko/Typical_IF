package typical_if.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiAudio;

import java.util.ArrayList;

import typical_if.android.AudioPlayer;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;

/**
 * Created by gigamole on 01.02.15.
 */
public class AudioAdapter extends BaseAdapter {

    public final ArrayList<VKApiAudio> audios;

    public final LayoutInflater layoutInflater;
    public final Context context;

    public AudioAdapter(ArrayList<VKApiAudio> audios, Context context) {
        this.audios = audios;

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return audios.size();
    }

    @Override
    public Object getItem(int position) {
        return audios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VKApiAudio audio = (VKApiAudio) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.audio_container, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (AudioPlayer.playedPausedRecord.audioUrl != null && AudioPlayer.playedPausedRecord.audioUrl.equals(audio.url) && AudioPlayer.playedPausedRecord.isPlayed) {
            viewHolder.cbAction.setChecked(true);

            try {
                AudioPlayer.tempThread.interrupt();
            } catch (NullPointerException e) {
            }

            AudioPlayer.progressBar(viewHolder.progress).start();
            AudioPlayer.tempThread = AudioPlayer.progressBar(viewHolder.progress);
            Constants.previousCheckBoxState = viewHolder.cbAction;
            Constants.previousSeekBarState = viewHolder.progress;
            viewHolder.progress.setVisibility(View.VISIBLE);
        }
        if (AudioPlayer.playedPausedRecord.audioUrl != null && AudioPlayer.playedPausedRecord.audioUrl.equals(audio.url) && AudioPlayer.playedPausedRecord.isPaused) {

            AudioPlayer.progressBar(viewHolder.progress).start();
            try {
                AudioPlayer.tempThread.interrupt();
            } catch (NullPointerException e) {
            }
            viewHolder.progress.setVisibility(View.VISIBLE);
            AudioPlayer.tempThread = AudioPlayer.progressBar(viewHolder.progress);
        }


        viewHolder.duration.setText(ItemDataSetter.getMediaTime(audio.duration));
        viewHolder.artist.setText(audio.artist);
        viewHolder.title.setText(audio.title);

        AudioPlayer.getOwnMediaPlayer(audio.url, viewHolder.cbAction, viewHolder.progress, audio.title, audio.artist);

        return convertView;
    }

    public static class ViewHolder {
        public final CheckBox cbAction;
        public final SeekBar progress;
        public final TextView duration;
        public final TextView title;
        public final TextView artist;

        ViewHolder(View view) {
            this.cbAction = (CheckBox) view.findViewById(R.id.cb_audio_play_or_stop);
            this.progress = (SeekBar) view.findViewById(R.id.progress_audio);
            this.duration = (TextView) view.findViewById(R.id.txt_audio_duration);
            this.title = (TextView) view.findViewById(R.id.txt_audio_title);
            this.artist = (TextView) view.findViewById(R.id.txt_audio_artist);
        }
    }
}
