package typical_if.android.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import typical_if.android.MyApplication;
import typical_if.android.R;
import typical_if.android.model.Photo;
/**
 * Created by LJ on 21.07.2014.
 */
public class FullScreenImageAdapter extends PagerAdapter {
    final int displayHeight = MyApplication.getDisplayHeight();
    LayoutInflater inflater;

    public static ArrayList<Photo> photos;
    public static int static_like_status;
    public FragmentManager fragmentManager;
    public Bundle arguments;
    private View rootView;
    private DisplayImageOptions options;


    public FullScreenImageAdapter(ArrayList<Photo> photos, LayoutInflater inflater, Bundle arguments, long groupID, long albumID, long userID, FragmentManager fragmentManager, View rootView) {
        this.rootView = rootView;
        this.photos = photos;
        this.inflater = inflater;
        this.arguments = arguments;
        this.fragmentManager = fragmentManager;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // TODO resource or drawable
                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
                .showImageOnFail(R.drawable.ic_error) // TODO resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    int realPosition;

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {


        ImageView imageView;
        View viewLayout = inflater.inflate(R.layout.fragment_full_screen_item, null);
        imageView = (ImageView) viewLayout.findViewById(R.id.full_screen_photo);
        ((ViewPager) container).addView(viewLayout);
      //  Log.d("Current VIEW", position + "");
          loadImage(photos.get(position), imageView);///////////////////////////////////////////////////////////////////
        //ImageLoader.getInstance().displayImage(photos.get(position).photo_75, imageView, options);
        return viewLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public void loadImage(Photo photo, ImageView imageView) {
        String url = null;
        if (!TextUtils.isEmpty(photo.photo_2048) && displayHeight > 1199) {
            url = photo.photo_2048;
        } else if (!TextUtils.isEmpty(photo.photo_1280) && displayHeight > 799) {
            url = photo.photo_1280;
        } else if (!TextUtils.isEmpty(photo.photo_807) && displayHeight > 600) {
            url = photo.photo_807;
        } else if (!TextUtils.isEmpty(photo.photo_604)) {
            url = photo.photo_604;
        } else if (!TextUtils.isEmpty(photo.photo_130)) {
            url = photo.photo_130;
        } else if (!TextUtils.isEmpty(photo.photo_75)) {
            url = photo.photo_75;
        }
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }


//
}