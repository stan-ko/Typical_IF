package typical_if.android.model;

import android.text.TextUtils;

import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONObject;

import typical_if.android.MyApplication;

/**
 * Created by Стас on 20.08.2014.
 */
public class TFVKPhoto extends VKApiPhoto {


    final static int displayHeight = MyApplication.getDisplayHeight();

    public String getFullScreenUrl() {
        final String urlOfFullScreenPhoto;
        if (!TextUtils.isEmpty(photo_2560) && displayHeight > 1199)
            urlOfFullScreenPhoto = photo_2560;
        else if (!TextUtils.isEmpty(photo_1280) && displayHeight > 799)
            urlOfFullScreenPhoto = photo_1280;
        else if (!TextUtils.isEmpty(photo_807) && displayHeight > 600)
            urlOfFullScreenPhoto = photo_807;
        else if (!TextUtils.isEmpty(photo_604))
            urlOfFullScreenPhoto = photo_604;
        else if (!TextUtils.isEmpty(photo_130))
            urlOfFullScreenPhoto = photo_130;
        else if (!TextUtils.isEmpty(photo_75))
            urlOfFullScreenPhoto = photo_75;
        else
            urlOfFullScreenPhoto = null;

        return urlOfFullScreenPhoto;
    }

    public String getPreviewUrl() {
        final String urlOfPreviewPhoto;
        if (MyApplication.getDisplayHeight() < 1000)
            urlOfPreviewPhoto = photo_75;
        else
            urlOfPreviewPhoto = photo_130;

        return urlOfPreviewPhoto;
    }

    @Override
    public TFVKPhoto parse(JSONObject from) {
        return (TFVKPhoto) super.parse(from);
    }
}