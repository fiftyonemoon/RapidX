package com.fom.rapidx.provider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 18th Sept 2022.
 * <p>
 * A class to read media available in the device.
 *
 * @author <a ref="https://github.com/fiftyonemoon/">hardkgosai</a>.
 * @since 1.0
 */
public class Media {

    private Context context;
    private MediaAction action;
    private MediaObserver observer;
    private boolean withAlbumArt;
    private boolean isTerminate;

    public static final HashMap<String, ArrayList<MediaObject>> audioMap = new HashMap<>();
    public static final HashMap<String, ArrayList<MediaObject>> videoMap = new HashMap<>();
    public static final HashMap<String, ArrayList<MediaObject>> imagesMap = new HashMap<>();
    public static final ArrayList<MediaObject> audioList = new ArrayList<>();
    public static final ArrayList<MediaObject> selectedAudioList = new ArrayList<>();
    public static final ArrayList<MediaObject> videoList = new ArrayList<>();
    public static final ArrayList<MediaObject> selectedVideoList = new ArrayList<>();
    public static final ArrayList<MediaObject> imagesList = new ArrayList<>();
    public static final ArrayList<MediaObject> selectedImagesList = new ArrayList<>();

    /**
     * Pass context.
     */
    public Media with(Context context) {
        this.context = context;
        return this;
    }

    /**
     * Set {@link MediaAction} according to need.
     */
    public Media action(MediaAction action) {
        this.action = action;
        return this;
    }

    /**
     * Set true to get audio album thumb else false.
     */
    public Media withAlbumArt(boolean withAlbumArt) {
        this.withAlbumArt = withAlbumArt;
        return this;
    }

    /**
     * Terminate currently active executor task.
     * <p>
     * {@link MediaObserver#onComplete()} will called after termination.
     */
    public void terminate() {
        this.isTerminate = true;
    }

