package typical_if.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.daimajia.swipe.SwipeLayout;
import com.devspark.robototextview.widget.RobotoTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiPhoto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.util.DateUtils;
import typical_if.android.view.ResizableImageView;

/**
 * Created by gigamole on 16.02.15.
 */
public class RecyclerEventAdapter extends RecyclerView.Adapter<RecyclerEventAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;

    public static FragmentManager fragmentManager;
    public ArrayList<EventObject> eventObjects;

    public RecyclerEventAdapter(ArrayList<EventObject> eventObjects, LayoutInflater inflater, FragmentManager fragmentManager) {
        this.layoutInflater = inflater;
        this.context = TIFApp.getAppContext();
        this.fragmentManager = fragmentManager;

        this.eventObjects = eventObjects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        initEventViewHolder(viewHolder, eventObjects.get(i));

        if (i == 0) {
            viewHolder.itemView.setPadding(0, TIFApp.getScaledDp(48), 0, 0);
        } else {
            viewHolder.itemView.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return eventObjects.size();
    }

    public void setEvent(ArrayList<EventObject> events) {
        this.eventObjects.clear();
        this.eventObjects.addAll(events);
        notifyDataSetChanged();
    }

    public final View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<VKApiPhoto> photos = (ArrayList<VKApiPhoto>) v.getTag();
            VKHelper.countOfPhotos = photos.size();
            ItemDataSetter.makeSaveTransaction(fragmentManager, photos, 0);
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
        } catch (ParseException e) {
        }

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

    public void initEventViewHolder(RecyclerEventAdapter.ViewHolder viewHolder, EventObject item) {
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

                if (i == 1) {
                    eventSwipeLayout.getBottomView().setBackgroundColor(context.getResources().getColor(R.color.stantsiya_bg));
                } else {
                    eventSwipeLayout.getBottomView().setBackgroundColor(context.getResources().getColor(R.color.music_progress));
                }

                if (DateUtils.isToday(item.date)) {
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
        eventSwipeLayout.setSwipeEnabled(true);
        eventSwipeLayout.getSurfaceView().setTag(eventSwipeLayout);
        eventSwipeLayout.getSurfaceView().setOnClickListener(slClickListener);

        eventSwipeLayout.setTag(calendarData);
        eventSwipeLayout.getBottomView().setTag(eventSwipeLayout);
        eventSwipeLayout.getBottomView().setOnClickListener(flClickListener);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ResizableImageView imgPhoto;
        public final Button btImgPhoto;
        public final LinearLayout fakeTodayListView;
        public final LinearLayout fakeStationListView;
        public final LinearLayout fakePeriodListView;

        ViewHolder(View convertView) {
            super(convertView);
            this.btImgPhoto = (Button) convertView.findViewById(R.id.bt_img_event);
            this.imgPhoto = (ResizableImageView) convertView.findViewById(R.id.img_event_item);
            this.fakeTodayListView = (LinearLayout) convertView.findViewById(R.id.fake_lv_today_event_item);
            this.fakeStationListView = (LinearLayout) convertView.findViewById(R.id.fake_lv_yesterday_event_item);
            this.fakePeriodListView = (LinearLayout) convertView.findViewById(R.id.fake_lv_period_event_item);
        }
    }
}
