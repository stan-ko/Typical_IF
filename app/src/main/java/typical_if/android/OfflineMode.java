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


    public static void saveJSON(JSONObject jsonObject, long gid) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(gid),Activity.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        final String JsonString = jsonObject.toString();
        final String JsonKey = String.valueOf(gid);
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
                //String countJ = jsonObj.getString("count");
           //     Log.d("****--****", ""+countJ);

            Log.d("-------------Respons-----Load----Secsesful---------",savedText );

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("-------------Respons-----Load----Error---------",savedText );
        }
        return jsonObj;
    }
    public static boolean isOnline(final Context context) {
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
    public static boolean isJsonNull(long id) {
        final SharedPreferences sPref = MyApplication.getAppContext().getSharedPreferences(String.valueOf(id), Activity.MODE_PRIVATE);
        final String JsonKey = String.valueOf(id);
        final String savedText = sPref.getString(JsonKey, "");
        try {
            final JSONObject jsonObj = new JSONObject(savedText);
            Log.d("-------------Response-----NotNull---------", savedText);
            return true;
        } catch (JSONException e) {
            Log.d("-------------Response-----IsNull---------", savedText);
            return false;
        }
    }

        public static JSONObject jsonPlus( JSONObject j, JSONObject j2){

            try {
                int countJ = j.getInt("count");
                int countJ2 = j2.getInt("count");
                int count = countJ+countJ2;

                String itemsJ = j.getString("items");
                String itemsJ2 = j2.getString("items");
                String items = itemsJ+itemsJ2;

                String groupsJ = j.getString("groups");
                String groupsJ2 = j2.getString("groups");
                String groups = groupsJ+groupsJ2;

                String profilesJ = j.getString("profiles");
                String profilesJ2 = j2.getString("profiles");
                String profiles = profilesJ+profilesJ2;

                String JsonString = "{\"response\":{\"count\":"+Integer.toString(count)+",\"items\":[{"+items+"}]";

                JSONObject jsonObject = new JSONObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject();

            return jsonObject;
        }


    }

