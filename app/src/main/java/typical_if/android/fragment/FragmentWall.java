package typical_if.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.support.v7.widget.SearchView;

import typical_if.android.Constants;
import typical_if.android.FloatingToolbar_ButtonHelper;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.RecyclerEventAdapter;
import typical_if.android.adapter.RecyclerWallAdapter;
import typical_if.android.model.Wall.Wall;


import static com.vk.sdk.VKUIHelper.getApplicationContext;
import static com.vk.sdk.VKUIHelper.getTopActivity;


/**
 * Created by admin on 14.07.2014.
 */


public class FragmentWall extends FragmentWithAttach {

    RecyclerView wallListView;
    RecyclerView.Adapter adapter;
    private View totalToolBar;
    RelativeLayout spinnerLayout;

    private View rootView;
    private SearchView searchView;
    NewPauseOnScrollListener pauseOnScrollListener;
    public static LayoutInflater inflaterGlobal;
    final int offsetO = 0;
    private EditText searchEditText;
    private final int countPostDefaultForOffset = 50;
    public static int playableLogoRes;
    private ImageView SearchCloseButton;
    private JSONObject jsonObjectOld;
    private TextView toolbarTitle;
    private int Offset = Constants.TIF_VK_PRELOAD_POSTS_COUNT;
    boolean isSuggested;
    FragmentManager fragmentManager;
    long tempGroupId;
    boolean doEndless = true;

    FloatingActionButton floatingActionButtonBackToTop;
    Toolbar toolbar;
    TextView mTitleToolBar;

    // Bundle arguments;
    SwipeRefreshLayout swipeView;
    FloatingActionButton floatingActionButtonCreate;

    private SimpleCursorAdapter mAdapter;

