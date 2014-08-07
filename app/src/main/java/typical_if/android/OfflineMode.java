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
    long time =5;

    public static void saveJSON(JSONObject jsonObject, long gid) {
        SharedPreferences sPref;
        sPref = VKUIHelper.getTopActivity().getBaseContext().getSharedPreferences(String.valueOf(gid),VKUIHelper.getTopActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        String JsonString = jsonObject.toString();
        String SAVED_JSON;
        SAVED_JSON = String.valueOf(gid);
        ed.clear();
        ed.putString(SAVED_JSON, JsonString);
        Log.d("------------------Respons------Save------Secsesful-----", JsonString);
        ed.commit();
    }

    public JSONObject loadJSON(long gid)  {
        sPref = VKUIHelper.getTopActivity().getBaseContext().getSharedPreferences(String.valueOf(gid),VKUIHelper.getTopActivity().MODE_PRIVATE);
        SAVED_JSON = String.valueOf(gid);
        String savedText = sPref.getString(SAVED_JSON, "").toString();
        try {
            jsonObj = new JSONObject(savedText);
            Log.d("-------------Respons-----Load----Secsesful---------",savedText );

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("-------------Respons-----Load----Error---------",savedText );
        }
        return jsonObj;
    }
}
