package typical_if.android.view;

/**
 * Created by gigamole on 23.01.15.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {
    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableImageView(Context context) {
        super(context);
    }

    private int resizeHeight =0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();

        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = resizeHeight = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight()
                    / (float) d.getIntrinsicWidth());
            resizeHeight =height;

            //  Log.v(VIEW_LOG_TAG, " height : "+height);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    public int getResizeHeight() {
        return resizeHeight;
    }

    public void setResizeHeight(int resizeHeight) {
        this.resizeHeight = resizeHeight;
    }

}
