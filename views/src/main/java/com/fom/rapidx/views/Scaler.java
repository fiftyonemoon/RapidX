package com.fom.rapidx.views;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 18th Sept 2022.
 * A class to scale hybrid rapid views with applied attributes {@link MeasureWith}.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class Scaler {

    private View view;

    int w1080 = 1080;
    int h1920 = 1920;

    private int default_width;
    private int default_height;

    private int displayW;
    private int displayH;
    private int measureWith;
    private boolean measureMargin;
    private boolean measurePadding;
    private boolean landscapeMode;
    private boolean autoScale;

    /**
     * Default constructor.
     */
    public Scaler() {

    }

    /**
     * Scale with specific width and height.
     */
    public Scaler(int width, int height) {
        this.w1080 = width;
        this.h1920 = height;
    }

    /**
     * The view which we have to scale.
     */
    public Scaler view(View view) {
        this.view = view;
        return this;
    }

    /**
     * Measure view according to width and height.
     *
     * @param measureWith - {@link MeasureWith}.
     */
    public Scaler measureWith(@MeasureWith int measureWith) {
        this.measureWith = measureWith;
        return this;
    }

    /**
     * Measure applied margin.
     *
     * @param measureMargin - if true applied margin will be measured, false to keep as it is.
     */
    public Scaler measureMargin(boolean measureMargin) {
        this.measureMargin = measureMargin;
        return this;
    }

    /**
     * Measure applied padding.
     *
     * @param measurePadding - if true applied padding will be measured, false to keep as it is.
     */
    public Scaler measurePadding(boolean measurePadding) {
        this.measurePadding = measurePadding;
        return this;
    }

    /**
     * Set layout orientation.
     *
     * @param landscapeMode - device orientation.
     */
    public Scaler landscapeMode(boolean landscapeMode) {
        this.landscapeMode = landscapeMode;
        return this;
    }

    /**
     * Scale view even if width and height is not given.
     *
     * @param autoScale - true to scale view automatically else false.
     */
    public Scaler autoScale(boolean autoScale) {
        this.autoScale = autoScale;
        return this;
    }

    /**
     * Scale conditions.
     *
     * @param measureWith    - {@link MeasureWith}.
     * @param measureMargin  - if true applied margin will be measured, false to keep as it is.
     * @param measurePadding - if true applied padding will be measured, false to keep as it is.
     * @param landscapeMode  - device orientation.
     * @deprecated - use individual instead.
     */
    public Scaler with(@MeasureWith int measureWith, boolean measureMargin, boolean measurePadding, boolean landscapeMode) {
        this.measureWith = measureWith;
        this.measureMargin = measureMargin;
        this.measurePadding = measurePadding;
        this.landscapeMode = landscapeMode;
        return this;
    }

    /**
     * Start view scaling.
     *
     * @throws RuntimeException - view is missing {@link #view(View)}.
     */
    public void now(Context context) {

        preconditions(); //check null object

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        displayW = displayMetrics.widthPixels;
        displayH = displayMetrics.heightPixels;

        default_width = landscapeMode ? h1920 : w1080; //set width according to orientation
        default_height = landscapeMode ? w1080 : h1920; //set height according to orientation

        ViewGroup.LayoutParams params = view.getLayoutParams();

        updateWidthHeight(params); //update width height of the view
        updatePadding(); //update padding of the view
        updateMargin(params); //update margin of the view
        view.setLayoutParams(params); //set new params in the view
        view.requestLayout(); //request to refresh layout
    }

    /**
     * Update width and height of the view as per device width and height.
     */
    private void updateWidthHeight(ViewGroup.LayoutParams params) {
        int[] intrinsic = getIntrinsicWidthHeight();
        int width = autoScale ? intrinsic[0] : params.width;
        int height = autoScale ? intrinsic[1] : params.height;

        boolean withWidth = measureWith == MeasureWith.width;
        boolean withHeight = measureWith == MeasureWith.height;

        if (width > 0) {
            params.width = (withHeight ? displayH : displayW) * width / (withHeight ? default_height : default_width);
        }
        if (height > 0) {
            params.height = (withWidth ? displayW : displayH) * height / (withWidth ? default_width : default_height);
        }

        new Logs(view, Logs.logs.wh).show(params.width, params.height);
    }

    /**
     * Update padding of the view as per device width and height.
     */
    private void updatePadding() {
        int top = view.getPaddingTop();
        int bottom = view.getPaddingBottom();
        int start = getPaddingStart();
        int end = getPaddingEnd();

        int paddingTop = measurePadding ? displayH * top / default_height : top;
        int paddingBottom = measurePadding ? displayH * bottom / default_height : bottom;
        int paddingStart = measurePadding ? displayW * start / default_width : start;
        int paddingEnd = measurePadding ? displayW * end / default_width : end;

        view.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);

        new Logs(view, Logs.logs.padding).show(paddingStart, paddingTop, paddingEnd, paddingBottom);
    }

    /**
     * Update margin of the view as per device width and height.
     */
    private void updateMargin(ViewGroup.LayoutParams params) {

        if (params instanceof ViewGroup.MarginLayoutParams) {

            int top = ((ViewGroup.MarginLayoutParams) params).topMargin;
            int bottom = ((ViewGroup.MarginLayoutParams) params).bottomMargin;
            int start = getMarginStart(params);
            int end = getMarginEnd(params);

            int marginTop = measureMargin ? displayH * top / default_height : top;
            int marginBottom = measureMargin ? displayH * bottom / default_height : bottom;
            int marginStart = measureMargin ? displayW * start / default_width : start;
            int marginEnd = measureMargin ? displayW * end / default_width : end;

            ((ViewGroup.MarginLayoutParams) params).setMargins(marginStart, marginTop, marginEnd, marginBottom);

            new Logs(view, Logs.logs.margin).show(marginStart, marginTop, marginEnd, marginBottom);
        }
    }

    /**
     * Get start margin according to SDK {@link android.os.Build}.
     */
    private int getMarginStart(ViewGroup.LayoutParams params) {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                ((ViewGroup.MarginLayoutParams) params).getMarginStart() :
                ((ViewGroup.MarginLayoutParams) params).leftMargin;
    }

    /**
     * Get end margin according to SDK {@link android.os.Build}.
     */
    private int getMarginEnd(ViewGroup.LayoutParams params) {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                ((ViewGroup.MarginLayoutParams) params).getMarginEnd() :
                ((ViewGroup.MarginLayoutParams) params).rightMargin;
    }

    /**
     * Get start padding according to SDK {@link android.os.Build}.
     */
    private int getPaddingStart() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                view.getPaddingStart() : view.getPaddingLeft();
    }

    /**
     * Get end padding according to SDK {@link android.os.Build}.
     */
    private int getPaddingEnd() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                view.getPaddingEnd() : view.getPaddingRight();
    }

    /**
     * @return view's original width and height.
     */
    private int[] getIntrinsicWidthHeight() {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            if (imageView.getDrawable() != null) {
                return new int[]{imageView.getDrawable().getIntrinsicWidth()
                        , imageView.getDrawable().getIntrinsicHeight()};
            } else if (imageView.getBackground() != null) {
                return new int[]{imageView.getBackground().getIntrinsicWidth()
                        , imageView.getBackground().getIntrinsicHeight()};
            }
        } else if (view instanceof ViewGroup) {
            if (view.getBackground() != null) {
                return new int[]{view.getBackground().getIntrinsicWidth()
                        , view.getBackground().getIntrinsicHeight()};
            }
        }
        return new int[]{view.getWidth(), view.getHeight()};
    }

    /**
     * Check preconditions before scale the view.
     */
    private void preconditions() {
        if (view == null) {
            throw new RuntimeException("View is missing. To fix add 'view()' method");
        }
    }

    /**
     * MeasureWith Attribute class.
     */
    @IntDef({MeasureWith.none, MeasureWith.width, MeasureWith.height})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MeasureWith {

        /**
         * It will measure width and height of view according device width and height.
         */
        int none = 0;

        /**
         * It will measure width and height of view according device width.
         * Use in portrait mode for best practice.
         */
        int width = -1;

        /**
         * It will measure width and height of view according device height.
         * Use in landscape mode for best practice.
         */
        int height = -2;

    }

    /**
     * AutoScale Attribute class.
     */
    @IntDef({AutoScale.all, AutoScale.parent, AutoScale.children})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AutoScale {

        /**
         * It will scale only parent automatically.
         */
        int parent = 0x271201;

        /**
         * It will scale all parent children automatically.
         */
        int children = 0x271202;

        /**
         * It will scale parent with children automatically.
         */
        int all = parent | children;

    }
}
