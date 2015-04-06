package typical_if.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.vk.sdk.VKSdk;

/**
 * Created by SokeOner on 4/6/15.
 */
public class ToolBarHelper {

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

    public static void onPrepareToolBarOptionsMenu(final Menu menu) {
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
                menu.findItem(R.id.join_leave_group).setEnabled(false);
            }

        });
    }

   public static void totalToolbarShow(View totalToolbar){
       totalToolbar.animate().translationY(-totalToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
   }

    public static void totalToolbarHide(View totalToolbar){
        totalToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
