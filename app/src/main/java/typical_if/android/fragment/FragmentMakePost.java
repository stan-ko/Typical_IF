package typical_if.android.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.viewpagerindicator.CirclePageIndicator;
import com.vk.sdk.VKSdk;

import de.greenrobot.event.EventBus;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.RecyclerWallAdapter;
import typical_if.android.event.EventShowPhotoAttachDialog;
import typical_if.android.view.TouchMakePostImageButton;

/**
 * Created by admin on 18.08.2014.
 */
public class FragmentMakePost extends FragmentWithAttach {

    private static long gid;
    private long pid;
    private int type;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentMakePost newInstance(long vkGroupId, long post_id, int typeParam) {
        Constants.isFragmentMakePostLoaded =true;
        FragmentMakePost fragment = new FragmentMakePost();
        Bundle args = new Bundle();

        fragment.gid = vkGroupId;
        fragment.pid = post_id;
        fragment.type = typeParam;

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMakePost() {}

//    static EditText textField;
//    static RobotoTextView txtPostAttachCounter;
//
//    static AddFloatingActionButton btSendPost;
//    static LinearLayout makePostAttachmentsContainer;
//    static LinearLayout makePostAudioContainer;
//    static LinearLayout makePostDocContainer;
//    static RelativeLayout makePostMediaContainer;
//
//    static ViewPager makePostMediaPager;
//    static ImageButton makePostMediaPagerVideoButton;
//    static CirclePageIndicator makePostMediaPagerIndicator;
//
//    static TouchMakePostImageButton imgPostAttachPhoto;
//    static TouchMakePostImageButton imgPostAttachVideo;
//    static TouchMakePostImageButton imgPostAttachAudio;
//    static TouchMakePostImageButton imgPostAttachDoc;
//
//    private static final View.OnClickListener tooManyAttachments = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(TIFApp.getAppContext(), R.string.max_attaches, Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    private static View.OnClickListener photoAttachClick;
//
//    private static final View.OnClickListener audioAttachClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newAudioAttachInstance()).addToBackStack(null).commit();
//        }
//    };
//
//    private static final View.OnClickListener videoAttachClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newVideoAttachInstance()).addToBackStack(null).commit();
//        }
//    };
//
//    private static final View.OnClickListener docAttachClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newDocAttachInstance()).addToBackStack(null).commit();
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Constants.isFragmentMakePostLoaded =true;
        Log.d("Make post","  Fragment");
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        View rootView = inflater.inflate(R.layout.fragment_make_post, container, false);
        setRetainInstance(true);

        photoAttachClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventShowPhotoAttachDialog(gid * (-1), 0));
            }
        };

        textField = (EditText) rootView.findViewById(R.id.etxt_make_post_field);
        if (RecyclerWallAdapter.surpriseCounter!=0 && RecyclerWallAdapter.surpriseCounter==15 && VKSdk.isLoggedIn()){
            OfflineMode.saveInt(15, "surprise");
            textField.setText("Вітаємо ви стали учасником розіграшу 10 годин у антикафе STANTSIYA," +
                    " для того щоб підтвердити вашу участь в розіграші виберіть свою фотографію і " +
                    "натисніть кнопку 'Відправити' " );
        }

        txtPostAttachCounter = (RobotoTextView) rootView.findViewById(R.id.txt_post_attach_counter);

        imgPostAttachPhoto = (TouchMakePostImageButton) rootView.findViewById(R.id.img_post_attach_photo);
        imgPostAttachPhoto.setOnClickListener(photoAttachClick);

        imgPostAttachVideo = (TouchMakePostImageButton) rootView.findViewById(R.id.img_post_attach_video);
        imgPostAttachVideo.setOnClickListener(videoAttachClick);

        imgPostAttachAudio = (TouchMakePostImageButton) rootView.findViewById(R.id.img_post_attach_audio);
        imgPostAttachAudio.setOnClickListener(audioAttachClick);

        imgPostAttachDoc = (TouchMakePostImageButton) rootView.findViewById(R.id.img_post_attach_doc);
        imgPostAttachDoc.setOnClickListener(docAttachClick);

        makePostAttachmentsContainer = (LinearLayout) rootView.findViewById(R.id.make_post_attachments_container_new);
        makePostAudioContainer = (LinearLayout) rootView.findViewById(R.id.make_post_audio_container);
        makePostDocContainer = (LinearLayout) rootView.findViewById(R.id.make_post_doc_container);
        makePostMediaContainer = (RelativeLayout) rootView.findViewById(R.id.make_post_media_container);
        btSendPost = (AddFloatingActionButton) rootView.findViewById(R.id.bt_post_send);

        makePostMediaPager = (ViewPager) rootView.findViewById(R.id.media_pager);
        makePostMediaPagerIndicator = (CirclePageIndicator) rootView.findViewById(R.id.media_circle_indicator);
        makePostMediaPagerVideoButton = (ImageButton) rootView.findViewById(R.id.ib_goto_video_page);

        switch (type) {
            case 0:
                Constants.tempMaxPostAttachCounter = 10;
                btSendPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VKHelper.doWallPost(gid, textField.getText(), getAttachesForPost(), new VKRequestListener() {
                            @Override
                            public void onSuccess() {
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.popBackStack();
                                Toast.makeText(TIFApp.getAppContext(), ItemDataSetter.context.getString(R.string.post_added), Toast.LENGTH_SHORT).show();
                            }
//                            @Override
//                            public void onError() {
//                                OfflineMode.onErrorToast();
//                            }
                        });
                    }
                });
                break;
            case 1:
                Constants.tempMaxPostAttachCounter = 10;
                setAttachmentsOnEdit();
                btSendPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VKHelper.editSuggestedPost(gid, pid, textField.getText(), getAttachesForPost(), new VKRequestListener() {
                            @Override
                            public void onSuccess() {
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentWall.newInstance(true)).addToBackStack(null).commit();
                            }
//                            @Override
//                            public void onError() {
//                                OfflineMode.onErrorToast();
//                            }
                        });
                    }
                });
        }

        txtPostAttachCounter.setText(Constants.tempPostAttachCounter + " / " + + Constants.tempMaxPostAttachCounter);

        btSendPost.setVisibility(View.INVISIBLE);

        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (textField.getText().length() == 0 || Constants.tempPostAttachCounter == 0) {
                    btSendPost.setVisibility(View.INVISIBLE);
                } else {
                    btSendPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textField.getText().length()  == 0) {
                    btSendPost.setVisibility(View.INVISIBLE);
                } else {
                    btSendPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textField.getText().length()  == 0) {
                    btSendPost.setVisibility(View.INVISIBLE);
                } else {
                    btSendPost.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

}
