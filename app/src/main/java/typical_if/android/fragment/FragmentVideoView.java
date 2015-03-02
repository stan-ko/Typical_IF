package typical_if.android.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.vk.sdk.api.model.VKApiVideo;

import typical_if.android.R;

/**
 * Created by LJ on 11.08.2014.
 */
public class FragmentVideoView extends Fragment {

    ProgressDialog pDialog;
    VideoView videoview;
    VKApiVideo video;

   // static String videoURL = "http://cs634005v4.vk.me/u106880118/videos/b892209e1d.240.mp4?extra=cN3FmRT76KMgP631XZmgnsaoYN3BTo2mLVM7-v3J-s5M2V5GxdeKZwg9XWh910VoAjRwlna7MigJcXK1R3dWFfwo7DMHjkY";

    public static FragmentVideoView newInstance(String url, VKApiVideo video) {
        FragmentVideoView fragment = new FragmentVideoView(video);

        Bundle args = new Bundle();
        args.putString("url",url);

        fragment.setArguments(args);



        return fragment;
    }

    public FragmentVideoView(VKApiVideo video ){
        this.video=video;
    }
    public FragmentVideoView(){}
    ActionBar actionBar;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_video_view, container, false);
        setRetainInstance(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        actionBar= getActivity().getActionBar();
        actionBar.hide();
        playVideo(getArguments().getString("url"), rootView);
        getActivity().getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                actionBar.show();

            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  actionBar.show();
      //  getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void playVideo(String url, final View view){
        videoview = (VideoView) view.findViewById(R.id.videoView);


        final ImageView preview = ((ImageView) view.findViewById(R.id.video_preview));
                ImageLoader.getInstance().displayImage(video.photo.get(1).src,preview,new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.event_stub) // TODO resource or drawable
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build());
        preview.setVisibility(View.VISIBLE);
        pDialog = new ProgressDialog(getActivity());




        // Set progressbar navDrawTitle
       // pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
       // pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();
       // pDialog.setContentView(R.layout.custom_progress_bar);

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(getActivity());
            mediacontroller.setAnchorView(videoview);
            mediacontroller.setAlpha(1f);


            // Get the URL from String VideoURL
            Uri video = Uri.parse(url);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                preview.setVisibility(View.GONE);
                videoview.start();

                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            pDialog.show();
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            pDialog.dismiss();
                        }
                        return false;
                    }
                });
            }
        });
    }


}
