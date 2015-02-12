package typical_if.android.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by gigamole on 12.02.15.
 */
public class ToggleFloatingActionsMenu extends FloatingActionsMenu {

    private static final int TRANSLATE_DURATION_MILLIS = 200;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private boolean mVisible = true;

    public ToggleFloatingActionsMenu(Context context) {
        super(context);
    }

    public ToggleFloatingActionsMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleFloatingActionsMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void show() {
        show(true);
    }
    public void hide() {
        hide(true);
    }
    public void show(boolean animate) {
        toggle(true, animate, false);
    }
    public void hide(boolean animate) {
        toggle(false, animate, false);
    }
    private void toggle(final boolean visible, final boolean animate, boolean force) {
        if (mVisible != visible || force) {
            mVisible = visible;
            int height = getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggle(visible, animate, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int translationY = visible ? 0 : height + getMarginBottom();
            if (animate) {
                ViewPropertyAnimator.animate(this).setInterpolator(mInterpolator)
                        .setDuration(TRANSLATE_DURATION_MILLIS)
                        .translationY(translationY);
            } else {
                ViewHelper.setTranslationY(this, translationY);
            }

// On pre-Honeycomb a translated view is still clickable, so we need to disable clicks manually
            if (!hasHoneycombApi()) {
                setClickable(visible);
            }
        }
    }

    private int getMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    private boolean hasHoneycombApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

}
