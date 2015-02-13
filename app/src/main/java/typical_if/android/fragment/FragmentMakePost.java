package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.event.EventShowPhotoAttachDialog;
import typical_if.android.view.TouchMakePostImageButton;

/**
 * Created by admin on 18.08.2014.
 */
public class FragmentMakePost extends Fragment {

    private static long gid;
    private long pid;
    private int type;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentMakePost newInstance(long vkGroupId, long post_id, int typeParam) {
        FragmentMakePost fragment = new FragmentMakePost();
        Bundle args = new Bundle();

        fragment.gid = vkGroupId;
        fragment.pid = post_id;
        fragment.type = typeParam;

        fragment.setArguments(args);
        return fragment;
    }


    public FragmentMakePost() {}

    static EditText textField;
    static RobotoTextView txtPostAttachCounter;

    static AddFloatingActionButton btSendPost;
    static LinearLayout makePostAttachmentsContainer;
    static LinearLayout makePostAudioContainer;
    static LinearLayout makePostDocContainer;
    static RelativeLayout makePostMediaContainer;

    static ViewPager makePostMediaPager;
    static ImageButton makePostMediaPagerVideoButton;
    static CirclePageIndicator makePostMediaPagerIndicator;

    static TouchMakePostImageButton imgPostAttachPhoto;
    static TouchMakePostImageButton imgPostAttachVideo;
    static TouchMakePostImageButton imgPostAttachAudio;
    static TouchMakePostImageButton imgPostAttachDoc;

