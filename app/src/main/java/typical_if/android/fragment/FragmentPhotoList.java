package typical_if.android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ShowHideOnScroll;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.PhotoListAdapter;
import typical_if.android.util.PhotoUrlHelper;


public class FragmentPhotoList extends FragmentWithAttach implements AbsListView.OnScrollListener {

    public ArrayList<VKApiPhoto> finalPhotos = new ArrayList<VKApiPhoto>();
    private OnFragmentInteractionListener mListener;
    private boolean isRequestNull;
    int type;
    final private static Bundle args = new Bundle();
    public int sizeOfAlbum;
    public String albumTitle;
    public String albumSizeWithNoun;

    public static FragmentPhotoList newInstance(int type, int albumOriginalSize, String title, String cover) {
        Constants.isPhotoListFragmentLoaded = true;
        FragmentPhotoList fragment = new FragmentPhotoList();
        args.clear();
        args.putInt("albumOriginalSize", albumOriginalSize);
        args.putString("title", title);
        args.putString("cover", cover);
        fragment.type = type;
        fragment.setArguments(args);
        return fragment;
    }


    public FragmentPhotoList() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        if (getArguments() != null) {
            this.sizeOfAlbum = getArguments().getInt("albumOriginalSize");
            this.albumTitle = getArguments().getString("title");
            if (this.sizeOfAlbum==1){
                this.albumSizeWithNoun=sizeOfAlbum+" "+getString(R.string.photos_equal_one);
            }
            if (this.sizeOfAlbum<5){
                this.albumSizeWithNoun=sizeOfAlbum+" "+getString(R.string.photos_less_five);

            }else {
                this.albumSizeWithNoun=sizeOfAlbum+" "+getString(R.string.photos_more_five);
            }

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        final View rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);

        setRetainInstance(true);
        Constants.isPhotoListFragmentLoaded = true;

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.add_photo_from);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = ((MainActivity) getActivity()).addPhotoFrom();
                if (dialog == null) {
                    return;
                } else {
                    dialog.show();
                }
            }
        });
        if (!VKSdk.isLoggedIn()) {
            floatingActionButton.setVisibility(View.GONE);
        }
        gridOfPhotos = (GridViewWithHeaderAndFooter) rootView.findViewById(R.id.gridOfPhotos);
        doRequest(rootView);

