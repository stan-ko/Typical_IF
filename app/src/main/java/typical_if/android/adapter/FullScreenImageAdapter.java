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
import com.stanko.tools.Log;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.util.PhotoUrlHelper;

/**
 * Created by LJ on 21.07.2014.
 */
public class FullScreenImageAdapter extends PagerAdapter {
    final int displayHeight = TIFApp.getDisplayHeight();
    private final VKApiPhoto fromPhoto;
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

    public FullScreenImageAdapter(VKApiPhoto fromPhoto, ArrayList<VKApiPhoto> photos, LayoutInflater inflater, Bundle arguments, long groupID, long albumID, long userID, FragmentManager fragmentManager, View rootView) {
        this.fromPhoto = fromPhoto;
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
        View viewPhotoFullScreen = inflater.inflate(R.layout.fragment_full_screen_item, null);
        final ImageView imageView = (ImageView) viewPhotoFullScreen.findViewById(R.id.full_screen_photo);
        final ProgressBar pbImageIsLoading = (ProgressBar) viewPhotoFullScreen.findViewById(R.id.pbImageIsLoading);
        ((ViewPager) container).addView(viewPhotoFullScreen);
        //  Log.d("Current VIEW", position + "");
        Log.d ("PHOTOS00"," = "+photos.get(position)+" = "+ imageView+ "  ="+ pbImageIsLoading);
        loadPreview(/*position, */photos.get(position), imageView, pbImageIsLoading);///////////////////////////////////////////////////////////////////
        return viewPhotoFullScreen;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((RelativeLayout) object);
    }

    private void loadPreview(/*final int position, */final VKApiPhoto photo, final ImageView imageView, final ProgressBar pbImageIsLoading) {
        ImageLoader.getInstance().displayImage(photo.photo_75, imageView, options);

//        Log.d("fromPhoto:" , "" +fromPhoto.getId());
//        final String urlOfPhotoPreview;
//        if (photo.getId()==fromPhoto.getId()){
//            Log.i(this, "photo is SAME as fromPhoto!");
//            if (PhotoUrlHelper.isImageCached(fromPhoto.photo_604))
//                Log.i(this, "fromPhoto is cached!!!");
//            urlOfPhotoPreview = fromPhoto.photo_604;
//        }
//        else {
//            urlOfPhotoPreview = PhotoUrlHelper.getPreviewUrl(photo);
//        }
//
//        final String urlOfFullScreenPhoto = PhotoUrlHelper.getBestQualityUrl(photo.src);
//        Log.i(this, "urlOfFullScreenPhoto: "+urlOfFullScreenPhoto);


//        Glide.with(TIFApp.getAppContext())
//                .load(urlOfPhotoPreview)
//                .placeholder(R.drawable.event_stub)
////                .crossFade()
//                .into(new GlideDrawableImageViewTarget(imageView){
//                    @Override
//                    public void onStart() {
//                        pbImageIsLoading.setVisibility(View.VISIBLE);
//                        super.onStart();
//                    }
//
//                    @Override
//                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                        pbImageIsLoading.setVisibility(View.GONE);
//                        super.onLoadFailed(e, errorDrawable);
//                    }
//
//                    @Override
//                    public void onLoadCleared(Drawable placeholder) {
//                        pbImageIsLoading.setVisibility(View.GONE);
//                        super.onLoadCleared(placeholder);
//                    }
//
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                        pbImageIsLoading.setVisibility(View.GONE);
//                        super.onResourceReady(resource, animation);
//                        loadFullScreenPhoto(urlOfFullScreenPhoto, imageView, pbImageIsLoading);
//                    }
//                });
//        ImageLoader.getInstance().displayImage(urlOfPhotoPreview, imageView, options, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                pbImageIsLoading.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                pbImageIsLoading.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                loadFullScreenPhoto(urlOfFullScreenPhoto, imageView, pbImageIsLoading);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                pbImageIsLoading.setVisibility(View.GONE);
//            }
//        });
    }

    void loadFullScreenPhoto(final String url, final ImageView imageView, final ProgressBar pbImageIsLoading) {
//        Glide.with(TIFApp.getAppContext())
//                .load(url)
////                .placeholder(R.drawable.event_stub)
//                .crossFade()
//                .into(new GlideDrawableImageViewTarget(imageView){
//                    @Override
//                    public void onStart() {
//                        pbImageIsLoading.setVisibility(View.VISIBLE);
//                        super.onStart();
//                    }
//
//                    @Override
//                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                        pbImageIsLoading.setVisibility(View.GONE);
//                        super.onLoadFailed(e, errorDrawable);
//                    }
//
//                    @Override
//                    public void onLoadCleared(Drawable placeholder) {
//                        pbImageIsLoading.setVisibility(View.GONE);
//                        super.onLoadCleared(placeholder);
//                    }
//
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
//                        pbImageIsLoading.setVisibility(View.GONE);
//                        super.onResourceReady(resource, animation);
//                    }
//                });
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