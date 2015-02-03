package typical_if.android.model.Wall;

import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;

import typical_if.android.ItemDataSetter;

/**
 * Created by admin on 18.08.2014.
 */
public class VKWallPostWrapper extends VKApiPost {
    public final VKApiPost post;

    public final int postPinnedVisibility;
    public final int postTextVisibility;
    public final int postAttachmentsVisibility;
    public final int postGeoVisibility;
    public final int postSignedVisibility;

    public int copyHistoryTextContainerVisibility;
    public int copyHistoryAttachmentsContainerVisibility;
    public int copyHistoryGeoContainerVisibility;
    public int copyHistorySignedContainerVisibility;
    public final int copyHistoryContainerVisibility;

    public final boolean postTextChecker;
    public final boolean postAttachmentsChecker;
    public final boolean postGeoChecker;
    public final boolean postSignedChecker;

    public final boolean copyHistoryChecker;
    public boolean copyHistoryTextChecker;
    public boolean copyHistoryAttachmentsChecker;
    public boolean copyHistoryGeoChecker;
    public boolean copyHistorySignedChecker;

    public SpannableStringBuilder parsedPostText;
    public String postGeoUrl;

    public String copyHistoryTitle = "";
    public String copyHistoryLogo = "";
    public String copyHistoryName = "";
    public Uri copyHistoryUri;
    public SpannableStringBuilder parsedCopyHistoryText;
    public String copyHistoryGeoUrl;

    public int groupId;

    public VKWallPostWrapper(VKApiPost post, Wall wall) {
        this.post = post;
        ArrayList<VKApiCommunity> groups = wall.groups;
        ArrayList<VKApiUser> profiles = wall.profiles;

        if (post.is_pinned == 1) {
            this.postPinnedVisibility = View.VISIBLE;
        } else {
            this.postPinnedVisibility = View.INVISIBLE;
        }

        if (post.text.length() != 0) {
            this.postTextVisibility = View.VISIBLE;
            this.postTextChecker = true;

            parsedPostText = ItemDataSetter.getParsedText(post.text);
        } else {
            this.postTextVisibility = View.GONE;
            this.postTextChecker = false;
        }

        if (post.copy_history != null && post.copy_history.size() != 0) {
            this.copyHistoryContainerVisibility = View.VISIBLE;
            this.copyHistoryChecker = true;

            final VKApiPost copyHistory = post.copy_history.get(0);
            VKApiCommunity tempGroup;

            for (int i = 0; i < groups.size(); i++) {
                tempGroup = groups.get(i);
                if (copyHistory.from_id * (-1) == tempGroup.id) {
                    copyHistoryTitle = tempGroup.name;
                    copyHistoryLogo = tempGroup.photo_100;
                    copyHistoryName = tempGroup.screen_name;
                }
            }

            if (copyHistoryTitle.equals("") && copyHistoryLogo.equals("")) {
                VKApiUser profile;
                for (int i = 0; i < profiles.size(); i++) {
                    profile = profiles.get(i);
                    if (copyHistory.from_id == profile.id) {
                        copyHistoryTitle = profile.last_name + " " + profile.first_name;
                        copyHistoryLogo = profile.photo_100;
                        copyHistoryName = profile.screen_name;
                    }
                }
            }

            copyHistoryUri = Uri.parse("http://vk.com/" + copyHistoryName);

            if (copyHistory.text.length() != 0) {
                this.copyHistoryTextContainerVisibility = View.VISIBLE;
                this.copyHistoryTextChecker = true;

                parsedCopyHistoryText = ItemDataSetter.getParsedText(copyHistory.text);
            } else {
                this.copyHistoryTextContainerVisibility = View.GONE;
                this.copyHistoryTextChecker = false;
            }

            if (copyHistory.attachments != null && copyHistory.attachments.size() != 0) {
                this.copyHistoryAttachmentsContainerVisibility = View.VISIBLE;
                this.copyHistoryAttachmentsChecker = true;
            } else {
                this.copyHistoryAttachmentsContainerVisibility = View.GONE;
                this.copyHistoryAttachmentsChecker = false;
            }

            if (copyHistory.geo != null) {
                this.copyHistoryGeoContainerVisibility = View.VISIBLE;
                this.copyHistoryGeoChecker = true;

                final String[] coordinates = copyHistory.geo.coordinates.split(" ");
                copyHistoryGeoUrl = "http://maps.google.com/maps/api/staticmap?center=" + coordinates[0] + "," + coordinates[1] + "&zoom=15&size=600x400&sensor=false";
            } else {
                this.copyHistoryGeoContainerVisibility = View.GONE;
                this.copyHistoryGeoChecker = false;
            }

            if (copyHistory.signer_id != 0) {
                this.copyHistorySignedContainerVisibility = View.VISIBLE;
                this.copyHistorySignedChecker = true;
            } else {
                this.copyHistorySignedContainerVisibility = View.GONE;
                this.copyHistorySignedChecker = false;
            }

        } else {
            this.copyHistoryContainerVisibility = View.GONE;
            this.copyHistoryChecker = false;
        }

        if (post.attachments != null && post.attachments.size() != 0) {
            this.postAttachmentsVisibility = View.VISIBLE;
            this.postAttachmentsChecker = true;
        } else {
            this.postAttachmentsVisibility = View.GONE;
            this.postAttachmentsChecker = false;
        }

        if (post.geo != null) {
            this.postGeoVisibility = View.VISIBLE;
            this.postGeoChecker = true;

            final String[] coordinates = post.geo.coordinates.split(" ");
            postGeoUrl = "http://maps.google.com/maps/api/staticmap?center=" + coordinates[0] + "," + coordinates[1] + "&zoom=15&size=600x400&sensor=false";
        } else {
            this.postGeoVisibility = View.GONE;
            this.postGeoChecker = false;
        }

        if (post.signer_id != 0) {
            this.postSignedVisibility = View.VISIBLE;
            this.postSignedChecker = true;
        } else {
            this.postSignedVisibility = View.GONE;
            this.postSignedChecker = false;
        }
    }
}