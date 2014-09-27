package typical_if.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKApiPlace;
import com.vk.sdk.api.model.VKApiPoll;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKApiWikiPage;
import com.vk.sdk.api.model.VKAttachments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typical_if.android.adapter.CommentsListAdapter;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.fragment.FragmentFullScreenViewer;
import typical_if.android.fragment.FragmentPhotoList;
import typical_if.android.model.Wall.Wall;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Created by admin on 05.08.2014.
 */
public class ItemDataSetter {

    public static Context context = TIFApp.getAppContext();
    public static LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    public final static Animation animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
    public final static ImageLoadingListener animationLoader = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            view.startAnimation(animationFadeIn);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    };

    public static ViewGroup getPreparedView(ViewGroup parent, int layoutRes) {
        parent.setVisibility(View.VISIBLE);
        parent.removeAllViews();

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(layoutRes, parent, false);
        viewGroup.setVisibility(View.VISIBLE);

        return viewGroup;
    }

    public static Wall wall;
    public static String postColor;
    public static WallAdapter.ViewHolder wallViewHolder;
    public static CommentsListAdapter.ViewHolder commentViewHolder;

    public static int position;
    public static android.support.v4.app.FragmentManager fragmentManager;


    public static void setSuggestAttachments(VKAttachments attachments) {
        int counter = 0;
        for (VKAttachments.VKApiAttachment attachment : attachments) {
            if (attachment.getType().equals(VKAttachments.TYPE_PHOTO)) {
                Constants.tempPhotoPostAttach.add((VKApiPhoto) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_VIDEO)) {
                Constants.tempVideoPostAttach.add((VKApiVideo) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_AUDIO)) {
                Constants.tempAudioPostAttach.add((VKApiAudio) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_DOC)) {
                Constants.tempDocPostAttach.add((VKApiDocument) attachment);
            }
            counter++;
        }
        Constants.tempPostAttachCounter = counter;
    }

    public static void setAttachemnts(VKAttachments attachments, LinearLayout parentLayout, int type) {
        final ArrayList<VKApiPhoto> photos = new ArrayList<VKApiPhoto>();
        final ArrayList<VKApiVideo> videos = new ArrayList<VKApiVideo>();
        final ArrayList<VKApiAudio> audios = new ArrayList<VKApiAudio>();
        final ArrayList<VKApiDocument> docs = new ArrayList<VKApiDocument>();
        final ArrayList<VKApiPhotoAlbum> albums = new ArrayList<VKApiPhotoAlbum>();
        VKApiWikiPage wikiPage = null;
        VKApiLink link = null;
        VKApiPoll poll = null;

        parentLayout.setVisibility(View.VISIBLE);

        for (VKAttachments.VKApiAttachment attachment : attachments) {
            if (attachment.getType().equals(VKAttachments.TYPE_PHOTO)) {
                photos.add((VKApiPhoto) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_VIDEO)) {
                videos.add((VKApiVideo) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_AUDIO)) {
                audios.add((VKApiAudio) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_DOC)) {
                docs.add((VKApiDocument) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_ALBUM)) {
                albums.add((VKApiPhotoAlbum) attachment);
            } else if (attachment.getType().equals(VKAttachments.TYPE_WIKI_PAGE)) {
                wikiPage = (VKApiWikiPage) attachment;
            } else if (attachment.getType().equals(VKAttachments.TYPE_POLL)) {
                poll = (VKApiPoll) attachment;
            } else if (attachment.getType().equals(VKAttachments.TYPE_LINK)) {
                link = (VKApiLink) attachment;
            }
        }


        RelativeLayout mediaLayout = null;
        LinearLayout audioLayout = null;
        LinearLayout documentLayout = null;
        LinearLayout albumLayout = null;
        RelativeLayout wikiPageLayout = null;
        RelativeLayout linkLayout = null;
        RelativeLayout pollLayout = null;

        switch (type) {
            case 0:
                mediaLayout = (RelativeLayout) parentLayout.findViewById(R.id.copyHistoryMediaLayout);
                audioLayout = (LinearLayout) parentLayout.findViewById(R.id.copyHistoryAudioLayout);
                documentLayout = (LinearLayout) parentLayout.findViewById(R.id.copyHistoryDocumentLayout);
                albumLayout = (LinearLayout) parentLayout.findViewById(R.id.copyHistoryAlbumLayout);
                wikiPageLayout = (RelativeLayout) parentLayout.findViewById(R.id.copyHistoryWikiPageLayout);
                linkLayout = (RelativeLayout) parentLayout.findViewById(R.id.copyHistoryLinkLayout);
                pollLayout = (RelativeLayout) parentLayout.findViewById(R.id.copyHistoryPollLayout);
                break;
            case 1:
                mediaLayout = wallViewHolder.postMediaLayout;
                audioLayout = wallViewHolder.postAudioLayout;
                documentLayout = wallViewHolder.postDocumentLayout;
                albumLayout = wallViewHolder.postAlbumLayout;
                wikiPageLayout = wallViewHolder.postWikiPageLayout;
                linkLayout = wallViewHolder.postLinkLayout;
                pollLayout = wallViewHolder.postPollLayout;
                break;
            case 2:
                mediaLayout = commentViewHolder.commentMediaLayout;
                audioLayout = commentViewHolder.commentAudioLayout;
                documentLayout = commentViewHolder.commentDocumentLayout;
                albumLayout = commentViewHolder.commentAlbumLayout;
                wikiPageLayout = commentViewHolder.commentWikiPageLayout;
                linkLayout = commentViewHolder.commentLinkLayout;
                pollLayout = commentViewHolder.commentPollLayout;
                break;
        }
        if (photos != null && photos.size() != 0 || videos != null && videos.size() != 0) {
            setMedia(mediaLayout, photos, videos);
        } else {
            mediaLayout.setVisibility(View.GONE);
        }

        if (audios != null && audios.size() != 0) {
            setAudios(audioLayout, audios);
        } else {
            audioLayout.setVisibility(View.GONE);
        }

        if (docs != null && docs.size() != 0) {
            setDocs(documentLayout, docs);
        } else {
            documentLayout.setVisibility(View.GONE);
        }

        if (albums != null && albums.size() != 0) {
            setAlbums(albumLayout, albums);
        } else {
            albumLayout.setVisibility(View.GONE);
        }

        if (wikiPage != null) {
            setWikiPage(wikiPageLayout, wikiPage);
        } else {
            wikiPageLayout.setVisibility(View.GONE);
        }

        if (link != null) {
            setLink(linkLayout, link);
        } else {
            linkLayout.setVisibility(View.GONE);
        }

        if (poll != null) {
            setPoll(pollLayout, poll);
        } else {
            pollLayout.setVisibility(View.GONE);
        }
//        final RelativeLayout mediaLayout1= mediaLayout;
//
//        for(i=0 ; i<photos.size(); i++){
//            VKHelper.getPhotoByID(photos.get(i).owner_id+"_"+photos.get(i).id,new VKRequest.VKRequestListener() {
//                @Override
//                public void onComplete(final VKResponse response) {
//                    super.onComplete(response);
//                    try {
//                        photos2.add(VKHelper.getPhotoFromJSONArray(response.json));
//
//                        setMedia(mediaLayout1, photos2, videos);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
    }

    public static void setPoll(RelativeLayout parent, VKApiPoll poll) {
        ViewGroup pollContainer = getPreparedView(parent, R.layout.poll_container);

        ((TextView) pollContainer.getChildAt(0)).setText(poll.question);

        if (poll.anonymous == 0) {
            ((TextView) pollContainer.getChildAt(1)).setText(Constants.POLL_NOT_ANONYMOUS + " " + poll.votes);
        } else {
            ((TextView) pollContainer.getChildAt(1)).setText(Constants.POLL_ANONYMOUS + " " + poll.votes);
        }
        ((ImageView) pollContainer.getChildAt(2)).setBackgroundColor(Color.parseColor(postColor));

        pollContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.toastInProgress.show();
            }
        });

        parent.addView(pollContainer);
    }

    public static SpannableStringBuilder getParsedText(String text) {
        final Matcher matTags = Pattern.compile("#\\w+").matcher(text);

        StringBuilder stringB = new StringBuilder(text);
        final SpannableStringBuilder spannable = new SpannableStringBuilder(text);

        int start = 0;
        int end = 0;

        while (matTags.find()) {
            start = stringB.indexOf(matTags.group());
            end = start + matTags.group().length();

            spannable.setSpan(new BackgroundColorSpan(Color.parseColor(postColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            final String temp = matTags.group();

            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse("http://vk.com/feed?q=%23" + temp.replaceFirst("#", "") + "&section=search");
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final Matcher matLinks = Pattern.compile(
                new StringBuilder()
                        .append("((?:(http|https|Http|Https|rtsp|Rtsp):")
                        .append("\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)")
                        .append("\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_")
                        .append("\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?")
                        .append("((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+")   // named host
                        .append("(?:")   // plus top level domain
                        .append("(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])")
                        .append("|(?:biz|b[abdefghijmnorstvwyz])")
                        .append("|(?:cat|com|coop|c[acdfghiklmnoruvxyz])")
                        .append("|d[ejkmoz]")
                        .append("|(?:edu|e[cegrstu])")
                        .append("|f[ijkmor]")
                        .append("|(?:gov|g[abdefghilmnpqrstuwy])")
                        .append("|h[kmnrtu]")
                        .append("|(?:info|int|i[delmnoqrst])")
                        .append("|(?:jobs|j[emop])")
                        .append("|k[eghimnrwyz]")
                        .append("|l[abcikrstuvy]")
                        .append("|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])")
                        .append("|(?:name|net|n[acefgilopruz])")
                        .append("|(?:org|om)")
                        .append("|(?:pro|p[aefghklmnrstwy])")
                        .append("|qa")
                        .append("|r[eouw]")
                        .append("|s[abcdeghijklmnortuvyz]")
                        .append("|(?:tel|travel|t[cdfghjklmnoprtvwz])")
                        .append("|u[agkmsyz]")
                        .append("|v[aceginu]")
                        .append("|w[fs]")
                        .append("|y[etu]")
                        .append("|z[amw]))[^\\s]+))").toString()
//                        .append("|(?:(?:25[0-5]|2[0-4]") // or ip address
//                        .append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
//                        .append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
//                        .append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
//                        .append("|[1-9][0-9]|[0-9])))")
//                        .append("(?:\\:\\d{1,5})?)") // plus option port number
//                        .append("(\\/(?:(?:a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")  // plus option query params
//                        .append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
//                        .append("(?:\\b|$)").toString()
        ).matcher(text);

        while (matLinks.find()) {
            start = stringB.indexOf(matLinks.group());
            end = start + matLinks.group().length();

            spannable.setSpan(new BackgroundColorSpan(Color.parseColor(postColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            final String temp = matLinks.group();

            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse(temp);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final Matcher matSite = Pattern.compile("@club26363301 \\(fromMobileIF\\)").matcher(text);
        while (matSite.find()) {
            start = stringB.indexOf(matSite.group());
            end = start + matSite.group().length();

            final String replier = "fromMobileIF";
            stringB.replace(start, end, replier);
            spannable.replace(start, end, replier);

            end = start + replier.length();
            spannable.setSpan(new BackgroundColorSpan(Color.parseColor(postColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse(replier);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final Matcher matReply = Pattern.compile("\\[(club|id)\\d+\\|[a-zA-ZА-Яа-яєЄіІїЇюЮйЙ 0-9(\\w)(\\W)_]+?\\]").matcher(text);
        while (matReply.find()) {
            start = stringB.indexOf(matReply.group());
            end = start + matReply.group().length();

            final String[] replier = matReply.group().replaceFirst("\\[", "").replaceFirst("\\]", "").split("\\|");
            stringB.replace(start, end, replier[1]);
            spannable.replace(start, end, replier[1]);

            end = start + replier[1].length();
            spannable.setSpan(new BackgroundColorSpan(Color.parseColor(postColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse("http://vk.com/" + replier[0]);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }




    public static void setText(String text, RelativeLayout parent) {
        ViewGroup textContainer = getPreparedView(parent, R.layout.post_text_layout);

        final TextView mainText = ((TextView) textContainer.getChildAt(0));
        final CheckBox showAll = ((CheckBox) textContainer.getChildAt(1));

        final SpannableStringBuilder spannable = getParsedText(text);

        mainText.setText(spannable);
        mainText.setMovementMethod(LinkMovementMethod.getInstance());

        if (spannable.length() > 300) {
            showAll.setVisibility(View.VISIBLE);
            showAll.setBackgroundColor(Color.parseColor(postColor));
            final SpannableStringBuilder originalSpannable = spannable;
            final SpannableStringBuilder tempSpannable = new SpannableStringBuilder();
            tempSpannable.append(spannable);
            final SpannableStringBuilder tempModifySpannable = tempSpannable.insert(297, "...").delete(300, tempSpannable.length());
            mainText.setText(tempModifySpannable);
            showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (mainText.onPreDraw()) {
                            mainText.setText(originalSpannable);
                        }
//                        expand(mainText, originalSpannable);
                        showAll.setText(Constants.SHOW_MIN_TEXT);
                    } else {
                        mainText.setText(tempModifySpannable);
                        showAll.setText(Constants.SHOW_ALL_TEXT);
//                        collapse(mainText, tempModifySpannable);
                    }
                }
            });
        } else {
            showAll.setVisibility(View.GONE);
        }

        parent.addView(textContainer);
    }

    public static void setSigned(final int id, RelativeLayout parent) {
        VKApiUser profile;
        String name = null;
        String image = null;

        for (int i = 0; i < wall.profiles.size(); i++) {
            profile = wall.profiles.get(i);
            if (id == profile.id) {
                name = profile.last_name + " " + profile.first_name;
                image = profile.photo_50;
            }
        }

        ViewGroup signedContainer = getPreparedView(parent, R.layout.signed_post_container);
        ImageLoader.getInstance().displayImage(image, (ImageView) signedContainer.getChildAt(0));

        TextView txt_name = ((TextView) signedContainer.getChildAt(1));
        txt_name.setText(name);

        signedContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://vk.com/id" + valueOf(id));
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        parent.addView(signedContainer);
    }

    public static void setLink(RelativeLayout parent, final VKApiLink link) {
        ViewGroup linkContainer = getPreparedView(parent, R.layout.link_container);

        ImageLoader.getInstance().displayImage(link.image_src, (ImageView) linkContainer.getChildAt(0));
        ((TextView) linkContainer.getChildAt(1)).setText(link.title);
        ((TextView) linkContainer.getChildAt(2)).setText(link.url);

        TextView description = (TextView) linkContainer.getChildAt(3);
        if (link.description.equals("")) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(link.description);
        }


        linkContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(link.url);
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        parent.addView(linkContainer);
    }

    public static void setGeo(VKApiPlace geo, RelativeLayout parent) {
        ViewGroup geoContainer = getPreparedView(parent, R.layout.geo_container);

        ImageView image = (ImageView) geoContainer.findViewById(R.id.img_geo);
        final String[] coordinates = geo.coordinates.split(" ");
        String url = "http://maps.google.com/maps/api/staticmap?center=" + coordinates[0] + "," + coordinates[1] + "&zoom=15&size=600x400&sensor=false";
        ImageLoader.getInstance().displayImage(url, image);

        TextView txt = (TextView) geoContainer.findViewById(R.id.txt_geo);
        txt.setText(geo.title);

        geoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "geo:" + coordinates[0] + "," + coordinates[1];
                context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        parent.addView(geoContainer);
    }

    public static void setWikiPage(RelativeLayout parent, final VKApiWikiPage wikiPage) {
        ViewGroup wikiPageContainer = getPreparedView(parent, R.layout.wiki_page_container);

        wikiPageContainer.getChildAt(0).setBackgroundColor(Color.parseColor(postColor));
        ((TextView) wikiPageContainer.getChildAt(1)).setText(wikiPage.title);

        wikiPageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(wikiPage.source);
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        parent.addView(wikiPageContainer);
    }

    public static void setAlbums(LinearLayout parent, final ArrayList<VKApiPhotoAlbum> albums) {

        ViewGroup tempAlbumContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (final VKApiPhotoAlbum album : albums) {
            tempAlbumContainer = (ViewGroup) inflater.inflate(R.layout.album_container, parent, false);
            tempAlbumContainer.setVisibility(View.VISIBLE);

            ImageView image = (ImageView) tempAlbumContainer.findViewById(R.id.img_album_thumb);
            ImageLoader.getInstance().displayImage(album.photo_604, image);
            ((TextView) tempAlbumContainer.getChildAt(1)).setText(valueOf(album.size));
            ((TextView) tempAlbumContainer.getChildAt(2)).setText(album.title);

            tempAlbumContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.ALBUM_ID = album.id;
                    Constants.TEMP_OWNER_ID = album.owner_id;
                    fragmentManager.beginTransaction().add(R.id.container, FragmentPhotoList.newInstance(1)).addToBackStack(null).commit();
                }
            });

            parent.addView(tempAlbumContainer);
        }
    }

    public static void setAudios(LinearLayout parent, final ArrayList<VKApiAudio> audios) {
        ViewGroup tempAudioContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (final VKApiAudio audio : audios) {
            tempAudioContainer = (ViewGroup) inflater.inflate(R.layout.audio_container, parent, false);
            tempAudioContainer.setVisibility(View.VISIBLE);
            //tempAudioContainer.getChildAt(0).setBackgroundColor(Color.parseColor(postColor));
            final CheckBox play_pause_music = (CheckBox) tempAudioContainer.getChildAt(0);
            SeekBar progressBar = (SeekBar) tempAudioContainer.getChildAt(1);

            if (Constants.playedPausedRecord.audioUrl != null && Constants.playedPausedRecord.audioUrl.equals(audio.url) && Constants.playedPausedRecord.isPlayed == true) {
                play_pause_music.setChecked(true);
                try {
                    Constants.tempThread.interrupt();
                } catch (NullPointerException e) {

                }
                AudioPlayer.progressBar(progressBar).start();
                Constants.tempThread = AudioPlayer.progressBar(progressBar);
                Constants.previousCheckBoxState = play_pause_music;
                Constants.previousSeekBarState = progressBar;
                progressBar.setVisibility(View.VISIBLE);
            }
            if (Constants.playedPausedRecord.audioUrl != null && Constants.playedPausedRecord.audioUrl.equals(audio.url) && Constants.playedPausedRecord.isPaused == true) {

                AudioPlayer.progressBar(progressBar).start();
                try {
                    Constants.tempThread.interrupt();
                } catch (NullPointerException e) {
                }
                progressBar.setVisibility(View.VISIBLE);
                Constants.tempThread = AudioPlayer.progressBar(progressBar);
            }


            ((TextView) tempAudioContainer.getChildAt(2)).setText(getMediaTime(audio.duration));
            ((TextView) tempAudioContainer.getChildAt(3)).setText(audio.artist);
            ((TextView) tempAudioContainer.getChildAt(4)).setText(audio.title);

            AudioPlayer.getOwnMediaPlayer(audio.url, play_pause_music, progressBar, audio.title, audio.artist);
            parent.addView(tempAudioContainer);
        }
    }

    public static void setDocs(LinearLayout parent, final ArrayList<VKApiDocument> docs) {
        ViewGroup tempDocumentContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (final VKApiDocument doc : docs) {
            tempDocumentContainer = (ViewGroup) inflater.inflate(R.layout.document_container, parent, false);
            tempDocumentContainer.setVisibility(View.VISIBLE);

            final ProgressBar spinner = (ProgressBar) tempDocumentContainer.getChildAt(0);
            final ImageView image = (ImageView) tempDocumentContainer.getChildAt(1);
            final TextView title = (TextView) tempDocumentContainer.getChildAt(2);
            final TextView size = (TextView) tempDocumentContainer.getChildAt(3);

            title.setText(doc.title);

            if (doc.isImage()) {
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(doc.photo_100, image);
                size.setText(Constants.DOC_TYPE_IMAGE + " " + readableFileSize(doc.size));
                tempDocumentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(doc.url);
                        context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.DOWNLOADER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            } else if (doc.isGif()) {
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(doc.photo_100, image);
                size.setText(Constants.DOC_TYPE_ANIMATION + " " + readableFileSize(doc.size));
                tempDocumentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image.setEnabled(true);
                        image.setClickable(true);
                        title.setVisibility(View.GONE);
                        size.setVisibility(View.GONE);
                        spinner.setVisibility(View.VISIBLE);
                        Ion.with(image).animateGif(true).load(doc.url);
                        image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        image.setScaleType(ImageView.ScaleType.MATRIX);
                        image.setAdjustViewBounds(true);
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                spinner.setVisibility(View.GONE);
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                image.setLayoutParams(new RelativeLayout.LayoutParams(setInDp(100), setInDp(60)));
                                ImageLoader.getInstance().displayImage(doc.photo_100, image);
                                title.setVisibility(View.VISIBLE);
                                size.setVisibility(View.VISIBLE);
                                image.setEnabled(false);
                                image.setClickable(false);
                            }
                        });
                    }
                });
            } else {
                image.setImageDrawable(Constants.RESOURCES.getDrawable(android.R.drawable.ic_menu_save));
                image.setBackgroundColor(Color.parseColor(postColor));
                image.setLayoutParams(new RelativeLayout.LayoutParams(setInDp(50), setInDp(50)));

                RelativeLayout.LayoutParams paramsForTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsForTitle.setMargins(setInDp(55), 0, 0, 0);
                title.setLayoutParams(paramsForTitle);

                RelativeLayout.LayoutParams paramsForSize = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsForSize.setMargins(setInDp(55), setInDp(20), 0, 0);
                size.setLayoutParams(paramsForSize);
                size.setText(Constants.DOC_TYPE_DOCUMENT + " " + readableFileSize(doc.size));
                tempDocumentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(doc.url);
                        context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.DOWNLOADER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }

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
                mediaContainer = getPreparedView(parent, R.layout.media_container);
                break;
            case 2:
                mediaContainer = getPreparedView(parent, R.layout.media_container2);
                break;
            case 3:
                mediaContainer = getPreparedView(parent, R.layout.media_container3);
                break;
            case 4:
                mediaContainer = getPreparedView(parent, R.layout.media_container4);
                break;
            case 5:
                mediaContainer = getPreparedView(parent, R.layout.media_container5);
                break;
            case 6:
                mediaContainer = getPreparedView(parent, R.layout.media_container6);
                break;
            case 7:
                mediaContainer = getPreparedView(parent, R.layout.media_container7);
                break;
            case 8:
                mediaContainer = getPreparedView(parent, R.layout.media_container8);
                break;
            case 9:
                mediaContainer = getPreparedView(parent, R.layout.media_container9);
                break;
            case 10:
                mediaContainer = getPreparedView(parent, R.layout.media_container10);
                break;
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
                } else {
                    if (photosCount > 1) {
                        int newWidth;
                        if (getScreenOrientation() == 1) {
                            newWidth = TIFApp.getDisplayWidth(); //this method should return the width of device screen.
                        } else {
                            newWidth = TIFApp.getDisplayHeight(); //this method should return the width of device screen.
                        }
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newWidth);
                        layout_i.setLayoutParams(params);
                    }
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
                            if (photosCount == 1 && videos.size() == 0) {
                                int newWidth;
                                if (getScreenOrientation() == 1) {
                                    newWidth = TIFApp.getDisplayWidth(); //this method should return the width of device screen.
                                } else {
                                    newWidth = TIFApp.getDisplayHeight(); //this method should return the width of device screen.
                                }
                                float scaleFactor = (float) newWidth / ((float) photos.get(finalJ).width);
                                int newHeight = (int) (photos.get(finalJ).height * scaleFactor);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
                                img.setLayoutParams(params);
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }

                            ImageLoader.getInstance().displayImage(photos.get(finalJ).photo_604, img);


                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    VKHelper.countOfPhotos = photosCount;
                                    makeSaveTransaction(photos, finalJ);
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

                                ImageLoader.getInstance().displayImage(photos.get(finalL).photo_604, img);

                                img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        VKHelper.countOfPhotos = photosCount;
                                        makeSaveTransaction(photos, finalL);
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
                } else {
                    if (videos.size() == 1 || videos.size() == 2 && photos.size() == 0) {
                        if (videos.size() == 1 || videos.size() == 2 && photos.size() == 0) {
                            int newWidth;
                            if (getScreenOrientation() == 1) {
                                newWidth = TIFApp.getDisplayWidth(); //this method should return the width of device screen.
                            } else {
                                newWidth = TIFApp.getDisplayHeight(); //this method should return the width of device screen.
                            }
                            float scaleFactor = (float) newWidth / 320;
                            int newHeight = (int) (240 * scaleFactor);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
                            layout_i.setLayoutParams(params);
                        }
                    } else if (photos.size() == 0 && videos.size() > 1) {
                        int newWidth;
                        if (getScreenOrientation() == 1) {
                            newWidth = TIFApp.getDisplayWidth(); //this method should return the width of device screen.
                        } else {
                            newWidth = TIFApp.getDisplayHeight(); //this method should return the width of device screen.
                        }
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newWidth);
                        layout_i.setLayoutParams(params);
                    }
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

                            final int finalJ = videoPointer++;

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            img.setLayoutParams(params);
                            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_320, img);

                            relativeLayout = (RelativeLayout) layout_i_j.getChildAt(k + 1);
                            relativeLayout.setVisibility(View.VISIBLE);
                            relativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Constants.toastInProgress.show();
                                }
                            });
                            ((TextView) relativeLayout.getChildAt(1)).setText(getMediaTime(videos.get(finalJ).duration));
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
                                    ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_130, img);

