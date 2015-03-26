package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import typical_if.android.Constants;
import typical_if.android.ExtendedViewPager;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.FullScreenImageAdapter;
import typical_if.android.event.EventReturnNeedAdapter;

public class FragmentFullScreenViewer extends Fragment implements ExtendedViewPager.OnPageChangeListener {

    //    public static final String LIKED = "LIKED: ";
//    public static final String LIKE_DELETED = "LIKE DELETED";
    private FragmentFullScreenViewer.OnFragmentInteractionListener mListener;
    public ArrayList<VKApiPhoto> photos;
    private ExtendedViewPager imagePager;
    public int currentPosition;
    public FullScreenImageAdapter adapter;

//    public static final String ARG_VK_GROUP_ID = "vk_group_id";
//    public static final String ARG_VK_ALBUM_ID = "vk_album_id";

    public static final String ARG_VK_USER_ID = "user_id";
    public static final String TYPE = "photo";
    public static Bundle args;
    //public View rootView;

    long user_id;

    //  TextView countLikes;
    //  TextView countComments;
//    View addLike;
//    View goToComments;
    TextView photoHeader;
    //  CheckBox likedOrNotLikedBox;
    TextView counterOfPhotos;
    TextView albumSize;
    Button btnLike;
    Button btnComment;
    public int originalSizeOfAlbum;
//    public static View panel;

//    public FragmentFullScreenViewer(ArrayList<VKApiPhoto> photos, int currentposition, int sizeOfAlbum) {
//    this.photos = photos;
//    this.currentPosition = currentposition;
//        this.originalSizeOfAlbum = sizeOfAlbum;
//     setArguments(new Bundle());
//    }

    public FragmentFullScreenViewer() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        args.putSerializable("finalPhotos", photos);
//        args.putInt("position", position);
//        args.putInt("sizeOfAlbum", 0);
        super.onCreate(savedInstanceState);
        Constants.isFragmentFullScreenLoaded = true;
        if (getArguments() != null) {
            this.photos = (ArrayList<VKApiPhoto>) getArguments().getSerializable("finalPhotos");
            this.currentPosition = getArguments().getInt("position");
            this.originalSizeOfAlbum = getArguments().getInt("sizeOfAlbum");
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        Constants.isFragmentFullScreenLoaded = true;
        EventBus.getDefault().register(this);

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        final View rootView = inflater.inflate(R.layout.fragment_fullscreen_list, container, false);
//        addLike = rootView.findViewById(R.id.add_like);
//        goToComments = rootView.findViewById(R.id.go_to_comments);
        photoHeader = (TextView) rootView.findViewById(R.id.photoHeader);
        photoHeader.setVisibility(View.GONE);
        counterOfPhotos = (TextView) rootView.findViewById(R.id.counterOfPhotos);
        albumSize = (TextView) rootView.findViewById(R.id.amountOfPhotos);
//        panel = rootView.findViewById(R.id.fullscreen_action_panel);
        //btnLike = ((CheckBox) rootView.findViewById(R.id.cb_photo_like));
        //btnComment= ((CheckBox) rootView.findViewById(R.id.cb_photo_comment));

        btnLike = (Button) rootView.findViewById(R.id.add_like);
        btnComment = (Button) rootView.findViewById(R.id.go_to_comments);


        FragmentManager manager = getFragmentManager();
        imagePager = (ExtendedViewPager) rootView.findViewById(R.id.pager);
        imagePager.setOnPageChangeListener(this);
        onPageSelected(0);

        adapter = new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments, OfflineMode.loadLong(Constants.VK_GROUP_ID),
                Constants.ALBUM_ID, arguments.getLong(ARG_VK_USER_ID), manager, rootView);

        adapter.notifyDataSetChanged();
        imagePager.setAdapter(adapter);
        imagePager.setCurrentItem(currentPosition);
        adapter.notifyDataSetChanged();

        VKHelper.getMyselfInfo(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);

                JSONArray arr = response.json.optJSONArray("response");
                JSONObject jsonObject = arr.optJSONObject(0);
                Constants.USER_ID = jsonObject.optLong("id");
                arguments.putLong(ARG_VK_USER_ID, user_id);

            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast();
            }
        });

        setRetainInstance(true);
        Constants.queueOfAdapters.add(adapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        VKUIHelper.onResume(getActivity());
        Constants.isFragmentFullScreenLoaded = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(getActivity());
        EventBus.getDefault().unregister(this);
        Constants.isFragmentFullScreenLoaded = false;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.isFragmentFullScreenLoaded = true;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Constants.isFragmentFullScreenLoaded = false;
    }


    @Override
    public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {

        if (photos.get(position).likes == 0) {
            btnLike.setText(null);
        } else btnLike.setText(String.valueOf(photos.get(position).likes));

        if (photos.get(position).comments == 0) {
            btnComment.setText(null);
        } else
            btnComment.setText(String.valueOf(photos.get(position).comments));

        counterOfPhotos.setText(String.valueOf(position + 1));

        // albumSize.setText(String.valueOf(Constants.COUNT_OF_PHOTOS));
        if (originalSizeOfAlbum == 0) {
            albumSize.setText(String.valueOf(photos.size()));
        } else
            albumSize.setText(String.valueOf(originalSizeOfAlbum));


        VKHelper.isLiked("photo", OfflineMode.loadLong(Constants.VK_GROUP_ID), photos.get(position).id, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject j = response.json.optJSONObject("response");
                    photos.get(position).user_likes = j.optInt("liked");
                } catch (NullPointerException e) {}

            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast();
            }
        });

        btnLike.setSelected(photos.get(position).user_likes > 0);
//        if (photos.get(position).user_likes == 0) {
//            btnLike.setChecked(false);
//        }
//        else{
//            btnLike.setChecked(true);
//        }

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VKSdk.isLoggedIn()) {

                    if (photos.get(position).user_likes == 0 & !btnLike.isSelected()) {
                        VKHelper.setLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);

                                btnLike.setSelected(true);
                                final int likesCount = ++photos.get(position).likes;
                                btnLike.setText(likesCount==0 ? null : String.valueOf(likesCount));
                                photos.get(position).user_likes = 1;
                            }

                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast();
                            }
                        });
                    }
                    else {
                        VKHelper.deleteLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);

                                btnLike.setSelected(false);
                                final int likesCount = --photos.get(position).likes;
                                photos.get(position).user_likes = 0;
                                btnLike.setText(likesCount==0 ? null : String.valueOf(likesCount));
                            }

                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast();
                            }
                        });
                    }
                }
                else if (!VKSdk.isLoggedIn()) {
                    Toast.makeText(TIFApp.getAppContext(), R.string.you_are_not_logged_in, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VKSdk.isLoggedIn()) {
                    FragmentComments fragment = FragmentComments.newInstanceForPhoto(photos.get(position), Constants.USER_ID);
                    getFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                } else if (!VKSdk.isLoggedIn()) {
                    Toast.makeText(TIFApp.getAppContext(), R.string.you_are_not_logged_in, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        //Log.d("stateOnPageScrollStateChanged^---------------------------------------------------------------->" + "   ", state + "");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @SuppressWarnings("unused") // used via EventBus but is Lint undetectable
    public void onEventMainThread(EventReturnNeedAdapter event) {
        adapter = event.adapter;
        adapter.notifyDataSetChanged();
        imagePager.setAdapter(adapter);
        imagePager.setCurrentItem(currentPosition);
        adapter.notifyDataSetChanged();
    }

}
