package typical_if.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiAudio;

import java.util.ArrayList;

import typical_if.android.R;

/**
 * Created by admin on 19.08.2014.
 */
public class AudioAttachAdapter extends BaseAdapter {

    private ArrayList<VKApiAudio> audios;
    private LayoutInflater layoutInflater;

    public AudioAttachAdapter(ArrayList<VKApiAudio> audios, LayoutInflater inflater) {
        this.audios = audios;
        this.layoutInflater = inflater;
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
        return audios.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.audio_attach_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VKApiAudio audio = audios.get(position);

        viewHolder.txt_audio_attach_artist.setText(audio.artist);
        viewHolder.txt_audio_attach_title.setText(audio.title);

        return convertView;
    }

    public static class ViewHolder {
        public final TextView txt_audio_attach_artist;
        public final TextView txt_audio_attach_title;

        public ViewHolder(View convertView) {
            this.txt_audio_attach_artist = (TextView) convertView.findViewById(R.id.txt_audio_attach_artist);
            this.txt_audio_attach_title= (TextView) convertView.findViewById(R.id.txt_audio_attach_title);
        }
    }
}
