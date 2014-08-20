package typical_if.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 05.08.14.
 */
public class OfflineMode {

    //What these fields are for?
    //SharedPreferences sPref;
    //String SAVED_JSON = "saved_text";
    //JSONObject jsonObj;
    //long time =5;
    public static void saveJSON(JSONObject jsonObject, long gid) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(gid),Activity.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        final String JsonString = jsonObject.toString();
        final String JsonKey = String.valueOf(gid);
        //ed.clear();
        ed.putString(JsonKey, JsonString);
        Log.d("------------------Respons------Save------Secsesful-----", JsonString);
        ed.commit();
    }

    public static JSONObject loadJSON(long gid)  {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(gid), Activity.MODE_PRIVATE);
        final String JsonKey = String.valueOf(gid);
        final String savedText = sPref.getString(JsonKey, "");
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(savedText);
            Log.d("-------------Respons-----Load----Secsesful---------",savedText );

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("-------------Respons-----Load----Error---------",savedText );
        }
        return jsonObj;
    }
    public static boolean isOnline(final Context context) {
        //final Activity activity = new Activity();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            Log.v("status", "ONLINE");
            return true;
        }
        else {
            Log.v("status", "OFFLINE");
            return false;
        }
    }
    public static boolean isJsonNull(long id){
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String JsonKey = String.valueOf(id);
        final String savedText = sPref.getString(JsonKey, "");
        try {
            final JSONObject jsonObj = new JSONObject(savedText);
            Log.d("-------------Response-----NotNull---------",savedText );
            return true;
        } catch (JSONException e) {
           // e.printStackTrace();
            Log.d("-------------Response-----IsNull---------", savedText);
            return false;
        }



    }
}
