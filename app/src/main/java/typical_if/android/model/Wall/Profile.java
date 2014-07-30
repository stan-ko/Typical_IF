package typical_if.android.model.Wall;

/**
 * Created by admin on 17.07.2014.
 */
public class Profile {
    public long id;
    public String first_name;
    public String last_name;
    public int sex;
    public String screen_name;
    public String photo_50;
    public String photo_100;
    public int online;

    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_FIRST_NAME = "first_name";
    public static final String JSON_KEY_LAST_NAME = "last_name";
    public static final String JSON_KEY_SEX = "sex";
    public static final String JSON_KEY_SCREEN_NAME = "screen_name";
    public static final String JSON_KEY_ONLINE = "online";
    public static final String JSON_KEY_PHOTO_50 = "photo_50";
    public static final String JSON_KEY_PHOTO_100 = "photo_100";
}
