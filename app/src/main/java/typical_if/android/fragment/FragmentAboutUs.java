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
import android.widget.ImageView;
import android.widget.TextView;

import typical_if.android.Constants;
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
        final ImageView devLight = (ImageView) rootView.findViewById(R.id.DevLightBtn);
        final ImageView stantsiya = (ImageView) rootView.findViewById(R.id.stantsiya);
        final TextView tf = (TextView) rootView.findViewById(R.id.typical_if_ua_ref);
        final TextView viktor = (TextView)rootView.findViewById(R.id.viktor_ref);
        final TextView yura = (TextView)rootView.findViewById(R.id.yurij_ref);
        final TextView lyubomir= (TextView)rootView.findViewById(R.id.lyubomir_ref);
        final TextView vasil = (TextView)rootView.findViewById(R.id.vasil_ref);
        final TextView our_site_ref = (TextView)rootView.findViewById(R.id.devlight_com_ua_ref);

        devLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://devLight.com.ua");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
             }
        });

        viktor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://vk.com/sokeoner");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        yura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://vk.com/yura0202");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        lyubomir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://vk.com/lubomiru4");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        vasil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://vk.com/gigamole");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

         stantsiya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://vk.com/stantsiya_if");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
              }
        });

        tf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://typical.if.ua");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                 }
        });

        our_site_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://devLight.com.ua");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        return rootView;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
