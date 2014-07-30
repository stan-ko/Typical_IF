package typical_if.android.fragment;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import typical_if.android.R;

/**
 * Created by LJ on 29.07.2014.
 */
public class FragmentPhotoFromCamera extends Fragment {

    private static Uri tmpPhoto;
    ImageView photofromcamera;
    Button retry;
    Button upload;
    ImageLoader imageLoader;
    public static FragmentPhotoFromCamera newInstance(Uri tmpPhoto) {
        FragmentPhotoFromCamera fragment = new FragmentPhotoFromCamera();
        FragmentPhotoFromCamera.tmpPhoto = tmpPhoto;
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_upload_photo_from_camera, container, false);
        setRetainInstance(true);
        photofromcamera = (ImageView) rootView.findViewById(R.id.image_from_photocamera);
        retry = (Button) rootView.findViewById(R.id.btn_retry_photo);
        upload = (Button) rootView.findViewById(R.id.btn_upload_photo_from_camera);
        Log.d("URI -_----TO FRAGMENT>>>>>>>>>>>>>>>", String.valueOf(tmpPhoto));

        imageLoader.getInstance().displayImage(String.valueOf(tmpPhoto), photofromcamera);

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }




}
