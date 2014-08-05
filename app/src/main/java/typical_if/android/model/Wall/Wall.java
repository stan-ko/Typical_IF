package typical_if.android.model.Wall;

import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPostArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.VKHelper;

/**
 * extended by Miller on 16.07.2014.
 */
public class Wall {
    private static final String TAG = "Parsers";

    public int count;


    public boolean isFixedPost = false;
    public VKPostArray posts = new VKPostArray();
    public final ArrayList<Profile> profiles = new ArrayList<Profile>();
    public final ArrayList<Group> groups = new ArrayList<Group>();

    public Group group;
    public Profile profile;

    public static final String JSON_KEY_RESPONSE = "response";
    public static final String JSON_KEY_COUNT = "count";
    public static final String JSON_KEY_PROFILES = "profiles";
    public static final String JSON_KEY_GROUPS = "groups";


    public static Wall getGroupWallFromJSON(final JSONObject jsonObject) {
        final Wall wall = new Wall();
        final JSONObject object = jsonObject.optJSONObject(JSON_KEY_RESPONSE);
        wall.count = object.optInt(JSON_KEY_COUNT);
        Log.d(TAG, String.valueOf(wall.count));
        // items
        final VKPostArray posts = new VKPostArray();
        try {
            posts.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // groups
        final JSONArray groups = object.optJSONArray(JSON_KEY_GROUPS);
        Log.d(TAG, "Wall groups: " + groups.toString());
        Group group;
        VKApi.users().get();
        for (int i = 0; i < groups.length(); i++) {
            group = getGroupFromJSON(groups.optJSONObject(i));
            wall.groups.add(group);
        }
        wall.group = getGroupFromJSON(groups.optJSONObject(0));

        // profiles
        final JSONArray profiles = object.optJSONArray(JSON_KEY_PROFILES);
        Log.d(TAG, "Wall profiles: " + profiles.toString());
        Profile profile;
        for (int i = 0; i < profiles.length(); i++) {
            profile = getProfileFromJSON(profiles.optJSONObject(i));
            wall.profiles.add(profile);
        }

        VKHelper.getFixedPostId(wall.group.screen_name, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                JSONArray object = response.json.optJSONArray("response");
                try {
                    if (object.getJSONObject(0).has("fixed_post")) {
                        int pid = object.getJSONObject(0).optInt("fixed_post");
                        final String pidFull = "-" + wall.group.id + "_" + pid;
                        VKHelper.getFixedPost(pidFull, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Log.d(pidFull, "");
                                final JSONObject object = response.json.optJSONObject(JSON_KEY_RESPONSE);
                                final VKPostArray postsFixed = new VKPostArray();
                                try {
                                    postsFixed.parse(object);
                                    if (postsFixed != null && postsFixed.size() != 0) {
                                        posts.add(0, postsFixed.get(0));
                                        wall.isFixedPost = true;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        wall.posts = posts;
        return wall;
    }

    public static Group getGroupFromJSON(final JSONObject jsonObject) {
        final Group group = new Group();
        group.name = jsonObject.optString(Group.JSON_KEY_NAME);
        group.screen_name = jsonObject.optString(Group.JSON_KEY_SCREEN_NAME);
        group.photo_100 = jsonObject.optString(Group.JSON_KEY_PHOTO_100);
        group.photo_50 = jsonObject.optString(Group.JSON_KEY_PHOTO_50);
        group.photo_200 = jsonObject.optString(Group.JSON_KEY_PHOTO_200);
        group.id = jsonObject.optLong(Group.JSON_KEY_ID);
        group.type = jsonObject.optString(Group.JSON_KEY_TYPE);
        group.is_admin = jsonObject.optInt(Group.JSON_KEY_IS_ADMIN);
        group.is_closed = jsonObject.optInt(Group.JSON_KEY_IS_CLOSED);
        group.is_member = jsonObject.optInt(Group.JSON_KEY_IS_MEMBER);
        return group;
    }

    public static Profile getProfileFromJSON(final JSONObject jsonObject) {
        final Profile profile = new Profile();
        profile.last_name = jsonObject.optString(Profile.JSON_KEY_LAST_NAME);
        profile.first_name = jsonObject.optString(Profile.JSON_KEY_FIRST_NAME);
        profile.photo_100 = jsonObject.optString(Profile.JSON_KEY_PHOTO_100);
        profile.photo_50 = jsonObject.optString(Profile.JSON_KEY_PHOTO_50);
        profile.id = jsonObject.optLong(Profile.JSON_KEY_ID);
        profile.screen_name = jsonObject.optString(Profile.JSON_KEY_SCREEN_NAME);
        profile.online = jsonObject.optInt(Profile.JSON_KEY_ONLINE);
        profile.sex = jsonObject.optInt(Profile.JSON_KEY_SEX);
        return profile;
    }
}
