package typical_if.android.model.Wall;

import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;

/**
 * extended by Miller on 16.07.2014.
 */
public class Wall {
    public static final String TAG = "Parsers";

    public int count;

    public ArrayList<VKWallPostWrapper> posts = new ArrayList<VKWallPostWrapper>();
    public ArrayList<VKApiUser> profiles = new ArrayList<VKApiUser>();
    public ArrayList<VKApiCommunity> groups = new ArrayList<VKApiCommunity>();
    public VKApiCommunity group;

    public static final String JSON_KEY_RESPONSE = "response";
    public static final String JSON_KEY_COUNT = "count";
    public static final String JSON_KEY_PROFILES = "profiles";
    public static final String JSON_KEY_GROUPS = "groups";
    public static final String JSON_KEY_ITEMS = "items";

}
