package typical_if.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKApiPlace;
import com.vk.sdk.api.model.VKApiPoll;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKApiWikiPage;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typical_if.android.adapter.CommentsListAdapter;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.Profile;
import typical_if.android.model.Wall.Wall;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Created by admin on 05.08.2014.
 */
public class ItemDataSetter {
    public static Context context = VKUIHelper.getApplicationContext();
    public static LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    public static void setAttachemnts(VKAttachments attachments, LinearLayout parentLayout, int type) {
        ArrayList<VKApiPhoto> photos = new ArrayList<VKApiPhoto>();
        ArrayList<VKApiVideo> videos = new ArrayList<VKApiVideo>();
        ArrayList<VKApiAudio> audios = new ArrayList<VKApiAudio>();
        ArrayList<VKApiDocument> docs = new ArrayList<VKApiDocument>();
        ArrayList<VKApiPhotoAlbum> albums = new ArrayList<VKApiPhotoAlbum>();
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
    }

    public static void setPoll(RelativeLayout parent, VKApiPoll poll) {
        ViewGroup pollContainer = getPreparedView(parent, R.layout.poll_container);

        ((TextView) pollContainer.getChildAt(0)).setText(poll.question);

        if (poll.anonymous == 0) {
            ((TextView) pollContainer.getChildAt(1)).setText(Constants.poll_not_anonymous + " " + poll.votes);
        } else {
            ((TextView) pollContainer.getChildAt(1)).setText(Constants.poll_anonymous + " " + poll.votes);
        }
        ((ImageView) pollContainer.getChildAt(2)).setBackgroundColor(Color.parseColor(postColor));

        parent.addView(pollContainer);
    }

    public static void setText(String text, RelativeLayout parent) {
        ViewGroup textContainer = getPreparedView(parent, R.layout.post_text_layout);

        final TextView mainText = ((TextView) textContainer.getChildAt(0));
        final CheckBox showAll = ((CheckBox) textContainer.getChildAt(1));

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
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.viewer_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                        .append("|z[amw]))")
                        .append("|(?:(?:25[0-5]|2[0-4]") // or ip address
                        .append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
                        .append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
                        .append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
                        .append("|[1-9][0-9]|[0-9])))")
                        .append("(?:\\:\\d{1,5})?)") // plus option port number
                        .append("(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")  // plus option query params
                        .append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
                        .append("(?:\\b|$)").toString()
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
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.browser_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        final Matcher matReply = Pattern.compile("\\[(club|id)\\d+\\|[a-zA-ZА-Яа-яєЄіІїЇюЮйЙ 0-9(\\W)]+?\\]").matcher(text);

        while (matReply.find()) {
            start = stringB.indexOf(matReply.group());
            end = start + matReply.group().length();

            final String[] replier = matReply.group().replaceAll("[\\[\\]]", "").split("\\|");
            stringB.replace(start, end, replier[1]);
            spannable.replace(start, end, replier[1]);

            end = start + replier[1].length();
            spannable.setSpan(new BackgroundColorSpan(Color.parseColor(postColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new NonUnderlinedClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Uri uri = Uri.parse("http://vk.com/" + replier[0]);
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.viewer_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

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
                        mainText.setText(originalSpannable);
                        showAll.setText(Constants.show_min_text);
                    } else {
                        showAll.setText(Constants.show_all_text);
                        mainText.setText(tempModifySpannable);
                    }
                }
            });
        } else {
            showAll.setVisibility(View.GONE);
        }