//        FadingActionBarHelper helper = new FadingActionBarHelper()
//                .actionBarBackground(R.drawable.action_bar_shape_background)
//                .headerLayout(R.layout.photo_list__info_header)
//                .contentLayout(R.layout.fragment_photo_list);
//        Constants.mainActivity.setContentView(helper.createView(Constants.mainActivity));
//        helper.initActionBar(Constants.mainActivity);

        return rootView;
    }
     View headerView;
    private void attachHeaderView(final View root) {
try {
      headerView  = ItemDataSetter.inflater.inflate(R.layout.photo_list__info_header, null);
}catch (InflateException x ){
      headerView  = ItemDataSetter.inflater.inflate(R.layout.photo_list__info_header, null);
}
        Typeface titleTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        Typeface sizeTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        final ImageView cover_im = (ImageView) headerView.findViewById(R.id.photo_list_header_image);
        final RelativeLayout shell = ((RelativeLayout) headerView.findViewById(R.id.header_relative_layout));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, TIFApp.getDisplayHeight() / 2);
        shell.setLayoutParams(params);
        TextView title = ((TextView) shell.findViewById(R.id.photo_album_title));
        TextView size = ((TextView) shell.findViewById(R.id.size_of_album));
        title.setText(this.albumTitle);
        size.setText(this.albumSizeWithNoun);
        title.setTypeface(titleTypeface);
        size.setTypeface(sizeTypeface);


        ImageLoader.getInstance().loadImage(args.getString("cover"), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    cover_im.setImageBitmap(PhotoUrlHelper.fastBlur(loadedImage, 10));
                    cover_im.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    RelativeLayout l = ((RelativeLayout) root.findViewById(R.id.while_loading_rel_layout));
                    l.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
                }
            });


        gridOfPhotos.addHeaderView(headerView);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.isPhotoListFragmentLoaded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.isPhotoListFragmentLoaded = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Constants.isPhotoListFragmentLoaded = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //    inflater.inflate(R.menu.main, null);
    }

    boolean temp = false;
    boolean updated = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    int _lastInScreen;
    int _subtract;
    float _ratio;
    int _count = 100;

    @Override
    public void onScroll(final AbsListView view, int firstVisibleItem, final int visibleItemCount, int totalItemCount) {
        _lastInScreen = firstVisibleItem + visibleItemCount;
        if (_lastInScreen >= totalItemCount && totalItemCount >= 100 && totalItemCount != VKHelper.countOfPhotos) {

            if (_lastInScreen < VKHelper.countOfPhotos) {
                _subtract = VKHelper.countOfPhotos - _lastInScreen;
                _ratio = (_subtract / 100f);
                if (_ratio < 2) {
                    _count = 0;
                }
                if (_ratio > 1) {
                    _count = 100;
                    --_ratio;
                } else {
                    _count = (int) (_ratio * 100);
                }
                if (OfflineMode.isOnline()) {
                    getElsePhotos(firstVisibleItem, visibleItemCount, totalItemCount, view);
                    scrollPhotoListToBottom(gridOfPhotos, _lastInScreen);
                }
            }
        }
    }


    private void getElsePhotos(int firstVisibleItem, final int visibleItemCount, final int totalItemCount, final AbsListView view) {

        if (firstVisibleItem + visibleItemCount >= totalItemCount && !updated) {


            VKHelper.getPhotoList(OfflineMode.loadLong(Constants.VK_GROUP_ID), Constants.ALBUM_ID, 1, _count, new VKRequestListener() {
                @Override
                public void onSuccess() {
                    OfflineMode.saveJSON(vkJson, Constants.ALBUM_ID);
                    handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
                    initPhotoList();
                }

                @Override
                public void onError() {
                    //show no toast
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

        if (OfflineMode.isOnline()) {
            temp = false;
            if (type == 0) {
                VKHelper.getPhotoList(Constants.USER_ID, Constants.ALBUM_ID, 1, 100, new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        OfflineMode.saveJSON(vkJson, Constants.ALBUM_ID);
                        handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
                        attachHeaderView(view);
                        initPhotoList();
                    }
                    @Override
                    public void onError() {
                        // show NO default toast?
                    }
                });
            } else {
                VKHelper.getPhotoList(Constants.TEMP_OWNER_ID, Constants.ALBUM_ID, 1, 100, new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        OfflineMode.saveJSON(vkJson, Constants.ALBUM_ID);
                        handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
                        attachHeaderView(view);
                        initPhotoList();
                    }
                    @Override
                    public void onError() {
                        // show NO default toast?
                    }
                });

            }
            isRequestNull = true;
        }
        if (!OfflineMode.isOnline() && !OfflineMode.isJsonNull(Constants.ALBUM_ID)) {
            handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), columns, view);
            isRequestNull = true;
        } else {
            if (temp) {
                isRequestNull = false;
                TIFApp.showCommonErrorToast();
            }
        }
        return isRequestNull;
    }


    public static int albumSize;
    GridViewWithHeaderAndFooter gridOfPhotos;
    FloatingActionButton floatingActionButton;
    PhotoListAdapter photoListAdapter;
    ArrayList<VKApiPhoto> dozenPhotos;
    protected void handleResponse(JSONObject jsonObject, final int columns, View view) {

        dozenPhotos = VKHelper.getPhotosFromJSONArray(jsonObject);
        albumSize = VKHelper.countOfPhotos;
        for (int i = 0; i < dozenPhotos.size(); i++) {
            finalPhotos.add(dozenPhotos.get(i));

        }
        gridOfPhotos.setNumColumns(columns);
        if (VKSdk.isLoggedIn()) {
            gridOfPhotos.setOnTouchListener(new ShowHideOnScroll(floatingActionButton));
        } else {
            gridOfPhotos.setOnTouchListener(null);
        }


    }

    private void initPhotoList(){
        if (photoListAdapter == null) {
            Log.d("finalPhotos.size= " + finalPhotos.size(), "getActivity = " + getActivity());
            photoListAdapter = new PhotoListAdapter(finalPhotos, getActivity().getLayoutInflater());
        } else {
            photoListAdapter.notifyDataSetChanged();
        }
        gridOfPhotos.setAdapter(photoListAdapter);

        if (photoListAdapter == null) {
            Log.d("finalPhotos.size= " + finalPhotos.size(), "getActivity = " + getActivity().getLayoutInflater());

            photoListAdapter = new PhotoListAdapter(finalPhotos, getActivity().getLayoutInflater());
        } else {
            photoListAdapter.notifyDataSetChanged();
        }
        gridOfPhotos.setAdapter(photoListAdapter);


        updated = false;
        gridOfPhotos.setOnScrollListener(this);

        if (type == 0) {
            gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    Constants.tempPostAttachCounter++;
                    if (position >= 11) {
                        position = position - 10;
                    }
                    Constants.tempPhotoPostAttach.add(finalPhotos.get(position));
                    refreshMakePostFragment(0);
                }
            });

            floatingActionButton.setVisibility(View.GONE);
        } else {
            gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  //  int position = position1-10;
                    Log.d("positionO"," = "+(position-4));
                    // Fragment fragment = FragmentFullScreenViewer.newInstance(finalPhotos, position);
                    final Fragment fragment = new FragmentFullScreenViewer();
                    args.clear();
                    args.putSerializable("finalPhotos", finalPhotos);
                  //  if (position >= 11) {
                     //   args.putInt("position", position-10);
                  ////  }else {
                    args.putInt("position", position-4);
                 //   }
                    args.putInt("sizeOfAlbum", sizeOfAlbum);
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();

                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
                    transaction.add(R.id.container, fragment).addToBackStack("String").commit();
                }
            });
            if (VKSdk.isLoggedIn()) {
                floatingActionButton.setVisibility(View.VISIBLE);
            }


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