//                                    img.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            String videoID = videos.get(finalJ).toAttachmentString().toString();
//                                            videoID = videoID.replaceFirst("video", "");
//                                            Log.d("", videoID);
//                                            VKHelper.doPlayerRequest(videoID, new VKRequest.VKRequestListener() {
//                                                @Override
//                                                public void onComplete(final VKResponse response) {
//                                                    super.onComplete(response);
//                                                    JSONObject mainResponse = response.json.optJSONObject("response");
//                                                    JSONArray item = mainResponse.optJSONArray("items");
//                                                    try {
//                                                        videos.get(finalJ).player = ((JSONObject) item.get(0)).optString("player");
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    Toast.makeText(context, videos.get(finalJ).player, Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        }
//                                    });
                                    relativeLayout = (RelativeLayout) layout_i_j_k_l.getChildAt(1);
                                    relativeLayout.setVisibility(View.VISIBLE);
                                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Constants.toastInProgress.show();
                                        }
                                    });

                                    ((TextView) relativeLayout.getChildAt(1)).setText(getMediaTime(videos.get(finalJ).duration));
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


    static int g;
    static ArrayList<VKApiPhoto> finalPhotos;

    public static void makeSaveTransaction(final ArrayList<VKApiPhoto> photos, final int position) {

        if (OfflineMode.isOnline(Constants.mainActivity.getApplicationContext())) {
            VKHelper.getPhotoByID(photosKeyGen(photos), new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    //photosParam1=photosParam;
                    OfflineMode.saveJSON(response.json, photosKeyGen(photos));
                    finalPhotos = VKHelper.getPhotosByIdFromJSON(OfflineMode.loadJSON(photosKeyGen(photos)));
                    Fragment fragment = FragmentFullScreenViewer.newInstance(finalPhotos, position);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    OfflineMode.saveJSON(response.json, photosKeyGen(photos));
                }
                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    Fragment fragment = FragmentFullScreenViewer.newInstance(photos, position);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
        } else if(OfflineMode.isJsonNull(photosKeyGen(photos))) {
            finalPhotos = VKHelper.getPhotosByIdFromJSON(OfflineMode.loadJSON(photosKeyGen(photos)));
            Fragment fragment = FragmentFullScreenViewer.newInstance(finalPhotos, position);
            fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        } else {
            showAlertNoInternet(WallAdapter.wallAdapterView);
        }
    }
    private static String photosKeyGen(final ArrayList<VKApiPhoto> photos){
        String photosParam = "";
        for (g = 0; g < photos.size(); g++) {
            photosParam = photosParam.concat(photos.get(g).owner_id + "_" + photos.get(g).id + ",");
        }
        return photosParam;
    }


    static void showAlertNoInternet(final View view) {
        Toast.makeText(Constants.mainActivity.getApplicationContext(), context.getString(R.string.no_internet_retry), Toast.LENGTH_SHORT).show();
    }


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{Constants.SIZE_IN_B, Constants.SIZE_IN_KB, Constants.SIZE_IN_MB, Constants.SIZE_IN_GB, Constants.SIZE_IN_TB};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getMediaTime(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        if (hours > 1) {
            return format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return format("%02d:%02d", minutes, seconds);
        }
    }

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));
        smsTime.setTimeInMillis(smsTimeInMilis * 1000);

        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return String.format(Constants.TODAY, DateFormat.format(Constants.TIME_FORMAT_STRING, smsTime));
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return String.format(Constants.YESTERDAY, DateFormat.format(Constants.TIME_FORMAT_STRING, smsTime));
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(Constants.DATE_TIME_FORMAT_STRING, smsTime).toString();
        } else
            return DateFormat.format(Constants.OTHER_FORMAT_STRING, smsTime).toString();
    }

    public static int setInDp(int dps) {
        final float scale = Constants.RESOURCES.getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static int getScreenOrientation() {
        Display getOrient = Constants.mainActivity.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    public static String getPostColor(long groupIndex) {
        if (groupIndex == Constants.TF_ID) {
            return "#3DA2A9";
        } else if (groupIndex == Constants.TZ_ID) {
            return "#D5902FA7";
        } else if (groupIndex == Constants.FB_ID) {
            return "#1799CD";
        } else if (groupIndex == Constants.FN_ID) {
            return "#DE9C0E";
        } else {
            return "#84134800";
        }
    }

    public static int getPlayingLogo(long groupIndex) {
        if (groupIndex == Constants.TF_ID) {
            return R.drawable.tf_logo;
        } else if (groupIndex == Constants.TZ_ID) {
            return R.drawable.tz_logo;
        } else if (groupIndex == Constants.FB_ID) {
            return R.drawable.fb_logo;
        } else {
            return R.drawable.fn_logo;
        }
    }

    public static void saveUserId(long uid) {
        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        final long user_id = uid;
        final String long_key = "uid";
        ed.putLong(long_key, user_id);
        ed.commit();
    }

    public static void loadUserId() {
        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
        final String long_key = "uid";
        final long user_id = sPref.getLong(long_key, 0);
        Constants.USER_ID = user_id;
    }

    public static class NonUnderlinedClickableSpan extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.WHITE);
            ds.setUnderlineText(false); // set to false to remove underline
        }

        @Override
        public void onClick(View widget) {
        }
    }

