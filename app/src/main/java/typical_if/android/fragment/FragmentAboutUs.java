package typical_if.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        getActivity().getActionBar().show();
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(null, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(false);
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        setRetainInstance(true);
//        getActivity().getActionBar().hide();

        final ImageView devLight = (ImageView) rootView.findViewById(R.id.DevLightBtn);
        final ImageView stantsiya = (ImageView) rootView.findViewById(R.id.stantsiya);
        final ImageView tf = (ImageView) rootView.findViewById(R.id.tf_site);

        final TextView viktor = (TextView)rootView.findViewById(R.id.viktor_ref);
        final SpannableString content = new SpannableString(viktor.getText());
        content.setSpan(new UnderlineSpan(), 0, viktor.length(), 0);
        viktor.setText(content);

        final TextView yura = (TextView)rootView.findViewById(R.id.yurij_ref);
        final SpannableString content1 = new SpannableString(yura.getText());
        content1.setSpan(new UnderlineSpan(), 0, yura.length(), 0);
        yura.setText(content1);

        final TextView lyubomir= (TextView)rootView.findViewById(R.id.lyubomir_ref);
        final SpannableString content2 = new SpannableString(lyubomir.getText());
        content2.setSpan(new UnderlineSpan(), 0, lyubomir.length(), 0);
        lyubomir.setText(content2);

        final TextView vasil = (TextView)rootView.findViewById(R.id.vasil_ref);
        final SpannableString content3 = new SpannableString(vasil.getText());
        content3.setSpan(new UnderlineSpan(), 0, vasil.length(), 0);
        vasil.setText(content3);

        final TextView our_site_ref = (TextView)rootView.findViewById(R.id.devlight_com_ua_ref);
        final SpannableString content4 = new SpannableString(our_site_ref.getText());
        content4.setSpan(new UnderlineSpan(), 0, our_site_ref.length(), 0);
        our_site_ref.setText(content4);

        final TextView goToLicensePage = (TextView)rootView.findViewById(R.id.go_to_license);
        final SpannableString content5 = new SpannableString(goToLicensePage.getText());
        content5.setSpan(new UnderlineSpan(), 0, goToLicensePage.length(), 0);
        goToLicensePage.setText(content5);

        goToLicensePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LicenseFragment fragment = LicenseFragment.newInstance();
                getFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

            }
        });


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
