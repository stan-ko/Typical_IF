package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.FullScreenImageAdapter;

public class FragmentFullScreenViewer extends Fragment implements ViewPager.OnPageChangeListener {


    public static final String LIKED = "LIKED: ";
    public static final String LIKE_DELETED = "LIKE DELETED";
    private FragmentFullScreenViewer.OnFragmentInteractionListener mListener;
    public static ArrayList<VKApiPhoto> photos;
    private ViewPager imagepager;
    public static int currentPosition;

    public static final String ARG_VK_GROUP_ID = "vk_group_id";
    public static final String ARG_VK_ALBUM_ID = "vk_album_id";

    public static final String ARG_VK_USER_ID = "user_id";
    public static final String TYPE = "photo";
    public static Bundle args;
    public View rootView;

    long user_id;

    TextView countLikes;
    TextView countComments;
    ImageView like;
    ImageView comment;
    TextView photoHeader;
    CheckBox likedOrNotLikedBox;
    TextView counterOfPhotos;
    TextView albumSize;
   public static RelativeLayout panel;

    public static FragmentFullScreenViewer newInstance(ArrayList<VKApiPhoto> photos, int currentposition) {

        FragmentFullScreenViewer fragment = new FragmentFullScreenViewer();
        args = new Bundle();
        fragment.setArguments(args);
        FragmentFullScreenViewer.photos = photos;
        FragmentFullScreenViewer.currentPosition = currentposition;



        return fragment;
    }

    public FragmentFullScreenViewer() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();

        ((MainActivity)getActivity()).getSupportActionBar().hide();

        rootView = inflater.inflate(R.layout.fragment_fullscreen_list, container, false);

        countLikes = (TextView) rootView.findViewById(R.id.count_of_likes);
        countComments = (TextView) rootView.findViewById(R.id.count_of_comments);
        like = (ImageView) rootView.findViewById(R.id.image_not_liked);
        comment = (ImageView) rootView.findViewById(R.id.image_comment);
        likedOrNotLikedBox = ((CheckBox) rootView.findViewById(R.id.liked_or_not_liked_checkbox));
        photoHeader = (TextView) rootView.findViewById(R.id.photoHeader);
        counterOfPhotos = (TextView) rootView.findViewById(R.id.counterOfPhotos);
        albumSize = (TextView) rootView.findViewById(R.id.amountOfPhotos);
        panel = ((RelativeLayout) rootView.findViewById(R.id.fullscreen_action_panel));

        FragmentManager manager = getFragmentManager();
        imagepager = (ViewPager) rootView.findViewById(R.id.pager);
        imagepager.setOnPageChangeListener(this);
        onPageSelected(0);

        imagepager.setAdapter(new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments,Constants.GROUP_ID,
                Constants.ALBUM_ID, arguments.getLong(ARG_VK_USER_ID), manager, rootView));
        imagepager.setCurrentItem(currentPosition);


        //Log.d("Current VIEW", photos.get(imagepager.getCurrentItem()).text);
        VKHelper.getMyselfInfo(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                JSONArray arr = response.json.optJSONArray("response");
                JSONObject jsonObject = arr.optJSONObject(0);
                Constants.USER_ID = jsonObject.optLong("id");
                arguments.putLong(ARG_VK_USER_ID, user_id);

            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });


        setRetainInstance(true);

        return rootView;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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


        photoHeader.setText(photos.get(position).text);
        countLikes.setText(String.valueOf(photos.get(position).likes));
        countComments.setText(String.valueOf(photos.get(position).comments));
        counterOfPhotos.setText(String.valueOf(position + 1));
      if (VKHelper.countOfPhotos==0)
          albumSize.setText(String.valueOf(Constants.COUNT_OF_PHOTOS));
      else
        albumSize.setText(String.valueOf(VKHelper.countOfPhotos));


        VKHelper.isLiked("photo",Constants.GROUP_ID, photos.get(position).id, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            JSONObject j = response.json.optJSONObject("response");

                            photos.get(position).user_likes = j.optInt("liked");;
                        }catch (NullPointerException ex){


                        }

                    }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
                });

        if (photos.get(position).user_likes == 0) {

            countLikes.setText(String.valueOf(photos.get(position).likes));
            countComments.setText(String.valueOf(photos.get(position).comments));
            like.setBackgroundResource((R.drawable.ic_post_btn_like_up));
            likedOrNotLikedBox.setChecked(false);
        }
        else{
            countLikes.setText(String.valueOf(photos.get(position).likes));
            countComments.setText(String.valueOf(photos.get(position).comments));
            like.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
            likedOrNotLikedBox.setChecked(true);
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos.get(position).user_likes == 0) {
                    VKHelper.setLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            like.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
                            likedOrNotLikedBox.setChecked(true);

                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString()) + 1));
                            ++photos.get(position).likes;
                            photos.get(position).user_likes = 1;

                        }
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                }
                else {
                    VKHelper.deleteLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            likedOrNotLikedBox.setChecked(false);

                            like.setBackgroundResource((R.drawable.ic_post_btn_like_up));
                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString()) - 1));
                            --photos.get(position).likes;
                            photos.get(position).user_likes = 0;

                        }

                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentWithComments fragment = FragmentWithComments.newInstanceForPhoto(

                        photos.get(position), Constants.USER_ID);
                getFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
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


//        fullScreenPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickCounter++;
//                if(clickCounter%2==0) {
//                    { likePhoto.startAnimation(animTextViewUp);
//                    commentPhoto.startAnimation(animTextViewUp);}
//                }else{
//                    {commentPhoto.startAnimation(animTextViewDown);
//                    likePhoto.startAnimation(animTextViewDown);}
//                }
//
//int clickCounter = -1;
//
//            }
//        });


}
