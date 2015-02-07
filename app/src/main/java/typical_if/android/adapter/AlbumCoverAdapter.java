package typical_if.android.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.model.Album;

public class AlbumCoverAdapter extends BaseAdapter {
    private List<Album> albumList;
    LayoutInflater layoutInflater;
    public static int _albumSize;

    int newWidth = TIFApp.getDisplayWidth();

    public ArrayList<View> views = new ArrayList<View>();

    public AlbumCoverAdapter(List<Album> list, LayoutInflater inflater) {
        albumList = list;
        layoutInflater = inflater;

        for (Album album : albumList) {
            views.add(null);
        }
    }


    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return albumList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Album album = albumList.get(position);
        _albumSize = album.size;

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_albums_list_item, null);
            viewHolder = new ViewHolder(convertView);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, (int) (newWidth / 1.5));
            viewHolder.album_cover.setLayoutParams(params);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.album_name.setText(album.title);
        viewHolder.photos_count.setText(album.size + "");

        String url = album.sizes.optJSONObject(2).optString("src");
        ImageLoader.getInstance().displayImage(url, viewHolder.album_cover, TIFApp.additionalOptions);

        views.set(position, convertView);

        return convertView;
    }

    public Drawable getImageBitmap(int position) {
         ViewHolder viewHolder = (ViewHolder) views.get(position).getTag();
        return viewHolder.album_cover.getDrawable();
    }

    public static class ViewHolder {
        public final ImageView album_cover;
        public final TextView album_name;
        public final TextView photos_count;

        public ViewHolder(View convertView) {
            this.album_cover = (ImageView) convertView.findViewById(R.id.img_album_cover);
            this.album_name = (TextView) convertView.findViewById(R.id.txt_album_name);
            this.photos_count = (TextView) convertView.findViewById(R.id.txt_photos_count);
        }
    }


}