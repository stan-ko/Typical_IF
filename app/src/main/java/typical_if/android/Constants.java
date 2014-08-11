package typical_if.android;

import android.content.res.Resources;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKUIHelper;

/**
 * Created by Miller on 17.07.2014.
 */
public class Constants {
    public static final String[] sMyScope = new String[]{
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

    //public static final long TF_ID = -27922438;
    public static final long TF_ID = -36573302;
    public static final long TZ_ID = -26363301;
    public static final long FB_ID = -39253850;
    public static final long FN_ID = -38380107;

    public static final String APP_ID = "4456259";
    public static long USER_ID;


    public static final Resources RESOURCES = VKUIHelper.getApplicationContext().getResources();

    public static final String timeFormatString = "kk:mm";
    public static final String dateTimeFormatString = "d MMMM о kk:mm";
    public static final String otherFormatString = "MMMM dd yyyy, kk:mm";

    public static final String today = RESOURCES.getString(R.string.date_today);
    public static final String yesterday = RESOURCES.getString(R.string.date_yesterday);

    public static final String docTypeAnimation = RESOURCES.getString(R.string.doc_type_animation);
    public static final String docTypeDocument = RESOURCES.getString(R.string.doc_type_document);
    public static final String docTypeImage = RESOURCES.getString(R.string.doc_type_image);

    public static final String size_in_b = RESOURCES.getString(R.string.size_in_b);
    public static final String size_in_kb = RESOURCES.getString(R.string.size_in_kb);
    public static final String size_in_mb = RESOURCES.getString(R.string.size_in_mb);
    public static final String size_in_gb = RESOURCES.getString(R.string.size_in_gb);
    public static final String size_in_tb = RESOURCES.getString(R.string.size_in_tb);

    public static final String browser_chooser = RESOURCES.getString(R.string.browser_chooser);
    public static final String downloader_chooser = RESOURCES.getString(R.string.downloader_chooser);
    public static final String viewer_chooser = RESOURCES.getString(R.string.viewer_chooser);

    public static final String show_all_text = RESOURCES.getString(R.string.show_all_text);
    public static final String show_min_text = RESOURCES.getString(R.string.show_min_text);

    public static final String poll_anonymous = RESOURCES.getString(R.string.poll_anonymous);
    public static final String poll_not_anonymous = RESOURCES.getString(R.string.poll_not_anonymous);
    public static final String txt_dialog_comment = RESOURCES.getString(R.string.txt_dialog_comment);

    public static final String POST_REPORT = RESOURCES.getString(R.string.post_report);
    public static final String POST_COPY_LINK = RESOURCES.getString(R.string.post_copy_link);
    public static final String POST_REPORT_SPAM = RESOURCES.getString(R.string.post_report_spam);
    public static final String POST_REPORT_OFFENSE = RESOURCES.getString(R.string.post_report_offense);
    public static final String POST_REPORT_ADULT = RESOURCES.getString(R.string.post_report_adult);
    public static final String POST_REPORT_DRUGS = RESOURCES.getString(R.string.post_report_drugs);
    public static final String POST_REPORT_PORNO = RESOURCES.getString(R.string.post_report_porno);
    public static final String POST_REPORT_VIOLENCE = RESOURCES.getString(R.string.post_report_violence);
}