//    private static int textOriginalHeight;
//
//    public static void expand(final View v, final SpannableStringBuilder text) {
//        int ANIMATION_DURATION = 500;//in milisecond
//        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        final int targtetHeight = v.getMeasuredHeight();
//        textOriginalHeight = v.getHeight();
//
//        v.getLayoutParams().height = textOriginalHeight;
//        v.setVisibility(View.VISIBLE);
//        Animation a = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1
//                        ? ViewGroup.LayoutParams.WRAP_CONTENT
//                        : textOriginalHeight + (int) (targtetHeight * interpolatedTime);
//                v.requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration(ANIMATION_DURATION);
//        a.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                ((TextView) v).setText(text);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        v.startAnimation(a);
//    }
//
//    public static void collapse(final View v, final SpannableStringBuilder text) {
//        final int initialHeight = v.getMeasuredHeight();
//        int ANIMATION_DURATION = 500;
//
//        final Animation a = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1 || initialHeight * (1 - interpolatedTime) <= textOriginalHeight
//                        ? ViewGroup.LayoutParams.WRAP_CONTENT
//                        : initialHeight - (int) (initialHeight * interpolatedTime);
//                if (initialHeight * (1 - interpolatedTime) <= textOriginalHeight) {
//                    ((TextView) v).setText(text);
//                }
//
//                v.requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration(ANIMATION_DURATION);
//        v.startAnimation(a);
//    }
}
