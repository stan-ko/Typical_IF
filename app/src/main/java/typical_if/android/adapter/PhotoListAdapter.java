package typical_if.android.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.List;

import typical_if.android.MyApplication;
import typical_if.android.R;

/**
 * Created by LJ on 16.07.2014.
 */
public class PhotoListAdapter extends BaseAdapter {
    List<VKApiPhoto> photoList;
    LayoutInflater layoutInflater;
    final DisplayImageOptions options;

    ImageLoader imageLoader;

    public PhotoListAdapter(List<VKApiPhoto> list, LayoutInflater inflater) {
        this.photoList = list;
        this.layoutInflater = inflater;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.gray_icon) // TODO resource or drawable
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
        final VKApiPhoto photo = photoList.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_photo_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ProgressBar pbPreviewImageIsLoading = viewHolder.pbPreviewImageIsLoading;

        final String photoUrl;
        if (MyApplication.getDisplayHeight()<1000)
            photoUrl = photo.photo_75;
        else
            photoUrl = photo.photo_130;

        imageLoader.getInstance().displayImage(photoUrl, viewHolder.photo, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                pbPreviewImageIsLoading.setVisibility(View.VISIBLE);
                //((ViewGroup)view.getParent()).findViewById(R.id.pbPreviewImageIsLoading).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                pbPreviewImageIsLoading.setVisibility(View.GONE);
                //((ViewGroup)view.getParent()).findViewById(R.id.pbPreviewImageIsLoading).setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                pbPreviewImageIsLoading.setVisibility(View.GONE);
                //((ViewGroup)view.getParent()).findViewById(R.id.pbPreviewImageIsLoading).setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                viewHolder.pbPreviewImageIsLoading.setVisibility(View.GONE);
                //((ViewGroup)view.getParent()).findViewById(R.id.pbPreviewImageIsLoading).setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public final ImageView photo;
        public final ProgressBar pbPreviewImageIsLoading;

        public ViewHolder(View convertView) {
            this.photo = (ImageView) convertView.findViewById(R.id.img_photo_cover);
            this.pbPreviewImageIsLoading = (ProgressBar) convertView.findViewById(R.id.pbPreviewImageIsLoading);
        }
    }

}