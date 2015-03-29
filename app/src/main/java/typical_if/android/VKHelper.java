package typical_if.android;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKApiPoll;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiVideo;
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

    // SOME VK SDK KEYS
    public static final String TIF_VK_SDK_KEY_POST = "post";
    public static final String TIF_VK_SDK_KEY_PHOTO = "photo";
    public static final String TIF_VK_SDK_KEY_OWNER_ID = "owner_id";
    public static final String TIF_VK_SDK_KEY_DOMAIN = "domain";
    public static final String TIF_VK_SDK_KEY_OFFSET = "offset";
    public static final String TIF_VK_SDK_KEY_COUNT = "count";
    public static final String TIF_VK_SDK_KEY_FILTER = "filter";
    public static final String TIF_VK_SDK_KEY_ALL = "all";
    public static final String TIF_VK_SDK_KEY_EXTENDED = "extended";
    public static final String TIF_VK_SDK_KEY_ALBUM_ID = "album_id";
    public static final String TIF_VK_SDK_KEY_COMMENT_ID = "comment_id";
    public static final String TIF_VK_SDK_KEY_GROUP_ID = "group_id";
    public static final String TIF_VK_SDK_KEY_ITEM_ID = "item_id";
    public static final String TIF_VK_SDK_KEY_POST_ID = "post_id";
    public static final String TIF_VK_SDK_KEY_POLL_ID = "poll_id";
    public static final String TIF_VK_SDK_KEY_ANSWER_ID = "answer_id";
    public static final String TIF_VK_SDK_KEY_REV = "rev";
    public static final String TIF_VK_SDK_KEY_MESSAGE = "message";
    public static final String TIF_VK_SDK_KEY_ATTACHMENTS = "attachments";
    public static final String TIF_VK_SDK_KEY_NEED_COVERS = "need_covers";
    public static final String TIF_VK_SDK_KEY_PHOTO_SIZES = "photo_sizes";
    public static final String TIF_VK_SDK_KEY_RESPONSE = "response";
    public static final String TIF_VK_SDK_KEY_ITEMS = "items";
    public static final String TIF_VK_SDK_KEY_PROFILES = "profiles";
    public static final String TIF_VK_SDK_KEY_GROUPS = "groups";
    public static final String TIF_VK_SDK_KEY_TYPE = "type";
    public static final String TIF_VK_SDK_KEY_IS_BOARD = "is_board";


    public static int offsetCounter;

    public static void getAlbumList(long groupID, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, groupID);
        params.put(TIF_VK_SDK_KEY_NEED_COVERS, 1);
        params.put(TIF_VK_SDK_KEY_PHOTO_SIZES, 1);
        final VKRequest request = new VKRequest("photos.getAlbums", params);
        request.executeWithListener(listener);
    }

    public static void getPhotoList(long owner_id, long album_id, int rev, int count, VKRequestListener listener) {
        VKParameters params = new VKParameters();

        if (offsetCounter == 0) {
            if (count == 0) {
                params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
                params.put(TIF_VK_SDK_KEY_ALBUM_ID, album_id);
                params.put(TIF_VK_SDK_KEY_REV, rev);
                params.put(TIF_VK_SDK_KEY_EXTENDED, 1);
                params.put(TIF_VK_SDK_KEY_OFFSET, 0);
            }
            else {
                params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
                params.put(TIF_VK_SDK_KEY_ALBUM_ID, album_id);
                params.put(TIF_VK_SDK_KEY_REV, rev);
                params.put(TIF_VK_SDK_KEY_EXTENDED, 1);
                params.put(TIF_VK_SDK_KEY_OFFSET, 0);
                params.put(TIF_VK_SDK_KEY_COUNT, count);
            }
        }
        else {

            int offset = offsetCounter * 50;

            params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
            params.put(TIF_VK_SDK_KEY_ALBUM_ID, album_id);
            params.put(TIF_VK_SDK_KEY_REV, rev);
            params.put(TIF_VK_SDK_KEY_EXTENDED, 1);
            params.put(TIF_VK_SDK_KEY_OFFSET, String.valueOf(offset));
            params.put(TIF_VK_SDK_KEY_COUNT, 100);
        }

        offsetCounter++;
        offsetCounter++;
        final VKRequest request = new VKRequest("photos.get", params);
        request.executeWithListener(listener);
    }

    public static void editSuggestedPost(long gid, long pid, Editable message, String attachments, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_POST_ID, pid);
        params.put(TIF_VK_SDK_KEY_OWNER_ID, gid);
        params.put(TIF_VK_SDK_KEY_MESSAGE, message);
        params.put(TIF_VK_SDK_KEY_ATTACHMENTS, attachments);

        final VKRequest request = new VKRequest("wall.edit", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void deleteSuggestedPost(long gid, long pid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_POST_ID, pid);
        params.put(TIF_VK_SDK_KEY_OWNER_ID, gid);

        final VKRequest request = new VKRequest("wall.delete", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void getSuggestedPosts(long gid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, gid);
        params.put(TIF_VK_SDK_KEY_DOMAIN, gid);
        params.put(TIF_VK_SDK_KEY_OFFSET, 0);
        params.put(TIF_VK_SDK_KEY_COUNT, 100);
        params.put(TIF_VK_SDK_KEY_FILTER, "suggests");
        params.put(TIF_VK_SDK_KEY_EXTENDED, 1);

        final VKRequest request = VKApi.wall().get(params);
        request.executeWithListener(vkRequestListener);
    }

    public static void doGroupWallRequest(int offset, int countPosts, long gid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, gid);
        params.put(TIF_VK_SDK_KEY_DOMAIN, gid);
        params.put(TIF_VK_SDK_KEY_OFFSET, offset);
        params.put(TIF_VK_SDK_KEY_COUNT, countPosts);
        params.put(TIF_VK_SDK_KEY_FILTER, TIF_VK_SDK_KEY_ALL);
        params.put(TIF_VK_SDK_KEY_EXTENDED, 1);
        final VKRequest request = VKApi.wall().get(params);
        request.executeWithListener(vkRequestListener);
    }

    public static void setLikePost(long item_id, VKRequestListener listener) {
        setLike(TIF_VK_SDK_KEY_POST, OfflineMode.loadLong(Constants.VK_GROUP_ID), item_id, listener);
    }
    public static void setLikePost(long owner_id, long item_id, VKRequestListener listener) {
        setLike(TIF_VK_SDK_KEY_POST, owner_id, item_id, listener);
    }
    public static void setLike(String type, long owner_id, long item_id, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_TYPE, type);
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_ITEM_ID, item_id);
        final VKRequest request = new VKRequest("likes.add", params);
        request.executeWithListener(listener);
    }

    public static void deleteLikePost(long item_id, VKRequestListener listener) {
        deleteLike(TIF_VK_SDK_KEY_POST, OfflineMode.loadLong(Constants.VK_GROUP_ID), item_id, listener);
    }
    public static void deleteLike(String type, long owner_id, long item_id, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_TYPE, type);
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_ITEM_ID, item_id);
        final VKRequest request = new VKRequest("likes.delete", params);
        request.executeWithListener(listener);
    }

    public static void isLikedPhoto(long item_id, VKRequestListener listener) {
        isLiked(TIF_VK_SDK_KEY_PHOTO, OfflineMode.loadLong(Constants.VK_GROUP_ID), item_id, listener);
    }
    public static void isLiked(String type, long owner_id, long item_id, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        //params.put("user_id",user_id );
        params.put(TIF_VK_SDK_KEY_TYPE, type);
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_ITEM_ID, item_id);
        final VKRequest request = new VKRequest("likes.isLiked", params);
        request.executeWithListener(listener);

    }

    public static void getPollById(long owner_id, int is_board, long poll_id, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_IS_BOARD, is_board);
        params.put(TIF_VK_SDK_KEY_POLL_ID, poll_id);
        final VKRequest request = new VKRequest("polls.getById", params);
        request.executeWithListener(listener);
    }

    public static VKApiPoll getVKApiPollFromJSON(JSONObject response) throws NullPointerException {
//       if (response!=null){
        VKApiPoll poll = new VKApiPoll().parse(response);
        return poll;
//       }
//       else return null;
    }

    public static void addVote(long owner_id, long poll_id, long answer_id, int is_board, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_POLL_ID, poll_id);
        params.put(TIF_VK_SDK_KEY_ANSWER_ID, answer_id);
        params.put(TIF_VK_SDK_KEY_IS_BOARD, is_board);
        final VKRequest request = new VKRequest("polls.addVote", params);
        request.executeWithListener(listener);
    }

    public static void deleteVote(long owner_id, long poll_id, long answer_id, int is_board,  VKRequestListener listener ) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID,owner_id);
        params.put(TIF_VK_SDK_KEY_POLL_ID,poll_id);
        params.put(TIF_VK_SDK_KEY_ANSWER_ID,answer_id);
        params.put(TIF_VK_SDK_KEY_IS_BOARD,is_board);
        final VKRequest request = new VKRequest("polls.deleteVote", params);
        request.executeWithListener(listener);
    }


    public static void getUserAudios(VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, Constants.USER_ID);

        final VKRequest request = new VKRequest("audio.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void getUserVideos(VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, Constants.USER_ID);

        final VKRequest request = new VKRequest("video.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void getUserDocs(VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, Constants.USER_ID);

        final VKRequest request = new VKRequest("docs.get", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void doRepost(String pid, String message, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("object", pid);
        params.put(TIF_VK_SDK_KEY_MESSAGE, message);
        final VKRequest request = new VKRequest("wall.repost", params);
        request.executeWithListener(listener);
    }

    public static void doReportPost(long oid, long pid, int reason, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, oid);
        params.put(TIF_VK_SDK_KEY_POST_ID, pid);
        params.put("reason", reason);
        final VKRequest request = new VKRequest("wall.reportPost", params);
        request.executeWithListener(listener);
    }

    public static void getComments(long owner_id, long item_id, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(Constants.PARAM_NAME2, item_id);
        params.put("need_likes", 1);
        params.put(TIF_VK_SDK_KEY_OFFSET, 0);
        params.put(TIF_VK_SDK_KEY_COUNT, 100);

        params.put("sort", "desc");
        params.put("access_key", "");
        params.put(TIF_VK_SDK_KEY_EXTENDED, 1);

        final VKRequest request = new VKRequest(Constants.GET_COMMENTS_METHOD_NAME, params);
        request.executeWithListener(listener);
    }


    public static JSONArray[] getResponseArrayOfComment(JSONObject response) {

        JSONArray[] array = new JSONArray[3];
        array[0] = response.optJSONObject(TIF_VK_SDK_KEY_RESPONSE).optJSONArray(TIF_VK_SDK_KEY_ITEMS);
        array[1] = response.optJSONObject(TIF_VK_SDK_KEY_RESPONSE).optJSONArray(TIF_VK_SDK_KEY_PROFILES);
        array[2] = response.optJSONObject(TIF_VK_SDK_KEY_RESPONSE).optJSONArray(TIF_VK_SDK_KEY_GROUPS);
        return array;
    }

    public static void deleteComment(long owner_id, long item_id, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_COMMENT_ID, item_id);
        final VKRequest request = new VKRequest(Constants.DELETE_COMMENT_METHOD_NAME, params);
        request.executeWithListener(listener);
    }

    public static void createComment(long owner_id, long item_id, String message, int reply_to_comment, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(Constants.PARAM_NAME2, item_id);
        params.put(Constants.PARAM_NAME, message);
        params.put("reply_to_comment", reply_to_comment);
        final VKRequest request = new VKRequest(Constants.CREATE_COMMENT_METHOD_NAME, params);
        request.executeWithListener(listener);
    }

    public static void editComment(long owner_id, long comment_id, String message, VKAttachments.VKApiAttachment attachments, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_COMMENT_ID, comment_id);
        params.put(TIF_VK_SDK_KEY_MESSAGE, message);
        params.put(TIF_VK_SDK_KEY_ATTACHMENTS, attachments);
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


    public static void getMyselfInfo(VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("fields", "photo_100");
        final VKRequest request = new VKRequest("users.get", params);
        request.executeWithListener(listener);
    }

    public static void getWhoIsPosted(long user_id, String fields, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("user_ids", user_id);
        params.put("fields", fields);
        final VKRequest request = new VKRequest("users.get", params);
        request.executeWithListener(listener);
    }

    public static void doWallPost(long owner_id, Editable message, String attachments, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, owner_id);
        params.put(TIF_VK_SDK_KEY_MESSAGE, message);
        params.put(TIF_VK_SDK_KEY_ATTACHMENTS, attachments);

        final VKRequest request = new VKRequest("wall.post", params);
        request.executeWithListener(vkRequestListener);
    }


    public static void getVideoPlay(String videos, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put("videos", videos);
        params.put(TIF_VK_SDK_KEY_EXTENDED, 1);

        final VKRequest request = new VKRequest("video.get", params);
        request.executeWithListener(vkRequestListener);
    }


    public static void getPhotoByID(String photos, VKRequestListener listener) {
        VKParameters params = new VKParameters();
        params.put("photos", photos);
        params.put(TIF_VK_SDK_KEY_EXTENDED, 1);
        final VKRequest request = new VKRequest("photos.getById", params);
        request.executeWithListener(listener);
    }

    public static ArrayList<VKApiPhotoAlbum> getAlbumFromJSONArray(JSONObject jsonArray) {
        JSONObject object = jsonArray.optJSONObject(TIF_VK_SDK_KEY_RESPONSE);
        JSONArray array = object.optJSONArray(TIF_VK_SDK_KEY_ITEMS);
        final ArrayList<VKApiPhotoAlbum> albums = new ArrayList<VKApiPhotoAlbum>();
        for (int i = 0; i < array.length(); i++) {
            final VKApiPhotoAlbum album = new VKApiPhotoAlbum().parse(array.optJSONObject(i));
            albums.add(album);
        }
        return albums;
    }

    public static ArrayList<VKApiUser> getProfilesFromJSONArray(JSONArray array) {
        final ArrayList<VKApiUser> profiles = new ArrayList<VKApiUser>();
        for (int i = 0; i < array.length(); i++) {
            final VKApiUser profile = new VKApiUser().parse(array.optJSONObject(i));
            profiles.add(profile);
        }
        return profiles;
    }

    public static ArrayList<VKApiCommunity> getGroupsFromJSONArray(JSONArray array) {
        final ArrayList<VKApiCommunity> groups = new ArrayList<VKApiCommunity>();
        for (int i = 0; i < array.length(); i++) {
            final VKApiCommunity community = new VKApiCommunity().parse(array.optJSONObject(i));
            groups.add(community);
        }
        return groups;
    }

    public static VKApiPhoto getPhotoFromJSONArray(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.optJSONArray(TIF_VK_SDK_KEY_RESPONSE);
        JSONObject o = (JSONObject) array.get(0);
        VKApiPhoto photo = new VKApiPhoto().parse(o);
        return photo;
    }

    public static VKApiVideo getVideoSourceFromJson(JSONObject object) {

        JSONObject response = object.optJSONObject(TIF_VK_SDK_KEY_RESPONSE);
        JSONArray items = response.optJSONArray(TIF_VK_SDK_KEY_ITEMS);
        JSONObject video_object = null;
        try {
            video_object = items.getJSONObject(0);
        } catch (JSONException e) {

        }
        if (video_object != null) {

            VKApiVideo video = new VKApiVideo().parse(video_object);

            return video;
        } else
            return null;
    }


    public static ArrayList<VKApiPhoto> getPhotosFromJSONArray(JSONObject jsonArray) {
        JSONObject object = jsonArray.optJSONObject(TIF_VK_SDK_KEY_RESPONSE);
        try {
            countOfPhotos = object.getInt(TIF_VK_SDK_KEY_COUNT);
        } catch (Exception e) {
        }
        JSONArray array = object.optJSONArray(TIF_VK_SDK_KEY_ITEMS);

        final ArrayList<VKApiPhoto> photos = new ArrayList<VKApiPhoto>();
        for (int i = 0; i < array.length(); i++) {
            final VKApiPhoto photo = new VKApiPhoto().parse(array.optJSONObject(i));
            photos.add(photo);
        }
        return photos;
    }



    public static int countOfPhotos;

    public static ArrayList<VKApiPhoto> getPhotosByIdFromJSON(JSONObject json) {
        JSONArray array = json.optJSONArray(TIF_VK_SDK_KEY_RESPONSE);
        try {
            countOfPhotos = json.getInt(TIF_VK_SDK_KEY_COUNT);
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.i(VKHelper.class.getSimpleName(),json.toString());
        }
        final ArrayList<VKApiPhoto> photos = new ArrayList<VKApiPhoto>();
        for (int i = 0; i < array.length(); i++) {
            final VKApiPhoto photo = new VKApiPhoto().parse(array.optJSONObject(i));
            photos.add(photo);
        }
        return photos;
    }

    public static void isMember(long gid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_GROUP_ID, gid);

        final VKRequest request = new VKRequest("groups.isMember", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void exec(VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();


        final VKRequest request = new VKRequest("https://api.vk.com/method/execute.winTheGame", params);
        request.executeWithListener(vkRequestListener);
    }


    public static void groupJoin(long gid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_GROUP_ID, gid);

        final VKRequest request = new VKRequest("groups.join", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void groupLeave(long gid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_GROUP_ID, gid);

        final VKRequest request = new VKRequest("groups.leave", params);
        request.executeWithListener(vkRequestListener);
    }

    public static void doGroupWallRequest(int extended, int offset, int countPosts, long gid, VKRequestListener vkRequestListener) {
        VKParameters params = new VKParameters();
        params.put(TIF_VK_SDK_KEY_OWNER_ID, gid);
        params.put(TIF_VK_SDK_KEY_DOMAIN, gid);
        params.put(TIF_VK_SDK_KEY_OFFSET, offset);
        params.put(TIF_VK_SDK_KEY_COUNT, countPosts);
        params.put(TIF_VK_SDK_KEY_FILTER, TIF_VK_SDK_KEY_ALL);
        params.put(TIF_VK_SDK_KEY_EXTENDED, extended);

        final VKRequest request = VKApi.wall().get(params);
        request.executeWithListener(vkRequestListener);
    }

    public static Wall getGroupWallFromJSON(final JSONObject jsonObject) {
        final Wall wall = new Wall();

        final JSONObject object = jsonObject.optJSONObject(Wall.JSON_KEY_RESPONSE);

        wall.count = object.optInt(Wall.JSON_KEY_COUNT);

        // groups
        final JSONArray groups = object.optJSONArray(Wall.JSON_KEY_GROUPS);
        VKApiCommunity group;
        VKApi.users().get();

        for (int i = 0; i < groups.length(); i++) {
            group = new VKApiCommunity().parse(groups.optJSONObject(i));
            wall.groups.add(group);
        }
        wall.group = new VKApiCommunity().parse(groups.optJSONObject(0));

        // profiles
        wall.profiles = getProfilesFromJSONArray(object.optJSONArray(Wall.JSON_KEY_PROFILES));

        // items
        final VKPostArray posts = new VKPostArray();
        try {
            posts.parse(jsonObject);
        } catch (JSONException e) {
        }

        ArrayList<VKWallPostWrapper> wallPosts = new ArrayList<VKWallPostWrapper>();
        for (int i = 0; i < posts.size(); i++) {
            wallPosts.add(new VKWallPostWrapper(posts.get(i), wall));
        }

        wall.posts = wallPosts;

        return wall;
    }

    public static String TIF_VK_API_KEY_RESPONSE = TIF_VK_SDK_KEY_RESPONSE;

    public static class UserObject {
        public long id;
        public String photo;
        public String fullName;

        public UserObject(long id, String photo, String fullName) {
            this.id = id;
            this.photo = photo;
            this.fullName = fullName;

            setUserToShared(this);
        }

        static SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TIFApp.getAppContext());

        public static final String USER_OBJECT = "USER_OBJECT";

        public static UserObject getUserFromShared() {
            String userString = sharedPreferences.getString(USER_OBJECT, "0** **" + TIFApp.getAppContext().getString(R.string.tif_title_header));
            String[] userStrings = userString.split("\\*\\*");

            return new UserObject(Long.valueOf(userStrings[0]), userStrings[1], userStrings[2]);
        }

        public static void setUserToShared(UserObject user) {
            String userString = String.format("%d**%s**%s", user.id, user.photo, user.fullName);
            sharedPreferences.edit().putString(USER_OBJECT, userString).commit();
        }
    }

    public static UserObject getUserFromResponse(final VKResponse response) {
        final JSONArray arr = response.json.optJSONArray(TIF_VK_API_KEY_RESPONSE);
        final JSONObject jsonObject = arr == null ? null : arr.optJSONObject(0);

        UserObject user;

        if (jsonObject != null) {
            long id = jsonObject.optLong("id");
            String photo = jsonObject.optString("photo_100");
            String fullName = String.format("%s %s", jsonObject.optString("first_name"), jsonObject.optString("last_name"));

            user = new UserObject(id, photo, fullName);
        } else {
            user = new UserObject(0, "", "");
        }

        return user;
    }
}