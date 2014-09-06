package typical_if.android;

import android.text.Editable;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPostArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;


/**
 * Created by admin on 17.07.2014.
 */
public class VKHelper {


    public static int offsetCounter;
    public static void getAlbumList(long groupID, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", groupID);
        params.put("need_covers", 1);
        params.put("photo_sizes", 1);
        final VKRequest request = new VKRequest("photos.getAlbums", params);
        request.executeWithListener(listener);
    }

    public static void getPhotoList(long owner_id, long album_id,int rev, int count, VKRequest.VKRequestListener listener) {
         VKParameters params = new VKParameters();


        if (offsetCounter==0){

            if(count==0){

                params.put("owner_id", owner_id);
                params.put("album_id", album_id);
                params.put("rev",rev);
                params.put("extended", 1);
                params.put("offset",0);

            }else {
                params.put("owner_id", owner_id);
                params.put("album_id", album_id);
                params.put("rev",rev);
                params.put("extended", 1);
                params.put("offset",0);
                params.put("count",count);

            }
        }

        else {


            int offset =offsetCounter*50 ;

            params.put("owner_id", owner_id);
            params.put("album_id", album_id);
            params.put("rev",rev);
            params.put("extended", 1);
            params.put("offset",String.valueOf(offset));
            params.put("count",100);

        }


        offsetCounter++;
        offsetCounter++;
        final VKRequest request = new VKRequest("photos.get", params);
        request.executeWithListener(listener);
    }

