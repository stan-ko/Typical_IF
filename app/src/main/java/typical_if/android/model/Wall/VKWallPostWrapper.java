package typical_if.android.model.Wall;

import android.view.View;

import com.vk.sdk.api.model.VKApiPost;

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

    public VKWallPostWrapper(VKApiPost post) {
        this.post = post;

        if (post.is_pinned == 1) {
            this.postPinnedVisibility = View.VISIBLE;
        } else {
            this.postPinnedVisibility = View.INVISIBLE;
        }

        if (post.text.length() != 0) {
            this.postTextVisibility = View.VISIBLE;
            this.postTextChecker = true;
        } else {
            this.postTextVisibility = View.GONE;
            this.postTextChecker = false;
        }

        if (post.copy_history != null && post.copy_history.size() != 0) {
            this.copyHistoryContainerVisibility = View.VISIBLE;
            this.copyHistoryChecker = true;

            final VKApiPost copyHistory = post.copy_history.get(0);

            if (copyHistory.text.length() != 0) {
                this.copyHistoryTextContainerVisibility = View.VISIBLE;
                this.copyHistoryTextChecker = true;
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
