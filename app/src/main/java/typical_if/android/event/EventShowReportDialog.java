package typical_if.android.event;

/**
 * Created by admin on 10.09.2014.
 */
public class EventShowReportDialog {

    public final long gid;
    public final int which;

    public EventShowReportDialog(final long gid, final int which) {
        this.which = which;
        this.gid = gid;
    }
}
