package com.fom.rapidx.provider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 27th Sept 2022.
 * A class to create new directory or file in android specified directory.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class Directory {

    private Context context;
    private int type;

    public Directory with(Context context) {
        this.context = context;
        return this;
    }

    /**
     * Provide media type like audio, video or images.
     */
    public Directory type(@DirectoryType int type) {
        this.type = type;
        return this;
    }

    /**
     * Create directory.
     */
    public boolean createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return dir.exists();
    }

    /**
     * Create file with directory in android directories.
     */
    public Uri createFile(String directory, String filename) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directory);
            return generateUri(contentValues);

        } else {

            File file = new File(directory, filename);

            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        return Uri.parse(file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * Create file in android directories.
     */
    public Uri createFile(String filename) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        return generateUri(contentValues);
    }

    /**
     * @return new media uri address.
     */
    public Uri generateUri() {
        return generateUri(new ContentValues());
    }

    /**
     * @param contentValues - set of media values.
     * @return new media uri address.
     */
    public Uri generateUri(ContentValues contentValues) {
        return context.getContentResolver().insert(getUri(), contentValues);
    }

    /**
     * @return media uri address or null if {@link DirectoryType} is not defined.
     */
    public Uri getUri() {
        if (type == DirectoryType.Audio) {
            return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else if (type == DirectoryType.Video) {
            return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (type == DirectoryType.Images) {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (type == DirectoryType.Document) {
            return MediaStore.Files.getContentUri("external");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && type == DirectoryType.Downloads) {
            return MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        } else return null;
    }

    @IntDef({DirectoryType.Audio, DirectoryType.Video, DirectoryType.Images, DirectoryType.Document, DirectoryType.Downloads})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectoryType {
        /**
         * Android Music directory.
         */
        int Audio = 0x27129600;
        /**
         * Android Movie directory.
         */
        int Video = 0x27129601;
        /**
         * Android Pictures directory.
         */
        int Images = 0x27129602;
        /**
         * Android Documents directory.
         */
        int Document = 0x27129603;
        /**
         * Android Downloads directory.
         */
        @RequiresApi(api = Build.VERSION_CODES.Q)
        int Downloads = 0x27129604;
    }
}
