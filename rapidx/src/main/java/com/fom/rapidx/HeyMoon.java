package com.fom.rapidx;

import com.fom.rapidx.provider.Dialogs;
import com.fom.rapidx.provider.Directory;
import com.fom.rapidx.provider.Files;
import com.fom.rapidx.provider.Media;
import com.fom.rapidx.ui.UI;
import com.fom.rapidx.views.Scaler;

/**
 * 18th Sept 2022.
 * A virtual assistant of rapid library.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class HeyMoon {

    /**
     * {@link Scaler} class constructor.
     * A class that scale layout views.
     *
     * @since 1.0
     */
    public static Scaler scale() {
        return new Scaler();
    }

    /**
     * {@link UI} class constructor.
     * A class that handle ui views.
     *
     * @since 1.0
     */
    public static UI ui() {
        return new UI();
    }

    /**
     * {@link Dialogs} class constructor.
     * A class to show alert & progress dialog.
     *
     * @since 1.0
     */
    public static Dialogs dialogs() {
        return new Dialogs();
    }

    /**
     * {@link Files} class constructor.
     * A collection class of file functions.
     *
     * @since 1.0
     */
    public static Files file() {
        return new Files();
    }

    /**
     * {@link Directory} class constructor.
     * A class to create directory as well as file in android specified directory.
     *
     * @since 1.0
     */
    public static Directory directory() {
        return new Directory();
    }

    /**
     * {@link Media} class constructor.
     * A class to read all media objects from device.
     *
     * @since 1.0
     */
    public static Media media() {
        return new Media();
    }
}
