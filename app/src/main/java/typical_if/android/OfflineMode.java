package typical_if.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import typical_if.android.model.Wall.Wall;

/**
 * Created by pc on 05.08.14.
 */
public class OfflineMode {


    public static void saveJSON(JSONObject jsonObject, long gid) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(gid),Activity.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        final String JsonString = jsonObject.toString();
        final String JsonKey = String.valueOf(gid);
        ed.putString(JsonKey, JsonString);
        ed.commit();
    }
    public static void saveJSON(JSONObject jsonObject, String id) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(id),Activity.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        final String JsonString = jsonObject.toString();
        final String JsonKey = id;
        ed.putString(JsonKey, JsonString);
        ed.commit();
    }

    public static JSONObject loadJSON(long gid)  {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(gid), Activity.MODE_PRIVATE);
        final String JsonKey = String.valueOf(gid);
        final String savedText = sPref.getString(JsonKey, "");
        JSONObject jsonObj = null;

        try {
            jsonObj = new JSONObject(savedText);
        } catch (JSONException e) {
           // e.printStackTrace();
        }
        return jsonObj;
    }
    public static JSONObject loadJSON(String id)  {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String JsonKey = String.valueOf(id);
        final String savedText = sPref.getString(JsonKey, "");
        JSONObject jsonObj = null;

        try {
            jsonObj = new JSONObject(savedText);
        } catch (JSONException e) {
          //  e.printStackTrace();
        }
        return jsonObj;
    }
    public static boolean isOnline(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isJsonNull(long id) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String JsonKey = String.valueOf(id);
        final String savedText = sPref.getString(JsonKey, "");
        try {
            final JSONObject jsonObj = new JSONObject(savedText);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
    public static boolean isJsonNull(String id) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String JsonKey = id;
        final String savedText = sPref.getString(JsonKey, "");
        try {
            final JSONObject jsonObj = new JSONObject(savedText);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public static synchronized JSONObject jsonPlus (final JSONObject jsonObject, final JSONObject jsonObjectOffset) {

        //---------------1-----------------------------
        final JSONObject object = jsonObject.optJSONObject(Wall.JSON_KEY_RESPONSE);
        // items
        String items = object.optString(Wall.JSON_KEY_ITEMS);
        StringBuilder itemsSB = new StringBuilder(items.subSequence(0, items.length()));
        itemsSB.delete(0, 1);
        itemsSB.delete(itemsSB.length()-1, itemsSB.length());
        items= itemsSB.toString();
        // profiles
        String profiles = object.optString(Wall.JSON_KEY_PROFILES);
        StringBuilder profilesSB = new StringBuilder(profiles.subSequence(0, profiles.length()));
        profilesSB.delete(0, 1);
        profilesSB.delete(profilesSB.length()-1, profilesSB.length());
        profiles= profilesSB.toString();
        //---------------end 1-----------------------------

        //---------------2-----------------------------
        final JSONObject objectOffset = jsonObjectOffset.optJSONObject(Wall.JSON_KEY_RESPONSE);
        final int countOffset = objectOffset.optInt(Wall.JSON_KEY_COUNT);
        // items
        String itemsOffset = objectOffset.optString(Wall.JSON_KEY_ITEMS);
        StringBuilder itemsSBOffset = new StringBuilder(itemsOffset.subSequence(0, itemsOffset.length()));
        itemsSBOffset.delete(0, 1);
        itemsSBOffset.delete(itemsSBOffset.length()-1, itemsSBOffset.length());
        itemsOffset= itemsSBOffset.toString();
        // groups
        final String groupsOffset = objectOffset.optString(Wall.JSON_KEY_GROUPS);
        // profiles
        String profilesOffset = objectOffset.optString(Wall.JSON_KEY_PROFILES);
        StringBuilder profilesOffsetSB = new StringBuilder(profilesOffset.subSequence(0, profilesOffset.length()));
        profilesOffsetSB.delete(0, 1);
        profilesOffsetSB.delete(profilesOffsetSB.length()-1, profilesOffsetSB.length());
        profilesOffset= profilesOffsetSB.toString();
        //---------------end 2-----------------------------
        final String response = "{\"response\":{" +
                "\"count\":"+countOffset+"," +
                "\"items\":["+items+","+itemsOffset+"]," +
                "\"groups\":"+groupsOffset+"," +
                " \"profiles\":["+profiles+","+profilesOffset+"]}}" ;
        JSONObject wall;
        try {
            wall = new JSONObject(response);
        } catch (JSONException e) {
            wall= null;
            e.printStackTrace();
        }
        return wall;
    }
    public  static void onErrorToast(Context ApplicationContext){
        Toast.makeText(ApplicationContext, ApplicationContext.getString(R.string.OnErrorToast), Toast.LENGTH_SHORT).show();

    }


    }

