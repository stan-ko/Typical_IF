package typical_if.android.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;

/**
 * Created by admin on 18.08.2014.
 */
public class FragmentMakePost extends Fragment {

    private static long gid;
    private long pid;
    private int type;
    private static Activity activity;
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

    public FragmentMakePost() {
    }

    static EditText textField;
    static TextView txtPostAttachCounter;

    Button btSendPost;
    static LinearLayout makePostAttachmentsContainer;
    static LinearLayout makePostAudioContainer;
    static LinearLayout makePostDocContainer;
    static RelativeLayout makePostMediaContainer;

    static ImageView imgPostAttachPhoto;
    static ImageView imgPostAttachVideo;
    static ImageView imgPostAttachAudio;
    static ImageView imgPostAttachDoc;

    public static final String TOO_MANY_ATTACHES = "Too many attaches!";
    private static final View.OnClickListener tooManyAttachments = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(ItemDataSetter.context, TOO_MANY_ATTACHES, Toast.LENGTH_SHORT).show();
        }
    };

    private static final View.OnClickListener photoAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Dialogs.photoAttachDialog(activity, gid * (-1), 0);
        }
    };

    private static final View.OnClickListener audioAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newInstance(2)).addToBackStack(null).commit();
        }
    };

    private static final View.OnClickListener videoAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newInstance(1)).addToBackStack(null).commit();
        }
    };

    private static final View.OnClickListener docAttachClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentAttachPostList.newInstance(3)).addToBackStack(null).commit();
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
        View rootView = inflater.inflate(R.layout.fragment_make_post, container, false);
        setRetainInstance(true);
        activity = getActivity();

        textField = (EditText) rootView.findViewById(R.id.etxt_make_post_field);

        txtPostAttachCounter = (TextView) rootView.findViewById(R.id.txt_post_attach_counter);

        imgPostAttachPhoto = (ImageView) rootView.findViewById(R.id.img_post_attach_photo);
        imgPostAttachPhoto.setOnClickListener(photoAttachClick);

        imgPostAttachVideo = (ImageView) rootView.findViewById(R.id.img_post_attach_video);
        imgPostAttachVideo.setOnClickListener(videoAttachClick);

        imgPostAttachAudio = (ImageView) rootView.findViewById(R.id.img_post_attach_audio);
        imgPostAttachAudio.setOnClickListener(audioAttachClick);

        imgPostAttachDoc = (ImageView) rootView.findViewById(R.id.img_post_attach_doc);
        imgPostAttachDoc.setOnClickListener(docAttachClick);

        makePostAttachmentsContainer = (LinearLayout) rootView.findViewById(R.id.make_post_attachments_container_new);
        makePostAudioContainer = (LinearLayout) rootView.findViewById(R.id.make_post_audio_container);
        makePostDocContainer = (LinearLayout) rootView.findViewById(R.id.make_post_doc_container);
        makePostMediaContainer = (RelativeLayout) rootView.findViewById(R.id.make_post_media_container);

        btSendPost = (Button) rootView.findViewById(R.id.bt_post_send);

        switch (type) {
            case 0:
                Constants.tempMaxPostAttachCounter = 10;
                btSendPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VKHelper.doWallPost(gid, textField.getText(), getAttachesForPost(), new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.popBackStack();
                                Toast.makeText(getActivity(), "Posted", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(VKError error) {
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
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.popBackStack();
                                ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, FragmentWall.newInstance(true)).addToBackStack(null).commit();
                                Toast.makeText(getActivity(), "Edited", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });
                    }
                });
        }

        txtPostAttachCounter.setText(": " + Constants.tempPostAttachCounter + "/" + + Constants.tempMaxPostAttachCounter);

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

        txtPostAttachCounter.setText(": " + Constants.tempPostAttachCounter + "/" + Constants.tempMaxPostAttachCounter);
        textField.setText(Constants.tempTextSuggestPost);
    }

    public static void refreshMakePostFragment(int which) {
        txtPostAttachCounter.setText(": " + Constants.tempPostAttachCounter + "/" + Constants.tempMaxPostAttachCounter);

        if (Constants.tempPostAttachCounter == 0) {
            makePostAttachmentsContainer.setVisibility(View.GONE);
        } else {
            makePostAttachmentsContainer.setVisibility(View.VISIBLE);
        }


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
                makePostMediaContainer.setVisibility(View.VISIBLE);
                setMedia(makePostMediaContainer, Constants.tempPhotoPostAttach, Constants.tempVideoPostAttach);
                break;
            case 1:
                makePostMediaContainer.setVisibility(View.VISIBLE);
                setMedia(makePostMediaContainer, Constants.tempPhotoPostAttach, Constants.tempVideoPostAttach);
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

            tempAudioContainer.getChildAt(0).setBackgroundColor(Color.parseColor(ItemDataSetter.postColor));
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
                image.setBackgroundColor(Color.parseColor(ItemDataSetter.postColor));
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

    public static void setMedia(RelativeLayout parent, final ArrayList<VKApiPhoto> photos, final ArrayList<VKApiVideo> videos) {
        ImageView img;
        RelativeLayout relativeLayout;
        ViewGroup mediaContainer = null;

        final int count = (photos != null ? photos.size() : 0) + (videos != null ? videos.size() : 0);
        switch (count) {
            case 1:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container);
                break;
            case 2:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container2);
                break;
            case 3:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container3);
                break;
            case 4:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container4);
                break;
            case 5:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container5);
                break;
            case 6:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container6);
                break;
            case 7:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container7);
                break;
            case 8:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container8);
                break;
            case 9:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container9);
                break;
            case 10:
                mediaContainer = ItemDataSetter.getPreparedView(parent, R.layout.media_container10);
                break;
            default:
                parent.setVisibility(View.GONE);
                return;
        }

        mediaContainer.setVisibility(View.VISIBLE);

        int lastPositionJ = 0;
        int lastPositionK = 0;
        int lastPositionL = 0;

        if (photos != null) {
            final int photosCount = photos.size();
            for (int i = 0; i < photosCount; i++) {
                final ViewGroup layout_i = (ViewGroup) mediaContainer.getChildAt(i);
                if (!(layout_i instanceof LinearLayout)) {
                    continue;
                }
                layout_i.setVisibility(View.VISIBLE);
                linearBreak:
                for (int j = 0, photoPointer = 0; j < photos.size(); j++) {
                    final ViewGroup layout_i_j = (ViewGroup) layout_i.getChildAt(j);
                    if (!(layout_i_j instanceof RelativeLayout)) {
                        continue;
                    }
                    final int kMax = layout_i_j.getChildCount();
                    for (int k = 0; k < kMax; k++) {
                        final View view_i_j_k = layout_i_j.getChildAt(k);
                        if (view_i_j_k instanceof ImageView) {
                            img = (ImageView) view_i_j_k;
                            final int finalJ = photoPointer++;
                            ImageLoader.getInstance().displayImage(photos.get(finalJ).photo_604, img, ItemDataSetter.animationLoader);
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteAttaches(0, photos.get(finalJ));
                                }
                            });
                            if (photoPointer == photos.size()) {
                                lastPositionJ = j + 1;
                                lastPositionK = k;
                                break;
                            }
                        } else if (view_i_j_k instanceof LinearLayout) {
                            final ViewGroup layout_i_j_k = (LinearLayout) view_i_j_k;
                            final int lMax = layout_i_j_k.getChildCount();
                            for (int l = 0; l < lMax; l++) {
                                final ViewGroup layout_i_j_k_l = (ViewGroup) layout_i_j_k.getChildAt(l);
                                if (photoPointer == photos.size()) {
                                    lastPositionJ = j;
                                    lastPositionL = l;
                                    lastPositionK = k;
                                    break linearBreak;
                                }
                                img = (ImageView) layout_i_j_k_l.getChildAt(0);
                                final int finalL = photoPointer++;
                                ImageLoader.getInstance().displayImage(photos.get(finalL).photo_130, img, ItemDataSetter.animationLoader);
                                img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteAttaches(0, photos.get(finalL));
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        if (videos != null) {
            final int videosCount = videos.size();
            for (int i = 0; i < videosCount; i++) {
                final ViewGroup layout_i = (ViewGroup) mediaContainer.getChildAt(i);
                if (!(layout_i instanceof LinearLayout)) {
                    continue;
                }
                layout_i.setVisibility(View.VISIBLE);
                final int jMax = layout_i.getChildCount();
                for (int j = lastPositionJ, videoPointer = 0; j < jMax; j++) {
                    final ViewGroup layout_i_j = (ViewGroup) layout_i.getChildAt(j);
                    if (!(layout_i_j instanceof RelativeLayout)) {
                        continue;
                    }
                    final int kMax = layout_i_j.getChildCount();
                    for (int k = lastPositionK; k < kMax; k++) {
                        final View view_i_j_k = layout_i_j.getChildAt(k);
                        if (view_i_j_k instanceof ImageView) {
                            if (videoPointer == videosCount) {
                                break;
                            }
                            img = (ImageView) view_i_j_k;
                            if (videosCount == 1) {
                                img.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ItemDataSetter.setInDp(250)));
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                            final int finalJ = videoPointer++;
                            ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_320, img, ItemDataSetter.animationLoader);

                            relativeLayout = (RelativeLayout) layout_i_j.getChildAt(k + 1);
                            relativeLayout.setVisibility(View.VISIBLE);
                            relativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteAttaches(1, videos.get(finalJ));
                                }
                            });

                            ((TextView) relativeLayout.getChildAt(1)).setText(ItemDataSetter.getMediaTime(videos.get(finalJ).duration));
                            ((TextView) relativeLayout.getChildAt(2)).setText(videos.get(finalJ).title);
                        } else if (view_i_j_k instanceof LinearLayout) {
                            final ViewGroup layout_i_j_k = (LinearLayout) view_i_j_k;
                            final int lMax = layout_i_j_k.getChildCount();
                            for (int l = lastPositionL; l < lMax; l++) {
                                final ViewGroup layout_i_j_k_l = (ViewGroup) layout_i_j_k.getChildAt(l);
                                lastPositionL = 0;
                                if (layout_i_j_k_l instanceof RelativeLayout) {
                                    if (videoPointer == videosCount) {
                                        break;
                                    }
                                    final int finalJ = videoPointer++;
                                    img = (ImageView) layout_i_j_k_l.getChildAt(0);
                                    ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_130, img, ItemDataSetter.animationLoader);

                                    relativeLayout = (RelativeLayout) layout_i_j_k_l.getChildAt(1);
                                    relativeLayout.setVisibility(View.VISIBLE);
                                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteAttaches(1, videos.get(finalJ));
                                        }
                                    });

                                    ((TextView) relativeLayout.getChildAt(1)).setText(ItemDataSetter.getMediaTime(videos.get(finalJ).duration));
                                    ((TextView) relativeLayout.getChildAt(2)).setText(videos.get(finalJ).title);
                                }
                            }
                        }
                    }
                }
            }
        }
        parent.addView(mediaContainer);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
