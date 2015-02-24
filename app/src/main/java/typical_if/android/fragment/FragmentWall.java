package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shamanland.fab.ShowHideOnScroll;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.RecyclerEventAdapter;
import typical_if.android.adapter.RecyclerWallAdapter;
import typical_if.android.model.Wall.Wall;
import typical_if.android.view.ToggleFloatingActionsMenu;

import static com.vk.sdk.VKUIHelper.getApplicationContext;


/**
 * Created by admin on 14.07.2014.
 */


public class FragmentWall extends Fragment {

    static RecyclerView wallListView;
    RecyclerView.Adapter adapter;
    ActionBar actionBar;

    RelativeLayout spinnerLayout;
    View rootView;
    NewPauseOnScrollListener pauseOnScrollListener;
    LayoutInflater inflaterGlobal;
    final int offsetO = 0;
    final int countPostDefaultForOffset = 100;
    public static int playableLogoRes;
    JSONObject jsonObjectOld;

    int Offset = Constants.TIF_VK_PRELOAD_POSTS_COUNT;
    static boolean isSuggested;

    boolean temp = true;
    boolean temp2 = true;
    boolean enable = false;

    Bundle arguments;
    SwipeRefreshLayout swipeView;
    ToggleFloatingActionsMenu toggleFloatingActionsMenu;

    LinearLayoutManager linearLayoutManager;

    RecyclerView.OnScrollListener onScrollListenerRecyclerObject = new RecyclerView.OnScrollListener() {
        int mLastFirstVisibleItem = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            temp = true;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            final int lastItem = linearLayoutManager.findFirstVisibleItemPosition() + linearLayoutManager.getChildCount();
            final int totalItemCount = linearLayoutManager.getItemCount();

            if (recyclerView.getId() == wallListView.getId()) {

                final int currentFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    actionBar.hide();
                    toggleFloatingActionsMenu.hide(true);
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    actionBar.show();
                    toggleFloatingActionsMenu.show(true);
                }

                mLastFirstVisibleItem = currentFirstVisibleItem;
            }

            if (lastItem == totalItemCount - 20 & temp2) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Offset = Offset + 100;
                        endlessGet(Offset);
                    }
                }).start();
                temp2 = false;
            }

            if (lastItem == totalItemCount & temp) {
                endlessAdd(lastItem);
                temp = false;
                temp2 = true;
            }

            if (recyclerView.getChildCount() > 0) {
                boolean firstItemVisible = linearLayoutManager.findFirstVisibleItemPosition() == 0;
                boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            swipeView.setEnabled(enable);
        }
    };

    public static FragmentWall newInstance(boolean isSuggestedParam) {
        FragmentWall fragment = new FragmentWall();
        Bundle args = new Bundle();
        isSuggested = isSuggestedParam;
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentWall() {
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.isFragmentCommentsLoaded=false;
        Log.d("isFragmentCommentsLoaded: "+ Constants.isFragmentCommentsLoaded," was changed in OnResume in FragmentWall");
    }

    @Override
    public void onDetach() {
        if (isSuggested) {
            ((MainActivity) getActivity()).getSupportActionBar().show();
            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
            setDisabledMenu();
        }

        super.onDetach();
    }

    FragmentManager fragmentManager;
    long tempGroupId;

    FloatingActionButton fabPhoto;
    FloatingActionButton fabSuggest;
    com.shamanland.fab.FloatingActionButton floatingActionButtonBackToTop;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wall, container, false);

        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.spinner_layout);
        inflaterGlobal = inflater;
        arguments = getArguments();

        fragmentManager = getActivity().getSupportFragmentManager();
        tempGroupId =  OfflineMode.loadLong(Constants.VK_GROUP_ID);


        floatingActionButtonBackToTop = (com.shamanland.fab.FloatingActionButton) rootView.findViewById(R.id.beckToTop);
        floatingActionButtonBackToTop.setBackgroundColor(0);

        floatingActionButtonBackToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallListView.scrollToPosition(0);
                actionBar.show();

            }
        });


        toggleFloatingActionsMenu = (ToggleFloatingActionsMenu) rootView.findViewById(R.id.fab_wall);
        fabPhoto = (FloatingActionButton) rootView.findViewById(R.id.fab_wall_photo);
        fabSuggest = (FloatingActionButton) rootView.findViewById(R.id.fab_wall_suggest);

        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //  OfflineMode.loadLong(Constants.VK_GROUP_ID) = tempGroupId;
                FragmentAlbumsList fragment = FragmentAlbumsList.newInstance(1);
                ((MainActivity) getActivity()).addFragment(fragment);
                ((MainActivity) getActivity()).restoreActionBar();
            }
        });

        fabSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).addFragment(FragmentMakePost.newInstance( OfflineMode.loadLong(Constants.VK_GROUP_ID), 0, 0));
            }
        });

        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.show();

        playableLogoRes = ItemDataSetter.getPlayingLogo( OfflineMode.loadLong(Constants.VK_GROUP_ID));
        pauseOnScrollListener = new NewPauseOnScrollListener(ImageLoader.getInstance(), true, true, onScrollListenerRecyclerObject);
        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeView.setColorSchemeResources(android.R.color.white, android.R.color.white, android.R.color.white);
        swipeView.setProgressBackgroundColor(R.color.music_progress);
        swipeView.setProgressViewOffset(true, 0, 150);
        swipeView. setSize(SwipeRefreshLayout.DEFAULT);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!OfflineMode.isOnline(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_message_toast_en), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        VKHelper.doGroupWallRequest(offsetO, Offset,  OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                OfflineMode.saveJSON(response.json,  OfflineMode.loadLong(Constants.VK_GROUP_ID));
                                initGroupWall(OfflineMode.loadJSON( OfflineMode.loadLong(Constants.VK_GROUP_ID)), inflaterGlobal);
                            }

                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });

                        swipeView.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        wallListView = (RecyclerView) rootView.findViewById(R.id.listViewWall);

        wallListView.setHasFixedSize(true);
        wallListView.setOnTouchListener(new ShowHideOnScroll(floatingActionButtonBackToTop));
