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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import typical_if.android.adapter.VoteItemAdapter;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.fragment.FragmentFullScreenViewer;
import typical_if.android.fragment.FragmentPhotoList;
import typical_if.android.util.VKPoll;

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

    public static void setAttachemnts(VKAttachments attachments,
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
            setMediaPager(mediaPager, mediaPagerIndicator, mediaPagerVideoButton, mediaLayout, photos, videos);
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

        if (poll != null ) {

            /////////////////////////////////////////////////////////////////////////////
            setPoll(pollLayout, pollTitle, poll);
        } else {
            pollLayout.setVisibility(View.GONE);
        }
    }

    public static void setPoll(final RelativeLayout parent, final TextView title, final VKApiPoll poll) {

        final TextView answers_anonymous_text = ((TextView) parent.findViewById(R.id.answers_anonymous_text));
        final String isAnonymous;


        title.setText(poll.question);

        if (poll.anonymous == 1) {
            isAnonymous = Constants.mainActivity.getResources().getString(R.string.anonymous_poll);
        } else
            isAnonymous = Constants.mainActivity.getResources().getString(R.string.public_poll);


        answers_anonymous_text.setText(isAnonymous + " " + poll.votes);
        parent.setVisibility(View.VISIBLE);





//
//            VKHelper.getPollById(poll.owner_id, 0, poll.id, new VKRequest.VKRequestListener() {
//                @Override
//                public void onComplete(VKResponse response) {
//                        super.onComplete(response);
//                     if (poll.answer_id==0){
//                        fillPollLayout (poll, response,false);
//                    }else {
//                        fillPollLayout (poll, response,true);
//
//                    }
//                }
//



//                    final String isAnonymous;
//
//                    OfflineMode.saveJSON(response.json, poll.owner_id + poll.id);
//                    VKPoll detailPoll = new VKPoll().parse(OfflineMode.loadJSON(poll.owner_id + poll.id));
//                    title.setText(detailPoll.question);
//                    if (detailPoll.anonymous == 1) {
//                        isAnonymous = Constants.mainActivity.getResources().getString(R.string.anonymous_poll);
//                    } else
//                        isAnonymous = Constants.mainActivity.getResources().getString(R.string.public_poll);
//                    answers_anonymous_text.setText(isAnonymous + " " + detailPoll.votes);
//
//                  //  View v = inflater.inflate(R.layout.vote_item_layout, parent);
//
//                    ListView pollList = (ListView) parent.findViewById(R.id.listOfVotes);
//
//                    VoteItemAdapter adapter = new VoteItemAdapter(pollList, detailPoll,detailPoll.answers, context, user_answered);
//                    pollList.setAdapter(adapter);
//                    setListViewHeightBasedOnChildren(pollList);
//
//                     pollList.setVisibility(View.VISIBLE);



            //    }
        //    });

      //  }
        //else {
           // parent.setVisibility(View.GONE);
       //     ListView pollList = (ListView) parent.findViewById(R.id.listOfVotes);
       //     pollList.setVisibility(View.GONE);
}

    static VKPoll detailPoll;
    static boolean user_answered;

    public static void fillPollLayout(final VKApiPoll poll,final View parent) {
        final View listFooterView = inflater.inflate( R.layout.list_of_votes_footer_view, null);
        final ListView pollList = (ListView)  parent.findViewById(R.id.listOfVotes);
        pollList.setVisibility(View.VISIBLE);

        final RelativeLayout spinner=((RelativeLayout) parent.findViewById(R.id.changeVotesSpinnerLayout));


        RelativeLayout votesParentLayout = ((RelativeLayout) parent.findViewById(R.id.votesParentLayout));
        votesParentLayout.setVisibility(View.VISIBLE);

        VKHelper.getPollById(poll.owner_id, 0, poll.id, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
               // OfflineMode.saveJSON(response.json, poll.owner_id + poll.id);
                detailPoll = new VKPoll().parse(response.json);
                boolean user_answered;
                if (poll.answer_id==0){
                    user_answered=false;
                }else {
                    user_answered=true;
                }
                ItemDataSetter.user_answered=user_answered;


                final VoteItemAdapter adapter = new VoteItemAdapter(pollList, detailPoll ,detailPoll.answers, context, user_answered, parent);
                pollList.setAdapter(adapter);
                setListViewHeightBasedOnChildren(pollList, adapter);
                pollList.invalidateViews();
                adapter.notifyDataSetChanged();
                pollList.invalidateViews();



                TextView changeDecision = ((TextView) listFooterView.findViewById(R.id.change_my_decision_tw));
                changeDecision.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        VKHelper.deleteVote(poll.owner_id, poll.id, poll.answer_id, 0, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Log.d("VOTE_DELETED", response.json.toString());
                                 poll.answer_id=0;
                                for (int i =0 ; i<pollList.getAdapter().getCount(); i++){
                                    adapter.showProgress(poll.answers.get(i));
                                }

                                 adapter.notifyDataSetChanged();
                             }
                        });
                    }
                });


              if (VoteItemAdapter.pos==pollList.getAdapter().getCount()){
                  spinner.setLayoutParams(new RelativeLayout.LayoutParams(pollList.getWidth(),pollList.getHeight()*pollList.getAdapter().getCount()));
                  spinner.setVisibility(View.VISIBLE);
              }

            }


        });
 }

    public static void refreshList (View parent, ListView pollList){
        pollList.setAdapter(null);
        VoteItemAdapter adapter = new VoteItemAdapter(pollList, detailPoll,detailPoll.answers, context, user_answered, parent);
        pollList.setAdapter(adapter);
        pollList.invalidateViews();
        adapter.notifyDataSetChanged();
        pollList.invalidateViews();

    }


    public static void setListViewHeightBasedOnChildren(ListView listView,   VoteItemAdapter mAdapter) {

        //VoteItemAdapter mAdapter = ((VoteItemAdapter)(HeaderViewListAdapter)listView.getAdapter();

        int totalHeight = 0;


        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i,null, listView);

