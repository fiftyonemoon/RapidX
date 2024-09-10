package com.fom.rapidx.views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

/**
 * 18th Sept 2022.
 * Hybrid view of {@link android.widget.ImageView}.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class RapidImageView extends androidx.appcompat.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    private int measureWith;
    private boolean measureMargin;
    private boolean measurePadding;
    private boolean autoScale;
    private boolean landscapeMode;

    private int customWidth = 1080;
    private int customHeight = 1920;

    public RapidImageView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public RapidImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public RapidImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    /**
     * Initialize {@link RapidImageView}.
     */
    private void initialize(Context context, AttributeSet attrs, int defStyle) {

        if (attrs != null) {

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RapidImageView, defStyle, 0);

            measureWith = array.getInt(R.styleable.RapidImageView_measureWith, Scaler.MeasureWith.none);
            measureMargin = array.getBoolean(R.styleable.RapidImageView_measureMargin, false);
            measurePadding = array.getBoolean(R.styleable.RapidImageView_measurePadding, false);
            int autoScaleFlag = array.getInt(R.styleable.RapidView_autoScale, 0);

            autoScale = autoScaleFlag == Scaler.AutoScale.parent;

            customWidth = array.getInt(R.styleable.RapidView_customWidth, 1080);
            customHeight = array.getInt(R.styleable.RapidView_customHeight, 1920);

            landscapeMode = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

            //add this view on global layout listener
            getViewTreeObserver().addOnGlobalLayoutListener(this);

            array.recycle();
        }
    }

    /**
     * {@link RapidImageView} on successfully populated.
     */
    @Override
    public void onGlobalLayout() {

        //once view is populated remove from global layout listener
        getViewTreeObserver().removeOnGlobalLayoutListener(this);

        scale(this); // scale view
    }

    /**
     * Request {@link Scaler} to scale view.
     *
     * @param view which we have to scale.
     */
    private void scale(View view) {
        new Scaler(customWidth, customHeight)
                .view(view)
                .measureWith(measureWith)
                .measureMargin(measureMargin)
                .measurePadding(measurePadding)
                .landscapeMode(landscapeMode)
                .autoScale(autoScale)
                .now(getContext());
    }
}