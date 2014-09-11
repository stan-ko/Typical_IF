package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import typical_if.android.R;


/**
 * Created by admin on 14.07.2014.
 */
public class FragmentAboutUs extends Fragment{

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentAboutUs newInstance() {
        FragmentAboutUs fragment = new FragmentAboutUs();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAboutUs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_list, container, false);
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
