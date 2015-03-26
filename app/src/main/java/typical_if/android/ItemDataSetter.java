package typical_if.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.viewpagerindicator.CirclePageIndicator;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiCommunity;
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
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typical_if.android.adapter.AudioAdapter;
import typical_if.android.adapter.MediaPagerAdapter;
import typical_if.android.adapter.VotesItemAdapter;
import typical_if.android.fragment.FragmentFullScreenViewer;
import typical_if.android.fragment.FragmentPhotoList;
import typical_if.android.fragment.FragmentWithAttach;
import typical_if.android.fragment.PollFragment;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Created by admin on 05.08.2014.
 */
public class ItemDataSetter {
    public static boolean comments_visibility;

    public int sizeOfAlbum;

    public static Context context = TIFApp.getAppContext();
    public static LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    public static final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

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

    public static FragmentManager fragmentManager;


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

    public static void setAttachemnts(FragmentWithAttach fragment,
                                      VKAttachments attachments,
                                      RelativeLayout mediaLayout,
                                      ViewPager mediaPager,
                                      CirclePageIndicator mediaPagerIndicator,
                                      ImageButton mediaPagerVideoButton,
                                      LinearLayout audioLayout,
                                      ListView audioListView,
                                      LinearLayout documentLayout,
                                      LinearLayout albumLayout,
                                      RelativeLayout wikiPageLayout,
                                      RelativeLayout linkLayout,
                                      TextView linkSrc,
                                      TextView linkTitle,
                                      RelativeLayout pollLayout,
                                      TextView pollTitle
    ) {

        final ArrayList<VKApiPhoto> photos = new ArrayList<VKApiPhoto>();
        final ArrayList<VKApiVideo> videos = new ArrayList<VKApiVideo>();
        final ArrayList<VKApiAudio> audios = new ArrayList<VKApiAudio>();
        final ArrayList<VKApiDocument> docs = new ArrayList<VKApiDocument>();
        final ArrayList<VKApiPhotoAlbum> albums = new ArrayList<VKApiPhotoAlbum>();
        VKApiWikiPage wikiPage = null;
        VKApiLink link = null;
        VKApiPoll poll = null;

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

        if (photos.size() != 0 || videos.size() != 0) {
            mediaLayout.setTag(true);
            setMediaPager(fragment, mediaPager, mediaPagerIndicator, mediaPagerVideoButton, mediaLayout, photos, videos);
        } else {
            mediaLayout.setVisibility(View.GONE);
        }

        if (audios.size() != 0) {
            setAudios(audioLayout, audioListView, audios);
        } else {
            audioLayout.setVisibility(View.GONE);
        }

        if (docs.size() != 0) {
            setDocs(documentLayout, docs);
        } else {
            documentLayout.setVisibility(View.GONE);
        }

        if (albums.size() != 0) {
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
            setLink(linkLayout, linkSrc, linkTitle, link);
        } else {
            linkLayout.setVisibility(View.GONE);
        }

        if (poll != null) {

            /////////////////////////////////////////////////////////////////////////////
            setPoll(pollLayout, pollTitle, poll);
        } else {
            pollLayout.setVisibility(View.GONE);
        }
    }


    public static void setPoll(final RelativeLayout parent, final TextView title, final VKApiPoll poll) {

        final TextView answers_anonymous_text = ((TextView) parent.findViewById(R.id.answers_anonymous_text_preview));
        final String isAnonymous;
        final RelativeLayout go_to_poll = ((RelativeLayout) parent.findViewById(R.id.go_to_poll_details));


        title.setText(poll.question);

        if (poll.anonymous == 1)
            isAnonymous = TIFApp.getAppContext().getString(R.string.anonymous_poll);
        else
            isAnonymous = TIFApp.getAppContext().getString(R.string.public_poll);

        final String answers_anonymous_textStr = isAnonymous + " " + poll.votes;
        answers_anonymous_text.setText(answers_anonymous_textStr);
        parent.setVisibility(View.VISIBLE);
        go_to_poll.setVisibility(View.VISIBLE);
        go_to_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PollFragment();
                args.clear();
                args.putParcelable("poll", poll);
                args.putString("isAnonymous", isAnonymous);
                args.putString("answers_anonymous_text", answers_anonymous_textStr);
                fragment.setArguments(args);
                fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
        //
        //  if (Constants.isFragmentCommentsLoaded){
        //    Log.d("polls_are_loaded","Constants.isFragmentCommentsLoaded= "+Constants.isFragmentCommentsLoaded);
        //  initVotes (poll,list_of_polls);
        // }

    }