    private static final View.OnClickListener tooManyAttachments = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(TIFApp.getAppContext(), ItemDataSetter.context.getString(R.string.max_attaches), Toast.LENGTH_SHORT).show();
        }
    };

    private static View.OnClickListener photoAttachClick;

    private static final View.OnClickListener audioAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newAudioAttachInstance()).addToBackStack(null).commit();
        }
    };

    private static final View.OnClickListener videoAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newVideoAttachInstance()).addToBackStack(null).commit();
        }
    };

    private static final View.OnClickListener docAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newDocAttachInstance()).addToBackStack(null).commit();
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.tempAudioPostAttach.clear();
        Constants.tempPhotoPostAttach.clear();
        Constants.tempVideoPostAttach.clear();
        Constants.tempDocPostAttach.clear();
        Constants.tempPostAttachCounter = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
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
        if (WallAdapter.surpriseCounter!=0 && WallAdapter.surpriseCounter==15 && VKSdk.isLoggedIn()){
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
                        VKHelper.doWallPost(gid, textField.getText(), getAttachesForPost(), new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.popBackStack();
                                Toast.makeText(TIFApp.getAppContext(), ItemDataSetter.context.getString(R.string.post_added), Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
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
                        VKHelper.editSuggestedPost(gid, pid, textField.getText(), getAttachesForPost(), new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentWall.newInstance(true)).addToBackStack(null).commit();
                            }
                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
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

    private static String getAttachesForPost() {
        StringBuilder attachments = new StringBuilder("");
        if (Constants.tempPostAttachCounter != 0) {
            VKApiPhoto photo;
            for (int i = 0; i < Constants.tempPhotoPostAttach.size(); i++) {
                photo = Constants.tempPhotoPostAttach.get(i);
                attachments.append("photo").append(photo.owner_id).append("_").append(photo.id).append(",");
            }
            VKApiVideo video;
            for (int i = 0; i < Constants.tempVideoPostAttach.size(); i++) {
                video = Constants.tempVideoPostAttach.get(i);
                attachments.append("video").append(video.owner_id).append("_").append(video.id).append(",");
            }
            VKApiAudio audio;
            for (int i = 0; i < Constants.tempAudioPostAttach.size(); i++) {
                audio = Constants.tempAudioPostAttach.get(i);
                attachments.append("audio").append(audio.owner_id).append("_").append(audio.id).append(",");
            }
            VKApiDocument doc;
            for (int i = 0; i < Constants.tempDocPostAttach.size(); i++) {
                doc = Constants.tempDocPostAttach.get(i);
                attachments.append("doc").append(doc.owner_id).append("_").append(doc.id).append(",");
            }

            attachments.deleteCharAt(attachments.lastIndexOf(","));
        }
        return attachments.toString();
    };

    public static void setAttachmentsOnEdit() {
        if (Constants.tempPhotoPostAttach.size() != 0) {
            refreshMakePostFragment(0);
        }
        if (Constants.tempVideoPostAttach.size() != 0) {
            refreshMakePostFragment(1);
        }
        if (Constants.tempAudioPostAttach.size() != 0) {
            refreshMakePostFragment(2);
        }
        if (Constants.tempDocPostAttach.size() != 0) {
            refreshMakePostFragment(3);
        }

        txtPostAttachCounter.setText(Constants.tempPostAttachCounter + " / " + Constants.tempMaxPostAttachCounter);
        textField.setText(Constants.tempTextSuggestPost);
    }

    public static void refreshMakePostFragment(int which) {
        if (Constants.tempPostAttachCounter >= 1 || textField.getText().length() != 0) {
            btSendPost.setVisibility(View.VISIBLE);
        } else {
            btSendPost.setVisibility(View.INVISIBLE);
        }

        txtPostAttachCounter.setText(Constants.tempPostAttachCounter + " / " + Constants.tempMaxPostAttachCounter);

        if (Constants.tempPostAttachCounter != 10) {
            imgPostAttachPhoto.setOnClickListener(photoAttachClick);
            imgPostAttachVideo.setOnClickListener(videoAttachClick);
            imgPostAttachAudio.setOnClickListener(audioAttachClick);
            imgPostAttachDoc.setOnClickListener(docAttachClick);
        } else {
            imgPostAttachPhoto.setOnClickListener(tooManyAttachments);
            imgPostAttachDoc.setOnClickListener(tooManyAttachments);
            imgPostAttachAudio.setOnClickListener(tooManyAttachments);
            imgPostAttachVideo.setOnClickListener(tooManyAttachments);
        }

        switch (which) {
            case 0:
            case 1:
                if (Constants.tempPhotoPostAttach.size() == 0 && Constants.tempVideoPostAttach.size() == 0) {
                    makePostMediaContainer.setVisibility(View.GONE);
                } else {
                    makePostMediaContainer.setVisibility(View.VISIBLE);
                    setMedia(
                            makePostMediaPager,
                            makePostMediaPagerIndicator,
                            makePostMediaPagerVideoButton,
                            makePostMediaContainer,
                            Constants.tempPhotoPostAttach,
                            Constants.tempVideoPostAttach
                    );
                }

                break;
            case 2:
                makePostAudioContainer.setVisibility(View.VISIBLE);
                setAudios(makePostAudioContainer, Constants.tempAudioPostAttach);
                break;
            case 3:
                makePostDocContainer.setVisibility(View.VISIBLE);
                setDocs(makePostDocContainer, Constants.tempDocPostAttach);
                break;
        }
    }

    public static void deleteAttaches(int which, Object object) {
        switch (which) {
            case 0:
                Constants.tempPhotoPostAttach.remove(object);
                break;
            case 1:
                Constants.tempVideoPostAttach.remove(object);
                break;
            case 2:
                Constants.tempAudioPostAttach.remove(object);
                break;
            case 3:
                Constants.tempDocPostAttach.remove(object);
                break;
        }

        Constants.tempPostAttachCounter--;

        refreshMakePostFragment(which);
    }

    public static void setAudios(LinearLayout parent, final ArrayList<VKApiAudio> audios) {
        ViewGroup tempAudioContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (final VKApiAudio audio : audios) {
            tempAudioContainer = (ViewGroup) ItemDataSetter.inflater.inflate(R.layout.audio_container, parent, false);
            tempAudioContainer.setVisibility(View.VISIBLE);

            tempAudioContainer.getChildAt(0).setEnabled(false);
            ((TextView) tempAudioContainer.getChildAt(2)).setText(ItemDataSetter.getMediaTime(audio.duration));
            ((TextView) tempAudioContainer.getChildAt(3)).setText(audio.artist);
            ((TextView) tempAudioContainer.getChildAt(4)).setText(audio.title);

            tempAudioContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAttaches(2, audio);
                }
            });

            parent.addView(tempAudioContainer);
        }
    }

    public static void setDocs(LinearLayout parent, final ArrayList<VKApiDocument> docs) {
        ViewGroup tempDocumentContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (final VKApiDocument doc : docs) {
            tempDocumentContainer = (ViewGroup) ItemDataSetter.inflater.inflate(R.layout.document_container, parent, false);
            tempDocumentContainer.setVisibility(View.VISIBLE);

            final ImageView image = (ImageView) tempDocumentContainer.getChildAt(1);
            final TextView title = (TextView) tempDocumentContainer.getChildAt(2);
            final TextView size = (TextView) tempDocumentContainer.getChildAt(3);

            title.setText(doc.title);

            if (doc.isImage()) {
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(doc.photo_100, image, ItemDataSetter.animationLoader);
                size.setText(Constants.DOC_TYPE_IMAGE + " " + ItemDataSetter.readableFileSize(doc.size));
            } else if (doc.isGif()) {
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(doc.photo_100, image, ItemDataSetter.animationLoader);
                size.setText(Constants.DOC_TYPE_ANIMATION + " " + ItemDataSetter.readableFileSize(doc.size));
            } else {
                image.setImageDrawable(Constants.RESOURCES.getDrawable(android.R.drawable.ic_menu_save));
                image.setLayoutParams(new RelativeLayout.LayoutParams(ItemDataSetter.setInDp(50), ItemDataSetter.setInDp(50)));

                RelativeLayout.LayoutParams paramsForTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsForTitle.setMargins(ItemDataSetter.setInDp(55), 0, 0, 0);
                title.setLayoutParams(paramsForTitle);

                RelativeLayout.LayoutParams paramsForSize = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsForSize.setMargins(ItemDataSetter.setInDp(55), ItemDataSetter.setInDp(20), 0, 0);
                size.setLayoutParams(paramsForSize);
                size.setText(Constants.DOC_TYPE_DOCUMENT + " " + ItemDataSetter.readableFileSize(doc.size));
            }

            tempDocumentContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAttaches(3, doc);
                }
            });

            parent.addView(tempDocumentContainer);
        }
    }

    public static void setMedia(
            final ViewPager mediaPager,
            CirclePageIndicator mediaPagerIndicator,
            ImageButton mediaPagerVideoButton,
            RelativeLayout mediaLayout,
            final ArrayList<VKApiPhoto> photos,
            final ArrayList<VKApiVideo> videos) {

        mediaLayout.setTag(false);

        ItemDataSetter.setMediaPager(
                mediaPager,
                mediaPagerIndicator,
                mediaPagerVideoButton,
                mediaLayout,
                photos,
                videos
        );
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
