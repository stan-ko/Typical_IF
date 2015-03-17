package typical_if.android.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LJ on 14.07.2014.
 */
public class Album {
    public long id; //— идентификатор альбома;
    public String thumb_id; //— идентификатор фотографии, которая является обложкой;
    public long owner_id; //— идентификатор владельца альбома;
    public String title; //— название альбома;
    public String description; //— описание альбома; (не приходит для системных альбомов)
    public String created; //— дата создания альбома в формате unixtime; (не приходит для системных альбомов);
    public String updated; //— дата последнего обновления альбома в формате unixtime; (не приходит для системных альбомов);
    public int size; //— количество фотографий в альбоме;
    public int can_upload; //— 1, если текущий пользователь может загружать фотографии в альбом (при запросе информации об альбомах сообщества);
    public String privacy_view; //— настройки приватности для альбома в формате настроек приватности; (не приходит для системных альбомов)
    public String privacy_comment; //— настройки приватности для альбома в формате настроек приватности; (не приходит для системных альбомов)
    public String thumb_src; //— ссылка на изображение обложки альбома (если был указан параметр need_covers).
    public boolean coverUrlIsLoading; //— ссылка на изображение запрашивается
    public String coverUrl; //— ссылка на изображение обложки альбома
    public JSONArray sizes;
  //  public ArrayList<VKApiPhoto> photos;

    public Album(){}
    public Album(long id, String thumb_id, long owner_id, String title, String description, String created, String updated,
                 int size, int can_upload, String privacy_view, String privacy_comment, String thumb_src, JSONArray sizes) {

        this.id = id;
        this.thumb_id = thumb_id;
        this.owner_id = owner_id;
        this.title = title;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.size = size;
        this.can_upload = can_upload;
        this.privacy_view = privacy_view;
        this.privacy_comment = privacy_comment;
        this.thumb_src = thumb_src;
        this.sizes=sizes;
    }
    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_THUMB_ID = "thumb_id";
    public static final String JSON_KEY_OWNER_ID = "owner_id";
    public static final String JSON_KEY_TITLE = "title";
    public static final String JSON_KEY_DESCRIPTION = "description";
    public static final String JSON_KEY_CREATED = "created";
    public static final String JSON_KEY_UPDATED = "updated";
    public static final String JSON_KEY_SIZE = "size";
    public static final String JSON_KEY_CAN_UPLOAD = "can_upload";
    public static final String JSON_KEY_PRIVACY_VIEW ="privacy_view";
    public static final String JSON_KEY_PRIVACY_COMMENT = "privacy_comment";
    public static final String JSON_KEY_THUMBS_SRC = "thumb_src";
    public static final String JSON_KEY_SIZES = "sizes";
    public static final String JSON_URL_SRC = "src";

    public static Album getAlbumFromJSON(JSONObject albumJSON){
        return new Album(albumJSON.optLong(JSON_KEY_ID),
                albumJSON.optString(JSON_KEY_THUMB_ID),
                albumJSON.optLong(JSON_KEY_OWNER_ID),
                albumJSON.optString(JSON_KEY_TITLE),
                albumJSON.optString(JSON_KEY_DESCRIPTION),
                albumJSON.optString(JSON_KEY_CREATED),
                albumJSON.optString(JSON_KEY_UPDATED),
                albumJSON.optInt(JSON_KEY_SIZE),
                albumJSON.optInt(JSON_KEY_CAN_UPLOAD),
                albumJSON.optString(JSON_KEY_PRIVACY_VIEW),
                albumJSON.optString(JSON_KEY_PRIVACY_COMMENT),
                albumJSON.optString(JSON_KEY_THUMBS_SRC),
                albumJSON.optJSONArray(JSON_KEY_SIZES));
    }




    public static  ArrayList<Album> getAlbumFromJSONArray(JSONObject jsonArray) {
        JSONObject object = jsonArray.optJSONObject("response");
        JSONArray array = object.optJSONArray("items");
        final ArrayList<Album> albums = new ArrayList<Album>();
        for (int i=0; i<array.length(); i++){
            final Album album = getAlbumFromJSON(array.optJSONObject(i));
            albums.add(album);
        }
        return albums;
    }

//    public static String getCoverUrl (JSONArray array){
//        //for (int i=0; i<array.length(); i++){
//            String url = array.optJSONObject(2).optString(JSON_URL_SRC);
//        }//
//    }

}
