package typical_if.android;

import android.text.TextUtils;

import com.stanko.tools.InternetConnectionHelper;
import com.stanko.tools.SharedPrefsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import typical_if.android.model.Wall.Wall;

/**
 * Created by pc on 05.08.14.
 */
public class OfflineMode extends SharedPrefsHelper {

//    final static SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("key", Activity.MODE_PRIVATE);
//    final static SharedPreferences.Editor ed = sPref.edit();

    public static boolean saveJSON(JSONObject jsonObject, long gid) {
        return save(String.valueOf(gid), jsonObject.toString());
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(gid), Activity.MODE_PRIVATE);
//        final SharedPreferences.Editor ed = sPref.edit();
//        final String JsonString = jsonObject.toString();
//        final String JsonKey = ;
//        ed.clear();
//        ed.putString(JsonKey, JsonString);
//        ed.commit();
    }

    public static boolean saveJSON(JSONObject jsonObject, String id) {
        return save(id, jsonObject.toString());
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
//        final SharedPreferences.Editor ed = sPref.edit();
//        final String JsonString = jsonObject.toString();
//        final String JsonKey = id;
//        ed.clear();
//        ed.putString(JsonKey, JsonString);
//        ed.commit();
    }

    private final static String TIF_SP_KEY_IS_FIRST_RUN = "isFirstRun";
    public static boolean isFirstRun(String prefName) {
        if (!has(TIF_SP_KEY_IS_FIRST_RUN))
            return true;
        return getBoolean(TIF_SP_KEY_IS_FIRST_RUN);
//        final SharedPreferences tfFirstRunSPref = TIFApp.getAppContext().getSharedPreferences(prefName, Activity.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = tfFirstRunSPref.edit();
//        String key = "isFirstRun";
//        boolean isFirstRun = true;
//        boolean notFirstRun = false;
//        Boolean FirstRun = tfFirstRunSPref.getBoolean(key, isFirstRun);
////        Log.d("firsRun----------------------------",""+FirstRun);
//        if (FirstRun == isFirstRun) {
//            editor.clear();
//            editor.putBoolean(key, notFirstRun);
//            editor.commit();
//            return isFirstRun;
//        } else return notFirstRun;
    }

    public static JSONObject loadJSON(long gid) {
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(gid), Activity.MODE_PRIVATE);
        final String jsonKey = String.valueOf(gid);
//        final String savedText = sPref.getString(JsonKey, "");
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(getString(jsonKey));
        } catch (JSONException ignored) {}
        return jsonObj;
    }

    public static JSONObject loadJSON(String id) {
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String jsonKey = String.valueOf(id);
//        final String savedText = sPref.getString(JsonKey, "");
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(getString(jsonKey));
        } catch (JSONException ignored) {}
        return jsonObj;
    }

    public static boolean isOnline() {
        return InternetConnectionHelper.checkIsNetworkAvailable(TIFApp.getAppContext());
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo nInfo = cm.getActiveNetworkInfo();
//        if (nInfo != null && nInfo.isConnected()) {
//            return true;
//        } else {
//            return false;
//        }
    }

    public static boolean isJsonNull(long id) {
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String jsonKey = String.valueOf(id);
//        final String savedText = sPref.getString(JsonKey, "");
        if (!has(jsonKey))
            return true;
        boolean isJsonNull = false;
        try {
            isJsonNull = (new JSONObject(getString(jsonKey)) == null);
        } catch (JSONException ignored) {}
        return isJsonNull;
    }

    public static boolean isJsonNull(String id) {
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
//        final String JsonKey = id;
//        final String savedText = sPref.getString(JsonKey, "");
//        try {
//            final JSONObject jsonObj = new JSONObject(savedText);
//            return true;
//        } catch (JSONException e) {
//            return false;
//        }
        if (TextUtils.isEmpty(id) || !has(id))
            return true;
        boolean isJsonNull = false;
        try {
            isJsonNull = (new JSONObject(getString(id)) == null);
        } catch (JSONException ignored) {}
        return isJsonNull;
    }

    public static boolean saveLong(Long id, String key) {
        return save(key,id);
//        final String strId = Long.toString(id);
////        Log.d("SaveLong","------------------------------ "+id);
//        ed.clear();
//        ed.putString(key, strId);
//        ed.commit();
    }

    public static Long loadLong(String key) {
        return getLong(key);
//        final String id = sPref.getString(key, "0");
////        Log.d("LoadLong", "------------------------------ " + id);
//        return Long.valueOf(id);
    }

    public static boolean saveInt(int surprise, String id) {
        return save(id,surprise);
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
//        final SharedPreferences.Editor ed = sPref.edit();
//        final String JsonString = Integer.toString(surprise);
//        final String JsonKey = id;
//        ed.clear();
//        ed.putString(JsonKey, JsonString);
//        ed.commit();
    }

    public static int loadInt(String id) {
        return getInt(id);
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
//        final String JsonKey = String.valueOf(id);
//        final String savedText = sPref.getString(JsonKey, "");
//        // e.printStackTrace();
//
//        return Integer.parseInt(savedText);
    }

