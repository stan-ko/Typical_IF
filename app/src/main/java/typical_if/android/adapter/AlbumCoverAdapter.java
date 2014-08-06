package typical_if.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import typical_if.android.MyApplication;
import typical_if.android.R;
import typical_if.android.model.Album;

public class AlbumCoverAdapter extends BaseAdapter {
    private List<Album> albumList;
    private DisplayImageOptions options;
    LayoutInflater layoutInflater;
    final int imageHeight;
    ImageLoader imageLoader;
   public static int _albumSize;

    public AlbumCoverAdapter(List<Album> list, LayoutInflater inflater) {
        albumList = list;
        layoutInflater = inflater;
        imageHeight = MyApplication.getDisplayHeight() / 3;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // TODO resource or drawable
                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
                .showImageOnFail(R.drawable.ic_error) // TODO resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
//            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
//            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .build();
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
        _albumSize= album.size;

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_albums_list_item, null);
            viewHolder = new ViewHolder(convertView);
            ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) viewHolder.album_cover.getLayoutParams();
            lp.height = imageHeight;
            viewHolder.album_cover.setLayoutParams(lp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.album_name.setText(album.title);
        viewHolder.photos_count.setText(album.size + "");

        String url = album.sizes.optJSONObject(2).optString("src");
        imageLoader.getInstance().displayImage(url, viewHolder.album_cover, options);

        return convertView;
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