//            mView.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }





    static int startTag = 0;
    static int endTag = 0;
    static int startLink = 0;
    static int endLink = 0;
    static int startSite = 0;
    static int endSite = 0;
    static int startReply = 0;
    static int endReply = 0;

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
        ).matcher(text);

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

        if (parsedText.length() > 300) {
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
            fragmentManager.beginTransaction().add(R.id.container, FragmentPhotoList.newInstance(1, album.size)).addToBackStack(null).commit();
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

    public static void setMediaPager(final ViewPager mediaPager, CirclePageIndicator mediaPagerIndicator, ImageButton mediaPagerVideoButton, RelativeLayout mediaLayout, ArrayList<VKApiPhoto> photos, ArrayList<VKApiVideo> videos) {
        int newWidth = TIFApp.getDisplayWidth();
        final int count = photos.size() + videos.size();

        mediaLayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newWidth);
        mediaPager.setLayoutParams(params);

        MediaPagerAdapter mediaPagerAdapter = new MediaPagerAdapter(context, photos, videos);

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

//    if (!(layout_i instanceof LinearLayout)) {
//        continue;
//    } else {
//        if (photosCount > 1) {
//            int newWidth = TIFApp.getDisplayWidth();
//
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newWidth);
//            layout_i.setLayoutParams(params);
//        }
//    }
//    layout_i.setVisibility(View.VISIBLE);
//
//    linearBreak:
//            for (int j = 0, photoPointer = 0; j < photos.size(); j++) {
//        final ViewGroup layout_i_j = (ViewGroup) layout_i.getChildAt(j);
//        if (!(layout_i_j instanceof RelativeLayout)) {
//            continue;
//        }
//        final int kMax = layout_i_j.getChildCount();
//        for (int k = 0; k < kMax; k++) {
//            final View view_i_j_k = layout_i_j.getChildAt(k);
//            if (view_i_j_k instanceof ImageView) {
//                img = (ImageView) view_i_j_k;
//                final int finalJ = photoPointer++;
//                if (photosCount == 1 && videos.size() == 0) {
//                    int newWidth = TIFApp.getDisplayWidth(); //this method should return the width of device screen.
//
//                    float scaleFactor = (float) newWidth / ((float) photos.get(finalJ).width);
//                    int newHeight = (int) (photos.get(finalJ).height * scaleFactor);
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
//                    img.setLayoutParams(params);
//                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                }
//
//                ImageLoader.getInstance().displayImage(photos.get(finalJ).photo_604, img);
//
//
//                img.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        VKHelper.countOfPhotos = photosCount;
//                        makeSaveTransaction(photos, finalJ);
//                    }
//                });
//                if (photoPointer == photos.size()) {
//                    lastPositionJ = j + 1;
//                    lastPositionK = k;
//                    break;
//                }
//            } else if (view_i_j_k instanceof LinearLayout) {
//                final ViewGroup layout_i_j_k = (LinearLayout) view_i_j_k;
//                final int lMax = layout_i_j_k.getChildCount();
//                for (int l = 0; l < lMax; l++) {
//                    final ViewGroup layout_i_j_k_l = (ViewGroup) layout_i_j_k.getChildAt(l);
//                    if (photoPointer == photos.size()) {
//                        lastPositionJ = j;
//                        lastPositionL = l;
//                        lastPositionK = k;
//                        break linearBreak;
//                    }
//
//                    img = (ImageView) layout_i_j_k_l.getChildAt(0);
//                    final int finalL = photoPointer++;
//
//                    ImageLoader.getInstance().displayImage(photos.get(finalL).photo_604, img);
//
//                    img.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            VKHelper.countOfPhotos = photosCount;
//                            makeSaveTransaction(photos, finalL);
//                        }
//                    });
//                }
//            }
//        }
//    }
//}
//}
//
//        if (videos != null) {
//final int videosCount = videos.size();
//        for (int i = 0; i < videosCount; i++) {
//final ViewGroup layout_i = (ViewGroup) mediaContainer.getChildAt(i);
//        if (!(layout_i instanceof LinearLayout)) {
//        continue;
//        } else {
//        if (videos.size() == 1 || videos.size() == 2 && photos.size() == 0) {
//        int newWidth = TIFApp.getDisplayWidth();
//
//        float scaleFactor = (float) newWidth / 320;
//        int newHeight = (int) (240 * scaleFactor);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
//        layout_i.setLayoutParams(params);
//        } else if (photos.size() == 0 && videos.size() > 1) {
//        int newWidth = TIFApp.getDisplayWidth();
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newWidth);
//        layout_i.setLayoutParams(params);
//        }
//        }
//        layout_i.setVisibility(View.VISIBLE);
//final int jMax = layout_i.getChildCount();
//        for (int j = lastPositionJ, videoPointer = 0; j < jMax; j++) {
//final ViewGroup layout_i_j = (ViewGroup) layout_i.getChildAt(j);
//        if (!(layout_i_j instanceof RelativeLayout)) {
//        continue;
//        }
//final int kMax = layout_i_j.getChildCount();
//        for (int k = lastPositionK; k < kMax; k++) {
//final View view_i_j_k = layout_i_j.getChildAt(k);
//        if (view_i_j_k instanceof ImageView) {
//        if (videoPointer == videosCount) {
//        break;
//        }
//        img = (ImageView) view_i_j_k;
//
//final int finalJ = videoPointer++;
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        img.setLayoutParams(params);
//        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_320, img);
//
//        relativeLayout = (RelativeLayout) layout_i_j.getChildAt(k + 1);
//        relativeLayout.setVisibility(View.VISIBLE);
//
//        try {
//        Constants.files = new JSONObject("{\n" +
//        "\n" +
//        "   \"response\": {\n" +
//        "\n" +
//        "      \"count\": 1,\n" +
//        "\n" +
//        "      \"items\": [\n" +
//        "\n" +
//        "         {\n" +
//        "\n" +
//        "            \"id\": 170595910,\n" +
//        "\n" +
//        "            \"owner_id\": 106880118,\n" +
//        "\n" +
//        "            \"title\": \"Hans Zimmer - Interstellar Main Theme Piano Cover\",\n" +
//        "\n" +
//        "            \"duration\": 281,\n" +
//        "\n" +
//        "            \"description\": \"\",\n" +
//        "\n" +
//        "            \"date\": 1416238922,\n" +
//        "\n" +
//        "            \"views\": 2185,\n" +
//        "\n" +
//        "            \"comments\": 12,\n" +
//        "\n" +
//        "            \"photo_130\": \"https://pp.vk.me/c634005/u106880118/video/s_47bc0893.jpg\",\n" +
//        "\n" +
//        "            \"photo_320\": \"https://pp.vk.me/c634005/u106880118/video/l_53618910.jpg\",\n" +
//        "\n" +
//        "            \"files\": {\n" +
//        "\n" +
//        "               \"mp4_240\": \"http://cs634005v4.vk.me/u106880118/videos/b892209e1d.240.mp4?extra=cN3FmRT76KMgP631XZmgnsaoYN3BTo2mLVM7-v3J-s5M2V5GxdeKZw4zXWh910VoAjRwlna7MigJcXLmF3VREPx6u2UF2UQ\",\n" +
//        "\n" +
//        "               \"mp4_360\":\"http://cs634005v4.vk.me/u106880118/videos/b892209e1d.360.mp4?extra=cN3FmRT76KMgP631XZmgnsaoYN3BTo2mLVM7-v3J-s5M2V5GxdeKZw4zXWh910VoAjRwlna7MigJcXLmF3VREPx6u2UF2UQ\",\n" +
//        "\n" +
//        "               \"mp4_480\": \"http://cs634005v4.vk.me/u106880118/videos/b892209e1d.480.mp4?extra=cN3FmRT76KMgP631XZmgnsaoYN3BTo2mLVM7-v3J-s5M2V5GxdeKZw4zXWh910VoAjRwlna7MigJcXLmF3VREPx6u2UF2UQ\",\n" +
//        "\n" +
//        "               \"mp4_720\": \"http://cs634005v4.vk.me/u106880118/videos/b892209e1d.720.mp4?extra=cN3FmRT76KMgP631XZmgnsaoYN3BTo2mLVM7-v3J-s5M2V5GxdeKZw4zXWh910VoAjRwlna7MigJcXLmF3VREPx6u2UF2UQ\"\n" +
//        "\n" +
//        "            },\n" +
//        "\n" +
//        "            \"player\": \"http://vk.com/video_ext.php?oid=106880118&id=170595910&hash=4cce98c2eea10294&api_hash=1422805710101e694253c964274f\",\n" +
//        "\n" +
//        "            \"can_comment\": 1,\n" +
//        "\n" +
//        "            \"can_repost\": 1,\n" +
//        "\n" +
//        "            \"likes\": {\n" +
//        "\n" +
//        "               \"user_likes\": 0,\n" +
//        "\n" +
//        "               \"count\": 298\n" +
//        "\n" +
//        "            },\n" +
//        "\n" +
//        "            \"repeat\": 0\n" +
//        "\n" +
//        "         }\n" +
//        "\n" +
//        "      ],\n" +
//        "\n" +
//        "      \"profiles\": [\n" +
//        "\n" +
//        "         {\n" +
//        "\n" +
//        "            \"id\": 106880118,\n" +
//        "\n" +
//        "            \"first_name\": \"Халим\",\n" +
//        "\n" +
//        "            \"last_name\": \"Атамурадов\"\n" +
//        "\n" +
//        "         }\n" +
//        "\n" +
//        "      ],\n" +
//        "\n" +
//        "      \"groups\": []\n" +
//        "\n" +
//        "   }\n" +
//        "\n" +
//        "}");
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        String key = (videos.get(finalJ).owner_id+"_"+videos.get(finalJ).id+"");
//
//        VKHelper.getVideoPlay(key, new VKRequest.VKRequestListener() {
//@Override
//public void onComplete(VKResponse response) {
//        super.onComplete(response);
//        // Constants.toastInProgress.show();
//        Log.d(response.json.toString(),"VIDEO_FILE");
//        try {
//        VKHelper.getVideoSourceFromJson(Constants.files);
//        //Fragment fragment = new FragmentWebView(url);
//
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//        }
//        });
//        }
//        });
//        ((TextView) relativeLayout.getChildAt(1)).setText(getMediaTime(videos.get(finalJ).duration));
//        ((TextView) relativeLayout.getChildAt(2)).setText(videos.get(finalJ).title);
//        } else if (view_i_j_k instanceof LinearLayout) {
//final ViewGroup layout_i_j_k = (LinearLayout) view_i_j_k;
//final int lMax = layout_i_j_k.getChildCount();
//        for (int l = lastPositionL; l < lMax; l++) {
//final ViewGroup layout_i_j_k_l = (ViewGroup) layout_i_j_k.getChildAt(l);
//        lastPositionL = 0;
//        if (layout_i_j_k_l instanceof RelativeLayout) {
//        if (videoPointer == videosCount) {
//        break;
//        }
//final int finalJ = videoPointer++;
//        img = (ImageView) layout_i_j_k_l.getChildAt(0);
//        ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_130, img);
//
//        relativeLayout = (RelativeLayout) layout_i_j_k_l.getChildAt(1);
//        relativeLayout.setVisibility(View.VISIBLE);
//
//        relativeLayout.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        String key = (videos.get(finalJ).owner_id+"_"+videos.get(finalJ).id+"");
//
//
//
//        VKHelper.getVideoPlay(key, new VKRequest.VKRequestListener() {
//@Override
//public void onComplete(VKResponse response) {
//        super.onComplete(response);
//        try {
//        VKHelper.getVideoSourceFromJson(Constants.files);
//
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//
//
//
//
//        }
//        });
//        }
//        });
//        ((TextView) relativeLayout.getChildAt(1)).setText(getMediaTime(videos.get(finalJ).duration));
//        ((TextView) relativeLayout.getChildAt(2)).setText(videos.get(finalJ).title);
//        }
//        }
//        }
//        }
//        }
//        }
//        }
//        parent.addView(mediaContainer);
//    


    static int g;
    static ArrayList<VKApiPhoto> finalPhotos;

    public static void makeSaveTransaction(final ArrayList<VKApiPhoto> photos, final int position) {

        if (OfflineMode.isOnline(Constants.mainActivity.getApplicationContext())) {
            VKHelper.getPhotoByID(photosKeyGen(photos), new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    OfflineMode.saveJSON(response.json, photosKeyGen(photos));
                    finalPhotos = VKHelper.getPhotosByIdFromJSON(OfflineMode.loadJSON(photosKeyGen(photos)));
                    Fragment fragment = new FragmentFullScreenViewer(finalPhotos, position, 0);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    OfflineMode.saveJSON(response.json, photosKeyGen(photos));
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    Fragment fragment = new FragmentFullScreenViewer(photos, position, 0);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
        } else if (OfflineMode.isJsonNull(photosKeyGen(photos))) {
            finalPhotos = VKHelper.getPhotosByIdFromJSON(OfflineMode.loadJSON(photosKeyGen(photos)));
            Fragment fragment = new FragmentFullScreenViewer(finalPhotos, position, 0);
            fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        } else {
            showAlertNoInternet(WallAdapter.wallAdapterView);
        }
    }

    private static String photosKeyGen(final ArrayList<VKApiPhoto> photos) {
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
        final Resources res = Constants.mainActivity.getResources();
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
