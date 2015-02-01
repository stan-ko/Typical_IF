package typical_if.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import typical_if.android.R;
import typical_if.android.activity.MainActivity;

/**
 * Created by LJ on 11.08.2014.
 */
public class FragmentVideoView extends Fragment {

    ProgressDialog pDialog;
    VideoView videoview;

    String videoURL = "http://www.youtube.com/watch?v=6m91rRqn4UI";

    public static FragmentVideoView newInstance() {
        FragmentVideoView fragment = new FragmentVideoView();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_video_view, container, false);
        setRetainInstance(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        playVideo("https://cs535407.vk.me/u187576549/videos/2fee4723dc.720.mp4?extra=TwMvIAYEi2AGmKdGapjDWKZOWv9zIXe7Zt92h36ZjGolrfmD63oOGGPyxkWJEHhqanKecSX2qld7gINOsEguR7taIsW0EYPueg", rootView);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    public void playVideo(String url, View view){
        videoview = (VideoView) view.findViewById(R.id.videoView);
        // Execute StreamVideo AsyncTask
        // Create a progressbar
        pDialog = new ProgressDialog(getActivity());
        // Set progressbar navDrawTitle
        pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(getActivity());
            mediacontroller.setAnchorView(videoview);
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
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });
    }


}