    LinearLayoutManager linearLayoutManager;
    boolean temp = true;
    boolean temp2 = true;
    boolean enable = false;
    boolean hide = true;
    boolean show = false;
    public NavigationDrawerFragment mNavigationDrawerFragment;

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "Tag"});
        for (int i = 0; i < getActivity().getResources().getStringArray(R.array.string_array_hesh_suggestion_FB).length; i++) {
            if (getActivity().getResources().getStringArray(R.array.string_array_hesh_suggestion_FB)[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, getActivity().getResources().getStringArray(R.array.string_array_hesh_suggestion_FB)[i]});
        }
        mAdapter.changeCursor(c);
    }

    final ThreadLocal<RecyclerView.OnScrollListener> onScrollListenerRecyclerObject = new ThreadLocal<RecyclerView.OnScrollListener>() {
        @Override
        protected RecyclerView.OnScrollListener initialValue() {
            return new RecyclerView.OnScrollListener() {
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
                        if (currentFirstVisibleItem == 2 || currentFirstVisibleItem == 1 || currentFirstVisibleItem == 0) {
                            FloatingToolbar_ButtonHelper.animationShow(floatingActionButtonBackToTop);
                        }
                        if (currentFirstVisibleItem > mLastFirstVisibleItem && hide) {
                            FloatingToolbar_ButtonHelper.totalToolbarShow(totalToolBar);
                            FloatingToolbar_ButtonHelper.animationShow(floatingActionButtonBackToTop);
                            floatingActionButtonCreate.hide();
                            Log.d("hide", " " + hide);
                            show = true;
                            hide = false;

                        } else if (currentFirstVisibleItem < mLastFirstVisibleItem && show) {
                            FloatingToolbar_ButtonHelper.totalToolbarHide(totalToolBar);
                            floatingActionButtonCreate.show();
                            if (currentFirstVisibleItem > 3)
                                floatingActionButtonBackToTop.setVisibility(View.VISIBLE);
                            FloatingToolbar_ButtonHelper.animationHide(floatingActionButtonBackToTop);
                            Log.d("show", " " + show);
                            hide = true;
                            show = false;
                        }
                        mLastFirstVisibleItem = currentFirstVisibleItem;
                    }

                    if (OfflineMode.loadLong(Constants.VK_GROUP_ID) != Constants.ZF_ID) {

                        if (lastItem == totalItemCount - 5 && temp2) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Offset = Offset + countPostDefaultForOffset;
                                    endlessGet(Offset);
                                }
                            }).start();
                            temp2 = false;
                        }

                        if (lastItem == totalItemCount && temp) {
                            endlessAdd(lastItem);
                            temp = false;
                            temp2 = true;
                        }
                    }
                    if (recyclerView.getChildCount() > 0) {
                        boolean firstItemVisible = linearLayoutManager.findFirstVisibleItemPosition() == 0;
                        boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    swipeView.setEnabled(enable);
                }
            };
        }
    };

    public static FragmentWall newInstance(boolean isSuggestedParam) {
        FragmentWall fragment = new FragmentWall();
        final Bundle args = new Bundle();
        args.putBoolean("isSuggested", isSuggestedParam);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentWall() {
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.MtitlePoll = "";
        Constants.isFragmentCommentsLoaded = false;
        Log.d("isFragmentCommentsLoaded: " + Constants.isFragmentCommentsLoaded, " was changed in OnResume in FragmentWall");
    }

    @Override
    public void onDetach() {
        if (isSuggested) {
            //((MainActivity) getActivity()).getSupportActionBar().show();
            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
            //setDisabledMenu();
        }
        super.onDetach();
    }


    public void makeSearchRequest(String query, int extended, int offsetDefault, int countOfPosts) {
        VKHelper.searchWall(query, extended, offsetDefault, countOfPosts, Constants.FB_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                Log.d("searchRequest", "" + vkJson);
                initGroupWall(vkJson, inflaterGlobal);
                doEndless = false;
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wall, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();



        final String[] from = new String[]{"Tag"};
        final int[] to = new int[]{R.id.suggestionText};
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.search_ist_item,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        //// -- -- - - - T00LbAR - - --- - - - -- -
        mNavigationDrawerFragment = (NavigationDrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        totalToolBar = rootView.findViewById(R.id.toolbar_fragment_wall);
        toolbar = (Toolbar) rootView.findViewById(R.id.my_awesome_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        toolbarTitle = (TextView) rootView.findViewById(R.id.tool_bar_title);
        toolbarTitle.setText(Constants.Mtitle);
        mTitleToolBar = (TextView) rootView.findViewById(R.id.tool_bar_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationDrawerFragment.toggle();
            }
        });
        View photoFragment = rootView.findViewById(R.id.toolbar_go_to_photo_fragment);
        photoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAlbumsList fragment = FragmentAlbumsList.newInstance(1);
                ((MainActivity) getActivity()).addFragment(fragment);
            }
        });

        toolbar.inflateMenu(R.menu.make_post);
        FloatingToolbar_ButtonHelper.onPrepareToolBarOptionsMenu(toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.make_post:

                        ((MainActivity) getActivity()).addFragment(FragmentMakePost.newInstance(OfflineMode.loadLong(Constants.VK_GROUP_ID), 0, 0));
                        break;
                    case R.id.suggested_posts:
                        ((MainActivity) getActivity()).addFragment(FragmentWall.newInstance(true));
                        break;
                    case R.id.join_leave_group:
                        if (Constants.isMember == 0) {
                            VKHelper.groupJoin(OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), new VKRequestListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getActivity(), R.string.group_joined, Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
                                }
//                        @Override
//                        public void onError() {
//                            TIFApp.showCommonErrorToast();
//                        }
                            });
                        } else {

                            VKHelper.groupLeave(OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), new VKRequestListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getActivity(), R.string.group_leaved, Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
                                }

                            });
                        }
                        break;
                    case R.id.action_search_item:
                        if (OfflineMode.loadLong(Constants.VK_GROUP_ID) == Constants.FB_ID) {
                            searchView = FloatingToolbar_ButtonHelper.CreateSearchView(toolbar.getMenu(), getActivity().getComponentName());
                            searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
                            searchView.setSuggestionsAdapter(mAdapter);
                            SearchCloseButton = (ImageView) searchView.findViewById(R.id.search_close_btn);


                            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                                @Override
                                public boolean onSuggestionSelect(int i) {
                                    return false;
                                }

                                @Override
                                public boolean onSuggestionClick(int i) {
                                    final MatrixCursor mc = (MatrixCursor) mAdapter.getItem(i);
                                    searchView.setQuery(mc.getString(1), false);
                                    searchEditText.setText(mc.getString(1));
                                    makeSearchRequest(searchView.getQuery().toString(), 1, 0, Constants.CONT_OF_SEARCH_POST);
                                    return false;
                                }
                            });

                            SearchCloseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doEndless = true;
                                    Log.d("close", searchView.getQuery() + "");
                                    searchEditText.setText("");
                                    searchView.setQuery("", false);

                                }
                            });
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    populateAdapter(newText);
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    Log.d("onQueryTextSubmit", searchView.getQuery() + "");
                                    Toast.makeText(getApplicationContext(), getString(R.string.refresh_to_sww_all_posts), Toast.LENGTH_LONG).show();
                                    makeSearchRequest(searchView.getQuery().toString(), 1, 0, Constants.CONT_OF_SEARCH_POST);
                                    return false;
                                }
                            });
                        }
                        break;
                    case (R.id.list_of_tags):
                        new MaterialDialog.Builder(getActivity())
                                .title("Category")
                                .items(R.array.string_array_name_suggestion_FB)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        makeSearchRequest(getActivity().getResources().getStringArray(R.array.string_array_hesh_suggestion_FB)[which], 1, 0, Constants.CONT_OF_SEARCH_POST);
                                        Toast.makeText(getApplicationContext(), getString(R.string.toast_to_back_pressed_from_search), Toast.LENGTH_LONG).show();
                                        toolbarTitle.setText(text);
                                        onBackPressedOverride();
                                    }
                                })
                                .show();
                }
                return false;
            }
        });
        //// -- -- -END - - T00LbAR - - --- - - - -- -


        isSuggested = getArguments().getBoolean("isSuggested");
        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.spinner_layout);
        inflaterGlobal = inflater;
        fragmentManager = getActivity().getSupportFragmentManager();
        tempGroupId = OfflineMode.loadLong(Constants.VK_GROUP_ID);


        floatingActionButtonBackToTop = (FloatingActionButton) rootView.findViewById(R.id.beckToTop);
        floatingActionButtonCreate = (FloatingActionButton) rootView.findViewById(R.id.fab_wall);

        if (!VKSdk.isLoggedIn()) {

            floatingActionButtonCreate.setVisibility(View.GONE);
        }
        floatingActionButtonBackToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallListView.scrollToPosition(0);
                FloatingToolbar_ButtonHelper.animationShow(floatingActionButtonBackToTop);

            }
        });
        floatingActionButtonBackToTop.setVisibility(View.GONE);

        floatingActionButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).addFragment(FragmentMakePost.newInstance(OfflineMode.loadLong(Constants.VK_GROUP_ID), 0, 0));
            }
        });

        playableLogoRes = ItemDataSetter.getPlayingLogo(OfflineMode.loadLong(Constants.VK_GROUP_ID));
        pauseOnScrollListener = new NewPauseOnScrollListener(ImageLoader.getInstance(), true, true, onScrollListenerRecyclerObject.get());
        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeView.setColorSchemeResources(android.R.color.white, android.R.color.white, android.R.color.white);
        swipeView.setProgressBackgroundColor(R.color.FAB_UNSELECTED);
        swipeView.setProgressViewOffset(true, 0, 150);
        swipeView.setSize(SwipeRefreshLayout.DEFAULT);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                manualRefresh(3000);
            }
        });
        wallListView = (RecyclerView) rootView.findViewById(R.id.listViewWall);
        wallListView.setHasFixedSize(true);


        wallListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        linearLayoutManager = ((LinearLayoutManager) wallListView.getLayoutManager());

        if (!isSuggested) {


            Log.d("GROUP_ID", "" + OfflineMode.loadLong(Constants.VK_GROUP_ID));
            jsonObjectOld = OfflineMode.loadJSON(OfflineMode.loadLong(Constants.VK_GROUP_ID));
            initGroupWall(jsonObjectOld, inflater);

        } else {
            //setDisabledMenu();
            VKHelper.getSuggestedPosts(OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequestListener() {
                @Override
                public void onSuccess() {
                    initGroupWall(vkJson, inflater);
                    wallListView.setOnScrollListener(null);
                    swipeView.setOnRefreshListener(null);
                    swipeView.setEnabled(false);
                    swipeView.setRefreshing(false);
                }
//                @Override
//                public void onError() {
//                    showErrorToast();
//                }
            });
        }

        return rootView;
    }

    private void manualRefresh(long milSecond) {
        if (!OfflineMode.isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.no_internet_message_toast_en, Toast.LENGTH_SHORT).show();
        }
        swipeView.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VKHelper.doGroupWallRequest(offsetO, Offset, OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        OfflineMode.saveJSON(OfflineMode.loadLong(Constants.VK_GROUP_ID), vkJson);
                        initGroupWall(OfflineMode.loadJSON(OfflineMode.loadLong(Constants.VK_GROUP_ID)), inflaterGlobal);
                        toolbarTitle.setText(Constants.Mtitle);
                    }
                });

                swipeView.setRefreshing(false);
            }
        }, milSecond);

    }

    private void onBackPressedOverride(){
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (OfflineMode.loadLong(Constants.VK_GROUP_ID) == Constants.FB_ID) {
                        manualRefresh(2000);
                    } else getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }
    public class NewPauseOnScrollListener extends RecyclerView.OnScrollListener {

        private ImageLoader imageLoader;

        private final boolean pauseOnScroll;
        private final boolean pauseOnSettling;
        private final RecyclerView.OnScrollListener externalListener;


        public NewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling, RecyclerView.OnScrollListener customListener) {
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
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), R.string.no_suggested_posts, Toast.LENGTH_SHORT).show();
        }

        if (OfflineMode.loadLong(Constants.VK_GROUP_ID) == Constants.ZF_ID) {
            //fabPhoto.setVisibility(View.GONE);

            if (adapter == null) {
                ArrayList<RecyclerEventAdapter.EventObject> events = getEvents(wall);
                adapter = new RecyclerEventAdapter(events, inflater, fragmentManager);
                wallListView.setAdapter(adapter);
                wallListView.setOnScrollListener(pauseOnScrollListener);
            } else {
                ((RecyclerEventAdapter) adapter).setEvent(getEvents(wall));
            }
        } else {
            //fabPhoto.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new RecyclerWallAdapter(this, wall, inflater, fragmentManager, isSuggested);
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
            Constants.isFragmentCommentsLoaded = false;
            Log.d("isFragmentCommentsLoaded: " + Constants.isFragmentCommentsLoaded, " was changed in onActivityCreated in FragmentWall");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", wallListView.getScrollY());
    }


    private void endlessAdd(final int lastItem) {
        if (doEndless == true) {
            jsonObjectOld = OfflineMode.loadJSON(OfflineMode.loadLong(Constants.VK_GROUP_ID));
            initGroupWall(jsonObjectOld, inflaterGlobal);
            scrollCommentsToBottom(wallListView, lastItem);
        }
    }

    private void endlessGet(final int Offset) {

        if (doEndless == true) {

            VKHelper.doGroupWallRequest(Offset, countPostDefaultForOffset, OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequestListener() {
                @Override
                public void onSuccess() {
                    OfflineMode.saveJSON(OfflineMode.loadLong(Constants.VK_GROUP_ID), OfflineMode.jsonPlus(jsonObjectOld, vkJson));
                }

                @Override
                public void onError() {
                    super.onError();
                    endlessGet(Offset);
                }
            });
        }
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