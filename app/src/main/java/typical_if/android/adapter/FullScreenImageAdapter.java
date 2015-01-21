package typical_if.android.adapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

import typical_if.android.TIFApp;
import typical_if.android.R;
import typical_if.android.util.PhotoUrlHelper;

/**
 * Created by LJ on 21.07.2014.
 */
public class FullScreenImageAdapter extends PagerAdapter {
    final int displayHeight = TIFApp.getDisplayHeight();
    LayoutInflater inflater;

    public ArrayList<VKApiPhoto> photos;
//    public int count ;
    public FragmentManager fragmentManager;
    public Bundle arguments;
    public View rootView;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisc(true)
            .cacheInMemory(true)
            .resetViewBeforeLoading(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public FullScreenImageAdapter(ArrayList<VKApiPhoto> photos, LayoutInflater inflater, Bundle arguments, long groupID, long albumID, long userID, FragmentManager fragmentManager, View rootView) {
        this.rootView = rootView;
        this.photos = photos;
        this.inflater = inflater;
        this.arguments = arguments;
        this.fragmentManager = fragmentManager;
       // count = photos.size();
    }


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
        View viewPhotoFullSceen = inflater.inflate(R.layout.fragment_full_screen_item, null);
        final ImageView imageView = (ImageView) viewPhotoFullSceen.findViewById(R.id.full_screen_photo);
        final ProgressBar pbImageIsLoading = (ProgressBar) viewPhotoFullSceen.findViewById(R.id.pbImageIsLoading);
        ((ViewPager) container).addView(viewPhotoFullSceen);
        //  Log.d("Current VIEW", position + "");
        loadPreview(/*position, */photos.get(position), imageView, pbImageIsLoading);///////////////////////////////////////////////////////////////////
        return viewPhotoFullSceen;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);
    }

    private void loadPreview(/*final int position, */final VKApiPhoto photo, final ImageView imageView, final ProgressBar pbImageIsLoading) {
//        ImageLoader.getInstance().displayImage(photos.get(position).photo_75, imageView, options);
//
        final String urlOfPhotoPreview = PhotoUrlHelper.getPreviewUrl(photo);

        final String urlOfFullScreenPhoto = PhotoUrlHelper.getFullScreenUrl(photo);

        ImageLoader.getInstance().displayImage(urlOfPhotoPreview, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                pbImageIsLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                pbImageIsLoading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                loadFullSceenPhoto(urlOfFullScreenPhoto, imageView, pbImageIsLoading);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                pbImageIsLoading.setVisibility(View.GONE);
            }
        });
    }

    void loadFullSceenPhoto(final String url, final ImageView imageView, final ProgressBar pbImageIsLoading) {
        ImageLoader.getInstance().displayImage(url, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                pbImageIsLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                pbImageIsLoading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                pbImageIsLoading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                pbImageIsLoading.setVisibility(View.VISIBLE);
            }
        });
    }

}