package com.fom.rapidx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 18th Sept 2022.
 * Hybrid view of {@link ConstraintLayout}.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class RapidConstraintLayout extends ConstraintLayout {

    public RapidConstraintLayout(Context context) {
        super(context);
        new RapidView(context, this, null, 0);
    }

    public RapidConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        new RapidView(context, this, attrs, 0);
    }

    public RapidConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new RapidView(context, this, attrs, defStyleAttr);
    }

    @Override
    public ConstraintLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * {@link RapidConstraintLayout} own layout params.
     */
    public static class LayoutParams extends ConstraintLayout.LayoutParams {

        public boolean scale = true;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RapidConstraintLayout_Layout);
            try {
                if (a.hasValue(R.styleable.RapidConstraintLayout_Layout_scale)) {
                    scale = a.getBoolean(R.styleable.RapidConstraintLayout_Layout_scale, true);
                }
            } finally {
                a.recycle();
            }
        }
    }
}
