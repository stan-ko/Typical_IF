package typical_if.android.event;

/**
 * Created by admin on 10.09.2014.
 */
public class EventShowPhotoAttachDialog {

    public final long gid;
    public final int which;

    public EventShowPhotoAttachDialog(final long gid, final int which) {
        this.which = which;
        this.gid = gid;
    }
}
