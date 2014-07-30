package typical_if.android.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.model.UploadPhotos;

;

/**
 * Created by LJ on 25.07.2014.
 */
public class PhotoUploadAdapter extends BaseAdapter {
    String titlename;
    LayoutInflater layoutInflater;
    final DisplayImageOptions options;
    ArrayList<UploadPhotos> uploadphotolist;
    ImageLoader imageLoader;

    public PhotoUploadAdapter(String titlename, LayoutInflater inflater, ArrayList uploadphotolist) {
        this.titlename = titlename;
        this.layoutInflater = inflater;
        this.uploadphotolist=uploadphotolist;
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
        return uploadphotolist.size();
    }

    @Override
    public Object getItem(int position) {
        return uploadphotolist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_add_photo_item, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        File file = new File(uploadphotolist.get(position).photosrc);
        imageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), viewHolder.photo, options);

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                    uploadphotolist.get(position).ischecked = cb.isChecked();
                if (cb.isChecked()==true) {
                    viewHolder.background.setVisibility(View.VISIBLE);
                }
                else if (cb.isChecked()==false) {
                    viewHolder.background.setVisibility(View.INVISIBLE);
                }
            }
        });


        UploadPhotos up = uploadphotolist.get(position);
        viewHolder.checkbox.setChecked(up.ischecked);

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView photo;
        public CheckBox checkbox;
        public final ImageView background;

        public ViewHolder(View convertView) {
            this.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox_for_upload);
            this.photo = (ImageView) convertView.findViewById(R.id.thumb_photo_from_sd);
            this.background = (ImageView) convertView.findViewById(R.id.background_check);
        }
    }
}
