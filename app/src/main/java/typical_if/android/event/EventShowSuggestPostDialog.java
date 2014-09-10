package typical_if.android.event;

import com.vk.sdk.api.model.VKApiPost;

/**
 * Created by admin on 10.09.2014.
 */
public class EventShowSuggestPostDialog {

    public final long gid;
    public final VKApiPost post;

    public EventShowSuggestPostDialog(final long gid, final VKApiPost post) {
        this.post = post;
        this.gid = gid;
    }

}
