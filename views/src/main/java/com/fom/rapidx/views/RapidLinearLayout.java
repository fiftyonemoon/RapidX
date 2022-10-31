package com.fom.rapidx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 18th Sept 2022.
 * Hybrid view of {@link LinearLayout}.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class RapidLinearLayout extends LinearLayout {

    public RapidLinearLayout(Context context) {
        super(context);
        new RapidView(context, this, null, 0);
    }

    public RapidLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        new RapidView(context, this, attrs, 0);
    }

    public RapidLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new RapidView(context, this, attrs, defStyleAttr);
    }

    @Override
    public LinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * {@link RapidLinearLayout} own layout params.
     */
    public static class LayoutParams extends LinearLayout.LayoutParams {

        public boolean scale = true;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RapidLinearLayout_Layout);
            try {
                if (array.hasValue(R.styleable.RapidLinearLayout_Layout_scale)) {
                    scale = array.getBoolean(R.styleable.RapidLinearLayout_Layout_scale, true);
                }
            } finally {
                array.recycle();
            }
        }
    }
}
