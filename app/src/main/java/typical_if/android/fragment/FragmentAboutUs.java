package typical_if.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
        final   ImageButton devLight = (ImageButton) rootView.findViewById(R.id.DevLightBtn);
        final   ImageButton stantsiya = (ImageButton) rootView.findViewById(R.id.stantsiya);
        final   ImageButton tf = (ImageButton) rootView.findViewById(R.id.tf_btn);

        devLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devLight.com.ua"));
                startActivity(browserIntent);
            }
        });

        stantsiya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/stantsiya_if"));
                startActivity(browserIntent);
            }
        });
        tf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://typical.if.ua"));
                startActivity(browserIntent);
            }
        });
        return rootView;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
