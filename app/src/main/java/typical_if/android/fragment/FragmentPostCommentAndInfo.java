package typical_if.android.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.CommentsListAdapter;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Profile;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;

/**
 * Created by admin on 07.08.2014.
 */
public class FragmentPostCommentAndInfo extends Fragment {

    ListView listOfComments;
    ArrayList<VKApiComment> comments;
    ArrayList<Profile> profiles;
    CommentsListAdapter adapter;
    String message = null;
    EditText commentMessage;
    View rootView;
    int reply_to_comment = 0;
    Wall wall;
    VKWallPostWrapper postWrapper;
    VKApiPost post;
    String postColor;
    long gid;
    int position;
    WallAdapter.ViewHolder viewHolder;
    Button sendComment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public static FragmentPostCommentAndInfo newInstance(String postColor, int position, Wall wall, VKWallPostWrapper postWrapper) {
        FragmentPostCommentAndInfo fragment = new FragmentPostCommentAndInfo();
        fragment.postColor = postColor;
        fragment.wall = wall;
        fragment.position = position;
        fragment.postWrapper = postWrapper;
        fragment.post = postWrapper.post;
        fragment.gid = wall.group.id * -1;
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo_comment_and_info, container, false);
        listOfComments = ((ListView) rootView.findViewById(R.id.listOfComments));
        sendComment = (Button) rootView.findViewById(R.id.buttonSendComment);
        commentMessage = (EditText) rootView.findViewById(R.id.field_of_message_for_comment);

        final View wallItem = inflater.inflate(R.layout.wall_lv_item, null);
        viewHolder = new WallAdapter.ViewHolder(wallItem);
        WallAdapter.initViewHolder(viewHolder, postColor, wall, position, getFragmentManager(), postWrapper, getActivity().getBaseContext());

        viewHolder.txt_post_comment.setVisibility(View.GONE);
        viewHolder.img_post_comment.setVisibility(View.GONE);
        viewHolder.img_post_other.setVisibility(View.GONE);


        if (postWrapper.postTextChecker) {
            RelativeLayout textLayout = (RelativeLayout) viewHolder.postTextLayout.getChildAt(0);
            CheckBox checkBox = (CheckBox) textLayout.getChildAt(1);
            checkBox.setChecked(true);
            checkBox.setVisibility(View.GONE);
        }

        if (postWrapper.copyHistoryChecker) {
            if (post.copy_history.get(0).text.length() != 0) {
                LinearLayout copyHistoryContainer = (LinearLayout) ((RelativeLayout) viewHolder.copyHistoryLayout.getChildAt(0)).getChildAt(0);
                RelativeLayout parentCopyHistoryTextContainer = (RelativeLayout) copyHistoryContainer.findViewById(R.id.copyHistoryTextLayout);
                RelativeLayout textLayout = (RelativeLayout) parentCopyHistoryTextContainer.getChildAt(0);
                CheckBox checkBox = (CheckBox) textLayout.getChildAt(1);
                checkBox.setChecked(true);
                checkBox.setVisibility(View.GONE);
            }
        }
        listOfComments.addHeaderView(wallItem);

        updateCommentList(gid, post.id, listOfComments, inflater);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = commentMessage.getText().toString() + ", @club26363301 (fromMobileIF)";
                if (reply_to_comment == 0) {
                    VKHelper.createCommentForPost(gid, post.id, message, 0, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity().getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                            commentMessage.setText("");
                            updateCommentList(gid, post.id, listOfComments, inflater);
                        }
                    });
                } else {
                    VKHelper.createCommentForPost(gid, post.id, message, reply_to_comment, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity().getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                            commentMessage.setText("");
                            updateCommentList(gid, post.id, listOfComments, inflater);
                        }
                    });
                }
            }
        });
        return rootView;
    }


    public void updateCommentList(long gid, final long pid, final ListView listOfComments, final LayoutInflater inflater) {
        VKHelper.getCommentsForPost(gid, pid, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(response.json, pid);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        parseCommentList(OfflineMode.loadJSON(pid));
                    }
                }).start();
            }
        });


        if (!OfflineMode.isOnline(getActivity().getApplicationContext()) & OfflineMode.isJsonNull(pid)  ) {
                    parseCommentList(OfflineMode.loadJSON(pid));
            // If IsOnline and response from preferenses not null then load Json from preferenses
        }

        listOfComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    showContextMenu(position - 1);
                }

            }
        });
    }

    public void showContextMenu(int position) {
        VKApiComment comment = comments.get(position);

        CharSequence[] items;
        if (comment.from_id == Constants.USER_ID) {
            items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Поскаржитись", "Видалити"};
        } else {
            items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Поскаржитись"};
        }

        onInitContextMenu(items, position);
    }


    public void parseCommentList(final JSONObject response) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        JSONArray[] arrayOfComments = VKHelper.getResponseArrayOfComment(response);


        comments = VKHelper.getCommentsFromJSON(arrayOfComments[0]);

        Collections.sort(comments);
        profiles = Profile.getProfilesFromJSONArray(arrayOfComments[1]);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    String postColor = ItemDataSetter.getPostColor(gid);
                    adapter = new CommentsListAdapter(comments, profiles, inflater, postColor);
                    listOfComments.setAdapter(adapter);
                } else {
                    adapter.UpdateCommentList(comments, profiles, listOfComments);

                }
            }
        });
    }

    public void onInitContextMenu(final CharSequence[] items, final int position) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: {
                        Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).from_id + "");
                        getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Відкрити за допомогою")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    break;
                    case 1: {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reply_to_comment = comments.get(position).id;
                                commentMessage.setText(Identify(comments, profiles) + ", ");
                            }
                        });
                    }
                    break;
                    case 2: {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(comments.get(position).text);
                    }
                    break;
                    case 3: {
                        Toast.makeText(getActivity().getApplicationContext(), comments.get(position).likes + "", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case 4:
                        Dialogs.reportListDialog(Constants.mainActivity, gid, comments.get(position).id);
                        break;
                    case 5: {
                        VKHelper.deleteCommentForPost(gid, comments.get(position).id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                updateCommentList(gid, post.id, listOfComments, inflater);
                            }

                            @Override
                            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                super.attemptFailed(request, attemptNumber, totalAttempts);
                                Toast.makeText(getActivity().getApplicationContext(), "attemptFailed", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                Toast.makeText(getActivity().getApplicationContext(), "onError", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                                super.onProgress(progressType, bytesLoaded, bytesTotal);
                                Toast.makeText(getActivity().getApplicationContext(), "onProgress", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    break;

                    default:
                        Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.show();
    }

    public String Identify(ArrayList<VKApiComment> commentsList, ArrayList<Profile> profilesList) {
        String name = "";

        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).id == reply_to_comment) {
                for (int j = 0; j < profilesList.size(); j++) {
                    if (commentsList.get(i).from_id == profilesList.get(j).id) {
                        name = profilesList.get(j).first_name;
                    }
                }

            }

        }

        return name;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_device_item_remove, menu);
    }
}
