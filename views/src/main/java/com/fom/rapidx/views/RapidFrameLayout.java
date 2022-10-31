package com.fom.rapidx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 18th Sept 2022.
 * Hybrid view of {@link FrameLayout}.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class RapidFrameLayout extends FrameLayout {

    public RapidFrameLayout(Context context) {
        super(context);
        new RapidView(context, this, null, 0);
    }

    public RapidFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        new RapidView(context, this, attrs, 0);
    }

    public RapidFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new RapidView(context, this, attrs, defStyleAttr);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * {@link RapidFrameLayout} own layout params.
     */
    public static class LayoutParams extends FrameLayout.LayoutParams {

        public boolean scale = true;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RapidFrameLayout_Layout);
            try {
                if (array.hasValue(R.styleable.RapidFrameLayout_Layout_scale)) {
                    scale = array.getBoolean(R.styleable.RapidFrameLayout_Layout_scale, true);
                }
            } finally {
                array.recycle();
            }
        }
    }
}
