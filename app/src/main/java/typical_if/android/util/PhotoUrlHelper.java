package typical_if.android.util;

import android.text.TextUtils;

import com.vk.sdk.api.model.VKApiPhoto;

import typical_if.android.MyApplication;

/**
 * Created by Стас on 20.08.2014.
 */
public class PhotoUrlHelper {

    private final static int displayHeight = MyApplication.getDisplayHeight();

    public static String getFullScreenUrl(final VKApiPhoto photo) {
        final String urlOfFullScreenPhoto;
        if (!TextUtils.isEmpty(photo.photo_2560) && displayHeight > 1199)
            urlOfFullScreenPhoto = photo.photo_2560;
        else if (!TextUtils.isEmpty(photo.photo_1280) && displayHeight > 799)
            urlOfFullScreenPhoto = photo.photo_1280;
        else if (!TextUtils.isEmpty(photo.photo_807) && displayHeight > 600)
            urlOfFullScreenPhoto = photo.photo_807;
        else if (!TextUtils.isEmpty(photo.photo_604))
            urlOfFullScreenPhoto = photo.photo_604;
        else if (!TextUtils.isEmpty(photo.photo_130))
            urlOfFullScreenPhoto = photo.photo_130;
        else if (!TextUtils.isEmpty(photo.photo_75))
            urlOfFullScreenPhoto = photo.photo_75;
        else
            urlOfFullScreenPhoto = null;

        return urlOfFullScreenPhoto;
    }

    public static String getPreviewUrl(final VKApiPhoto photo) {
        final String urlOfPreviewPhoto;
        if (MyApplication.getDisplayHeight() < 1000)
            urlOfPreviewPhoto = photo.photo_75;
        else
            urlOfPreviewPhoto = photo.photo_130;

        return urlOfPreviewPhoto;
    }
}
