package typical_if.android.fragment;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.view.TouchMakePostImageButton;

/**
 * Created by admin on 18.08.2014.
 */
public abstract class FragmentWithAttach extends Fragment {

    private static long gid;
    private long pid;
    private int type;

    @Override
    public void onResume() {
        super.onResume();
        Constants.isFragmentMakePostLoaded = true;
    }


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

    static final View.OnClickListener tooManyAttachments = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(TIFApp.getAppContext(), R.string.max_attaches, Toast.LENGTH_SHORT).show();
        }
    };

    static View.OnClickListener photoAttachClick;

    static final View.OnClickListener audioAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newAudioAttachInstance()).addToBackStack(null).commit();
        }
    };

    static final View.OnClickListener videoAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newVideoAttachInstance()).addToBackStack(null).commit();
        }
    };

    static final View.OnClickListener docAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newDocAttachInstance()).addToBackStack(null).commit();
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        Constants.isFragmentMakePostLoaded = false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.tempAudioPostAttach.clear();
        Constants.tempPhotoPostAttach.clear();
        Constants.tempVideoPostAttach.clear();
        Constants.tempDocPostAttach.clear();
        Constants.tempPostAttachCounter = 0;
        Constants.isFragmentCommentsLoaded = false;
    }

    String getAttachesForPost() {
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
    }

    ;

    public void setAttachmentsOnEdit() {
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

    public void refreshMakePostFragment(int which) {
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

    public void deleteAttaches(int which, Object object) {
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

    public void setAudios(LinearLayout parent, final ArrayList<VKApiAudio> audios) {
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

    public void setDocs(LinearLayout parent, final ArrayList<VKApiDocument> docs) {
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

    public void setMedia(
            final ViewPager mediaPager,
            CirclePageIndicator mediaPagerIndicator,
            ImageButton mediaPagerVideoButton,
            RelativeLayout mediaLayout,
            final ArrayList<VKApiPhoto> photos,
            final ArrayList<VKApiVideo> videos) {

        mediaLayout.setTag(false);

        ItemDataSetter.setMediaPager(
                this,
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
        Constants.isFragmentMakePostLoaded = true;
    }

}
