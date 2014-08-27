package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.activity.MainActivity;


/**
 * Created by admin on 14.07.2014.
 */
public class FragmentEventsList extends Fragment{

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentEventsList newInstance() {
        FragmentEventsList fragment = new FragmentEventsList();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentEventsList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_list, container, false);

        TextView id = (TextView)rootView.findViewById(R.id.textViewEvents);
        id.setText(0+"");
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(Constants.GROUP_ID);
    }
}