        parent.addView(textContainer);
    }

    public static void setSigned(final int id, RelativeLayout parent) {
        Profile profile;
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
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.viewer_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.browser_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.viewer_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    Toast.makeText(context, valueOf(album.id), Toast.LENGTH_SHORT).show();
                }
            });

            parent.addView(tempAlbumContainer);
        }
    }

    public static void setAudios(LinearLayout parent, final ArrayList<VKApiAudio> audios) {
        ViewGroup tempAudioContainer;
        parent.removeAllViews();
        parent.setVisibility(View.VISIBLE);
        for (VKApiAudio audio : audios) {
            tempAudioContainer = (ViewGroup) inflater.inflate(R.layout.audio_container, parent, false);
            tempAudioContainer.setVisibility(View.VISIBLE);

            tempAudioContainer.getChildAt(0).setBackgroundColor(Color.parseColor(postColor));
            ((TextView) tempAudioContainer.getChildAt(2)).setText(getMediaTime(audio.duration));
            ((TextView) tempAudioContainer.getChildAt(3)).setText(audio.artist);
            ((TextView) tempAudioContainer.getChildAt(4)).setText(audio.title);

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
                size.setText(Constants.docTypeImage + " " + readableFileSize(doc.size));
                tempDocumentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, doc.url, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (doc.isGif()) {
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(doc.photo_100, image);
                size.setText(Constants.docTypeAnimation + " " + readableFileSize(doc.size));
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
                image.setImageDrawable(Constants.resources.getDrawable(android.R.drawable.ic_menu_save));
                image.setBackgroundColor(Color.parseColor(postColor));
                image.setLayoutParams(new RelativeLayout.LayoutParams(setInDp(50), setInDp(50)));

                RelativeLayout.LayoutParams paramsForTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsForTitle.setMargins(setInDp(55), 0, 0, 0);
                title.setLayoutParams(paramsForTitle);

                RelativeLayout.LayoutParams paramsForSize = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsForSize.setMargins(setInDp(55), setInDp(20), 0, 0);
                size.setLayoutParams(paramsForSize);
                size.setText(Constants.docTypeDocument + " " + readableFileSize(doc.size));
                tempDocumentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(doc.url);
                        context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.downloader_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                                int newWidth = MyApplication.getDisplayWidth(); //this method should return the width of device screen.
                                float scaleFactor = (float) newWidth / ((float) photos.get(finalJ).width + 30);
                                int newHeight = (int) (photos.get(finalJ).height * scaleFactor);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
                                img.setLayoutParams(params);
                            }
                            ImageLoader.getInstance().displayImage(photos.get(finalJ).photo_604, img);
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context, photos.get(finalJ).src.getImageForDimension(photos.get(finalJ).width, photos.get(finalJ).height) + "", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(context, photos.get(finalL).src.getImageForDimension(photos.get(finalL).width, photos.get(finalL).height) + "", Toast.LENGTH_SHORT).show();
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
                                img.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 250));
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                            final int finalJ = videoPointer++;
                            ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_320, img);
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String videoID = videos.get(finalJ).toAttachmentString().toString();
                                    videoID = videoID.replaceFirst("video", "");
                                    VKHelper.doPlayerRequest(videoID, new VKRequest.VKRequestListener() {
                                        @Override
                                        public void onComplete(VKResponse response) {
                                            super.onComplete(response);
                                            JSONObject mainResponse = response.json.optJSONObject("response");
                                            JSONArray item = mainResponse.optJSONArray("items");
                                            try {
                                                videos.get(finalJ).player = ((JSONObject) item.get(0)).optString("player");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            Toast.makeText(context, videos.get(finalJ).player, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            relativeLayout = (RelativeLayout) layout_i_j.getChildAt(k + 1);
                            relativeLayout.setVisibility(View.VISIBLE);
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
                                    ImageLoader.getInstance().displayImage(videos.get(finalJ).photo_320, img);

                                    img.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String videoID = videos.get(finalJ).toAttachmentString().toString();
                                            videoID = videoID.replaceFirst("video", "");
                                            Log.d("", videoID);
                                            VKHelper.doPlayerRequest(videoID, new VKRequest.VKRequestListener() {
                                                @Override
                                                public void onComplete(VKResponse response) {
                                                    super.onComplete(response);
                                                    JSONObject mainResponse = response.json.optJSONObject("response");
                                                    JSONArray item = mainResponse.optJSONArray("items");
                                                    try {
                                                        videos.get(finalJ).player = ((JSONObject) item.get(0)).optString("player");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Toast.makeText(context, videos.get(finalJ).player, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                    relativeLayout = (RelativeLayout) layout_i_j_k_l.getChildAt(1);
                                    relativeLayout.setVisibility(View.VISIBLE);
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


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{Constants.size_in_b, Constants.size_in_kb, Constants.size_in_mb, Constants.size_in_gb, Constants.size_in_tb};
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
            return Constants.today + " " + DateFormat.format(Constants.timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return Constants.yesterday + " " + DateFormat.format(Constants.timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(Constants.dateTimeFormatString, smsTime).toString();
        } else
            return DateFormat.format(Constants.otherFormatString, smsTime).toString();
    }

    public static int setInDp(int dps) {
        final float scale = Constants.resources.getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static String getPostColor(long groupIndex) {
        if (groupIndex == Constants.TF_ID) {
            return "#3DA2A9";
        } else if (groupIndex == Constants.TZ_ID) {
            return "#D5902FA7";
        } else if (groupIndex == Constants.FB_ID) {
            return "#1799CD";
        } else {
            return "#DE9C0E";
        }
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