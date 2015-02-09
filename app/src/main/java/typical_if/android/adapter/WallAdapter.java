package typical_if.android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.devspark.robototextview.widget.RobotoTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.fragment.FragmentComments;
import typical_if.android.fragment.FragmentMakePost;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;
import typical_if.android.util.BitmapCache;
import typical_if.android.view.ResizableImageView;

import static com.vk.sdk.VKUIHelper.getApplicationContext;
import static com.vk.sdk.VKUIHelper.getTopActivity;
import static java.lang.String.valueOf;

public class WallAdapter extends BaseAdapter {
    private final Context mContext;
    private final BitmapCache mMemoryCache;
    private Wall wall;
    private final ArrayList<VKWallPostWrapper> posts;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public static FragmentManager fragmentManager;
    private static boolean isSuggested;
    static boolean flag;
    public static int surpriseCounter = 0;

    public ArrayList<EventObject> eventObjects;

    public WallAdapter(Wall wall, LayoutInflater inflater, FragmentManager fragmentManager, boolean isSuggested) {
        this.wall = wall;
        this.layoutInflater = inflater;
        this.context = TIFApp.getAppContext();
        this.fragmentManager = fragmentManager;
        this.posts = wall.posts;
        WallAdapter.isSuggested = isSuggested;
        mContext = Constants.mainActivity.getApplicationContext();
        mMemoryCache = new BitmapCache();
    }

    public WallAdapter(ArrayList<EventObject> eventObjects, Wall wall, LayoutInflater inflater, FragmentManager fragmentManager) {
        this.wall = wall;
        this.posts = wall.posts;
        this.layoutInflater = inflater;
        this.context = TIFApp.getAppContext();
        this.fragmentManager = fragmentManager;
        mContext = Constants.mainActivity.getApplicationContext();
        mMemoryCache = new BitmapCache();

        this.eventObjects = eventObjects;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
        this.posts.clear();
        this.posts.addAll(wall.posts);
        notifyDataSetChanged();
    }

    public void setEvent(ArrayList<EventObject> events) {
        this.eventObjects.clear();
        this.eventObjects.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        try {
            return Constants.GROUP_ID != Constants.ZF_ID ? posts.size() : eventObjects.size();
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), context.getString(R.string.error), Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return Constants.GROUP_ID != Constants.ZF_ID ? posts.get(position) : eventObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Constants.GROUP_ID != Constants.ZF_ID ? posts.get(position).id : position;
    }

    public static View wallAdapterView;

    public void setGradientColors(int topColor, View post) {
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]
                {Color.parseColor("#ffafa084"), Color.parseColor("#ff89a790"), topColor});
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setCornerRadius(0.f);
        int decode = Integer.decode("303030");
        ColorDrawable colorDrawable = new ColorDrawable(decode);
        post.setBackgroundDrawable(colorDrawable);

//        post.setBackgroundDrawable(gradient);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (Constants.GROUP_ID == Constants.ZF_ID) {
           EventViewHolder viewHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.event_item_layout, null, false);
