package typical_if.android.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import typical_if.android.R;

/**
 * Created by gigamole on 13.02.15.
 */
public class TouchMakePostImageButton extends ImageButton {

    public TouchMakePostImageButton(Context context) {
        super(context);
    }

    public TouchMakePostImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchMakePostImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_ic_make_post));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                getBackground().setColorFilter(getResources().getColor(R.color.APP_SELECTOR_COLOR), PorterDuff.Mode.SRC_ATOP);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL: {
                getBackground().clearColorFilter();
                invalidate();
                break;
            }
        }

        return super.onTouchEvent(event);
    }
}
