package typical_if.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LJ on 16.07.2014.
 */
public class Photo {
    //  public int count; // количество записей, которое будет получено.
    public long id ;
    public long album_id; //идентификатор альбома. Для служебных альбомов используются следующие идентификаторы:
    public long owner_id; //идентификатор владельца альбома
    public long user_id;
    public String photo_75;
    public String photo_130;
    public String photo_604;
    public String photo_807;
    public String photo_1280;
    public String photo_2048;
    public int width;
    public int height;
    public String text;
    public long date;
    public int user_likes;
    public int likes;
    public int comments;
    public int can_comment;





    public Photo (long id,long owner_id, long album_id, long user_id, String photo_75, String photo_130,
                  String photo_604, String photo_807, String photo_1280,String photo_2048,
                  int width, int height,String text, long date, int user_likes,
                  int likes, int comments, int can_comment){

        this.id=id;
        this.album_id=album_id;
        this.owner_id=owner_id;
        this.user_id=user_id;
        this.photo_75=photo_75;
        this.photo_130=photo_130;
        this.photo_604=photo_604;
        this.photo_807=photo_807;
        this.photo_1280=photo_1280;
        this.photo_2048=photo_2048;
        this.width=width;
        this.height=height;
        this.text=text;
        this.date=date;
        this.user_likes=user_likes;
        this.likes=likes;
        this.comments=comments;
        this.can_comment=can_comment;
    }

    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_OWNER_ID = "owner_id";
    public static final String JSON_KEY_ALBUM_ID = "album_id";
    public static final String JSON_KEY_USER_ID = "user_id";
    public static final String JSON_KEY_PHOTO_75 = "photo_75";
    public static final String JSON_KEY_PHOTO_130= "photo_130";
    public static final String JSON_KEY_PHOTO_604 = "photo_604";
    public static final String JSON_KEY_PHOTO_807 = "photo_807";
    public static final String JSON_KEY_PHOTO_1280 = "photo_1280";
    public static final String JSON_KEY_PHOTO_2048= "photo_2048";
    public static final String JSON_KEY_WIDTH ="width";
    public static final String JSON_KEY_HEIGHT= "height";
    public static final String JSON_KEY_TEXT = "text";
    public static final String JSON_KEY_DATE= "date";
    public static final String JSON_KEY_USER_LIKES= "user_likes";
    public static final String JSON_KEY_LIKES= "count";
    public static final String JSON_KEY_COMMENTS= "count";
    public static final String JSON_KEY_CAN_COMMENT= "can_comment";



    public static Photo getPhotoFromJSON(JSONObject PhotoJSON) {
        JSONObject likes = PhotoJSON.optJSONObject("likes");
        JSONObject comments = PhotoJSON.optJSONObject("comments");
//        Log.d("----------------------likes---------->",likes.toString()+"");
        return new Photo(PhotoJSON.optLong(JSON_KEY_ID),
                PhotoJSON.optLong(JSON_KEY_OWNER_ID),
                PhotoJSON.optLong(JSON_KEY_ALBUM_ID),
                PhotoJSON.optLong(JSON_KEY_USER_ID),
                PhotoJSON.optString(JSON_KEY_PHOTO_75),
                PhotoJSON.optString(JSON_KEY_PHOTO_130),
                PhotoJSON.optString(JSON_KEY_PHOTO_604),
                PhotoJSON.optString(JSON_KEY_PHOTO_807),
                PhotoJSON.optString(JSON_KEY_PHOTO_1280),
                PhotoJSON.optString(JSON_KEY_PHOTO_2048),
                PhotoJSON.optInt(JSON_KEY_WIDTH),
                PhotoJSON.optInt(JSON_KEY_HEIGHT),
                PhotoJSON.optString(JSON_KEY_TEXT),
                PhotoJSON.optLong(JSON_KEY_DATE),
                likes.optInt(JSON_KEY_USER_LIKES),
                likes.optInt(JSON_KEY_LIKES),
                comments.optInt(JSON_KEY_COMMENTS),
                PhotoJSON.optInt(JSON_KEY_CAN_COMMENT));
    }
public static int countOfPhotos;
    public static ArrayList<Photo> getPhotosFromJSONArray(JSONObject jsonArray) {
      JSONObject object = jsonArray.optJSONObject("response");
        try {
            countOfPhotos = object.getInt("count");
        } catch (JSONException e) {}
        JSONArray array = object.optJSONArray("items");
        final ArrayList<Photo> photos = new ArrayList<Photo>();
       for (int i=0; i<array.length(); i++){
         final Photo photo = getPhotoFromJSON(array.optJSONObject(i));
             photos.add(photo);
         }

        return photos;



    }



}