//                LinearLayout postWrapper = (LinearLayout) convertView.findViewById(R.id.postParentLayout);
//                setGradientColors((Color.parseColor("#FF7C7A7E")), postWrapper);
                viewHolder = new EventViewHolder(convertView);
                convertView.setTag(viewHolder);


            } else {
                viewHolder = (EventViewHolder) convertView.getTag();
            }

            initEventViewHolder(viewHolder, (EventObject) getItem(position));
        } else {


            final VKWallPostWrapper post = posts.get(position);
         final ViewHolder viewHolder ;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.wall_lv_item, null, false);
                LinearLayout postWrapper = (LinearLayout) convertView.findViewById(R.id.postParentLayout);
                setGradientColors((Color.parseColor("#FF7C7A7E")), postWrapper);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            initViewHolder(viewHolder, wall, position, fragmentManager, post);
        }

        return convertView;
    }

    public final View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<VKApiPhoto> photos = (ArrayList<VKApiPhoto>) v.getTag();
            VKHelper.countOfPhotos = photos.size();
            ItemDataSetter.makeSaveTransaction(photos, 0);
        }
    };

    private static class CalendarData {
        public final String timeStart;
        public final String title;
        public final String location;

        private CalendarData(String timeStart, String title, String location) {
            this.timeStart = timeStart;
            this.title = title;
            this.location = location;
        }
    }

    public final View.OnClickListener flClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SwipeLayout eventSwipeLayout = (SwipeLayout) v.getTag();
            eventSwipeLayout.close(true);

            CalendarData calendarData = (CalendarData) eventSwipeLayout.getTag();
            pushAppointmentsToCalender(context, calendarData.title, calendarData.location, calendarData.timeStart);
        }
    };

    public final View.OnClickListener slClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((SwipeLayout) v.getTag()).toggle(true);
        }
    };

    public void pushAppointmentsToCalender(Context context, String title, String place, String startTime) {

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, context.getString(R.string.today_event_description))
                .putExtra(CalendarContract.Events.EVENT_LOCATION, place)
                .putExtra(CalendarContract.Events.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMC"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        long currentDate = 0;
        try {
            currentDate = dateFormat.parse(dateFormat.format(new Date())).getTime();
        } catch (ParseException e) {}

        long startDate;
        long endDate;

        try {
            startDate = currentDate + sdf.parse(startTime).getTime();
            endDate = startDate + 2000 * 60 * 60;
        } catch (ParseException e) {
            startDate = currentDate + 60 * 60 * 1000 * 24;
            endDate = startDate + 48 * 1000 * 60 * 60;

            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        }

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate);

        context.startActivity(intent);
    }

    public void initEventViewHolder(EventViewHolder viewHolder, EventObject item) {
        ItemDataSetter.fragmentManager = fragmentManager;

        if (!item.urlPhoto.get(0).photo_604.equals("fake_photo")) {
            ImageLoader.getInstance().displayImage(item.urlPhoto.get(0).photo_604, viewHolder.imgPhoto, TIFApp.eventOptions);

            viewHolder.btImgPhoto.setTag(item.urlPhoto);
            viewHolder.btImgPhoto.setOnClickListener(imgClickListener);
        } else {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.stub_null_event, viewHolder.imgPhoto, TIFApp.eventOptions);

            viewHolder.btImgPhoto.setTag(null);
            viewHolder.btImgPhoto.setOnClickListener(null);
        }

        LinearLayout tempLayout = null;

        for (int i = 0; i < Constants.EVENT_COUNT; i++) {
            switch (i) {
                case Constants.TODAY_EVENT:
                    tempLayout = viewHolder.fakeTodayListView;
                    break;
                case Constants.STATION_EVENT:
                    tempLayout = viewHolder.fakeStationListView;
                    break;
                case Constants.PERIOD_EVENT:
                    tempLayout = viewHolder.fakePeriodListView;
                    break;
            }

            tempLayout.removeAllViews();

            for (int j = 0; j < item.array.get(i).size(); j++) {
                View eventView = layoutInflater.inflate(R.layout.events_list_item_layout, null, false);

                RobotoTextView eventTextView = ((RobotoTextView) eventView.findViewById(R.id.txt_event_list_item));
                eventTextView.setText(item.array.get(i).get(j));

                SwipeLayout eventSwipeLayout = (SwipeLayout) eventView.findViewById(R.id.event_swipe_layout);

                if (ItemDataSetter.isToday(item.date)) {
                    if (item.array.get(i).get(j).equals(context.getString(R.string.null_events))) {
                        unsetSwipeLayout(eventSwipeLayout);
                    } else {
                        StringBuilder text = new StringBuilder(eventTextView.getText());
                        String startTime = "";
                        String title;
                        String location;

                        switch (i) {
                            case 0:
                            case 1:
                                startTime = text.substring(2, 7);

                                text = text.replace(0, 8, "");

                                break;
                            case 2:
                                startTime = "";

                                text = text.replace(0, 2, "");

                                break;
                        }

                        try {
                            title = text.substring(0, text.indexOf(","));
                            text = text.replace(0, text.indexOf(",") + 1, "");
                        } catch (StringIndexOutOfBoundsException e) {
                            title = text.substring(0, text.length());
                            text = text.replace(0, text.length(), "");
                        }

                        location = text.toString();

                        setSwipeLayout(eventSwipeLayout, new CalendarData(startTime, title, location));
                    }
                } else {
                    unsetSwipeLayout(eventSwipeLayout);
                }

                tempLayout.addView(eventView);
            }
        }
    }

    public void unsetSwipeLayout(SwipeLayout eventSwipeLayout) {
        eventSwipeLayout.getBottomView().setOnClickListener(null);
        eventSwipeLayout.getSurfaceView().setOnClickListener(null);
        eventSwipeLayout.setSwipeEnabled(false);
    }

    public void setSwipeLayout(SwipeLayout eventSwipeLayout, CalendarData calendarData) {
        eventSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        eventSwipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        eventSwipeLayout.getSurfaceView().setTag(eventSwipeLayout);
        eventSwipeLayout.getSurfaceView().setOnClickListener(slClickListener);

        eventSwipeLayout.setTag(calendarData);
        eventSwipeLayout.getBottomView().setTag(eventSwipeLayout);
        eventSwipeLayout.getBottomView().setOnClickListener(flClickListener);
    }

    public static final View.OnClickListener btLikeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (!OfflineMode.isIntNul("surprise")) {
                    surpriseCounter = OfflineMode.loadInt("surprise");
                } else {
                    surpriseCounter = 0;
                }
            } catch (Exception e) {
            }
            if (VKSdk.isLoggedIn()) {
                surpriseCounter++;
                OfflineMode.saveInt(surpriseCounter, "surprise");
            }
            try {
                if (OfflineMode.loadInt("surprise") == 15 && VKSdk.isLoggedIn()) {
                    ((MainActivity) getTopActivity()).addFragment(FragmentMakePost.newInstance(-77149556, 0, 0));
                }
            } catch (Exception e) {
                surpriseCounter = 0;
            }

            final ViewHolder viewHolder = (ViewHolder) v.getTag();
            final VKApiPost post = (VKApiPost) viewHolder.cb_post_like.getTag();

            if (!post.user_likes) {
                VKHelper.setLike("post", Constants.GROUP_ID, post.id, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        viewHolder.cb_post_like.setText(" " + String.valueOf(++post.likes_count));
                        viewHolder.cb_post_like.setChecked(true);
                        post.user_likes = true;
                    }
                });
            } else {

                VKHelper.deleteLike("post", Constants.GROUP_ID, post.id, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        viewHolder.cb_post_like.setText(" " + String.valueOf(--post.likes_count));
                        viewHolder.cb_post_like.setChecked(false);
                        post.user_likes = false;
                    }
                });
            }
        }
    };

    public static final View.OnClickListener btRepostOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = TIFApp.getAppContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            final ViewHolder viewHolder = (ViewHolder) v.getTag();
            final VKWallPostWrapper postWrapper = (VKWallPostWrapper) viewHolder.cb_post_repost.getTag();
            final VKApiPost post = postWrapper.post;

            try {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(Constants.mainActivity);

                View view = layoutInflater.inflate(R.layout.txt_dialog_comment, null);
                dialog.setView(view);
                dialog.setTitle(context.getString(R.string.comment_background));

                final EditText text = (EditText) view.findViewById(R.id.txt_dialog_comment);

                dialog.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String pidFull = "wall" + Constants.GROUP_ID + "_" + post.id;
                        VKHelper.doRepost(pidFull, text.getText().toString(), new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                JSONObject object = response.json.optJSONObject("response");
                                int isSuccessed = object.optInt("success");

                                if (isSuccessed == 1) {
                                    post.user_reposted = true;
                                    viewHolder.cb_post_repost.setChecked(true);
                                    viewHolder.cb_post_repost.setText(" " + String.valueOf(++post.reposts_count));

                                    if (!post.user_likes) {

                                        VKHelper.setLike("post", (postWrapper.groupId * (-1)), post.id, new VKRequest.VKRequestListener() {
                                            @Override
                                            public void onComplete(final VKResponse response) {
                                                super.onComplete(response);

                                                viewHolder.cb_post_like.setText(" " + String.valueOf(++post.likes_count));
                                                viewHolder.cb_post_like.setChecked(true);
                                                post.user_likes = true;

                                            }
                                        });
                                    }
                                    viewHolder.cb_post_repost.setChecked(true);
                                    viewHolder.cb_post_repost.setEnabled(false);
                                } else {
                                    viewHolder.cb_post_repost.setChecked(false);
                                }
                            }
                        });

                    }
                });

                dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.setCancelable(true);
                    }
                });
                dialog.create().show();

            } catch (NullPointerException npe) {
                Toast.makeText(getApplicationContext(), context.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    public static void initViewHolder(final ViewHolder viewHolder,
                                      final Wall wall,
                                      final int position,
                                      final FragmentManager fragmentManager,
                                      final VKWallPostWrapper postWrapper) {

        ItemDataSetter.fragmentManager = fragmentManager;

        final VKApiPost post = postWrapper.post;

        viewHolder.img_fixed_post.setVisibility(postWrapper.postPinnedVisibility);

        viewHolder.cb_post_comment.setText(" " + valueOf(post.comments_count));
        viewHolder.cb_post_like.setText(" " + valueOf(post.likes_count));
        viewHolder.cb_post_repost.setText(" " + String.valueOf(post.reposts_count));

        String s = String.valueOf(ItemDataSetter.getFormattedDate(post.date));
        if (s.contains("2014,")) {
            viewHolder.txt_post_date.setText(String.valueOf(s.replace(" 2014,", ",")));
        } else {
            viewHolder.txt_post_date.setText(ItemDataSetter.getFormattedDate(post.date));
        }

        ItemDataSetter.setNameOfPostAuthor(wall.profiles, wall.group, viewHolder.author_of_post, post.signer_id);

        if (post.user_likes) {
            viewHolder.cb_post_like.setChecked(true);
        } else {
            viewHolder.cb_post_like.setChecked(false);
        }
        viewHolder.cb_post_like.setTag(post);
        viewHolder.button_like.setTag(viewHolder);
        viewHolder.button_like.setOnClickListener(btLikeOnClickListener);

        if (post.user_reposted) {
            viewHolder.cb_post_repost.setChecked(true);
            viewHolder.cb_post_repost.setOnClickListener(null);
        } else {
            viewHolder.cb_post_repost.setChecked(false);
            if (VKSdk.isLoggedIn()) {
                postWrapper.groupId = wall.group.id;
                viewHolder.cb_post_repost.setTag(postWrapper);
                viewHolder.button_repost.setTag(viewHolder);
                viewHolder.button_repost.setOnClickListener(btRepostOnClickListener);
            }
        }

        viewHolder.postTextLayout.setVisibility(postWrapper.postTextVisibility);
        if (postWrapper.postTextChecker) {
            ItemDataSetter.setText(postWrapper.parsedPostText, viewHolder.txtPost, viewHolder.cbPostAllText);
        }

        viewHolder.copyHistoryLayout.setVisibility(postWrapper.copyHistoryContainerVisibility);
        if (postWrapper.copyHistoryChecker) {
            VKApiPost copyHistory = post.copy_history.get(0);

            viewHolder.copyHistoryHeader.setTag(postWrapper.copyHistoryUrl);
            viewHolder.copyHistoryHeader.setOnClickListener(ItemDataSetter.openActionViewChooserListener);
            viewHolder.txtCopyHistoryTitle.setText(postWrapper.copyHistoryTitle);
            viewHolder.txtCopyHistoryDate.setText(ItemDataSetter.getFormattedDate(copyHistory.date));
            ImageLoader.getInstance().displayImage(postWrapper.copyHistoryLogo, viewHolder.imgCopyHistoryLogo, TIFApp.additionalOptions);

            viewHolder.copyHistoryTextLayout.setVisibility(postWrapper.copyHistoryTextContainerVisibility);
            if (postWrapper.copyHistoryTextChecker) {
                ItemDataSetter.setText(postWrapper.parsedCopyHistoryText, viewHolder.copyHistoryTxtPost, viewHolder.copyHistoryCbPostAllText);
            }

            viewHolder.copyHistoryAttachmentsLayout.setVisibility(postWrapper.copyHistoryAttachmentsContainerVisibility);
            if (postWrapper.copyHistoryAttachmentsChecker) {
                ItemDataSetter.setAttachemnts(
                        copyHistory.attachments,
                        viewHolder.copyHistoryMediaLayout,
                        viewHolder.copyHistoryMediaPager,
                        viewHolder.copyHistoryMediaPagerIndicator,
                        viewHolder.copyHistoryMediaPagerVideoButton,
                        viewHolder.copyHistoryAudioLayout,
                        viewHolder.copyHistoryAudioListView,
                        viewHolder.copyHistoryDocumentLayout,
                        viewHolder.copyHistoryAlbumLayout,
                        viewHolder.copyHistoryWikiPageLayout,
                        viewHolder.copyHistoryLinkLayout,
                        viewHolder.copyHistoryTxtLinkSrc,
                        viewHolder.copyHistoryTxtLinkTitle,
                        viewHolder.copyHistoryPollLayout,
                        viewHolder.copyHistoryTxtPollTitle
                );
            }

            viewHolder.copyHistoryGeoLayout.setVisibility(postWrapper.copyHistoryGeoContainerVisibility);
            if (postWrapper.copyHistoryGeoChecker) {
                ItemDataSetter.setGeo(copyHistory.geo, viewHolder.copyHistoryImgGeo, viewHolder.copyHistoryTxtGeo, viewHolder.copyHistoryGeoLayout);
            }
        }

        viewHolder.postAttachmentsLayout.setVisibility(postWrapper.postAttachmentsVisibility);
        viewHolder.postMediaLayout.setVisibility(View.VISIBLE);
        if (postWrapper.postAttachmentsChecker) {
            ItemDataSetter.setAttachemnts(
                    post.attachments,
                    viewHolder.postMediaLayout,
                    viewHolder.postMediaPager,
                    viewHolder.postMediaPagerIndicator,
                    viewHolder.postMediaPagerVideoButton,
                    viewHolder.postAudioLayout,
                    viewHolder.postAudioListView,
                    viewHolder.postDocumentLayout,
                    viewHolder.postAlbumLayout,
                    viewHolder.postWikiPageLayout,
                    viewHolder.postLinkLayout,
                    viewHolder.txtLinkSrc,
                    viewHolder.txtLinkTitle,
                    viewHolder.postPollLayout,
                    viewHolder.txtPollTitle
            );
        }

        viewHolder.postGeoLayout.setVisibility(postWrapper.postGeoVisibility);
        if (postWrapper.postGeoChecker) {
            ItemDataSetter.setGeo(post.geo, viewHolder.imgGeo, viewHolder.txtGeo, viewHolder.postGeoLayout);
        }

        viewHolder.button_comment.setTag(new ParamsHolder(position, wall, postWrapper));
        if (OfflineMode.isOnline(getApplicationContext()) | OfflineMode.isJsonNull(post.id)) {
            viewHolder.button_comment.setOnClickListener(openCommentsFragmentListener);
        } else {
            viewHolder.button_comment.setOnClickListener(errorToastListener);
        }
    }

    public static final View.OnClickListener openCommentsFragmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            ParamsHolder paramsHolder = (ParamsHolder) v.getTag();


            FragmentComments fragment = FragmentComments.newInstanceForWall(paramsHolder.position, paramsHolder.wall, paramsHolder.post);

            fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        }
    };

    public static final View.OnClickListener errorToastListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), TIFApp.getAppContext().getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    };

    public static class ParamsHolder {
        public final int position;
        public final Wall wall;
        public final VKWallPostWrapper post;

        public ParamsHolder(int position, Wall wall, VKWallPostWrapper post) {
            this.position = position;
            this.wall = wall;
            this.post = post;
        }
    }

    public static class EventObject {
        public final SparseArray<List<String>> array;
        public final long date;
        public final ArrayList<VKApiPhoto> urlPhoto;

        public EventObject(SparseArray<List<String>> array, long date, ArrayList<VKApiPhoto> urlPhoto) {
            this.array = array;
            this.date = date;
            this.urlPhoto = urlPhoto;
        }
    }

    public static class EventViewHolder {
        public final ResizableImageView imgPhoto;
        public final Button btImgPhoto;
        public final LinearLayout fakeTodayListView;
        public final LinearLayout fakeStationListView;
        public final LinearLayout fakePeriodListView;

        EventViewHolder(View convertView) {
            this.btImgPhoto = (Button) convertView.findViewById(R.id.bt_img_event);
            this.imgPhoto = (ResizableImageView) convertView.findViewById(R.id.img_event_item);
            this.fakeTodayListView = (LinearLayout) convertView.findViewById(R.id.fake_lv_today_event_item);
            this.fakeStationListView = (LinearLayout) convertView.findViewById(R.id.fake_lv_yesterday_event_item);
            this.fakePeriodListView = (LinearLayout) convertView.findViewById(R.id.fake_lv_period_event_item);
        }
    }

    public static class ViewHolder {
        public int position;

        public final CardView postRootLayout;

        public final RelativeLayout postTextLayout;
        public final RelativeLayout postMediaLayout;
        public final LinearLayout postAudioLayout;
        public final RelativeLayout copyHistoryLayout;
        public final LinearLayout postAttachmentsLayout;
        public final LinearLayout postDocumentLayout;
        public final LinearLayout postAlbumLayout;
        public final RelativeLayout postGeoLayout;
        public final RelativeLayout postWikiPageLayout;
        public final RelativeLayout postLinkLayout;
        public final RelativeLayout postPollLayout;
        public final RelativeLayout postAuthorPanel;
        public final TextView postUserComment;
        public final RelativeLayout postFeatureLayout;
        public final CheckBox cb_post_like;
        public final CheckBox cb_post_repost;
        public final CheckBox cb_post_comment;
        public final Button button_like;
        public final Button button_repost;
        public final Button button_comment;
        public final TextView txt_post_date;
        public final ImageView img_fixed_post;
        public final TextView author_of_post;
        public final TextView txtPost;
        public final CheckBox cbPostAllText;
        public final ImageView imgWikiPage;
        public final TextView txtWikiPage;
        public final ImageView imgLink;
        public final TextView txtLinkTitle;
        public final TextView txtLinkSrc;
        public final ImageView imgPoll;
        public final TextView txtPollTitle;
        public final ImageView imgGeo;
        public final TextView txtGeo;
        public final ViewPager postMediaPager;
        public final CirclePageIndicator postMediaPagerIndicator;
        public final ListView postAudioListView;
        public final ImageButton postMediaPagerVideoButton;

        public final ImageView imgCopyHistoryLogo;
        public final TextView txtCopyHistoryDate;
        public final TextView txtCopyHistoryTitle;
        public final RelativeLayout copyHistoryHeader;
        public final RelativeLayout copyHistoryTextLayout;
        public final RelativeLayout copyHistoryMediaLayout;
        public final LinearLayout copyHistoryAudioLayout;
        public final LinearLayout copyHistoryAttachmentsLayout;
        public final LinearLayout copyHistoryDocumentLayout;
        public final LinearLayout copyHistoryAlbumLayout;
        public final RelativeLayout copyHistoryGeoLayout;
        public final RelativeLayout copyHistoryWikiPageLayout;
        public final RelativeLayout copyHistoryLinkLayout;
        public final RelativeLayout copyHistoryPollLayout;
        public final TextView copyHistoryTxtPost;
        public final CheckBox copyHistoryCbPostAllText;
        public final ImageView copyHistoryImgWikiPage;
        public final TextView copyHistoryTxtWikiPage;
        public final ImageView copyHistoryImgLink;
        public final TextView copyHistoryTxtLinkTitle;
        public final TextView copyHistoryTxtLinkSrc;
        public final ImageView copyHistoryImgPoll;
        public final TextView copyHistoryTxtPollTitle;
        public final ImageView copyHistoryImgGeo;
        public final TextView copyHistoryTxtGeo;
        public final ViewPager copyHistoryMediaPager;
        public final CirclePageIndicator copyHistoryMediaPagerIndicator;
        public final ListView copyHistoryAudioListView;
        public final ImageButton copyHistoryMediaPagerVideoButton;

        public ViewHolder(View convertView) {
            this.postRootLayout = (CardView) convertView.findViewById(R.id.card_view_wall_lv_item);

            this.postAttachmentsLayout = (LinearLayout) convertView.findViewById(R.id.postAttachmentsLayout);
            this.postTextLayout = (RelativeLayout) convertView.findViewById(R.id.postTextLayout);
            this.postMediaLayout = (RelativeLayout) convertView.findViewById(R.id.postMediaLayout);
            this.postAudioLayout = (LinearLayout) convertView.findViewById(R.id.postAudioLayout);
            this.postDocumentLayout = (LinearLayout) convertView.findViewById(R.id.postDocumentLayout);
            this.copyHistoryLayout = (RelativeLayout) convertView.findViewById(R.id.copyHistoryLayout);
            this.postAlbumLayout = (LinearLayout) convertView.findViewById(R.id.postAlbumLayout);
            this.postGeoLayout = (RelativeLayout) convertView.findViewById(R.id.postGeoLayout);
            this.postWikiPageLayout = (RelativeLayout) convertView.findViewById(R.id.postWikiPageLayout);
            this.postLinkLayout = (RelativeLayout) convertView.findViewById(R.id.postLinkLayout);
            this.postPollLayout = (RelativeLayout) convertView.findViewById(R.id.postPollLayout);
            this.img_fixed_post = (ImageView) convertView.findViewById(R.id.img_fixed_post);
            this.postAuthorPanel = (RelativeLayout) convertView.findViewById(R.id.author_post_panel);
            this.postFeatureLayout = (RelativeLayout) convertView.findViewById(R.id.postFeaturesLayout);
            this.author_of_post = (TextView) convertView.findViewById(R.id.autor_post_text);
            this.cb_post_like = (CheckBox) convertView.findViewById(R.id.cb_like);
            this.cb_post_comment = (CheckBox) convertView.findViewById(R.id.cb_comment);
            this.cb_post_repost = (CheckBox) convertView.findViewById(R.id.cb_repost);
            this.button_like = ((Button) convertView.findViewById(R.id.button_like));
            this.button_comment = ((Button) convertView.findViewById(R.id.button_comment));
            this.button_repost = ((Button) convertView.findViewById(R.id.button_repost));
            this.txt_post_date = ((TextView) convertView.findViewById(R.id.txt_post_date_of_comment));
            this.postUserComment = (TextView) convertView.findViewById(R.id.post_user_comment_text);
            this.txtPost = (TextView) convertView.findViewById(R.id.txt_post);
            this.cbPostAllText = (CheckBox) convertView.findViewById(R.id.cb_show_all_text);
            this.imgWikiPage = (ImageView) convertView.findViewById(R.id.img_wiki_page);
            this.txtWikiPage = (TextView) convertView.findViewById(R.id.txt_wiki_page);
            this.imgLink = (ImageView) convertView.findViewById(R.id.img_link);
            this.txtLinkTitle = (TextView) convertView.findViewById(R.id.txt_link_title);
            this.txtLinkSrc = (TextView) convertView.findViewById(R.id.txt_link_src);
            this.imgPoll = (ImageView) convertView.findViewById(R.id.img_poll_post);
            this.txtPollTitle = (TextView) convertView.findViewById(R.id.txt_poll_title);
            this.imgGeo = (ImageView) convertView.findViewById(R.id.img_geo);
            this.txtGeo = (TextView) convertView.findViewById(R.id.txt_geo);
            this.postMediaPager = (ViewPager) convertView.findViewById(R.id.media_pager);
            this.postMediaPagerIndicator = (CirclePageIndicator) convertView.findViewById(R.id.media_circle_indicator);
            this.postAudioListView = (ListView) convertView.findViewById(R.id.lv_simple);
            this.postMediaPagerVideoButton = (ImageButton) convertView.findViewById(R.id.ib_goto_video_page);

            View copyHistoryView = convertView.findViewById(R.id.incl_copy_history_layout);
            this.imgCopyHistoryLogo = (ImageView) copyHistoryView.findViewById(R.id.img_copy_history_logo);
            this.txtCopyHistoryDate = (TextView) copyHistoryView.findViewById(R.id.txt_copy_history_date);
            this.txtCopyHistoryTitle = (TextView) copyHistoryView.findViewById(R.id.txt_copy_history_title);
            this.copyHistoryHeader = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryHeader);
            this.copyHistoryAttachmentsLayout = (LinearLayout) copyHistoryView.findViewById(R.id.copyHistoryAttachmentsLayout);
            this.copyHistoryTextLayout = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryTextLayout);
            this.copyHistoryMediaLayout = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryMediaLayout);
            this.copyHistoryAudioLayout = (LinearLayout) copyHistoryView.findViewById(R.id.copyHistoryAudioLayout);
            this.copyHistoryDocumentLayout = (LinearLayout) copyHistoryView.findViewById(R.id.copyHistoryDocumentLayout);
            this.copyHistoryAlbumLayout = (LinearLayout) copyHistoryView.findViewById(R.id.copyHistoryAlbumLayout);
            this.copyHistoryGeoLayout = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryGeoLayout);
            this.copyHistoryWikiPageLayout = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryWikiPageLayout);
            this.copyHistoryLinkLayout = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryLinkLayout);
            this.copyHistoryPollLayout = (RelativeLayout) copyHistoryView.findViewById(R.id.copyHistoryPollLayout);
            this.copyHistoryTxtPost = (TextView) copyHistoryView.findViewById(R.id.txt_post);
            this.copyHistoryCbPostAllText = (CheckBox) copyHistoryView.findViewById(R.id.cb_show_all_text);
            this.copyHistoryImgWikiPage = (ImageView) copyHistoryView.findViewById(R.id.img_wiki_page);
            this.copyHistoryTxtWikiPage = (TextView) copyHistoryView.findViewById(R.id.txt_wiki_page);
            this.copyHistoryImgLink = (ImageView) copyHistoryView.findViewById(R.id.img_link);
            this.copyHistoryTxtLinkTitle = (TextView) copyHistoryView.findViewById(R.id.txt_link_title);
            this.copyHistoryTxtLinkSrc = (TextView) copyHistoryView.findViewById(R.id.txt_link_src);
            this.copyHistoryImgPoll = (ImageView) copyHistoryView.findViewById(R.id.img_poll_post);
            this.copyHistoryTxtPollTitle = (TextView) copyHistoryView.findViewById(R.id.txt_poll_title);
            this.copyHistoryImgGeo = (ImageView) copyHistoryView.findViewById(R.id.img_geo);
            this.copyHistoryTxtGeo = (TextView) copyHistoryView.findViewById(R.id.txt_geo);
            this.copyHistoryMediaPager = (ViewPager) copyHistoryView.findViewById(R.id.media_pager);
            this.copyHistoryMediaPagerIndicator = (CirclePageIndicator) copyHistoryView.findViewById(R.id.media_circle_indicator);
            this.copyHistoryAudioListView = (ListView) copyHistoryView.findViewById(R.id.lv_simple);
            this.copyHistoryMediaPagerVideoButton = (ImageButton) copyHistoryView.findViewById(R.id.ib_goto_video_page);
        }
    }
}