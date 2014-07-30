package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.FullScreenImageAdapter;
import typical_if.android.model.Photo;

public class FragmentFullScreenImagePhotoViewer extends Fragment implements Animation.AnimationListener {


    private OnFragmentInteractionListener mListener;
    public static ArrayList<Photo> photos;
    private static ViewPager imagepager;
    public static int currentposition;

    private static final String ARG_VK_GROUP_ID = "vk_group_id";
    private static final String ARG_VK_ALBUM_ID = "vk_album_id";
    private static final String FILTER = "likes";
    private static final String ARG_VK_USER_ID = "user_id";
    private static final String TYPE = "photo";
    int isLiked ;
    long user_id;

    public static FragmentFullScreenImagePhotoViewer newInstance(ArrayList<Photo> photos, int currentposition,long vk_group_id,long vk_album_id ) {

        FragmentFullScreenImagePhotoViewer fragment = new FragmentFullScreenImagePhotoViewer();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        FragmentFullScreenImagePhotoViewer.photos = photos;
        FragmentFullScreenImagePhotoViewer.currentposition = currentposition;
        args.putLong(ARG_VK_GROUP_ID, vk_group_id);
        args.putLong(ARG_VK_ALBUM_ID,vk_album_id);
        return fragment;
    }

    public FragmentFullScreenImagePhotoViewer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    int clickCounter = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final Animation animTextViewUp ;
        final Animation animTextViewDown ;
        animTextViewUp= AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.move_up);
        animTextViewDown=AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.move_down);
        animTextViewUp.setAnimationListener(this);
        animTextViewDown.setAnimationListener(this);

        final View rootView = inflater.inflate(R.layout.fragment_fullscreen_view, container, false);
        final View second = inflater.inflate(R.layout.fragment_fragment_full_screen_image_photo_viewer, container, false);
        //initAdapter(rootView, arguments);
        FragmentManager manager = getFragmentManager();
        imagepager = (ViewPager) rootView.findViewById(R.id.pager);
        imagepager.setAdapter(new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments, arguments.getLong(ARG_VK_GROUP_ID),
                arguments.getLong(ARG_VK_ALBUM_ID), arguments.getLong(ARG_VK_USER_ID), manager));
        imagepager.setCurrentItem(currentposition);

        //imagepager = (ViewPager) getView().findViewById(R.id.pager);
       // final ImageView fullScreenPhoto = (ImageView) rootView.findViewById(R.id.full_screen_photo);
//        final Button like = (Button)rootView.findViewById(R.id.like_photo);
//        final Button comment = (Button)rootView.findViewById(R.id.comment_photo);
//        final TextView countLikes = (TextView)rootView.findViewById(R.id.count_of_likes);
//        final TextView countComments = (TextView)rootView.findViewById(R.id.count_of_comments);
//        final ImageView notLikedPhoto = (ImageView)rootView.findViewById(R.id.image_not_liked);
//        final CheckBox likedOrNotLikedBox = ((CheckBox) rootView.findViewById(R.id.liked_or_not_liked_checkbox));
//        final TextView photoHeader = (TextView)rootView.findViewById(R.id.photoHeader);
//        photoHeader.setText(photos.get(currentposition).text);
//        countLikes.setText(String.valueOf(photos.get(currentposition).likes));
//        countComments.setText(String.valueOf(photos.get(currentposition).comments));
        Log.d("Current VIEW", photos.get(imagepager.getCurrentItem()).text);
        VKHelper.getUserInfo(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                JSONArray arr = response.json.optJSONArray("response");
                JSONObject jsonObject = arr.optJSONObject(0);
                user_id = jsonObject.optLong("id");
                arguments.putLong(ARG_VK_USER_ID, user_id);

            }
        });


//        VKHelper.isLIked(TYPE,photos.get(currentposition).owner_id,photos.get(currentposition).id,new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//
//                isLiked= (response.json.optJSONObject("response")).optInt("liked");
//                if(isLiked==0){notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
//                    likedOrNotLikedBox.setChecked(false);}
//                if(isLiked==1){notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
//                    likedOrNotLikedBox.setChecked(true);}
//
//                arguments.putInt("isLiked",isLiked);
//
//
//            }
//        });

//        like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isLiked == 0) {
//                    VKHelper.setLike(TYPE, photos.get(currentposition).owner_id, photos.get(currentposition).id, new VKRequest.VKRequestListener() {
//                        @Override
//                        public void onComplete(VKResponse response) {
//                            super.onComplete(response);
//                            notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
//                            likedOrNotLikedBox.setChecked(true);
//                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString())+1));
//
//                            Toast.makeText(getActivity().getApplicationContext(), "LIKED: " + isLiked, Toast.LENGTH_SHORT).show();
//                            isLiked=1;
//
//                        }});
//
//                }if (isLiked==1){
//                    VKHelper.deleteLike(TYPE, photos.get(currentposition).owner_id, photos.get(currentposition).id, new VKRequest.VKRequestListener() {
//                        @Override
//                        public void onComplete(VKResponse response) {
//                            super.onComplete(response);
//                            likedOrNotLikedBox.setChecked(false);
//                            notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_up));
//                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString())-1));
//                            isLiked=0;
//
//                            Toast.makeText(getActivity().getApplicationContext(), "LIKE DELETED", Toast.LENGTH_SHORT).show();
//
//                        }});
//                }
//            }});


//        comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Toast.makeText(getActivity().getApplicationContext(), "You clicked comment", Toast.LENGTH_SHORT).show();
//                Fragment fragment = FragmentPhotoCommentAndInfo.newInstance(arguments.getLong(ARG_VK_GROUP_ID),
//                        arguments.getLong(ARG_VK_ALBUM_ID),
//                        photos,arguments.getLong(ARG_VK_USER_ID),
//                        arguments.getInt("isLiked"),currentposition);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
//            }
//        });




        setRetainInstance(true);

        return rootView;


    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


//    protected void initAdapter(View view, Bundle argument, ) {
//        Bundle arguments = getArguments();
//        imagepager = (ViewPager) view.findViewById(R.id.pager);
//        imagepager.setAdapter(new FullScreenImageAdapter(photos, getLayoutInflater(arguments), argument));
//        imagepager.setCurrentItem(currentposition);
//    }

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
//
//
//            }
//        });
}
