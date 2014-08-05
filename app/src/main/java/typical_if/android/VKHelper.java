package typical_if.android;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 17.07.2014.
 */
public class VKHelper {
    public static void getAlbumList(long groupID, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", groupID);
        params.put("need_covers", 1);
        params.put("photo_sizes", 1);
        final VKRequest request = new VKRequest("photos.getAlbums", params);
        request.executeWithListener(listener);
    }

    public static void getPhotoList(long owner_id, long album_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("album_id", album_id);
        params.put("extended", 1);
        final VKRequest request = new VKRequest("photos.get", params);
        request.executeWithListener(listener);
    }

    public static void doGroupWallRequest(long gid, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", gid);
        params.put("domain", gid);
        params.put("offset", 0);
        params.put("count", 100);
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

    public static void getFixedPostId(String gid, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("group_id", gid);
        params.put("fields", "fixed_post");
        final VKRequest request = new VKRequest("groups.getById", params);
        request.executeWithListener(listener);
    }

    public static void getFixedPost(String pid, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("posts", pid);
        params.put("extended", 1);
        params.put("copy_history_depth", 1);
        final VKRequest request = new VKRequest("wall.getById", params);
        request.executeWithListener(listener);
    }

//    public static void doRepost(String pid, String message, VKRequest.VKRequestListener listener) {
//        VKParameters params = new VKParameters();
//        params.put("object", pid);
//        params.put("message", message);
//        final VKRequest request = new VKRequest("wall.repost", params);
//        request.executeWithListener(listener);
//    }

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

    public static JSONArray[] getResponseArrayOfComment(VKResponse response) {
        if (response == null || response.json == null) return null;
        JSONArray[] array = new JSONArray[2];
        array[0] = response.json.optJSONObject("response").optJSONArray("items");
        array[1] = response.json.optJSONObject("response").optJSONArray("profiles");

        return array;
    }

    public static void deleteCommentForPhoto(long owner_id, long comment_id, VKRequest.VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("owner_id", owner_id);
        params.put("comment_id", comment_id);
        final VKRequest request = new VKRequest("photos.deleteComment", params);
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

    public static void doPlayerRequest(String videos, VKRequest.VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("videos", videos);
        params.put("extended", 1);

        final VKRequest request = new VKRequest("video.get", params);
        request.executeWithListener(vkRequestListener);
    }
}
