package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import typical_if.android.R;


/**
 * Created by admin on 14.07.2014.
 */
public class LicenseFragment extends Fragment{

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static LicenseFragment newInstance() {
        LicenseFragment fragment = new LicenseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // getActivity().getActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.license_view_layout, container, false);
        getActivity().getActionBar().hide();
        TextView license = ((TextView) rootView.findViewById(R.id.license_text));
        license.setText(Html.fromHtml(String.format("%.1f", null) + " " + "<br>" +
                "<small>" + getStringFromAssetFile(getActivity()) + "</small>"));

        setRetainInstance(true);



        return rootView;

    }

    private String getStringFromAssetFile(Activity activity)
    {
        String fileName = "licenses.xml";
        byte[] buffer = null;
        InputStream is;
        try {
            is = activity.getAssets().open(fileName);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  new String(buffer);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
