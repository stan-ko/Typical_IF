package typical_if.android.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.R;

/**
 * Created by LJ on 25.07.2014.
 */
public class AlbumUploadAdapter extends BaseAdapter {
    ArrayList<String> titles;
    LayoutInflater layoutInflater;
    String[] arrPath;

    public AlbumUploadAdapter(ArrayList<String> titles, LayoutInflater layoutInflater, String[] arrPath) {
        this.titles = titles;
        this.layoutInflater = layoutInflater;
        this.arrPath = arrPath;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_album_upload_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int count = 0;
        final ArrayList covers = new ArrayList();
        for (String anArrPath : arrPath) {
            String[] temp = anArrPath.split("/");
            if (temp[temp.length - 2].equals(titles.get(position))) {
                count++;
                covers.add(anArrPath);
            }
        }

        File file = new File((String) covers.get(covers.size() - 1));

        ImageLoader.getInstance().displayImage(String.valueOf(Uri.fromFile(file)), viewHolder.album_cover);
        viewHolder.photos_count.setText(count + " фото");
        viewHolder.album_name.setText(titles.get(position));

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView album_cover;
        public final TextView album_name;
        public final TextView photos_count;

        public ViewHolder(View convertView) {
            this.album_cover = (ImageView) convertView.findViewById(R.id.album_image_image_view);
            this.album_name = (TextView) convertView.findViewById(R.id.album_title_for_upload);
            this.photos_count = (TextView) convertView.findViewById(R.id.count_of_photos);
        }
    }
}
