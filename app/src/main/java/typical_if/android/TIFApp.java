package typical_if.android;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.Locale;

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

        // default locale
        final String userLng = OfflineMode.getDefaultUserLanguage();
        if (TextUtils.isEmpty(userLng)) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ru")) {
                OfflineMode.saveDefaultUserLanguage("ru");
            } else {
                setUserLanguage("ua");
                OfflineMode.saveDefaultUserLanguage("ua");
            }
        }
        else {
            setUserLanguage(userLng);
        }
    }

    /**
     * Shows a toast with given String message
     *
     * @param msgToShow
     */
    public static void showToast(final String msgToShow) {
        Toast.makeText(getAppContext(), msgToShow, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a toast with given String Resources ID (R.string.) message
     *
     * @param stringResId
     */
    public static void showToast(final int stringResId) {
        Toast.makeText(getAppContext(), stringResId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a toast with a common error message
     */
    public static void showCommonErrorToast() {
        //    Toast.makeText(TIFApp.getAppContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    public static void setUserLanguage(final String lng) {
        Locale locale = new Locale(lng);
        setUserLanguage(locale);
    }

    public static void setUserLanguage(final Locale locale) {
        Locale.setDefault(locale);
        final Resources res = getAppContext().getResources();
        final Configuration config = res.getConfiguration();
        config.locale = locale;
        //res.updateConfiguration(config, null);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

}
