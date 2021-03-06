package typical_if.android;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.vk.sdk.VKScope;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

import typical_if.android.adapter.FullScreenImageAdapter;

/**
 * Created by Miller on 17.07.2014.
 */
public class Constants {

    public static final String[] S_MY_SCOPE = new String[]{
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.AUDIO,
            VKScope.VIDEO,
            VKScope.DIRECT,
            VKScope.GROUPS,
            VKScope.PAGES,
            VKScope.NOTES,
            VKScope.OFFLINE,
            VKScope.FRIENDS,
            VKScope.DOCS,
            VKScope.STATS,
            VKScope.STATUS,
            VKScope.NOTIFY,
            VKScope.ADS,
            VKScope.MESSAGES,
            VKScope.NOTIFICATIONS
    };

    public static final long TF_ID = -36573302;

    public static final long ZF_ID = -24818281; //true
//    public static final long ZF_ID = -85319952; //test

    public static final long TZ_ID = -26363301;
    public static final long FB_ID = -39253850;
    public static final long FN_ID = -38380107;
    public static final long ST_ID = -73502996;

    public static Activity mainActivity;
    public static final String APP_ID = "4456259";
    public static Locale LOCALE;
    public static boolean isFragmentCommentsLoaded;
    public static boolean isPollFragmentLoaded;
    public static boolean isPhotoListFragmentLoaded;
    public static boolean isFragmentMakePostLoaded;
    public static boolean isFragmentAlbumListLoaded;
    public static boolean isFragmentFullScreenLoaded;
    public static int isMember;


    public static final String ACTION_BOOT_COMPLETED = "1";
    public static final String ACTION_START_FROM_SPLASH_ACTIVITY = "2";
    public static final String ACTION_FIRST_RUN = "3";
    public static final int CONT_OF_SEARCH_POST = 50;

    public static final String SCHEDULE_FOR_EIGHT_HOUR = "eightHour";
    public static final String SCHEDULE_FOR_ONE_HOUR = "OneHour";
    public static final String REPEAT_ACTION = "repeat";

    public static final String DATE_OF_NOTIF_SEND = "dateOfNotifSend";
    public static final String VK_GROUP_ID = "groupId";


    public static long USER_ID;
    public static String USER_LANGUAGE;

    public static String PARAM_NAME;
    public static String PARAM_NAME2;

    public static String DELETE_COMMENT_METHOD_NAME;
    public static String CREATE_COMMENT_METHOD_NAME;
    public static String EDIT_COMMENT_METHOD_NAME;
    public static String GET_COMMENTS_METHOD_NAME;

    public static long GROUP_ID;
    public static long ALBUM_ID;
    public static long TEMP_OWNER_ID;

    public static Menu makePostMenu;

    public static Deque<FullScreenImageAdapter> queueOfAdapters = new LinkedList<FullScreenImageAdapter>();


    public static ArrayList<VKApiPhoto> tempPhotoPostAttach = new ArrayList<VKApiPhoto>();
    public static ArrayList<VKApiVideo> tempVideoPostAttach = new ArrayList<VKApiVideo>();
    public static ArrayList<VKApiAudio> tempAudioPostAttach = new ArrayList<VKApiAudio>();
    public static ArrayList<VKApiDocument> tempDocPostAttach = new ArrayList<VKApiDocument>();

    public static int tempPostAttachCounter = 0;
    public static int tempMaxPostAttachCounter = 0;
    public static int tempCurrentPhotoAttachCounter = 0;


    public static Intent myIntent;
    public static int refresherDrawerCounter;

    public static long timerForNotif = 0;
    public static int notifID = 2048;
    public static NotificationManager notificationManager;
    public static String title = "";
    public static String Mtitle = "";
    public static String MtitlePoll = "";
    public static String artist = "";
    public static String tempCameraPhotoFile = "";
    public static CheckBox previousCheckBoxState = null;
    public static SeekBar previousSeekBarState = null;

    public static String tempTextSuggestPost = "";

    public static final Resources RESOURCES = TIFApp.getAppContext().getResources();

    public static final String TIME_FORMAT_STRING = "kk:mm";
    public static final String DATE_TIME_FORMAT_STRING = "d MMMM о kk:mm";
    public static final String OTHER_FORMAT_STRING = "dd MMMM yyyy, kk:mm";

    public static final String TODAY = RESOURCES.getString(R.string.date_today);
    public static final String YESTERDAY = RESOURCES.getString(R.string.date_yesterday);

    public static final String DOC_TYPE_ANIMATION = RESOURCES.getString(R.string.doc_type_animation);
    public static final String DOC_TYPE_DOCUMENT = RESOURCES.getString(R.string.doc_type_document);
    public static final String DOC_TYPE_IMAGE = RESOURCES.getString(R.string.doc_type_image);

    public static final String SIZE_IN_B = RESOURCES.getString(R.string.size_in_b);
    public static final String SIZE_IN_KB = RESOURCES.getString(R.string.size_in_kb);
    public static final String SIZE_IN_MB = RESOURCES.getString(R.string.size_in_mb);
    public static final String SIZE_IN_GB = RESOURCES.getString(R.string.size_in_gb);
    public static final String SIZE_IN_TB = RESOURCES.getString(R.string.size_in_tb);

    public static final String BROWSER_CHOOSER = RESOURCES.getString(R.string.browser_chooser);
    public static final String DOWNLOADER_CHOOSER = RESOURCES.getString(R.string.downloader_chooser);
    public static final String VIEWER_CHOOSER = RESOURCES.getString(R.string.viewer_chooser);

    public static final String SHOW_ALL_TEXT = RESOURCES.getString(R.string.show_all_text);
    public static final String SHOW_MIN_TEXT = RESOURCES.getString(R.string.show_min_text);

    public static final String POLL_ANONYMOUS = RESOURCES.getString(R.string.poll_anonymous);
    public static final String POLL_NOT_ANONYMOUS = RESOURCES.getString(R.string.poll_not_anonymous);
    public static final String txt_dialog_comment = RESOURCES.getString(R.string.txt_dialog_comment);

    public static final String TIF_VK_API_KEY_TOKEN = "VK_ACCESS_TOKEN";
    public static final int TIF_VK_PRELOAD_POSTS_COUNT = 50;

    public static final int TODAY_EVENT = 0;
    public static final int STATION_EVENT = 1;
    public static final int PERIOD_EVENT = 2;

    public static final int EVENT_COUNT = 3;

    public static JSONObject files;

}
