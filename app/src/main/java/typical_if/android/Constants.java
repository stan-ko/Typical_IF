package typical_if.android;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.vk.sdk.VKScope;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import java.util.ArrayList;

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
    //public static final long TF_ID = -42709618;
    public static final long TZ_ID = -26363301;
    public static final long FB_ID = -39253850;
    public static final long FN_ID = -38380107;

    public static Activity mainActivity;
    public static final String APP_ID = "4456259";

    public static long USER_ID;

    public static ArrayList<VKApiPhoto> tempPhotoPostAttach = new ArrayList<VKApiPhoto>();
    public static ArrayList<VKApiVideo> tempVideoPostAttach = new ArrayList<VKApiVideo>();
    public static ArrayList<VKApiAudio> tempAudioPostAttach = new ArrayList<VKApiAudio>();
    public static ArrayList<VKApiDocument> tempDocPostAttach = new ArrayList<VKApiDocument>();
    public static int tempPostAttachCounter = 0;
    public static int tempMaxPostAttachCounter = 0;
    public static int tempCurrentPhotoAttachCounter = 0;


    public static String tempTextSuggestPost = "";

    public static String tempCameraPhotoFile = "";
    public static MediaPlayer mediaPlayer = null;
    public static boolean originRecord = true;
    public static Thread tempThread = null;
    public static CheckBox previousCheckBoxState = null;
    public static SeekBar previousSeekBarState = null;
    public static AudioRecords playedPausedRecord = new AudioRecords(null, false, false, false);



    public static final Resources RESOURCES = MyApplication.getAppContext().getResources();

    public static final String TIME_FORMAT_STRING = "kk:mm";
    public static final String DATE_TIME_FORMAT_STRING = "d MMMM о kk:mm";
    public static final String OTHER_FORMAT_STRING = "MMMM dd yyyy, kk:mm";

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

}
