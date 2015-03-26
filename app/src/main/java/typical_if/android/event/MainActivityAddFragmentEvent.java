package typical_if.android.event;

import typical_if.android.fragment.FragmentVideoView;

/**
 * Created by ADMIN on 26.03.2015.
 */
public class MainActivityAddFragmentEvent {

    public final FragmentVideoView fragmentVideoView;

    public MainActivityAddFragmentEvent(FragmentVideoView fragmentVideoView) {
        this.fragmentVideoView = fragmentVideoView;
    }
}