//        wallListView.setOnTouchListener(new ShowHideOnScroll(toggleFloatingActionsMenu));
        wallListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        linearLayoutManager = ((LinearLayoutManager) wallListView.getLayoutManager());

        if (!isSuggested) {
//if (OfflineMode.loadLong(Constants.VK_GROUP_ID)!=0) {
//     OfflineMode.loadLong(Constants.VK_GROUP_ID) = OfflineMode.loadLong(Constants.VK_GROUP_ID);
//} else {
//     OfflineMode.loadLong(Constants.VK_GROUP_ID) = Constants.TF_ID;
//}


            Log.d("GROUP_ID", ""+ OfflineMode.loadLong(Constants.VK_GROUP_ID));
            jsonObjectOld = OfflineMode.loadJSON( OfflineMode.loadLong(Constants.VK_GROUP_ID));
            initGroupWall(jsonObjectOld, inflater);

        } else {
            setDisabledMenu();
            VKHelper.getSuggestedPosts( OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    initGroupWall(response.json, inflater);
                    wallListView.setOnScrollListener(null);

                    swipeView.setOnRefreshListener(null);
                    swipeView.setEnabled(false);
                    swipeView.setRefreshing(false);
                }

                @Override
                public void onError(final VKError error) {
                    super.onError(error);
                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                }
            });
        }

        return rootView;
    }

    public void checkFabSuggest() {
        if (VKSdk.isLoggedIn()) {
            fabSuggest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).addFragment(FragmentMakePost.newInstance( OfflineMode.loadLong(Constants.VK_GROUP_ID), 0, 0));
                }
            });
            fabSuggest.setVisibility(View.VISIBLE);
        } else {
            fabSuggest.setOnClickListener(null);
            fabSuggest.setVisibility(View.GONE);
        }
    }

    public class NewPauseOnScrollListener extends RecyclerView.OnScrollListener {

        private ImageLoader imageLoader;

        private final boolean pauseOnScroll;
        private final boolean pauseOnSettling;
        private final RecyclerView.OnScrollListener externalListener;

        public NewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling) {
            this(imageLoader, pauseOnScroll, pauseOnSettling, null);
        }

        public NewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling,
                                        RecyclerView.OnScrollListener customListener) {
            this.imageLoader = imageLoader;
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnSettling = pauseOnSettling;
            externalListener = customListener;
        }


        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    imageLoader.resume();
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    if (pauseOnScroll) {
                        imageLoader.pause();
                    }
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    if (pauseOnSettling) {
                        imageLoader.pause();
                    }
                    break;
            }
            if (externalListener != null) {
                externalListener.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (externalListener != null) {
                externalListener.onScrolled(recyclerView, dx, dy);
            }
        }
    }

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater) {
        Wall wall = VKHelper.getGroupWallFromJSON(jsonObject);
        FragmentManager fragmentManager = getFragmentManager();

        if (wall.posts.size() == 0) {
            try {
                fragmentManager.popBackStack();
            } catch (NullPointerException npe) {
                Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), getString(R.string.no_suggested_posts), Toast.LENGTH_SHORT).show();
        }

        if ( OfflineMode.loadLong(Constants.VK_GROUP_ID) == Constants.ZF_ID) {
            fabPhoto.setVisibility(View.GONE);

            if (adapter == null) {
                ArrayList<RecyclerEventAdapter.EventObject> events = getEvents(wall);
                adapter = new RecyclerEventAdapter(events, inflater, fragmentManager);
                wallListView.setAdapter(adapter);
                wallListView.setOnScrollListener(pauseOnScrollListener);
            } else {
                ((RecyclerEventAdapter) adapter).setEvent(getEvents(wall));
            }
        } else {
            fabPhoto.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new RecyclerWallAdapter(wall, inflater, fragmentManager, isSuggested);
                wallListView.setAdapter(adapter);
                wallListView.setOnScrollListener(pauseOnScrollListener);
            } else {
                ((RecyclerWallAdapter) adapter).setWall(wall);
            }
        }

        spinnerLayout.setVisibility(View.GONE);
    }

    Pattern tempPattern;
    Matcher tempMatcher;

    public ArrayList<RecyclerEventAdapter.EventObject> getEvents(Wall wall) {
        ArrayList<RecyclerEventAdapter.EventObject> events = new ArrayList<RecyclerEventAdapter.EventObject>();

        VKApiPhoto fakePhoto = new VKApiPhoto();
        fakePhoto.photo_604 = "fake_photo";

        VKApiPost tempPost;
        String[] tempArray;

        for (int i = 0; i < wall.posts.size(); i++) {
            tempPost = wall.posts.get(i).post.copy_history.get(0);

            ArrayList<VKApiPhoto> photo = new ArrayList<VKApiPhoto>();

            if (tempPost.attachments != null && tempPost.attachments.size() != 0) {
                for (int j = 0; j < tempPost.attachments.size(); j++) {
                    if (tempPost.attachments.get(j).getType().equals(VKAttachments.TYPE_PHOTO)) {
                        photo.add((VKApiPhoto) tempPost.attachments.get(j));
                    } else {
                        photo.add(fakePhoto);
                    }
                }
            } else {
                photo.add(fakePhoto);
            }

            SparseArray<List<String>> eventData = new SparseArray<List<String>>();

            ArrayList<String> today = new ArrayList<String>();
            ArrayList<String> stantsiya = new ArrayList<String>();
            ArrayList<String> period = new ArrayList<String>();

            tempPost.text = tempPost.text.replaceFirst(":", "");
            tempArray = tempPost.text.split("(.+):\n");

            try {
                for (int j = 0; j < Constants.EVENT_COUNT; j++) {
                    switch (j) {
                        case Constants.TODAY_EVENT:
                            parseEvents(eventData, today, j, "(о ).+\n", tempArray[j]);
                            break;
                        case Constants.STATION_EVENT:
                            parseEvents(eventData, stantsiya, j, "(о ).+\n", tempArray[j]);
                            break;
                        case Constants.PERIOD_EVENT:
                            parseEvents(eventData, period, j, "- .+(\n|$)", tempArray[j]);
                            break;
                    }
                }
            } catch (Exception e) {
                today.clear();
                stantsiya.clear();
                period.clear();

                today.add(getString(R.string.null_events));
                stantsiya.add(getString(R.string.null_events));
                period.add(getString(R.string.null_events));

                eventData.put(0, today);
                eventData.put(1, stantsiya);
                eventData.put(2, period);
            }

            events.add(new RecyclerEventAdapter.EventObject(
                            eventData,
                            tempPost.date,
                            photo
                    )
            );
        }

        return events;
    }

    public void parseEvents(SparseArray<List<String>> data, ArrayList<String> list, int position, String regexp, String text) {
        tempPattern = Pattern.compile(regexp);
        tempMatcher = tempPattern.matcher(text);

        if (!text.contains(getString(R.string.null_events))) {
            while (tempMatcher.find()) {
                if (tempMatcher.group().contains("\n")) {
                    list.add(tempMatcher.group().replace("\n", ""));
                } else {
                    list.add(tempMatcher.group());
                }
            }
        } else {
            list.add(getString(R.string.null_events));
        }

        data.put(position, list);
    }

    int mCurCheckPosition = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            wallListView.scrollToPosition(0);
            Constants.isFragmentCommentsLoaded=false;
            Log.d("isFragmentCommentsLoaded: " + Constants.isFragmentCommentsLoaded, " was changed in onActivityCreated in FragmentWall");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        Constants.makePostMenu = menu;


        if (OfflineMode.isOnline(getApplicationContext())) {
            setEnabledMenu();
        } else {
            setDisabledMenu();
        }

        super.onPrepareOptionsMenu(menu);
        VKHelper.isMember( OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                Constants.isMember = response.json.optInt("response");
                if (VKSdk.isLoggedIn()) {
                    if (Constants.isMember == 0) {
                        try {
                            menu.findItem(R.id.join_leave_group).setTitle(getString(R.string.ab_title_group_join));
                        } catch (Exception e) {}
                    } else {
                        try {
                            menu.findItem(R.id.join_leave_group).setTitle(getString(R.string.ab_title_group_leave));
                        } catch (Exception e) {}
                    }
                } else {
                    menu.findItem(R.id.join_leave_group).setVisible(false);
                }
            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });
    }

    public static void setEnabledMenu() {
        if (Constants.makePostMenu.size() == 3) {
            Constants.makePostMenu.getItem(0).setVisible(true);
            Constants.makePostMenu.getItem(1).setVisible(true);
            Constants.makePostMenu.getItem(2).setVisible(true);
        }
    }

    public static void setDisabledMenu() {
        if (Constants.makePostMenu.size() == 3) {
            Constants.makePostMenu.getItem(0).setVisible(false);
            Constants.makePostMenu.getItem(1).setVisible(false);
            Constants.makePostMenu.getItem(2).setVisible(false);
            Constants.makePostMenu.close();
        }
        Constants.makePostMenu.close();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        if (VKSdk.isLoggedIn()) {
            Constants.makePostMenu = menu;
            inflater.inflate(R.menu.make_post, menu);

            if (!isSuggested) {
                setEnabledMenu();
            } else {
                setDisabledMenu();
            }

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.make_post:
                ((MainActivity) getActivity()).addFragment(FragmentMakePost.newInstance( OfflineMode.loadLong(Constants.VK_GROUP_ID), 0, 0));
                break;
            case R.id.suggested_posts:
                ((MainActivity) getActivity()).addFragment(FragmentWall.newInstance(true));
                break;
            case R.id.join_leave_group:
                if (Constants.isMember == 0) {
                    VKHelper.groupJoin( OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), getString(R.string.group_joined), Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
                        }

                        @Override
                        public void onError(final VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                } else {
                    VKHelper.groupLeave( OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), getString(R.string.group_leaved), Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
                        }

                        @Override
                        public void onError(final VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", wallListView.getScrollY());
    }
//    @Override
//    public void onPause(){
//        super.onPause();
//        OfflineMode.saveLong( OfflineMode.loadLong(Constants.VK_GROUP_ID), Constants.VK_GROUP_ID);
//    }


    private void endlessAdd(final int lastItem) {
        jsonObjectOld = OfflineMode.loadJSON( OfflineMode.loadLong(Constants.VK_GROUP_ID));
        initGroupWall(jsonObjectOld, inflaterGlobal);
        scrollCommentsToBottom(wallListView, lastItem);
    }

    private void endlessGet(final int Offset) {
        VKHelper.doGroupWallRequest(Offset, countPostDefaultForOffset,  OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(OfflineMode.jsonPlus(jsonObjectOld, response.json),  OfflineMode.loadLong(Constants.VK_GROUP_ID));
            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });
    }

    private void scrollCommentsToBottom(final RecyclerView listView, final int lastItem) {
        listView.post(new Runnable() {
            @Override
            public void run() {
               listView.getLayoutManager().scrollToPosition(lastItem - 2);
            }
        });

    }
}