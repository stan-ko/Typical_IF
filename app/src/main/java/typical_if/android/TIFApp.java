package typical_if.android;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by LJ on 14.07.2014.
 */
public class TIFApp extends Application {

    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    private static int displayHeight;
    private static int displayWidth;

    public static int getDisplayHeight() {
        return displayHeight;
    }

    public static int getDisplayWidth() {
        return displayWidth;
    }

    public static DisplayImageOptions additionalOptions = new DisplayImageOptions.Builder()
            .cacheOnDisc(true)
            .cacheInMemory(true)
                    //           .showImageOnLoading(R.drawable.pre_load_image_background) // TODO resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // TODO resource or drawable
            .resetViewBeforeLoading(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new FadeInBitmapDisplayer(1600)).build();

    public static DisplayImageOptions eventOptions = new DisplayImageOptions.Builder()
            .cacheOnDisc(true)
            .cacheInMemory(true)
            .showImageOnLoading(R.drawable.event_stub) // TODO resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // TODO resource or drawable
            .resetViewBeforeLoading(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new FadeInBitmapDisplayer(1600)).build();

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        final DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheOnDisc(true)
//                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.pre_load_image_background) // TODO resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
//                .showImageOnFail(R.drawable.pre_load_image_background) // TODO resource or drawable
                .resetViewBeforeLoading(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(1600)).build();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 320) // default = device screen dimensions
                .diskCacheExtraOptions(480, 320, null)
                        //.taskExecutorForCachedImages(...)
                        //.tasksProcessingOrder(QueueProcessingType.FIFO) // default
                        //.discCache(new FileCountLimitedDiscCache(cacheDir, new Md5FileNameGenerator(), 1000))
                        //.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                        //.memoryCacheSize(2 * 1024 * 1024)
                        //.memoryCacheSizePercentage(13) // default
                        //.diskCache(new UnlimitedDiscCache(cacheDir)) // default
                        //;;;
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(defaultOptions)
//                .tasksProcessingOrder(QueueProcessingType.FIFO)
//                .memoryCache(new WeakMemoryCache())
                        // .discCacheSize(100 * 1024 * 1024)

                        //.diskCacheSize(50 * 1024 * 1024)
                        //.diskCacheFileCount(100)
                        //.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                        //.imageDownloader(new BaseImageDownloader(context)) // default
                        //.imageDecoder(new BaseImageDecoder()) // default
                        // .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

        OfflineMode.init(this);

    }
}
