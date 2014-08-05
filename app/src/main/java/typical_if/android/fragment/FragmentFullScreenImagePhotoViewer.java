package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    int isLiked;
    long user_id;

    public static FragmentFullScreenImagePhotoViewer newInstance(ArrayList<Photo> photos, int currentposition, long vk_group_id, long vk_album_id) {

        FragmentFullScreenImagePhotoViewer fragment = new FragmentFullScreenImagePhotoViewer();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        FragmentFullScreenImagePhotoViewer.photos = photos;

        FragmentFullScreenImagePhotoViewer.currentposition = currentposition;
        args.putLong(ARG_VK_GROUP_ID, vk_group_id);
        args.putLong(ARG_VK_ALBUM_ID, vk_album_id);
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
//        final Animation animTextViewUp ;
//        final Animation animTextViewDown ;
//        animTextViewUp  =AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.move_up);
//        animTextViewDown=AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.move_down);
//        animTextViewUp.setAnimationListener(this);
//        animTextViewDown.setAnimationListener(this);


        final Animation animFadeInObjects;
        final Animation animFadeOutObjects;
        animFadeInObjects = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fade_in);
        animFadeOutObjects =AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fade_out);
        animFadeInObjects.setAnimationListener(this);
        animFadeOutObjects.setAnimationListener(this);


        final View rootView = inflater.inflate(R.layout.fragment_fullscreen_list, container, false);

        FragmentManager manager = getFragmentManager();
        imagepager = (ViewPager) rootView.findViewById(R.id.pager);

        imagepager.setAdapter(new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments, arguments.getLong(ARG_VK_GROUP_ID),
                arguments.getLong(ARG_VK_ALBUM_ID), arguments.getLong(ARG_VK_USER_ID), manager, rootView));
        imagepager.setCurrentItem(currentposition);

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

//        final TextView countLikes = (TextView) rootView.findViewById(R.id.count_of_likes);
//        final TextView countComments = (TextView) rootView.findViewById(R.id.count_of_comments);
//        final ImageView like = (ImageView) rootView.findViewById(R.id.image_not_liked);
//        final ImageView comment = (ImageView) rootView.findViewById(R.id.image_comment);
//        final CheckBox likedOrNotLikedBox = ((CheckBox) rootView.findViewById(R.id.liked_or_not_liked_checkbox));
//        final TextView photoHeader = (TextView) rootView.findViewById(R.id.photoHeader);


        imagepager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

//                like.setVisibility(View.VISIBLE);
//                countLikes.setVisibility(View.VISIBLE);
//                countComments.setVisibility(View.VISIBLE);
//                comment.setVisibility(View.VISIBLE);
//                likedOrNotLikedBox.setVisibility(View.VISIBLE);
//                photoHeader.setVisibility(View.VISIBLE);
//                like.setAnimation(animFadeInObjects);
//                countLikes.setAnimation(animFadeInObjects);
//                countComments.setAnimation(animFadeInObjects);
//                comment.setAnimation(animFadeInObjects);
//                likedOrNotLikedBox.setAnimation(animFadeOutObjects);
//                photoHeader.setAnimation(animFadeInObjects);

            }
        });
        imagepager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


//                like.setVisibility(View.INVISIBLE);
//                countLikes.setVisibility(View.INVISIBLE);
//                countComments.setVisibility(View.INVISIBLE);
//                comment.setVisibility(View.INVISIBLE);
//                likedOrNotLikedBox.setVisibility(View.INVISIBLE);
//                photoHeader.setVisibility(View.INVISIBLE);
//                like.setAnimation(animFadeOutObjects);
//                countLikes.setAnimation(animFadeOutObjects);
//                countComments.setAnimation(animFadeOutObjects);
//                comment.setAnimation(animFadeOutObjects);
//                likedOrNotLikedBox.setAnimation(animFadeOutObjects);
//                photoHeader.setAnimation(animFadeOutObjects);
                return false;
            }

        });
//        Button fadein = ((Button) rootView.findViewById(R.id.button_fade_in));
//        fadein.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                clickCounter++;
////                if(clickCounter%2==0) {
////                    {
//                        like.setAnimation(animFadeOutObjects);
//                        countLikes.setAnimation(animFadeOutObjects);
//                        countComments.setAnimation(animFadeOutObjects);
//                        comment.setAnimation(animFadeOutObjects);
//                        likedOrNotLikedBox.setAnimation(animFadeOutObjects);
//                        photoHeader.setAnimation(animFadeOutObjects);
//                        Toast.makeText(VKUIHelper.getApplicationContext(),"from 1.0 to 0.0", Toast.LENGTH_SHORT).show();
//
//
//
//
////                    }
////                }else{
////                    {
////                       like.setAnimation(animFadeInObjects);
////                        countLikes.setAnimation(animFadeInObjects);
////                        countComments.setAnimation(animFadeInObjects);
////                        comment.setAnimation(animFadeInObjects);
////                        likedOrNotLikedBox.setAnimation(animFadeOutObjects);
////                        photoHeader.setAnimation(animFadeInObjects);
////                        Toast.makeText(VKUIHelper.getApplicationContext(),"from 0.0 to 1.0", Toast.LENGTH_SHORT).show();
//
//
//                   // }
//               // }
//
//
//
//            }
//        });
//
//        Button fadeout = ((Button) rootView.findViewById(R.id.button_fade_out));
//        fadeout.setOnClickListener(new View.OnClickListener() {
//                                       @Override
//                                       public void onClick(View v) {
//
//                                           like.setAnimation(animFadeInObjects);
//                        countLikes.setAnimation(animFadeInObjects);
//                        countComments.setAnimation(animFadeInObjects);
//                        comment.setAnimation(animFadeInObjects);
//                        likedOrNotLikedBox.setAnimation(animFadeOutObjects);
//                        photoHeader.setAnimation(animFadeInObjects);
//                        Toast.makeText(VKUIHelper.getApplicationContext(),"from 0.0 to 1.0", Toast.LENGTH_SHORT).show();
//
//                                       }
//
//                                   });

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