    public static void editSuggestedPost(long gid, long pid, Editable message, String attachments, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("post_id", pid);
        params.put("owner_id", gid);
        params.put("message", message);
        params.put("attachments", attachments);

        final VKRequest request = new VKRequest("wall.edit", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void deleteSuggestedPost(long gid, long pid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("post_id", pid);
        params.put("owner_id", gid);

        final VKRequest request = new VKRequest("wall.delete", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void getSuggestedPosts(long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", gid);
        params.put("domain", gid);
        params.put("offset", 0);
        params.put("count", 100);
        params.put("filter", "suggests");
        params.put("extended", 1);

        final VKRequest request = VKApi.wall().get(params);
        request.executeWithListener(vkRequestListener);
    }

    public static void doGroupWallRequest(int offset, int countPosts, long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", gid);
        params.put("domain", gid);
        params.put("offset", offset);
        params.put("count", countPosts);
        params.put("filter", "all");
        params.put("extended", 1);

        final VKRequest request = VKApi.wall().get(params);
        request.executeWithListener(vkRequestListener);
    }

    public static void setLike(String type, long owner_id, long item_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("type", type);
        params.put("owner_id", owner_id);
        params.put("item_id", item_id);
        final VKRequest request = new VKRequest("likes.add", params);
        request.executeWithListener(listener);
    }

    public static void deleteLike(String type, long owner_id, long item_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("type", type);
        params.put("owner_id", owner_id);
        params.put("item_id", item_id);
        final VKRequest request = new VKRequest("likes.delete", params);
        request.executeWithListener(listener);
    }



    public static void isLiked(String type, long owner_id, long item_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        //params.put("user_id",user_id );
        params.put("type", type);
        params.put("owner_id", owner_id);
        params.put("item_id", item_id);
        final VKRequest request = new VKRequest("likes.isLiked", params);
        request.executeWithListener(listener);

    }

    public static void getUserAudios(VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", Constants.USER_ID);

        final VKRequest request = new VKRequest("audio.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void getUserVideos(VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", Constants.USER_ID);

        final VKRequest request = new VKRequest("video.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void getUserDocs(VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", Constants.USER_ID);

        final VKRequest request = new VKRequest("docs.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void doRepost(String pid, String message, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("object", pid);
        params.put("message", message);
        final VKRequest request = new VKRequest("wall.repost", params);
        request.executeWithListener(listener);
    }

    public static void doReportPost(long oid, long pid, int reason, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", oid);
        params.put("post_id", pid);
        params.put("reason", reason);
        final VKRequest request = new VKRequest("wall.reportPost", params);
        request.executeWithListener(listener);
    }

    public static void getComments(long owner_id, long item_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put(Constants.PARAM_NAME2, item_id);
        params.put("need_likes", 1);
        params.put("offset", 0);
        params.put("count", 100);

        params.put("sort", "desc");
        params.put("access_key", "");
        params.put("extended", 1);

        final VKRequest request = new VKRequest(Constants.GET_COMMENTS_METHOD_NAME, params);
        request.executeWithListener(listener);
    }


    public static JSONArray[] getResponseArrayOfComment(JSONObject response) {
       // if (response == null || response.json == null) return null;
       Log.d("getResponseArrayOfComment-00000000000000000000000000000000000000000000000",response+"");
        JSONArray[] array = new JSONArray[2];
        array[0] = response.optJSONObject("response").optJSONArray("items");
        array[1] = response.optJSONObject("response").optJSONArray("profiles");
        return array;
    }

    public static void deleteComment(long owner_id, long item_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("comment_id", item_id);
        final VKRequest request = new VKRequest(Constants.DELETE_COMMENT_METHOD_NAME, params);
        request.executeWithListener(listener);
    }

    public static void createComment(long owner_id, long item_id, String message, int reply_to_comment, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put(Constants.PARAM_NAME2, item_id);
        params.put(Constants.PARAM_NAME, message);
        params.put("reply_to_comment", reply_to_comment);
        final VKRequest request = new VKRequest(Constants.CREATE_COMMENT_METHOD_NAME, params);
        request.executeWithListener(listener);
    }

    public static void editComment(long owner_id, long comment_id, String message, VKAttachments.VKApiAttachment attachments, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("comment_id", comment_id);
        params.put("message", message);
        params.put("attachments", attachments);
        final VKRequest request = new VKRequest(Constants.EDIT_COMMENT_METHOD_NAME, params);
        request.executeWithListener(listener);
    }

    public static ArrayList<VKApiComment> getCommentsFromJSON(JSONArray arrayOfComments) {
        final ArrayList<VKApiComment> comments = new ArrayList<VKApiComment>();
        for (int i = 0; i < arrayOfComments.length(); i++) {
            VKApiComment comment1 = new VKApiComment();
            final VKApiComment comment = comment1.parse(arrayOfComments.optJSONObject(i));
            comments.add(comment);
        }
        return comments;
    }




    public static void getMyselfInfo(VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        final VKRequest request = new VKRequest("users.get", params);
        request.executeWithListener(listener);
    }

    public static void getWhoIsPosted(long user_id, String fields, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("user_ids",user_id);
        params.put("fields",fields);
        final VKRequest request = new VKRequest("users.get", params);
        request.executeWithListener(listener);
    }
    public static void doWallPost(long owner_id, Editable message, String attachments, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id",owner_id);
        params.put("message", message);
        params.put("attachments", attachments);

        final VKRequest request = new VKRequest("wall.post", params);
        request.executeWithListener(vkRequestListener);
    }


    public static void doPlayerRequest(String videos, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("videos", videos);
        params.put("extended", 1);

        final VKRequest request = new VKRequest("video.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static  ArrayList<VKApiPhotoAlbum> getAlbumFromJSONArray(JSONObject jsonArray) {
        JSONObject object = jsonArray.optJSONObject("response");
        JSONArray array = object.optJSONArray("items");
        final ArrayList<VKApiPhotoAlbum> albums = new ArrayList<VKApiPhotoAlbum>();
        for (int i=0; i<array.length(); i++){
            final VKApiPhotoAlbum album = new VKApiPhotoAlbum().parse(array.optJSONObject(i));
            albums.add(album);
        }
        return albums;
    }

    public static ArrayList<VKApiUser> getProfilesFromJSONArray(JSONArray array) {
        final ArrayList<VKApiUser> profiles = new ArrayList<VKApiUser>();
        for (int i=0; i<array.length(); i++) {
            final VKApiUser profile = new VKApiUser().parse(array.optJSONObject(i));
            profiles.add(profile);
        }
        return profiles;
    }

    public static ArrayList<VKApiPhoto> getPhotosFromJSONArray(JSONObject jsonArray) {
        JSONObject object = jsonArray.optJSONObject("response");
        try {
            countOfPhotos = object.getInt("count");
        } catch (JSONException e) {}
        JSONArray array = object.optJSONArray("items");

        final ArrayList<VKApiPhoto> photos = new ArrayList<VKApiPhoto>();
        for (int i=0; i<array.length(); i++){
            final VKApiPhoto photo = new VKApiPhoto().parse(array.optJSONObject(i));
            photos.add(photo);
        }
        return photos;
    }
    public static int countOfPhotos;

    public static void isMember(long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("group_id", gid);

        final VKRequest request = new VKRequest("groups.isMember", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void groupJoin(long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("group_id", gid);

        final VKRequest request = new VKRequest("groups.join", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void groupLeave(long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("group_id", gid);

        final VKRequest request = new VKRequest("groups.leave", params);
        request.executeWithListener(vkRequestListener);
    }

    public static Wall getGroupWallFromJSON(final JSONObject jsonObject) {
        final Wall wall = new Wall();
        final JSONObject object = jsonObject.optJSONObject(Wall.JSON_KEY_RESPONSE);
        wall.count = object.optInt(Wall.JSON_KEY_COUNT);
        Log.d(wall.TAG, String.valueOf(wall.count));
        // items
        final VKPostArray posts = new VKPostArray();
        try {
            posts.parse(jsonObject);
        } catch (JSONException e) {
        }

        ArrayList<VKWallPostWrapper> wallPosts = new ArrayList<VKWallPostWrapper>();
        for (int i = 0; i < posts.size(); i++) {
            wallPosts.add(new VKWallPostWrapper(posts.get(i)));
        }

        wall.posts = wallPosts;

        // groups
        final JSONArray groups = object.optJSONArray(Wall.JSON_KEY_GROUPS);
        //Log.d(wall.TAG, "Wall groups: " + groups.toString());
        VKApiCommunity group;
        VKApi.users().get();
        for (int i = 0; i < groups.length(); i++) {
            group = new VKApiCommunity().parse(groups.optJSONObject(i));
            wall.groups.add(group);
        }
        wall.group = new VKApiCommunity().parse(groups.optJSONObject(0));
        // profiles
        wall.profiles=getProfilesFromJSONArray(object.optJSONArray(Wall.JSON_KEY_PROFILES));
        return wall;
    }

}
