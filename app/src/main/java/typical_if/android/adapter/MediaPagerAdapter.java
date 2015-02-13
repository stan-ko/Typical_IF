package typical_if.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.fragment.FragmentMakePost;
import typical_if.android.fragment.FragmentWebView;

/**
 * Created by gigamole on 31.01.15.
 */
public class MediaPagerAdapter extends PagerAdapter {

    public final ArrayList<VKApiPhoto> photos;
    public final ArrayList<VKApiVideo> videos;

    public ArrayList<View> views = new ArrayList<View>();
    public ArrayList<Item> medias = new ArrayList<Item>();

    public boolean isPost;

    public final Context context;
    public final LayoutInflater layoutInflater;

    public View.OnClickListener deletePhotoAttachListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKApiPhoto photo = (VKApiPhoto) v.getTag();
            FragmentMakePost.deleteAttaches(0, photo);
        }
    };

    public View.OnClickListener deleteVideoAttachListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKApiVideo video = (VKApiVideo) v.getTag();
            FragmentMakePost.deleteAttaches(1, video);
        }
    };

    public View.OnClickListener openPhotosListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKHelper.countOfPhotos = getPhotosCount();
            ItemDataSetter.makeSaveTransaction(photos, ((Integer) v.getTag()));
        }
    };

    public View.OnClickListener openVideosListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKApiVideo video = (VKApiVideo) v.getTag();
            String key = (video.owner_id + "_" + video.id + "_" + video.access_key);
            VKHelper.getVideoPlay(key, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            VKApiVideo video = VKHelper.getVideoSourceFromJson(response.json);

                            if (video != null) {
                                Fragment fragment = new FragmentWebView(video);
                                ItemDataSetter.fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                            } else
                                Toast.makeText(Constants.mainActivity.getApplicationContext(), R.string.error_playing_video, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            Toast.makeText(Constants.mainActivity.getApplicationContext(), R.string.error_playing_video_auth, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        }
    };

    public enum RowType {
        PHOTO_ITEM, VIDEO_ITEM
    }

    public MediaPagerAdapter(Context context, boolean isPost, ArrayList<VKApiPhoto> photos, ArrayList<VKApiVideo> videos) {
        this.isPost = isPost;

        this.photos = photos;
        this.videos = videos;

        for (VKApiPhoto photo : photos) {
            medias.add(new Photo(photo));
        }

        for (VKApiVideo video : videos) {
            medias.add(new Video(video));
        }


        for (Item item : medias) {
            views.add(null);
        }

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public View getItemView(int position) {
        return views.get(position);
    }

    public int getViewTypeCount() {
        return RowType.values().length;

    }

    public Item getItem(int position) {
        Item item = medias.get(position);

        if (item.getViewType() == RowType.PHOTO_ITEM.ordinal()) {
            ((Photo) item).setPosition(position);
        }

        return item;
    }

    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    public int getVideosCount() {
        return videos.size();
    }

    public int getPhotosCount() {
        return photos.size();
    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View convertView = getItem(position).getView(layoutInflater, getItemView(position));
        (container).addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    public interface Item {
        public int getViewType();

        public View getView(LayoutInflater inflater, View convertView);
    }

    public class Photo implements Item {
        private final VKApiPhoto photo;
        private int position;

        public Photo(VKApiPhoto photo) {
            this.photo = photo;
        }

        @Override
        public int getViewType() {
            return RowType.PHOTO_ITEM.ordinal();
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.media_container, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(photo.photo_604, viewHolder.photo);

            if (isPost) {
                viewHolder.photo.setTag(position);
                viewHolder.photo.setOnClickListener(openPhotosListener);
            } else {
                viewHolder.photo.setTag(photo);
                viewHolder.photo.setOnClickListener(deletePhotoAttachListener);
            }

            return convertView;
        }
    }

    public class Video implements Item {
        private final VKApiVideo video;

        public Video(VKApiVideo video) {
            this.video = video;
        }

        @Override
        public int getViewType() {
            return RowType.VIDEO_ITEM.ordinal();
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.media_container, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(video.photo_320, viewHolder.photo);

            viewHolder.videoContainer.setVisibility(View.VISIBLE);

            if (isPost) {
                viewHolder.videoContainer.setTag(video);
                viewHolder.videoContainer.setOnClickListener(openVideosListener);
            } else {
                viewHolder.photo.setTag(video);
                viewHolder.photo.setOnClickListener(deleteVideoAttachListener);
            }

            viewHolder.videoDuration.setText(ItemDataSetter.getMediaTime(video.duration));
            viewHolder.videoTitle.setText(video.title);

            return convertView;
        }
    }

    public static class ViewHolder {
        public final ImageView photo;
        public final RelativeLayout videoContainer;
        public final TextView videoDuration;
        public final TextView videoTitle;

        ViewHolder(View convertView) {
            this.photo = (ImageView) convertView.findViewById(R.id.img_media_container);
            this.videoContainer = (RelativeLayout) convertView.findViewById(R.id.video_media_container);
            this.videoDuration = (TextView) convertView.findViewById(R.id.txt_video_duration_media_container);
            this.videoTitle = (TextView) convertView.findViewById(R.id.txt_video_title_media_container);
        }
    }
}
