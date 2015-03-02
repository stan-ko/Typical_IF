package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiPoll;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.VotesItemAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PollFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static VKApiPoll updatedPoll;
    public TextView answers_anonymous_text_preview;
    public String isAnonymous_preview;

    private OnFragmentInteractionListener mListener;
    private VKApiPoll poll;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView pollList;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    //  private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static PollFragment newInstance(String param1, String param2) {
        Constants.isPollFragmentLoaded = true;
        Log.d("isPollFragmentLoaded: " + Constants.isPollFragmentLoaded, " was changed in newInstance() in PollFragment");
        PollFragment fragment = new PollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        FragmentWall.setDisabledMenu();
        return fragment;
    }

    public static boolean isRunning;

//    public PollFragment(VKApiPoll poll, TextView answers_anonymous_text, String isAnonymous) {
//
//        this.answers_anonymous_text_preview=answers_anonymous_text;
//        this.isAnonymous_preview=isAnonymous;
//        if (Constants.isFragmentCommentsLoaded&updatedPoll!=null){
//            this.poll=updatedPoll;
//        }else {
//            this.poll=poll;
//        }
//    }

    public PollFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
//        args.clear();
//        args.putParcelable("poll", poll);
//        args.putString("isAnonymous", isAnonymous);
//        args.putSerializable("answers_anonymous_text", (java.io.Serializable) answers_anonymous_text);
//        fragment.setArguments(args);
        super.onCreate(savedInstanceState);
        final TextView answers_anonymous_text = ((TextView) getActivity().findViewById(R.id.answers_anonymous_text_preview));
        if (getArguments() != null) {
            this.isAnonymous_preview = getArguments().getString("isAnonymous");

            if (Constants.isFragmentCommentsLoaded & updatedPoll != null) {
                this.poll = updatedPoll;
            } else {
                this.poll = getArguments().getParcelable("poll");
                ;
            }
            answers_anonymous_text.setText(getArguments().getString("answers_anonymous_text"));
            this.answers_anonymous_text_preview = answers_anonymous_text;
            FragmentWall.setDisabledMenu();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().show();
        isRunning = true;

        if (VKSdk.isLoggedIn()) {


            getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.poll));
            Constants.MtitlePoll = getActivity().getActionBar().getTitle().toString();
            FragmentWall.setDisabledMenu();
        } else {
            getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.poll) + " ("
                                                    + getActivity().getResources().getString(R.string.login_to_vote) + ")");
            Constants.MtitlePoll = getActivity().getActionBar().getTitle().toString();
            FragmentWall.setDisabledMenu();
        }

        View view = inflater.inflate(R.layout.poll_view_container_fragment, container, false);
        View footerView = View.inflate(getActivity().getApplicationContext(), R.layout.footter_view_button_change_decision, null);
        final Button change_decision_button = ((Button) footerView.findViewById(R.id.change_vote_decision_button));
        final TextView title = ((TextView) view.findViewById(R.id.txt_poll_title));
        final TextView answers_anonymous_text = ((TextView) view.findViewById(R.id.answers_anonymous_text));
        pollList = (ListView) view.findViewById(R.id.listOfVotes);
        final String isAnonymous;


        title.setText(poll.question);

        if (poll.anonymous == 1) {
            isAnonymous = Constants.mainActivity.getResources().getString(R.string.anonymous_poll);
        } else
            isAnonymous = Constants.mainActivity.getResources().getString(R.string.public_poll);


        answers_anonymous_text.setText(isAnonymous + " " + poll.votes);
        view.setVisibility(View.VISIBLE);
        VotesItemAdapter adapter = new VotesItemAdapter(poll, answers_anonymous_text, answers_anonymous_text_preview,
                isAnonymous, isAnonymous_preview, change_decision_button);
        pollList.setAdapter(adapter);
        pollList.addFooterView(footerView);
        ItemDataSetter.setListViewHeightBasedOnChildren(pollList);


//        pollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                int counter =0;
//
//
//            }
//        });
        FragmentWall.setDisabledMenu();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Constants.isPollFragmentLoaded = true;
        Log.d("isPollFragmentLoaded: " + Constants.isPollFragmentLoaded, " was changed in OnAttach() in PollFragment");
        FragmentWall.setDisabledMenu();

    }

    @Override
    public void onDetach() {
        Constants.isPollFragmentLoaded = false;
        Log.d("isPollFragmentLoaded: " + Constants.isPollFragmentLoaded, " was changed in OnDetach() in PollFragment");
        getActivity().getActionBar().setTitle(Constants.Mtitle);
        isRunning = false;

        FragmentWall.setEnabledMenu();

        if (Constants.isFragmentCommentsLoaded) {
            getActivity().getActionBar().hide();
        }

        super.onDetach();

        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        getActivity().getActionBar().show();

        if (VKSdk.isLoggedIn()) {
            getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.poll));
            Constants.MtitlePoll = getActivity().getActionBar().getTitle().toString();
        } else {
            getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.poll) + " ("
          + getActivity().getResources().getString(R.string.login_to_vote) + ")");
            Constants.MtitlePoll = getActivity().getActionBar().getTitle().toString();
        }
        FragmentWall.setDisabledMenu();

        Constants.isPollFragmentLoaded = true;

        Log.d("isPollFragmentLoaded: " + Constants.isPollFragmentLoaded, " was changed in OnResume() in PollFragment");
        FragmentWall.setDisabledMenu();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //   mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
