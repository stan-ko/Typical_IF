package typical_if.android;

import android.content.SharedPreferences;
import android.util.Log;

import com.vk.sdk.VKUIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 05.08.14.
 */
public class OfflineMode {

    SharedPreferences sPref;
    String SAVED_JSON = "saved_text";
    JSONObject jsonObj;


    public void saveJSON(JSONObject jsonObject, long gid) {
        sPref = VKUIHelper.getTopActivity().getPreferences(VKUIHelper.getTopActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        String JsonString = jsonObject.toString();
        SAVED_JSON = String.valueOf(gid);
        ed.putString(SAVED_JSON, JsonString);
        Log.d("/--------------jsonInString-------------------/", jsonObject.toString());
        ed.commit();
    }

    public JSONObject loadJSON(long gid) {
        sPref = VKUIHelper.getTopActivity().getPreferences(VKUIHelper.getTopActivity().MODE_PRIVATE);
        SAVED_JSON = String.valueOf(gid);
        String savedText = sPref.getString(SAVED_JSON, "");
        try {
            jsonObj = new JSONObject(savedText);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("/---------------------savedJson--------ERROR-----------/", savedText);
        }
        return jsonObj;
    }
}
