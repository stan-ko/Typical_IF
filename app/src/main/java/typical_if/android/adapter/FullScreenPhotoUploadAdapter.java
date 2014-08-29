package typical_if.android.adapter;

import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.model.UploadPhotos;

/**
 * Created by LJ on 05.08.2014.
 */
public class FullScreenPhotoUploadAdapter extends PagerAdapter {

    ArrayList<UploadPhotos> photos;
    LayoutInflater inflater;

    public FullScreenPhotoUploadAdapter(ArrayList<UploadPhotos> photos, LayoutInflater inflater) {
        this.photos = photos;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView;
        View viewLayout = inflater.inflate(R.layout.fragment_full_screen_view_from_phone_photolist, null);
        imageView = (ImageView) viewLayout.findViewById(R.id.full_screen_photo_from_phone);
        ((ViewPager) container).addView(viewLayout);
        ImageLoader.getInstance().displayImage(String.valueOf(Uri.fromFile(new File(photos.get(position).photosrc))), imageView);
        return viewLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);

    }


}
