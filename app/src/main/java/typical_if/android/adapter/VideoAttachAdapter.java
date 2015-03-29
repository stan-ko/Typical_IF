package typical_if.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiVideo;

import java.util.ArrayList;

import typical_if.android.ItemDataSetter;
import typical_if.android.R;

/**
 * Created by admin on 19.08.2014.
 */
public class VideoAttachAdapter extends BaseAdapter {

    private ArrayList<VKApiVideo> videos;
    private LayoutInflater layoutInflater;

    public VideoAttachAdapter(ArrayList<VKApiVideo> videos, LayoutInflater inflater) {
        this.videos = videos;
        this.layoutInflater = inflater;
    }


    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return videos.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.video_attach_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VKApiVideo video = videos.get(position);

//        Glide.with(TIFApp.getAppContext())
//                .load(video.photo_320)
//                .placeholder(R.drawable.event_stub)
//                .crossFade()
//                .into(viewHolder.img_video_attach);
        ImageLoader.getInstance().displayImage(video.photo_320, viewHolder.img_video_attach);
        viewHolder.txt_video_attach_duration.setText(ItemDataSetter.getMediaTime(video.duration));
        viewHolder.txt_video_attach_title.setText(video.title);

        return convertView;
    }

    public static class ViewHolder {
        public final TextView txt_video_attach_duration;
        public final TextView txt_video_attach_title;
        public final ImageView img_video_attach;

        public ViewHolder(View convertView) {
            this.txt_video_attach_duration = (TextView) convertView.findViewById(R.id.txt_video_attach_duration);
            this.txt_video_attach_title= (TextView) convertView.findViewById(R.id.txt_video_attach_title);
            this.img_video_attach = (ImageView) convertView.findViewById(R.id.img_video_attach);
        }
    }
}
