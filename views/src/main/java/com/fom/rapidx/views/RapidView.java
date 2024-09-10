package com.fom.rapidx.views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * 18th Sept 2022.
 * A class to handle hybrid rapid views.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class RapidView implements ViewTreeObserver.OnGlobalLayoutListener {

    private final Context context;
    private final View view;
    private int measureWith;
    private boolean measurePadding;
    private boolean measureMargin;
    private boolean withChildren;
    private boolean autoScaleParent;
    private boolean autoScaleChildren;
    private boolean landscapeMode;

    private int customWidth = 1080;
    private int customHeight = 1920;

    public RapidView(Context context, View view, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        this.view = view;
        initialize(attrs, defStyleAttr);
    }

    /**
     * Initialize {@link RapidView}.
     */
    private void initialize(AttributeSet attrs, int defStyle) {

        if (attrs != null) {

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RapidView, defStyle, 0);
            measureWith = array.getInt(R.styleable.RapidView_measureWith, Scaler.MeasureWith.none);
            measureMargin = array.getBoolean(R.styleable.RapidView_measureMargin, false);
            measurePadding = array.getBoolean(R.styleable.RapidView_measurePadding, false);
            withChildren = array.getBoolean(R.styleable.RapidView_scaleChildren, false);
            int autoScaleFlag = array.getInt(R.styleable.RapidView_autoScale, 0);

            customWidth = array.getInt(R.styleable.RapidView_customWidth, 1080);
            customHeight = array.getInt(R.styleable.RapidView_customHeight, 1920);

            autoScaleParent = autoScaleFlag == Scaler.AutoScale.parent
                    || autoScaleFlag == Scaler.AutoScale.all;
            autoScaleChildren = autoScaleFlag == Scaler.AutoScale.children
                    || autoScaleFlag == Scaler.AutoScale.all;

            landscapeMode = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

            array.recycle();
        }

        //add this view on global layout listener
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * {@link RapidView} on successfully populated.
     */
    @Override
    public void onGlobalLayout() {
        // once view is populated remove from global layout listener
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        scale(view, autoScaleParent); // scale parent view first

        if (withChildren) {
            scaleChildren(view); // scale parent view children
        }
    }

    /**
     * Scaler all {@link RapidView} child.
     */
    private void scaleChildren(View parent) {

        for (int index = 0; index < ((ViewGroup) parent).getChildCount(); index++) {

            View view = ((ViewGroup) parent).getChildAt(index);

            if (isValid(view)) { //check validation of the view

                boolean canScale = canScale(view.getLayoutParams());

                if (canScale) {
                    scale(view, autoScaleChildren);
                }

                if (view instanceof ViewGroup) {
                    scaleChildren(view); // check view contains any child
                }
            }
        }
    }

    /**
     * Request {@link Scaler} to scale view.
     *
     * @param view which we have to scale.
     */
    private void scale(View view, boolean autoScale) {
        new Scaler(customWidth, customHeight)
                .view(view)
                .measureWith(measureWith)
                .measureMargin(measureMargin)
                .measurePadding(measurePadding)
                .landscapeMode(landscapeMode)
                .autoScale(autoScale)
                .now(context);
    }

    boolean canScale(ViewGroup.LayoutParams params) {
        if (params instanceof RapidConstraintLayout.LayoutParams) {
            return ((RapidConstraintLayout.LayoutParams) params).scale;
        } else if (params instanceof RapidFrameLayout.LayoutParams) {
            return ((RapidFrameLayout.LayoutParams) params).scale;
        } else if (params instanceof RapidRelativeLayout.LayoutParams) {
            return ((RapidRelativeLayout.LayoutParams) params).scale;
        } else if (params instanceof RapidLinearLayout.LayoutParams) {
            return ((RapidLinearLayout.LayoutParams) params).scale;
        } else return true;
    }

    /**
     * Check validation before scale the view.
     * Note: validation are only for the children view.
     */
    private boolean isValid(View view) {
        return !(view instanceof RapidImageView
                || view instanceof RapidRelativeLayout
                || view instanceof RapidLinearLayout
                || view instanceof RapidConstraintLayout
                || view instanceof RapidFrameLayout);
    }
}