//    public static boolean isIntNul(String id) {
//        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
//        final String JsonKey = id;
//        final String savedText = sPref.getString(JsonKey, "");
//        try {
//            final JSONObject jsonObj = new JSONObject(savedText);
//            return true;
//        } catch (JSONException e) {
//            return false;
//        }
//    }

    public static synchronized JSONObject jsonPlus(final JSONObject jsonObject, final JSONObject jsonObjectOffset) {

        //---------------1-----------------------------
        final JSONObject object = jsonObject.optJSONObject(Wall.JSON_KEY_RESPONSE);
        // items
        String items = object.optString(Wall.JSON_KEY_ITEMS);
        StringBuilder itemsSB = new StringBuilder(items.subSequence(0, items.length()));
        itemsSB.delete(0, 1);
        itemsSB.delete(itemsSB.length() - 1, itemsSB.length());
        items = itemsSB.toString();
        // profiles
        String profiles = object.optString(Wall.JSON_KEY_PROFILES);
        StringBuilder profilesSB = new StringBuilder(profiles.subSequence(0, profiles.length()));
        profilesSB.delete(0, 1);
        profilesSB.delete(profilesSB.length() - 1, profilesSB.length());
        profiles = profilesSB.toString();
        //---------------end 1-----------------------------

        //---------------2-----------------------------
        final JSONObject objectOffset = jsonObjectOffset.optJSONObject(Wall.JSON_KEY_RESPONSE);
        final int countOffset = objectOffset.optInt(Wall.JSON_KEY_COUNT);
        // items
        String itemsOffset = objectOffset.optString(Wall.JSON_KEY_ITEMS);
        StringBuilder itemsSBOffset = new StringBuilder(itemsOffset.subSequence(0, itemsOffset.length()));
        itemsSBOffset.delete(0, 1);
        itemsSBOffset.delete(itemsSBOffset.length() - 1, itemsSBOffset.length());
        itemsOffset = itemsSBOffset.toString();
        // groups
        final String groupsOffset = objectOffset.optString(Wall.JSON_KEY_GROUPS);
        // profiles
        String profilesOffset = objectOffset.optString(Wall.JSON_KEY_PROFILES);
        StringBuilder profilesOffsetSB = new StringBuilder(profilesOffset.subSequence(0, profilesOffset.length()));
        profilesOffsetSB.delete(0, 1);
        profilesOffsetSB.delete(profilesOffsetSB.length() - 1, profilesOffsetSB.length());
        profilesOffset = profilesOffsetSB.toString();
        //---------------end 2-----------------------------
        final String response = "{\"response\":{" +
                "\"count\":" + countOffset + "," +
                "\"items\":[" + items + "," + itemsOffset + "]," +
                "\"groups\":" + groupsOffset + "," +
                " \"profiles\":[" + profiles + "," + profilesOffset + "]}}";
        JSONObject wall = null;
        try {
            wall = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wall;
    }

}

