package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.adapter.PhotoUploadAdapter;
import typical_if.android.model.UploadPhotos;

/**
 * Created by LJ on 25.07.2014.
 */
public class FragmentUploadPhotoList extends Fragment {
    static String category;
    static String[] uris;
    ArrayList<UploadPhotos> photolist = null;
    static GridView photos;

    int which;
    long gid;

    public static FragmentUploadPhotoList newInstance(String category, String[] uris, long gid, int which) {
        FragmentUploadPhotoList fragment = new FragmentUploadPhotoList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.category = category;
        fragment.uris = uris;
        fragment.which = which;
        fragment.gid = gid;
        return fragment;
    }

    public FragmentUploadPhotoList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        float scalefactor = getResources().getDisplayMetrics().density * 100;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / (float) scalefactor);
        photolist = new ArrayList<UploadPhotos>();
        for (int i = 0; i < uris.length; i++) {
            String[] temp = uris[i].split("/");
            if (temp[temp.length - 2].contains(category)) {
                photolist.add(new UploadPhotos(uris[i]));
            }
        }

        final View rootView = inflater.inflate(R.layout.fragment_add_photo_upload_list, container, false);
        handleResponse(rootView, inflater, photolist, columns);
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

    public static void refreshCheckBoxes() {
        CheckBox checkBox;
        Toast.makeText(Constants.mainActivity, "Lo" + Constants.tempCurrentPhotoAttachCounter, Toast.LENGTH_SHORT).show();

        if (Constants.tempCurrentPhotoAttachCounter == (Constants.tempMaxPostAttachCounter - Constants.tempPostAttachCounter)) {
            for (int i = 0; i < photos.getCount(); i++) {
                checkBox = (CheckBox) photos.getChildAt(i).findViewById(R.id.checkBox_for_upload);
                if (!checkBox.isChecked()) {
                    checkBox.setEnabled(false);
                }
            }
        } else {
            for (int i = 0; i < photos.getCount(); i++) {
                checkBox = (CheckBox) photos.getChildAt(i).findViewById(R.id.checkBox_for_upload);
                if (!checkBox.isChecked()) {
                    checkBox.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.tempCurrentPhotoAttachCounter = 0;
    }

    protected void handleResponse(View rootView, LayoutInflater inflater, final ArrayList<UploadPhotos> photolist, int columns) {
        photos = (GridView) rootView.findViewById(R.id.adding_photo_upload);
        PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter(category, inflater, photolist, getActivity().getSupportFragmentManager(), which);
        photos.setAdapter(photoUploadAdapter);
        photos.setNumColumns(columns);
    }

    private AtomicInteger decrementer;

    public void decrementThreadsCounter() {
        if (decrementer.decrementAndGet() == 0) {
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().popBackStack();
            Constants.tempPostAttachCounter += Constants.tempCurrentPhotoAttachCounter;
            FragmentMakePost.refreshMakePostFragment(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upload_captured_photo, menu);
        MenuItem item = menu.getItem(0);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (which == 0) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    decrementer = new AtomicInteger(Constants.tempCurrentPhotoAttachCounter);

                    for (int j = 0; j < photolist.size(); j++) {
                        if (photolist.get(j).ischecked) {
                            VKApi.uploadWallPhotoRequest(new File(photolist.get(j).photosrc), Constants.USER_ID, (int) gid).executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    Constants.tempPhotoPostAttach.add(((VKPhotoArray) response.parsedModel).get(0));
                                    decrementThreadsCounter();
                                }
                            });
                        }
                    }

                    return true;
                }
            });
        } else {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    VKRequest req;
                    for (int j = 0; j < photolist.size(); j++) {
                        if (photolist.get(j).ischecked) {
                            req = VKApi.uploadAlbumPhotoRequest(new File(photolist.get(j).photosrc), Constants.ALBUM_ID, (int) gid);
                            req.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                }
                            });
                        }
                    }
                    return true;
                }
            });
        }
    }


}
