package typical_if.android;

import android.app.Application;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by LJ on 14.07.2014.
 */
public class MyApplication extends Application {

    private static int displayHeight;
    private static int displayWidth;

    public static int getDisplayHeight() {
        return displayHeight;
    }

    public static int getDisplayWidth() {
        return displayWidth;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final DisplayMetrics displayMetrics =  getApplicationContext().getResources().getDisplayMetrics();
        //int displayDensity = displayMetrics.densityDpi;
        displayHeight = displayMetrics.heightPixels;
        displayWidth =  displayMetrics.widthPixels;


        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                        //.taskExecutor(...)
                        //.taskExecutorForCachedImages(...)
                .threadPoolSize(10) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
//                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13) // default
//                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
//                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
//                .imageDownloader(new BaseImageDownloader(VKUIHelper.getApplicationContext())) // default
//                .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(getDisplayOptions()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);


    }
    public static DisplayImageOptions getDisplayOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.showImageForEmptyUri(R.drawable.error)
                //.showImageOnFail(R.drawable.error)
                .delayBeforeLoading(1000)
                .resetViewBeforeLoading(false)  // default
                .cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .build();
        return options;
    }
}