    public static void initVotes(final VKApiPoll poll, final ListView list_of_polls) {
        //  VotesItemAdapter adapter = new VotesItemAdapter(poll);
        //   list_of_polls.setAdapter(adapter);
        //   setListViewHeightBasedOnChildren(list_of_polls);


    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        BaseAdapter mAdapter;
        if (listView.getAdapter() instanceof VotesItemAdapter) {
            mAdapter = (VotesItemAdapter) listView.getAdapter();


            int totalHeight = 0;
            for (int i = 0; i < mAdapter.getCount(); i++) {
                View mView = mAdapter.getView(i, null, listView);
                mView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));

                mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalHeight += mView.getMeasuredHeight();
                Log.w("HEIGHT" + i, String.valueOf(totalHeight));

            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 50 + totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();


        }

    }


    static int startTag = 0;
    static int endTag = 0;
    static int startLink = 0;
    static int endLink = 0;
    static int startSite = 0;
    static int endSite = 0;
    static int startReply = 0;
    static int endReply = 0;

    private final static String linksPattern = "((?:(http|https|Http|Https|rtsp|Rtsp):" +
            "\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)" +
            "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_" +
            "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?" +
            "((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+" +
            "(?:" + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])" + "|(?:biz|b[abdefghijmnorstvwyz])" +
            "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])" + "|d[ejkmoz]" + "|(?:edu|e[cegrstu])" + "|f[ijkmor]" +
            "|(?:gov|g[abdefghilmnpqrstuwy])" + "|h[kmnrtu]" + "|(?:info|int|i[delmnoqrst])" +
            "|(?:jobs|j[emop])" + "|k[eghimnrwyz]" + "|l[abcikrstuvy]" + "|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])" +
            "|(?:name|net|n[acefgilopruz])" + "|(?:org|om)" + "|(?:pro|p[aefghklmnrstwy])" + "|qa" + "|r[eouw]" +
            "|s[abcdeghijklmnortuvyz]" + "|(?:tel|travel|t[cdfghjklmnoprtvwz])" +
            "|u[agkmsyz]" + "|v[aceginu]" + "|w[fs]" + "|y[etu]" + "|z[amw]))[^\\s]+))";

    public static SpannableStringBuilder getParsedText(String text) {
        final Matcher matTags = Pattern.compile("#\\w+").matcher(text);

        StringBuilder stringB = new StringBuilder(text);
        final SpannableStringBuilder spannable = new SpannableStringBuilder(text);


        while (matTags.find()) {
            startTag = stringB.indexOf(matTags.group());
            endTag = startTag + matTags.group().length();

            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), startTag, endTag, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), startTag, endTag, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            final String temp = matTags.group();

            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse("http://vk.com/feed?q=%23" + temp.replaceFirst("#", "") + "&section=search");
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, startTag, endTag, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        final Matcher matLinks = Pattern.compile(linksPattern).matcher(text);

        while (matLinks.find()) {
            startLink = stringB.indexOf(matLinks.group());
            endLink = startLink + matLinks.group().length();

            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), startLink, endLink, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), startLink, endLink, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextPaint t = new TextPaint();
            t.linkColor = Color.RED;
            t.setColor(Color.RED);
            spannable.setSpan(t, startLink, endLink, 0);
            final String temp = matLinks.group();

            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse(temp);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, startLink, endLink, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final Matcher matSite = Pattern.compile("@club26363301 \\(fromMobileIF\\)").matcher(text);
        while (matSite.find()) {
            startSite = stringB.indexOf(matSite.group());
            endSite = startSite + matSite.group().length();

            final String replier = "fromMobileIF";
            stringB.replace(startSite, endSite, replier);
            spannable.replace(startSite, endSite, replier);

            endSite = startSite + replier.length();
            spannable.setSpan(null, startSite, endSite, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), startSite, endSite, 0);
            spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), startSite, endSite, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse(replier);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, startSite, endSite, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final Matcher matReply = Pattern.compile("\\[(club|id)\\d+\\|[a-zA-ZА-Яа-яєЄіІїЇюЮйЙ 0-9(\\w)(\\W)_]+?\\]").matcher(text);
        while (matReply.find()) {
            startReply = stringB.indexOf(matReply.group());
            endReply = startReply + matReply.group().length();

            final String[] replier = matReply.group().replaceFirst("\\[", "").replaceFirst("\\]", "").split("\\|");
            stringB.replace(startReply, endReply, replier[1]);
            spannable.replace(startReply, endReply, replier[1]);

            endReply = startReply + replier[1].length();
            spannable.setSpan(null, startReply, endReply, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), startReply, endReply, 0);
            spannable.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), startReply, endReply, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse("http://vk.com/" + replier[0]);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, startReply, endReply, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static void setText(SpannableStringBuilder parsedText, TextView text, CheckBox showAll) {

        text.setText(parsedText, TextView.BufferType.SPANNABLE);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        if (!Constants.isFragmentCommentsLoaded & parsedText.length() > 300) {
            showAll.setVisibility(View.VISIBLE);
            showAll.setText(Constants.SHOW_ALL_TEXT);

            final SpannableStringBuilder tempSpannable = new SpannableStringBuilder();
            tempSpannable.append(parsedText);
            final SpannableStringBuilder tempModifySpannable = tempSpannable.insert(297, "...").delete(300, tempSpannable.length());
            text.setText(tempModifySpannable);

            TextParamsHolder textParamsHolder = new TextParamsHolder(parsedText, tempModifySpannable);
            text.setTag(textParamsHolder);

            showAll.setTag(text);
            showAll.setOnCheckedChangeListener(cbShowAllTextListener);
        } else {
            showAll.setVisibility(View.GONE);
        }
    }

    private static class TextParamsHolder {
        public final SpannableStringBuilder original;
        public final SpannableStringBuilder temp;

        private TextParamsHolder(SpannableStringBuilder original, SpannableStringBuilder temp) {
            this.original = original;
            this.temp = temp;
        }
    }

    public static final CompoundButton.OnCheckedChangeListener cbShowAllTextListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            TextView text = (TextView) buttonView.getTag();
            TextParamsHolder textParamsHolder = (TextParamsHolder) text.getTag();

            SpannableStringBuilder original = textParamsHolder.original;
            SpannableStringBuilder temp = textParamsHolder.temp;

            if (isChecked) {
                if (text.onPreDraw()) {
                    text.setText(original);
                }
                buttonView.setText(Constants.SHOW_MIN_TEXT);
            } else {
                text.setText(temp);
                buttonView.setText(Constants.SHOW_ALL_TEXT);
            }
        }
    };


    public static void setNameOfPostAuthor(ArrayList<VKApiUser> profiles, VKApiCommunity group, TextView textView, int id) {
        VKApiUser profile;
        String name = null;
        for (int i = 0; i < profiles.size(); i++) {
            profile = profiles.get(i);

            if (id == profile.id) {
                name = profile.last_name + " " + profile.first_name;
                textView.setTag("http://vk.com/id" + String.valueOf(id));
            }
        }
        if (id == 0) {
            name = group.name;
            textView.setTag("http://vk.com/" + group.screen_name);
        }

        textView.setText(name);
        textView.setOnClickListener(openActionViewChooserListener);
    }

    public static void
    setLink(RelativeLayout parent, TextView src, TextView title, final VKApiLink link) {
        parent.setVisibility(View.VISIBLE);

        title.setText(link.title);
        src.setText(link.url);

        parent.setTag(link.url);
        parent.setOnClickListener(openActionViewChooserListener);
    }

    public static void setGeo(VKApiPlace geo, ImageView imgGeo, TextView txtGeo, RelativeLayout geoLayout) {

        final String[] coordinates = geo.coordinates.split(" ");
        String url = "http://maps.google.com/maps/api/staticmap?center=" + coordinates[0] + "," + coordinates[1] + "&zoom=15&size=600x400&sensor=false";
        ImageLoader.getInstance().displayImage(url, imgGeo);

        txtGeo.setText(geo.title);

        geoLayout.setTag("geo:" + coordinates[0] + "," + coordinates[1]);
        geoLayout.setOnClickListener(openActionViewChooserListener);
    }

    public static final View.OnClickListener openActionViewChooserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse((String) v.getTag());
            context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    };

    public static void setWikiPage(RelativeLayout parent, final VKApiWikiPage wikiPage) {
        ViewGroup wikiPageContainer = getPreparedView(parent, R.layout.wiki_page_container);
        RelativeLayout childLayout = (RelativeLayout) wikiPageContainer.getChildAt(1);

        ((TextView) childLayout.getChildAt(0)).setText(wikiPage.title);

        wikiPageContainer.setTag(wikiPage.source);
        wikiPageContainer.setOnClickListener(openActionViewChooserListener);

        parent.addView(wikiPageContainer);
    }

    public static void setAlbums(LinearLayout parent, final ArrayList<VKApiPhotoAlbum> albums) {
        ViewGroup tempAlbumContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);

        for (final VKApiPhotoAlbum album : albums) {
            tempAlbumContainer = (ViewGroup) inflater.inflate(R.layout.album_container, parent, false);
            tempAlbumContainer.setVisibility(View.VISIBLE);

            int newWidth = TIFApp.getDisplayWidth();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newWidth);
            tempAlbumContainer.setLayoutParams(params);

            ImageView image = (ImageView) tempAlbumContainer.findViewById(R.id.img_album_thumb);
            ImageLoader.getInstance().displayImage(album.photo_604, image);
            ((TextView) tempAlbumContainer.getChildAt(1)).setText(valueOf(album.size));
            ((TextView) tempAlbumContainer.getChildAt(2)).setText(album.title);

            tempAlbumContainer.setTag(album);
            tempAlbumContainer.setOnClickListener(openAlbumClickListener);

            parent.addView(tempAlbumContainer);
        }
    }

    private static final View.OnClickListener openAlbumClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKApiPhotoAlbum album = (VKApiPhotoAlbum) v.getTag();

            Constants.ALBUM_ID = album.id;
            Constants.TEMP_OWNER_ID = album.owner_id;
            fragmentManager.beginTransaction().add(R.id.container, FragmentPhotoList.newInstance(1, album.size, album.title, album.photo.get(1).src)).addToBackStack(null).commit();
        }
    };

    public static void setAudios(LinearLayout parent, ListView audioListView, final ArrayList<VKApiAudio> audios) {
        parent.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (audios.size() * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics)));
        audioListView.setLayoutParams(params);

        AudioAdapter audioAdapter = new AudioAdapter(audios, context);
        audioListView.setAdapter(audioAdapter);
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
                tempDocumentContainer.setTag(doc.url);
                tempDocumentContainer.setOnClickListener(openActionViewChooserListener);
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
                                image.setLayoutParams(new RelativeLayout.LayoutParams(setInDp(60), setInDp(60)));
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
                image.setImageDrawable(Constants.RESOURCES.getDrawable(R.drawable.ic_attach_document));
                image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                size.setText(Constants.DOC_TYPE_DOCUMENT + " " + readableFileSize(doc.size));
                tempDocumentContainer.setTag(doc.url);
                tempDocumentContainer.setOnClickListener(openActionViewChooserListener);
            }

            parent.addView(tempDocumentContainer);
        }
    }

    public static void setMediaPager(FragmentWithAttach fragment, final ViewPager mediaPager, CirclePageIndicator mediaPagerIndicator, ImageButton mediaPagerVideoButton, RelativeLayout mediaLayout, ArrayList<VKApiPhoto> photos, ArrayList<VKApiVideo> videos) {
        int newWidth = TIFApp.getDisplayWidth();
        final int count = photos.size() + videos.size();

        mediaLayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params;

        if (count == 1) {
            int height;
            int width = TIFApp.getDisplayWidth();

            if (photos.size() == 1) {
                height = (int) Math.ceil(width * (float) photos.get(0).height / photos.get(0).width);
            } else {
                height = (int) Math.ceil(width * (float) 240 / 320);
            }

            params = new RelativeLayout.LayoutParams(width, height);
        } else {
            params = new RelativeLayout.LayoutParams(newWidth, newWidth);
        }

        mediaPager.setLayoutParams(params);

        MediaPagerAdapter mediaPagerAdapter = new MediaPagerAdapter(fragment, (Boolean) mediaLayout.getTag(), photos, videos);

        mediaPager.setOffscreenPageLimit(count);
        mediaPager.setAdapter(mediaPagerAdapter);

        if (videos.size() != 0) {
            mediaPagerVideoButton.setColorFilter(context.getResources().getColor(R.color.music_progress));
            mediaPagerVideoButton.setVisibility(View.VISIBLE);
            mediaPager.setTag(photos.size());
            mediaPagerVideoButton.setTag(mediaPager);
            mediaPagerVideoButton.setOnClickListener(ibOnCliclListener);
        } else {
            mediaPagerVideoButton.setVisibility(View.GONE);
        }

        final float density = context.getResources().getDisplayMetrics().density;

        mediaPagerIndicator.setViewPager(mediaPager);
        mediaPagerIndicator.setCentered(true);
        mediaPagerIndicator.setRadius(5 * density);
        mediaPagerIndicator.setGapWidth(6 * density);
        mediaPagerIndicator.setFillColor(context.getResources().getColor(R.color.music_progress));
        mediaPagerIndicator.setStrokeColor(context.getResources().getColor(R.color.music_progress_alt));
        mediaPagerIndicator.setStrokeWidth(density);

        ((RelativeLayout) mediaPagerIndicator.getParent()).setVisibility(count == 1 ? View.GONE : View.VISIBLE);
    }

    public static final ImageButton.OnClickListener ibOnCliclListener = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewPager viewPager = (ViewPager) v.getTag();
            viewPager.setCurrentItem((Integer) viewPager.getTag(), true);
        }
    };

    static int g;
    static ArrayList<VKApiPhoto> finalPhotos;
    final private static Bundle args = new Bundle();

    public static void makeSaveTransaction(final ArrayList<VKApiPhoto> photos, final int position) {

        if (OfflineMode.isOnline(TIFApp.getAppContext())) {
            VKHelper.getPhotoByID(photosKeyGen(photos), new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    OfflineMode.saveJSON(response.json, photosKeyGen(photos));
                    finalPhotos = VKHelper.getPhotosByIdFromJSON(OfflineMode.loadJSON(photosKeyGen(photos)));
//                    Fragment fragment = new FragmentFullScreenViewer(finalPhotos, position, 0);
                    Fragment fragment = new FragmentFullScreenViewer();
                    args.clear();
                    args.putSerializable("finalPhotos", finalPhotos);
                    args.putInt("position", position);
                    args.putInt("sizeOfAlbum", 0);
                    fragment.setArguments(args);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    OfflineMode.saveJSON(response.json, photosKeyGen(photos));
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    Fragment fragment = new FragmentFullScreenViewer();
                    args.clear();
                    args.putSerializable("finalPhotos", photos);
                    args.putInt("position", position);
                    args.putInt("sizeOfAlbum", 0);
                    fragment.setArguments(args);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
        } else if (OfflineMode.isJsonNull(photosKeyGen(photos))) {
            finalPhotos = VKHelper.getPhotosByIdFromJSON(OfflineMode.loadJSON(photosKeyGen(photos)));
            Fragment fragment = new FragmentFullScreenViewer();
            args.clear();
            args.putSerializable("finalPhotos", photos);
            args.putInt("position", position);
            args.putInt("sizeOfAlbum", 0);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        }
    }

    private static String photosKeyGen(final ArrayList<VKApiPhoto> photos) {
        String photosParam = "";
        for (g = 0; g < photos.size(); g++) {
            photosParam = photosParam.concat(photos.get(g).owner_id + "_" + photos.get(g).id + ",");
        }
        return photosParam;
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

    public static boolean isToday(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));
        smsTime.setTimeInMillis(smsTimeInMilis * 1000);

        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

        return now.get(Calendar.DATE) == smsTime.get(Calendar.DATE);
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

    public static boolean checkNewPostResult(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));
        smsTime.setTimeInMillis(smsTimeInMilis * 1000);

        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return true;
        } else return false;
    }

    public static int setInDp(int dps) {
        final float scale = Constants.RESOURCES.getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

//    public static int getScreenOrientation() {
//        Display getOrient = Constants.mainActivity.getWindowManager().getDefaultDisplay();
//        int orientation = Configuration.ORIENTATION_UNDEFINED;
//        if (getOrient.getWidth() == getOrient.getHeight()) {
//            orientation = Configuration.ORIENTATION_SQUARE;
//        } else {
//            if (getOrient.getWidth() < getOrient.getHeight()) {
//                orientation = Configuration.ORIENTATION_PORTRAIT;
//            } else {
//                orientation = Configuration.ORIENTATION_LANDSCAPE;
//            }
//        }
//        return orientation;
//    }

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

    public static void saveUserLanguage(int id, String lan) {

        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        final int key = id;
        final String value = lan;
        ed.putString(String.valueOf(key), value);
        ed.commit();
        Locale locale = new Locale(value);
        Locale.setDefault(locale);
        final Resources res = TIFApp.getAppContext().getResources();
        final Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, null);


    }


    public static Locale loadUserLanguage() {
        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
        final String key = "user_lan";
        final String app_lan = sPref.getString(String.valueOf(0), "");
        Constants.USER_LANGUAGE = app_lan;
        Locale locale = new Locale(app_lan);
        Locale.setDefault(locale);
        Constants.LOCALE.setDefault(locale);
        return locale;
    }

    public static String getUserLan() {
        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
        String lang = sPref.getString(String.valueOf(0), "");
        return lang;
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
}
