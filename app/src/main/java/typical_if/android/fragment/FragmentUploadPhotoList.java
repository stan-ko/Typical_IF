package typical_if.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.shamanland.fab.FloatingActionButton;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKPhotoArray;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.UploadPhotoService;
import typical_if.android.VKRequestListener;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.PhotoUploadAdapter;
import typical_if.android.model.UploadPhotos;

/**
 * Created by LJ on 25.07.2014.
 */
public class FragmentUploadPhotoList extends FragmentWithAttach {
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
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        float scalefactor = getResources().getDisplayMetrics().density * 100;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / (float) scalefactor);
        photolist = new ArrayList<UploadPhotos>();
        for (String uri : uris) {
            String[] temp = uri.split("/");
            if (temp[temp.length - 2].contains(category)) {
                photolist.add(new UploadPhotos(uri));
            }
        }

        final View rootView = inflater.inflate(R.layout.fragment_add_photo_upload_list, container, false);
        handleResponse(rootView, inflater, photolist, columns);
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        FragmentWall.setDisabledMenu();
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setFabVisibility(int VISIBILITY) {
        uploadPhotoFromSd.setVisibility(VISIBILITY);
    }

    public static void refreshCheckBoxes() {
        CheckBox checkBox = null;

        if (Constants.tempCurrentPhotoAttachCounter == (Constants.tempMaxPostAttachCounter - Constants.tempPostAttachCounter)) {
            for (int i = 0; i < photos.getCount(); i++) {
                checkBox = (CheckBox) photos.getChildAt(i).findViewById(R.id.checkBox_for_upload);
                if (!checkBox.isChecked()) {
                    checkBox.setEnabled(false);
                }
            }
        } else {
            for (int i = 0; i < photos.getCount(); i++) {
                try {
                    checkBox = (CheckBox) photos.getChildAt(i).findViewById(R.id.checkBox_for_upload);
                } catch (NullPointerException e) {

                }
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
        getActivity().stopService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
    }

    public FloatingActionButton uploadPhotoFromSd;

    protected void handleResponse(View rootView, LayoutInflater inflater, final ArrayList<UploadPhotos> photolist, int columns) {

        uploadPhotoFromSd = (FloatingActionButton) rootView.findViewById(R.id.upload_photo_from_sd);
        uploadPhotoFromSd.initBackground();

        if (which == 0) {
            uploadPhotoFromSd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrementer = new AtomicInteger(Constants.tempCurrentPhotoAttachCounter);

                    for (int j = 0; j < photolist.size(); j++) {
                        getActivity().startService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
                        if (photolist.get(j).isChecked) {
                            VKApi.uploadWallPhotoRequest(new File(photolist.get(j).photoSrc), Constants.USER_ID, (int) gid).executeWithListener(new VKRequestListener() {
                                @Override
                                public void onSuccess() {
                                    Constants.tempPhotoPostAttach.add(((VKPhotoArray) vkResponse.parsedModel).get(0));
                                    decrementThreadsCounter();
                                }
//                                @Override
//                                public void onError() {
//                                    TIFApp.showCommonErrorToast();
//                                }
                            });
                        }
                    }
                }
            });
        } else {
            uploadPhotoFromSd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < photolist.size(); j++) {
                        getActivity().startService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
                        if (photolist.get(j).isChecked) {
                            VKApi.uploadAlbumPhotoRequest(new File(photolist.get(j).photoSrc), Constants.ALBUM_ID, (int) gid).executeWithListener(new VKRequestListener() {
//                                @Override
//                                public void onError() {
//                                    showErrorToast();
//                                }
                            });

                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    }
                }
            });
        }

        photos = (GridView) rootView.findViewById(R.id.adding_photo_upload);
        PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter(this, category, inflater, photolist, getActivity().getSupportFragmentManager(), which);
        photos.setAdapter(photoUploadAdapter);
        photos.setNumColumns(columns);
    }

    private AtomicInteger decrementer;
    private int counter = 1;

    public void decrementThreadsCounter() {
        Toast.makeText(TIFApp.getAppContext(), TIFApp.getAppContext().getString(R.string.upload_progress,counter,Constants.tempCurrentPhotoAttachCounter), Toast.LENGTH_SHORT).show();
        if (decrementer.decrementAndGet() == 0) {
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().popBackStack();
            Constants.tempPostAttachCounter += Constants.tempCurrentPhotoAttachCounter;
            refreshMakePostFragment(0);
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
                        getActivity().startService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
                        if (photolist.get(j).isChecked) {
                            VKApi.uploadWallPhotoRequest(new File(photolist.get(j).photoSrc), Constants.USER_ID, (int) gid).executeWithListener(new VKRequestListener() {
                                @Override
                                public void onSuccess() {
                                    Constants.tempPhotoPostAttach.add(((VKPhotoArray) vkResponse.parsedModel).get(0));
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
                        getActivity().startService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
                        if (photolist.get(j).isChecked) {
                            req = VKApi.uploadAlbumPhotoRequest(new File(photolist.get(j).photoSrc), Constants.ALBUM_ID, (int) gid);
                            req.executeWithListener(new VKRequestListener() {
//                                @Override
//                                public void onError() {
//                                    showErrorToast();
//                                }
                            });
                        }
                    }
                    return true;
                }
            });
        }
    }


}
