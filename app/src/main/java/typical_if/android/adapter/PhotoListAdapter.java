package typical_if.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import typical_if.android.R;
import typical_if.android.model.Photo;

/**
 * Created by LJ on 16.07.2014.
 */
public class PhotoListAdapter extends BaseAdapter {
    List<Photo> photoList;
    LayoutInflater layoutInflater;
    final DisplayImageOptions options;

    ImageLoader imageLoader;

    public PhotoListAdapter(List<Photo> list, LayoutInflater inflater) {
        this.photoList = list;
        this.layoutInflater = inflater;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stubif) // TODO resource or drawable
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
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return photoList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Photo photo = photoList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_photo_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        imageLoader.getInstance().displayImage(photo.photo_604, viewHolder.photo, options);

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView photo;


        public ViewHolder(View convertView) {

            this.photo = (ImageView) convertView.findViewById(R.id.img_photo_cover);

        }
    }


}
