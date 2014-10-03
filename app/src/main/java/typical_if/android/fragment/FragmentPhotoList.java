package typical_if.android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.PhotoListAdapter;

public class FragmentPhotoList extends Fragment implements AbsListView.OnScrollListener {

    public ArrayList<VKApiPhoto> photos2 = new ArrayList<VKApiPhoto>();
    int height = TIFApp.getDisplayHeight();
    private OnFragmentInteractionListener mListener;
    private int counter = 5;

    private boolean isRequestNull;
    ImageView addPhoto;
    //private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private static final int PICK_FROM_CAMERA = 1;
    int type;
    final int PIC_CROP = 2;
    private static Uri mImageCaptureUri;


    public static FragmentPhotoList newInstance(int type) {
        FragmentPhotoList fragment = new FragmentPhotoList();
        Bundle args = new Bundle();
        fragment.type = type;
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPhotoList() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        final View rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);
        setRetainInstance(true);

        addPhoto = (ImageView) rootView.findViewById(R.id.add_photo_from);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=((MainActivity)getActivity()).addPhotoFrom();
                if (dialog==null){
                    return;
                }else {
                    dialog.show();
                }
            }
        });

        doRequest(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    boolean temp = false;
    boolean updated = false;
    int yPos;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(final AbsListView view, int firstVisibleItem, final int visibleItemCount, int totalItemCount) {

        _lastInScreen = firstVisibleItem + visibleItemCount;
        if (_lastInScreen >= totalItemCount & totalItemCount >= 100 & totalItemCount != VKHelper.countOfPhotos) {

            if (_lastInScreen < VKHelper.countOfPhotos) {
                _substract = VKHelper.countOfPhotos - _lastInScreen;
                _ratio = (_substract / 100f);
                if (_ratio < 2) {
                    _count = 0;
                }
                if (_ratio > 1) {
                    _count = 100;
                    --_ratio;
                } else {
                    _count = (int) (_ratio * 100);
                }
                if (OfflineMode.isOnline(Constants.mainActivity.getApplicationContext())) {
                    getElsePhotos(firstVisibleItem, visibleItemCount, totalItemCount, view);
                    scrollPhotoListToBottom(gridOfPhotos, _lastInScreen);
                }
            }
        }
    }

    int _lastInScreen;
    int _substract;
    float _ratio;
    int _count = 100;

    private void getElsePhotos(int firstVisibleItem, final int visibleItemCount, final int totalItemCount, final AbsListView view) {

        if (firstVisibleItem + visibleItemCount >= totalItemCount & !updated) {


            VKHelper.getPhotoList(Constants.GROUP_ID, Constants.ALBUM_ID, 1, _count, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {

                    super.onComplete(response);
                    OfflineMode.saveJSON(response.json, Constants.ALBUM_ID);
                    handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
                }

            });
            updated = true;
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    int columns;
    View view = null;

    public boolean doRequest(final View view) {
        this.view = view;


        float scaleFactor = getResources().getDisplayMetrics().density * 80;

        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / scaleFactor);
        this.columns = columns;
        VKHelper.offsetCounter = 0;

        if (OfflineMode.isOnline(getActivity().getApplicationContext())) {
            temp = false;
            if (type == 0) {
                VKHelper.getPhotoList(Constants.USER_ID, Constants.ALBUM_ID, 1, 100, new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json, Constants.ALBUM_ID);
                        handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
                    }
                });
            } else {
                VKHelper.getPhotoList(Constants.TEMP_OWNER_ID, Constants.ALBUM_ID, 1, 100, new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json, Constants.ALBUM_ID);
                        handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
                    }
                });

            }
            isRequestNull = true;
        }
        if (!OfflineMode.isOnline(getActivity().getApplicationContext()) & OfflineMode.isJsonNull(Constants.ALBUM_ID)) {
            handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
            isRequestNull = true;
        } else {
            if (temp) {
                isRequestNull = false;
                showAlertNoInternet(view);
            }
        }
        return isRequestNull;
    }

    void showAlertNoInternet(final View view) {

        final LinearLayout lv = (LinearLayout) view.findViewById(R.id.LinerLayout_Error_Photo);
        final Button btn = (Button) view.findViewById(R.id.ButtonFromOfflinePhoto);
        lv.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickView) {
                counter--;
                if (doRequest(view) == true) {
                    lv.setVisibility(View.GONE);
                }
            }
        });
        if (counter < 1) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
            counter = 5;
        }
        btn.setText("Retry " + counter);

    }

    public static int albumSize;
    GridView gridOfPhotos;
    PhotoListAdapter photoListAdapter;
    private static final int INITIAL_DELAY_MILLIS = 10;

    protected void handleResponse(JSONObject jsonObject, final int columns, View view) {

        final ArrayList<VKApiPhoto> photos = VKHelper.getPhotosFromJSONArray(jsonObject);
        albumSize = VKHelper.countOfPhotos;
        for (int i = 0; i < photos.size(); i++) {
            photos2.add(photos.get(i));
        }
        gridOfPhotos = (GridView) view.findViewById(R.id.gridOfPhotos);

        gridOfPhotos.setNumColumns(columns);
        if(photoListAdapter ==null){
        photoListAdapter= new PhotoListAdapter(photos2, getActivity().getLayoutInflater());}
        else { photoListAdapter.notifyDataSetChanged();}
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(photoListAdapter);
        swingBottomInAnimationAdapter.setAbsListView(gridOfPhotos);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        gridOfPhotos.setAdapter(swingBottomInAnimationAdapter);
        updated = false;
        gridOfPhotos.setOnScrollListener(this);

        if (type == 0) {
            gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    Constants.tempPostAttachCounter++;
                    Constants.tempPhotoPostAttach.add(photos.get(position));
                    FragmentMakePost.refreshMakePostFragment(0);
                }
            });

            addPhoto.setVisibility(View.GONE);
        } else {
            gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment fragment = FragmentFullScreenViewer.newInstance(photos2, position);
                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();

                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                    transaction.add(R.id.container, fragment).addToBackStack("String").commit();
                }
            });
            addPhoto.setVisibility(View.VISIBLE);
        }

    }

    private void scrollPhotoListToBottom(final GridView gridOfPhotos, final int lastItem) {
        gridOfPhotos.post(new Runnable() {
            @Override
            public void run() {
                gridOfPhotos.setSelection(lastItem);

            }
        });

    }

}