    /**
     * Observe retrieving process.
     */
    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void observe(MediaObserver observer) {

        preconditions();

        this.observer = observer;

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {

            clear();
            retrieve();

            handler.post(() -> {
                if (observer != null) {
                    observer.onComplete();
                }
            });
        });
    }

    /**
     * Check pre-conditions before start.
     */
    void preconditions() {
        if (context == null) {
            throw new NullPointerException("Context should not be null. to fix add 'with()' method.");
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Allow READ_EXTERNAL_STORAGE permission to access media.");
        }
    }

    /**
     * Start retrieving media object using {@link android.content.ContentResolver}.
     */
    private void retrieve() {

        Cursor cursor = context.getContentResolver().query(action.getUri()
                , null
                , null
                , null
                , null);

        if (cursor == null || cursor.getCount() == 0) return;

        cursor.moveToFirst();

        do {

            if (isTerminate) break;

            if (observer != null) {
                observer.onObserving(cursor.getPosition());
            }

            MediaObject object = new MediaObject();
            setCursorCommonObject(cursor, object);

            if (action == MediaAction.Audio || action == MediaAction.Video) {
                setCursorObject(cursor, object);
            }

            if (action == MediaAction.Audio && withAlbumArt) {
                object.art = getAlbumArt(cursor);
            }

            saveObject(object);

            if (observer != null) {
                int progress = cursor.getPosition() * 100 / cursor.getCount();
                observer.onProgress(cursor.getPosition(), progress);
            }

        } while (cursor.moveToNext());

        cursor.close();
    }

    /**
     * Set common cursor data.
     */
    private void setCursorCommonObject(Cursor cursor, MediaObject object) {
        object.id = getValidColumnValue_String(cursor, MediaColumns.ID);
        object.bucketId = getValidColumnValue_String(cursor, MediaColumns.BUCKET_ID);
        object.bucketName = getValidColumnValue_String(cursor, MediaColumns.BUCKET_DISPLAY_NAME);
        object.uri = getValidColumnValue_String(cursor, MediaColumns.DATA);
        object.name = getValidColumnValue_String(cursor, MediaColumns.DISPLAY_NAME);
        object.mime = getValidColumnValue_String(cursor, MediaColumns.MIME_TYPE);
        object.size = getValidColumnValue_Long(cursor, MediaColumns.SIZE);
        object.date = getValidColumnValue_Long(cursor, MediaColumns.DATE_MODIFIED);

        if (object.bucketName.isEmpty()) {
            File child = new File(object.uri);
            File parent = child.exists() ? child.getParentFile() : null;
            object.bucketName = parent != null && parent.exists() ? parent.getName() : "Unknown";
        }
    }

    /**
     * Set data using {@link Cursor}.
     */
    private void setCursorObject(Cursor cursor, MediaObject object) {
        object.album = getValidColumnValue_String(cursor, MediaColumns.ALBUM);
        object.artist = getValidColumnValue_String(cursor, MediaColumns.ARTIST);
        object.composer = getValidColumnValue_String(cursor, MediaColumns.COMPOSER);
        object.genre = getValidColumnValue_String(cursor, MediaColumns.GENRE);
        object.year = getValidColumnValue_String(cursor, MediaColumns.YEAR);
        object.resolution = getValidColumnValue_String(cursor, MediaColumns.RESOLUTION);
        object.duration = getValidColumnValue_Long(cursor, MediaColumns.DURATION);
    }

    /**
     * @return valid column {@link String} value.
     */
    private String getValidColumnValue_String(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);
        if (isColumnIndexValid(cursor, columnIndex)) {
            return cursor.getString(columnIndex);
        } else return "";
    }

    /**
     * @return valid column {@link Long} value.
     */
    private long getValidColumnValue_Long(Cursor cursor, String column) {
        return getValidColumnValue_Long(cursor, column, 0);
    }

    /**
     * @return valid column {@link Long} value.
     */
    private long getValidColumnValue_Long(Cursor cursor, String column, long def) {
        int columnIndex = cursor.getColumnIndex(column);
        if (isColumnIndexValid(cursor, columnIndex)) {
            return cursor.getLong(columnIndex);
        } else return def;
    }

    /**
     * Check and return cursor has column or not.
     */
    private boolean isColumnIndexValid(Cursor cursor, int columnIndex) {
        return columnIndex >= 0 && cursor != null && !cursor.isNull(columnIndex);
    }

    /**
     * Save object.
     */
    private void saveObject(MediaObject object) {

        save(object, action.getMap()); //save into folder wise map

        if (!action.getList().contains(object)) { //save into list
            action.getList().add(object);
        }
    }

    /**
     * Save object in particular folder.
     */
    private void save(MediaObject object, HashMap<String, ArrayList<MediaObject>> map) {

        String key = object.bucketName;

        if (key == null || key.trim().isEmpty()) return;

        if (!map.containsKey(key)) {

            ArrayList<MediaObject> arrayFileList = new ArrayList<>();
            arrayFileList.add(object);
            map.put(key, arrayFileList);

        } else {

            ArrayList<MediaObject> list = map.get(key);

            if (list != null && !list.contains(object)) {
                list.add(object);
                sort(list); //first sort the list by name and put in the map
                map.put(key, list);
            }
        }
    }

    /**
     * This function is only for {@link MediaAction#Audio} action.
     *
     * @return audio album thumbnail.
     */
    public String getAlbumArt(Cursor cursor) {

        long albumId = getValidColumnValue_Long(cursor, MediaStore.Audio.Media.ALBUM_ID, -1);

        if (albumId == -1) return null;

        Cursor cursorAlbum = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?", new String[]{String.valueOf(albumId)}, null);

        String art = "";

        if (cursorAlbum.moveToFirst()) {
            art = getValidColumnValue_String(cursor, MediaStore.Audio.Albums.ALBUM_ART);
        }

        cursorAlbum.close();

        return art;
    }

    /**
     * Sort media list by name.
     */
    private void sort(ArrayList<MediaObject> list) {
        Collections.sort(list, (o1, o2) -> {
            boolean isNull = o1 == null || o1.name == null
                    || o2 == null || o2.name == null;
            return isNull
                    ? -1
                    : o1.name.compareTo(o2.name);
        });
    }

    /**
     * Clear list and map.
     */
    private void clear() {
        action.getMap().clear();
        action.getList().clear();
        action.getSelectedList().clear();
    }

    /**
     * Media observer for observing each media object while reading.
     * Make sure use runOnUiThread while working with main UI component on result.
     */
    public interface MediaObserver {
        void onObserving(int position);

        void onProgress(int position, int progress);

        void onComplete();
    }

    /**
     * Media action to perform specific task.
     * <p>
     * Each action containing particular uri address, a list which contains all media objects,
     * a selected list which contains selected media objects and a map which contains folder wise media objects.
     */
    public enum MediaAction {
        Audio(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioList, selectedAudioList, audioMap),
        Video(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoList, selectedVideoList, videoMap),
        Images(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imagesList, selectedImagesList, imagesMap);

        private final Uri uri;
        private final ArrayList<MediaObject> list;
        private final ArrayList<MediaObject> selectedList;
        private final HashMap<String, ArrayList<MediaObject>> map;

        MediaAction(Uri uri, ArrayList<MediaObject> list, ArrayList<MediaObject> selectedList, HashMap<String, ArrayList<MediaObject>> map) {
            this.uri = uri;
            this.list = list;
            this.selectedList = selectedList;
            this.map = map;
        }

        /**
         * @return uri address.
         */
        @NonNull
        public Uri getUri() {
            return uri;
        }

        /**
         * @return media list.
         */
        @NonNull
        public ArrayList<MediaObject> getList() {
            return list;
        }

        /**
         * @return selected media list.
         */
        @NonNull
        public ArrayList<MediaObject> getSelectedList() {
            return selectedList;
        }

        /**
         * @return media map.
         */
        @NonNull
        public HashMap<String, ArrayList<MediaObject>> getMap() {
            return map;
        }
    }

    /**
     * Media columns.
     * <p>
     * Refer {@link MediaStore.MediaColumns} for more.
     */
    interface MediaColumns {
        String ID = "_id";
        String DATA = "_data";
        String DISPLAY_NAME = "_display_name";
        String SIZE = "_size";
        String BUCKET_ID = "bucket_id";
        String BUCKET_DISPLAY_NAME = "bucket_display_name";
        String DATE_MODIFIED = "date_modified";
        String DURATION = "duration";
        String ALBUM = "album";
        String ARTIST = "artist";
        String COMPOSER = "composer";
        String GENRE = "genre";
        String MIME_TYPE = "mime_type";
        String RESOLUTION = "resolution";
        String YEAR = "year";
    }

    /**
     * Data class for {@link Media}.
     */
    public static class MediaObject implements Serializable {

        public String bucketId;
        public String bucketName;
        public String id;
        public String name;
        public String uri;
        public String mime;
        public String album;
        public String art;
        public String artist;
        public String composer;
        public String genre;
        public String year;
        public String resolution;
        public long size;
        public long date;
        public long duration;
        public boolean selected;

        public MediaObject() {
        }

        @NonNull
        @Override
        public String toString() {
            return "MediaObject{" +
                    "bucketId='" + bucketId + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", mime='" + mime + '\'' +
                    ", album='" + album + '\'' +
                    ", art='" + art + '\'' +
                    ", artist='" + artist + '\'' +
                    ", composer='" + composer + '\'' +
                    ", genre='" + genre + '\'' +
                    ", year='" + year + '\'' +
                    ", resolution='" + resolution + '\'' +
                    ", size=" + size +
                    ", date=" + date +
                    ", duration=" + duration +
                    ", selected=" + selected +
                    '}';
        }
    }
}
