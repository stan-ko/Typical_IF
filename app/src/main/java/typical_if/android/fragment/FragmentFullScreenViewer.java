package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.FullScreenImageAdapter;
import typical_if.android.event.EventReturnNeedAdapter;

public class FragmentFullScreenViewer extends Fragment implements ExtendedViewPager.OnPageChangeListener {


    public static final String LIKED = "LIKED: ";
    public static final String LIKE_DELETED = "LIKE DELETED";
    private FragmentFullScreenViewer.OnFragmentInteractionListener mListener;
    public  ArrayList<VKApiPhoto> photos;
    private ExtendedViewPager imagepager;
    public int currentPosition;
    public FullScreenImageAdapter adapter;

    public static final String ARG_VK_GROUP_ID = "vk_group_id";
    public static final String ARG_VK_ALBUM_ID = "vk_album_id";

    public static final String ARG_VK_USER_ID = "user_id";
    public static final String TYPE = "photo";
    public static Bundle args;
    public View rootView;

    long user_id;

    //  TextView countLikes;
    //  TextView countComments;
    ImageView addLike;
    ImageView goToComments;
    TextView photoHeader;
    //  CheckBox likedOrNotLikedBox;
    TextView counterOfPhotos;
    TextView albumSize;
    CheckBox cb_like;
    CheckBox cb_comment;
    public int originalSizeOfAlbum;
    public static RelativeLayout panel;

//    public static FragmentFullScreenViewer newInstance(ArrayList<VKApiPhoto> photos, int currentposition) {
//
//        FragmentFullScreenViewer fragment = new FragmentFullScreenViewer();
//        args = new Bundle();
//        fragment.setArguments(args);
//        FragmentFullScreenViewer.photos = photos;
//        FragmentFullScreenViewer.currentPosition = currentposition;
//
//
//
//        return fragment;
//    }



    public FragmentFullScreenViewer(ArrayList<VKApiPhoto> photos, int currentposition, int sizeOfAlbum) {
    this.photos = photos;
    this.currentPosition = currentposition;
        this.originalSizeOfAlbum = sizeOfAlbum;
     setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        EventBus.getDefault().register(this);

        ((MainActivity)getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        rootView = inflater.inflate(R.layout.fragment_fullscreen_list, container, false);
        addLike = (ImageView) rootView.findViewById(R.id.add_like);
        goToComments = (ImageView) rootView.findViewById(R.id.go_to_comments);
        photoHeader = (TextView) rootView.findViewById(R.id.photoHeader);
        photoHeader.setVisibility(View.GONE);
        counterOfPhotos = (TextView) rootView.findViewById(R.id.counterOfPhotos);
        albumSize = (TextView) rootView.findViewById(R.id.amountOfPhotos);
        panel = ((RelativeLayout) rootView.findViewById(R.id.fullscreen_action_panel));
        cb_like = ((CheckBox) rootView.findViewById(R.id.cb_photo_like));
        cb_comment= ((CheckBox) rootView.findViewById(R.id.cb_photo_comment));

        FragmentManager manager = getFragmentManager();
        imagepager = (ExtendedViewPager) rootView.findViewById(R.id.pager);
        imagepager.setOnPageChangeListener(this);
        onPageSelected(0);

        adapter = new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments, Constants.GROUP_ID,
                Constants.ALBUM_ID, arguments.getLong(ARG_VK_USER_ID), manager, rootView);



        //if (adapter==null|imagepager.getAdapter()==null){
         //   adapter= new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments,Constants.GROUP_ID,
              //      Constants.ALBUM_ID, arguments.getLong(ARG_VK_USER_ID), manager, rootView);

       // }else {
          //  adapter = new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments, Constants.GROUP_ID,
            //        Constants.ALBUM_ID, arguments.getLong(ARG_VK_USER_ID), manager, rootView);
      //  }

            adapter.notifyDataSetChanged();
            imagepager.setAdapter(adapter);
            imagepager.setCurrentItem(currentPosition);
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
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(getActivity());
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }




    @Override
    public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) { }
    @Override
    public void onPageSelected(final int position) {
        if (photos.get(position).likes==0){
            cb_like.setText("");
        } else  cb_like.setText(String.valueOf(photos.get(position).likes));

        if (photos.get(position).comments==0){
            cb_comment.setText("");
        }else
            cb_comment.setText(String.valueOf(photos.get(position).comments));

      counterOfPhotos.setText(String.valueOf(position + 1));

           // albumSize.setText(String.valueOf(Constants.COUNT_OF_PHOTOS));
        if (originalSizeOfAlbum==0){
            albumSize.setText(String.valueOf(photos.size()));
        }
else
        albumSize.setText(String.valueOf(originalSizeOfAlbum));


        VKHelper.isLiked("photo",Constants.GROUP_ID, photos.get(position).id, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject j = response.json.optJSONObject("response");
                    photos.get(position).user_likes = j.optInt("liked");;
                }catch (NullPointerException ex){


                }

            }
            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });
        if (photos.get(position).user_likes == 0) {
            cb_like.setChecked(false);
        }
        else{
            cb_like.setChecked(true);
        }

        addLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VKSdk.isLoggedIn()){

                    if (photos.get(position).user_likes == 0 & !cb_like.isChecked()) {
                        VKHelper.setLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);

                                cb_like.setChecked(true);
                                if(cb_like.getText().toString()==""){cb_like.setText("0");}
                                cb_like.setText(String.valueOf(Integer.parseInt(cb_like.getText().toString()) + 1));
                                ++photos.get(position).likes;
                                photos.get(position).user_likes = 1;

                            }

                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });
                    } else {
                        VKHelper.deleteLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                cb_like.setChecked(false);
                                cb_like.setText(String.valueOf(Integer.parseInt(cb_like.getText().toString()) - 1));
                                if (cb_like.getText().toString()=="0"){cb_like.setText("");}
                                --photos.get(position).likes;
                                photos.get(position).user_likes = 0;

                            }

                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });
                    }

                }else if (!VKSdk.isLoggedIn()){
                 Toast.makeText(getActivity().getApplicationContext(),getString(R.string.you_are_not_logged_in),Toast.LENGTH_SHORT).show();

                }

            }
        });

        goToComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VKSdk.isLoggedIn()){
                    FragmentComments fragment = FragmentComments.newInstanceForPhoto(

                            photos.get(position), Constants.USER_ID);
                    getFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                }else if (!VKSdk.isLoggedIn()){
                    Toast.makeText(Constants.mainActivity.getApplicationContext(),getString(R.string.you_are_not_logged_in), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    @Override
    public void onPageScrollStateChanged(int state ) {

        //Log.d("stateOnPageScrollStateChanged^---------------------------------------------------------------->" + "   ", state + "");

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void onEventMainThread(EventReturnNeedAdapter event) {
        adapter = event.adapter;
        adapter.notifyDataSetChanged();
        imagepager.setAdapter(adapter);
        imagepager.setCurrentItem(currentPosition);
        adapter.notifyDataSetChanged();

    }

}
