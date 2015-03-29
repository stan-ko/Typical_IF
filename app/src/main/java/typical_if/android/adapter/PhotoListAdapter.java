package typical_if.android.adapter;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.felipecsl.asymmetricgridview.library.AsymmetricGridViewAdapterContract;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.List;

import typical_if.android.R;
import typical_if.android.util.PhotoUrlHelper;

/**
 * Created by LJ on 16.07.2014.
 */
public class PhotoListAdapter extends BaseAdapter implements AsymmetricGridViewAdapterContract {
    List<VKApiPhoto> photoList;
    LayoutInflater layoutInflater;

    public PhotoListAdapter(List<VKApiPhoto> list, LayoutInflater inflater) {
        this.photoList = list;
        this.layoutInflater = inflater;
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

        // final ProgressBar pbPreviewImageIsLoading = viewHolder.pbPreviewImageIsLoading;


//        Glide.with(TIFApp.getAppContext())
//                .load(PhotoUrlHelper.getPreviewUrl(photo))
//                .placeholder(R.drawable.event_stub)
//                .crossFade()
//                .into(viewHolder.photo);
        ImageLoader.getInstance().displayImage(PhotoUrlHelper.getPreviewUrl(photo), viewHolder.photo, new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.photo_item_background) // TODO resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // TODO resource or drawable
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build());

        return convertView;
    }

    @Override
    public void recalculateItemsPerRow() {


    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable parcelable) {

    }


    private static class ViewHolder {
        public final ImageView photo;
        //public final ProgressBar pbPreviewImageIsLoading;

        public ViewHolder(View convertView) {
            this.photo = (ImageView) convertView.findViewById(R.id.img_photo_cover);
            //  this.pbPreviewImageIsLoading = (ProgressBar) convertView.findViewById(R.id.pbPreviewImageIsLoading);
        }
    }

}