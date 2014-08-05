package typical_if.android;

import android.app.Application;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
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

        final DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;


        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                        //.taskExecutor(...)
                        //.taskExecutorForCachedImages(...)
                        //.threadPoolSize(3) // default
                        //.threadPriority(Thread.NORM_PRIORITY - 1) // default
                        //.tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
               // .discCache(new FileCountLimitedDiscCache(cacheDir, new Md5FileNameGenerator(), 1000))
                        //.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                        //.memoryCacheSize(2 * 1024 * 1024)
                        //.memoryCacheSizePercentage(13) // default
                        //.diskCache(new UnlimitedDiscCache(cacheDir)) // default
                        //.diskCacheSize(50 * 1024 * 1024)
                        //.diskCacheFileCount(100)
                        //.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                        //.imageDownloader(new BaseImageDownloader(context)) // default
                        //.imageDecoder(new BaseImageDecoder()) // default
                        //.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

    }
}
