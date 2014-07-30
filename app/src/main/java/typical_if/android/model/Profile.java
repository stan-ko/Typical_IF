package typical_if.android.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 24.07.2014.
 */
public class Profile {

    public long     id;
    public String  first_name;
    public String  last_name;
    public int     sex;
    public String  screen_name;
    public String  photo_50;
    public String  photo_100;
    public boolean online;


    public Profile(long id, String first_name, String last_name, int sex, String screen_name, String photo_50, String photo_100, boolean online){
        this.id=id;
        this.first_name=first_name;
        this.last_name=last_name;
        this.sex=sex;
        this.screen_name=screen_name;
        this.photo_50=photo_50;
        this.photo_100=photo_100;
        this.online=online;
     }

    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_FIRST_NAME = "first_name";
    public static final String JSON_KEY_LAST_NAME= "last_name";
    public static final String JSON_KEY_SEX = "sex";
    public static final String JSON_KEY_SCREEN_NAME= "screen_name";
    public static final String JSON_KEY_PHOTO_50 = "photo_50";
    public static final String JSON_KEY_PHOTO_100 = "photo_100";
    public static final String JSON_KEY_ONLINE= "online";



    public static Profile getProfileFromJSON (JSONObject profile){
        return new Profile(profile.optLong(JSON_KEY_ID),
                profile.optString(JSON_KEY_FIRST_NAME),
                profile.optString(JSON_KEY_LAST_NAME),
                profile.optInt(JSON_KEY_SEX),
                profile.optString(JSON_KEY_SCREEN_NAME),
                profile.optString(JSON_KEY_PHOTO_50),
                profile.optString(JSON_KEY_PHOTO_100),
                profile.optBoolean(JSON_KEY_ONLINE));
    }


    public static ArrayList<Profile> getProfilesFromJSONArray(JSONArray array) {
       final ArrayList<Profile> profiles = new ArrayList<Profile>();
        for (int i=0; i<array.length(); i++) {
        final Profile profile = getProfileFromJSON(array.optJSONObject(i));
         profiles.add(profile);
        }
     return profiles;
    }




}
