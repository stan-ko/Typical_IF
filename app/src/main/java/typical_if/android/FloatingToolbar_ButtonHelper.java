package typical_if.android;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.vk.sdk.VKSdk;

import org.json.JSONObject;

import typical_if.android.fragment.FragmentWall;

/**
 * Created by SokeOner on 4/6/15.
 */
public class FloatingToolbar_ButtonHelper {

    public static void setToolbarAttachments(final long groupIndex) {
        switch ((int) groupIndex) {
            case 0:
                Constants.Mtitle = TIFApp.getAppContext().getString(R.string.menu_group_title_tf);
                break;
            case 1:
                Constants.Mtitle = TIFApp.getAppContext().getString(R.string.menu_group_title_tz);
                break;
            case 2:
                Constants.Mtitle = TIFApp.getAppContext().getString(R.string.menu_group_title_fb);
                break;
            case 3:
                Constants.Mtitle = TIFApp.getAppContext().getString(R.string.menu_group_title_fn);
                break;
            case 4:
                Constants.Mtitle = TIFApp.getAppContext().getString(R.string.menu_group_title_stantsiya);
                break;
            case 5:
                Constants.Mtitle = TIFApp.getAppContext().getString(R.string.menu_group_title_events);
                break;
        }
    }

    public static SearchView CreateSearchView(final Menu menu, final ComponentName componentName){
        SearchManager searchManager = (SearchManager) TIFApp.getAppContext().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search_item).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint("Що ви шукаєте?");

        return searchView;
    }

    public static void onPrepareToolBarOptionsMenu(final Menu menu) {
        if (OfflineMode.loadLong(Constants.VK_GROUP_ID)==Constants.FB_ID){
            menu.findItem(R.id.action_search_item).setVisible(true);
            menu.findItem(R.id.list_of_tags).setVisible(true);
        }
        VKHelper.isMember(OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), new VKRequestListener() {
            @Override
            public void onSuccess() {
                if (hasJson) {
                    Constants.isMember = vkJson.optInt(VKHelper.TIF_VK_SDK_KEY_RESPONSE);
                    if (VKSdk.isLoggedIn()) {
                        if (Constants.isMember == 0) {
                            try {
                                menu.findItem(R.id.join_leave_group).setTitle(TIFApp.getAppContext().getString(R.string.ab_title_group_join));
                            } catch (Exception e) {
                            }
                        } else {
                            try {
                                menu.findItem(R.id.join_leave_group).setTitle(TIFApp.getAppContext().getString(R.string.ab_title_group_leave));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            @Override
            public void onError(){
                menu.findItem(R.id.join_leave_group).setVisible(false);
            }

        });
    }

    public static void totalToolbarShow(View totalToolbar){
       totalToolbar.animate().translationY(-totalToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
   }

    public static void totalToolbarHide(View totalToolbar){
        totalToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    public static void animationShow(View view){
        view.animate().translationY(-view.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }

    public static void animationHide(View view){
        view.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }


}
