package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.FullScreenPhotoUploadAdapter;
import typical_if.android.model.UploadPhotos;

/**
 * Created by LJ on 05.08.2014.
 */
public class FragmentFullScreenViewFromPhone extends Fragment {

    static int currentposition;
    public static ArrayList<UploadPhotos> photolist;

    public static FragmentFullScreenViewFromPhone newInstance(int currentposition, ArrayList<UploadPhotos> photolist) {
        FragmentFullScreenViewFromPhone fragment = new FragmentFullScreenViewFromPhone();
        FragmentFullScreenViewFromPhone.photolist=photolist;
        FragmentFullScreenViewFromPhone.currentposition=currentposition;
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_fullscreen_view, container, false);

        ((MainActivity)getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        ViewPager imagepager = (ViewPager) rootView.findViewById(R.id.pager);
        imagepager.setAdapter(new FullScreenPhotoUploadAdapter(photolist, inflater));
        imagepager.setCurrentItem(currentposition);
        setRetainInstance(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
