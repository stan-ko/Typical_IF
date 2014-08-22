package typical_if.android;

import android.text.Editable;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by admin on 17.07.2014.
 */
public class VKHelper {
    public static int count=0;
    public static void getAlbumList(long groupID, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", groupID);
        params.put("need_covers", 1);
        params.put("photo_sizes", 1);
        final VKRequest request = new VKRequest("photos.getAlbums", params);
        request.executeWithListener(listener);
    }

    public static void getPhotoList(long owner_id, long album_id,int rev, VKRequest.VKRequestListener listener) {


        VKParameters params = new VKParameters();

        if(count==0){

        params.put("owner_id", owner_id);
        params.put("album_id", album_id);
        params.put("rev",rev);
        params.put("extended", 1);
        params.put("offset",0);
        params.put("count",200);
        }

else {
           int offset =count*100 ;

            params.put("owner_id", owner_id);
            params.put("album_id", album_id);
            params.put("rev",rev);
            params.put("extended", 1);
            params.put("offset",String.valueOf(offset));
            params.put("count",200);

        }
        count++;
        count++;
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

    public static void doGroupWallRequest(int countPosts, long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", gid);
        params.put("domain", gid);
        params.put("offset", 0);
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

    public static void createCommentForPhoto(long owner_id, long photo_id, String message, int from_group, int reply_to_comment, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("photo_id", photo_id);
        params.put("message", message);
        params.put("reply_to_comment", reply_to_comment);
        final VKRequest request = new VKRequest("photos.createComment", params);
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
    
    public static void createCommentForPost(long owner_id, long post_id, String message, int reply_to_comment, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("post_id", post_id);
        params.put("text", message);
        params.put("reply_to_comment", reply_to_comment);
        final VKRequest request = new VKRequest("wall.addComment", params);
        request.executeWithListener(listener);
    }

//    public static void doRepost(String pid, String message, VKRequest.VKRequestListener listener) {
//        VKParameters params = new VKParameters();
//        params.put("object", pid);
//        params.put("message", message);
//        final VKRequest request = new VKRequest("wall.repost", params);
//        request.executeWithListener(listener);
//    }

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

    public static void getCommentsForPhoto(long owner_id, long photo_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("photo_id", photo_id);
        params.put("need_likes", 1);
        params.put("offset", 0);
        params.put("count", 100);

        params.put("sort", "desc");
        params.put("access_key", "");
        params.put("extended", 1);

        final VKRequest request = new VKRequest("photos.getComments", params);
        request.executeWithListener(listener);
    }

    public static void getCommentsForPost(long owner_id, long post_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("post_id", post_id);
        params.put("need_likes", 1);
        params.put("offset", 0);
        params.put("count", 100);
        params.put("sort", "asc");
        params.put("access_key", "");
        params.put("extended", 1);

        final VKRequest request = new VKRequest("wall.getComments", params);
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

    public static JSONArray[] getResponseArrayOfComment(JSONObject response) {
       // if (response == null || response.json == null) return null;
        JSONArray[] array = new JSONArray[2];
        array[0] = response.optJSONObject("response").optJSONArray("items");
        array[1] = response.optJSONObject("response").optJSONArray("profiles");
        return array;
    }

    public static void deleteCommentForPhoto(long owner_id, long comment_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("comment_id", comment_id);
        final VKRequest request = new VKRequest("photos.deleteComment", params);
        request.executeWithListener(listener);
    }

    public static void deleteCommentForPost(long owner_id, long comment_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("comment_id", comment_id);
        final VKRequest request = new VKRequest("wall.deleteComment", params);
        request.executeWithListener(listener);
    }

    public static void editCommentForPhoto(long owner_id, long comment_id, String message, Void attachments, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("comment_id", comment_id);
        params.put("message", message);
        params.put("attachments", attachments);
        final VKRequest request = new VKRequest("photos.editComment", params);
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

    public static void getUserInfo(VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        final VKRequest request = new VKRequest("users.get", params);
        request.executeWithListener(listener);
    }

    public static void getPostUserInfo(long user_id, String fields, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("user_id",user_id);
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


}
