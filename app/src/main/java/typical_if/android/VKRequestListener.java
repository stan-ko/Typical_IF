package typical_if.android;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

/**
 * Created by CTAC on 27.03.2015.
 */
public class VKRequestListener extends VKRequest.VKRequestListener {

    public VKResponse vkResponse;
    public JSONObject vkJson;
    public boolean hasJson;
    public boolean isSuccessful;
    public VKError vkError;

    @Override
    public void onComplete(final VKResponse response) {
        super.onComplete(response);
        this.vkResponse = response;
        this.hasJson = response!=null;
        if (hasJson) {
            this.vkJson = response.json;
        }
        isSuccessful = true;
        onComplete();
        onSuccess();
    }

    public void onComplete(){}

    public void onSuccess(){}

    @Override
    public void onError(final VKError error) {
        super.onError(error);
        vkError = error;
        isSuccessful = false;
//        onError(error.errorMessage);
        onComplete();
        onError();
    }

    public void onError() {
        // Toast.makeText(TIFApp.getAppContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

}
