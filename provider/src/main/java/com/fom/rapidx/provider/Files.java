package com.fom.rapidx.provider;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 18th Sept 2022.
 * A class to handle file related functions.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class Files {

    /**
     * {@link Editor} class constructor.
     */
    public Editor editor() {
        return new Editor();
    }

    /**
     * A class to modified a file.
     */
    public static class Editor {

        /**
         * {@link Editor} listener interface.
         */
        public interface EditorCallback {
            void onComplete(String path);

            void onError(String message);
        }

        /**
         * File copy.
         * Copy file object to anywhere in external storage.
         *
         * @param input - A file to copy.
         * @param dest  - A destination path where you want to copy.
         */
        public void copy(Context context, File input, String dest, EditorCallback callback) {

            Utils utils = new Utils();

            //final destination path
            String finalDest = dest.isEmpty() ? input.getPath() : dest;

            //final path with unique name
            String finalPath = utils.getUniqueFileName(finalDest);

            //create file object of final path
            File output = new File(finalPath);

            boolean success = utils.writeFile(input, output); //write input data to output

            if (success) { //write success

                //add new output file to media store
                utils.addToMediaStore(context, output);

                if (callback != null) callback.onComplete(finalPath); //complete callback

            } else if (callback != null) {
                callback.onError("Failed to copy"
                        + ", Input file is valid?"
                        + ", Destination is valid?"); //error callback
            }
        }

        /**
         * Delete file.
         * <p>
         * If {@link ContentResolver} failed to delete the file, use trick,
         * SDK version is >= 29(Q)? use {@link SecurityException} and again request for delete.
         * SDK version is >= 30(R)? use {@link MediaStore #createDeleteRequest(ContentResolver, Collection)}.
         */
        public int delete(Context context, String path, ActivityResultLauncher<IntentSenderRequest> launcher) {

            if (context == null) {
                return Activity.RESULT_CANCELED;
            }

            boolean success = new File(path).delete();

            if (success) {
                return Activity.RESULT_OK;
            }

            ContentResolver contentResolver = context.getContentResolver();

            Utils utils = new Utils();
            Uri delete = utils.getUriFromPath(context, path);

            if (delete == null) {
                return Activity.RESULT_CANCELED;
            }

            try {
                //delete object using resolver
                int result = contentResolver.delete(delete, null, null);
                return result == 1
                        ? Activity.RESULT_OK
                        : Activity.RESULT_CANCELED;

            } catch (SecurityException e) {

                PendingIntent pendingIntent = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    ArrayList<Uri> collection = new ArrayList<>();
                    collection.add(delete);
                    pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    //if exception is recoverable then again send delete request using intent
                    if (e instanceof RecoverableSecurityException) {
                        RecoverableSecurityException exception = (RecoverableSecurityException) e;
                        pendingIntent = exception.getUserAction().getActionIntent();
                    }
                }

                if (pendingIntent != null) {
                    IntentSender sender = pendingIntent.getIntentSender();
                    IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                    launcher.launch(request);
                }
            }

            return Activity.RESULT_FIRST_USER;
        }

        /**
         * File duplicate.
         * Duplicate file object in same directory.
         *
         * @param file - A file to duplicate.
         */
        public void duplicate(Context context, File file, EditorCallback callback) {
            copy(context, file, "", callback);
        }

        /**
         * File rename.
         *
         * @param input  - file to rename.
         * @param rename - new file name.
         */
        public void rename(Context context, File input, String rename, EditorCallback callback) {

            Utils utils = new Utils();

            //get file extension
            String fileExt = utils.getFileExtension(input.getPath());

            //check new name has extension
            boolean isExt = rename.contains(fileExt);

            //temporary path with new name
            String tempPath = input.getPath().replace(input.getName(), rename) + (isExt ? "" : fileExt);

            //final path with unique name
            String finalPath = utils.getUniqueFileName(tempPath);

            //create file object of final path
            File output = new File(finalPath);

            if (input.exists()) { //check file existence

                if (input.renameTo(output)) { //if rename success

                    //delete old file from media store
                    utils.removeFromMediaStore(context, input);

                    //add new renamed file to media store
                    utils.addToMediaStore(context, output);

                    if (callback != null) callback.onComplete(finalPath); //complete callback

                } else if (callback != null) {
                    callback.onError("Failed to rename, Security manager denies write access. permissions are granted?"); // error callback
                }
            } else if (callback != null) callback.onError("File not exist"); //error callback
        }
    }

    /**
     * {@link Utils} class constructor.
     */
    public Utils utils() {
        return new Utils();
    }

    /**
     * A class of collection of file related functions.
     */
    public static class Utils {

        /**
         * Share the desired files using application of choice by user
         *
         * @param file      - the file to be shared
         * @param authority - path provide authority
         * @param mimetype  - file type
         */
        public void shareFile(Context context, File file, String authority, String mimetype) {
            Uri uri = FileProvider.getUriForFile(context, authority, file);
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(uri);
            shareFile(context, uris, mimetype);
        }

        /**
         * Share the desired files using application of choice by user
         *
         * @param files     - the list of files to be shared
         * @param authority - path provide authority
         * @param mimetype  - file type
         */
        public void shareMultipleFiles(Context context, List<File> files, String authority, String mimetype) {
            ArrayList<Uri> uris = new ArrayList<>();
            for (File file : files) {
                Uri uri = FileProvider.getUriForFile(context, authority, file);
                uris.add(uri);
            }
            shareFile(context, uris, mimetype);
        }

        /**
         * Shared the desired files using application of choice by user
         *
         * @param uris     - list of uris to be shared
         * @param mimetype - file type
         */
        private void shareFile(Context context, ArrayList<Uri> uris, String mimetype) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType(mimetype);
            openIntent(context, Intent.createChooser(intent, context.getString(R.string.choose_app)));
        }

        /**
         * opens a file in appropriate application
         *
         * @param path - path of the file to be opened
         */
        public void openFile(Context context, String path, String authority, String mimetype) {
            if (path == null) {
                return;
            }

            File file = new File(path);
            openFile(context, file, authority, mimetype);
        }

        /**
         * This function is used to open the created file
         * applications on the device.
         *
         * @param file - file to be open
         */
        public void openFile(Context context, File file, String authority, String mimetype) {

            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                Uri uri = FileProvider.getUriForFile(context, authority, file);

                target.setDataAndType(uri, mimetype);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                openIntent(context, Intent.createChooser(target, context.getString(R.string.choose_app)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Opens the targeted intent (if possible).
         *
         * @param intent - input intent
         */
        public void openIntent(Context context, Intent intent) {
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Get unique name of file if original name already exist in directory.
         */
        public String getUniqueFileName(String filepath) {

            File file = new File(filepath);

            if (!file.exists())
                return filepath;

            String extension = getFileExtension(filepath);

            File parentFile = file.getParentFile();
            if (parentFile != null) {
                File[] listFiles = parentFile.listFiles();

                if (listFiles != null) {
                    int append = checkRepeat(filepath, Arrays.asList(listFiles), extension);
                    filepath = filepath.replace(extension, "(" + append + ")" + extension);
                }
            }

            return filepath;
        }

        /**
         * Checks if the new file already exists.
         *
         * @param finalOutputFile Path of file to check
         * @param files           List.
         * @return Number to be added finally in the name to avoid overwrite
         */
        int checkRepeat(String finalOutputFile, final List<File> files, String extension) {
            boolean flag = true;
            int append = 0;
            while (flag) {
                append++;
                String name = finalOutputFile.replace(extension, "(" + append + ")" + extension);
                flag = files.contains(new File(name));
            }
            return append;
        }

        /**
         * Extracts file name from the path
         *
         * @param path - file path
         * @return - extracted filename
         */
        public String getFileName(String path) {
            if (path == null)
                return null;

            File file = new File(path);
            return file.exists()
                    ? file.getName()
                    : null;
        }

        /**
         * Extracts file name from the path
         *
         * @param path - file path.
         * @return - extracted filename without extension.
         */
        public String getFileNameWithoutExt(String path) {
            if (path == null) return null;

            String filename = getFileName(path);
            int index = filename.lastIndexOf(".");
            return index == -1
                    ? path
                    : index < filename.length()
                    ? filename.substring(0, index)
                    : null;
        }

        /**
         * Extracts extension from the path
         *
         * @param path - file path.
         * @return - extension of file.
         */
        public String getFileExtension(String path) {
            if (path == null) return null;

            int index = path.lastIndexOf(".");
            return index == -1
                    ? path
                    : index < path.length()
                    ? path.substring(index)
                    : null;
        }

        /**
         * Extracts extension from the path
         *
         * @param path - file path.
         * @return - extension of file.
         */
        public String getFileMimeType(String path) {
            if (path == null) return null;

            String extension = getFileExtension(path);

            if (extension != null) {
                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.replace(".", ""));
            }
            return null;
        }

        /**
         * @return - time in millis when the file was created.
         * <p>
         * More info:{@link BasicFileAttributes#creationTime()}
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public long getFileCreationTime(File file) throws IOException {
            BasicFileAttributes attr = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return attr.creationTime().toMillis();
        }

        /**
         * @return - time in millis when the last time file was modified.
         * <p>
         * More info:{@link BasicFileAttributes#lastModifiedTime()}
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public long getFileLastModifiedTime(File file) throws IOException {
            BasicFileAttributes attr = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return attr.lastModifiedTime().toMillis();
        }

        /**
         * @return - time in millis when the last time file was accessed.
         * <p>
         * more info:{@link BasicFileAttributes#lastAccessTime()}
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public long getFileLastAccessTime(File file) throws IOException {
            BasicFileAttributes attr = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return attr.lastAccessTime().toMillis();
        }

        /**
         * @return - file size. The size may differ from the actual size on the file system.
         * <p>
         * more info:{@link BasicFileAttributes#size()}
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public long getFileSize(File file) throws IOException {
            BasicFileAttributes attr = java.nio.file.Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return attr.size();
        }

        /**
         * Store file object into android media store.
         *
         * @param file - to be add.
         */
        private void addToMediaStore(Context context, File file) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
        }

        /**
         * Remove file object from android media store.
         *
         * @param file - to be delete.
         */
        private int removeFromMediaStore(Context context, File file) {

            //check each media store and delete file from valid media store
            // if i = 1 means file is deleted, if 0 check next media store.
            int i = deleteFromVideoMediaStore(context, file); //delete file from video media store
            if (i == 0)
                i = deleteFromAudioMediaStore(context, file); //delete file from audio media store
            if (i == 0)
                i = deleteFromImagesMediaStore(context, file); //delete file from images media store
            if (i == 0)
                i = deleteFromFileMediaStore(context, file); //delete file from files media store

            return i;
        }

        /**
         * Delete media from {@link MediaStore.Video}.
         *
         * @param file - to be delete.
         */
        public int deleteFromVideoMediaStore(Context context, File file) {
            ContentResolver resolver = context.getContentResolver();
            return resolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    , MediaStore.Video.Media.DATA + "=?"
                    , new String[]{file.getAbsolutePath()});
        }

        /**
         * Delete media from {@link MediaStore.Audio}.
         *
         * @param file - to be delete.
         */
        public int deleteFromAudioMediaStore(Context context, File file) {
            ContentResolver resolver = context.getContentResolver();
            return resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , MediaStore.Video.Media.DATA + "=?"
                    , new String[]{file.getAbsolutePath()});
        }

        /**
         * Delete media from {@link MediaStore.Images}.
         *
         * @param file - to be delete.
         */
        public int deleteFromImagesMediaStore(Context context, File file) {
            ContentResolver resolver = context.getContentResolver();
            return resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , MediaStore.Video.Media.DATA + "=?"
                    , new String[]{file.getAbsolutePath()});
        }

        /**
         * Delete media from {@link MediaStore.Files}.
         *
         * @param file - to be delete.
         */
        public int deleteFromFileMediaStore(Context context, File file) {
            ContentResolver resolver = context.getContentResolver();
            return resolver.delete(MediaStore.Files.getContentUri("external")
                    , MediaStore.Video.Media.DATA + "=?"
                    , new String[]{file.getAbsolutePath()});
        }

        /**
         * @return file path from media uri.
         */
        public String getPathFromUri(Context context, Uri uri) {
            Cursor cursor = context.getContentResolver().query(uri
                    , new String[]{MediaStore.MediaColumns.DATA}
                    , null
                    , null
                    , null);

            String text = null;

            if (cursor == null || cursor.getCount() == 0) return null;

            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                text = isColumnIndexValid(cursor, index)
                        ? cursor.getString(index)
                        : null;
            }

            cursor.close();

            return text;
        }

        /**
         * @return media uri from file path.
         */
        public Uri getUriFromPath(Context context, String path) {

            File file = new File(path);
            String mime = getFileMimeType(path);

            Uri mediaUri = mime.startsWith("image")
                    ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    : mime.startsWith("video")
                    ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    : mime.startsWith("audio")
                    ? MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    : MediaStore.Files.getContentUri("external");

            Cursor cursor = context.getContentResolver().query(
                    mediaUri,
                    new String[]{MediaStore.MediaColumns._ID},
                    MediaStore.MediaColumns.DATA + "=? ",
                    new String[]{path}, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                int index = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int id = isColumnIndexValid(cursor, index) ? cursor.getInt(index) : -1;
                cursor.close();

                return id >= 0
                        ? Uri.withAppendedPath(mediaUri, id + "")
                        : null;

            } else {

                if (file.exists()) {

                    ContentResolver resolver = context.getContentResolver();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, file.getAbsolutePath());
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
                        Uri uri = resolver.insert(mediaUri, contentValues);
                        contentValues.clear();
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                        resolver.update(mediaUri, contentValues, null, null);
                        return uri;

                    } else {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.DATA, path);
                        return resolver.insert(mediaUri, values);
                    }

                } else {
                    return null;
                }
            }
        }

        /**
         * Check and return cursor has column or not.
         */
        boolean isColumnIndexValid(Cursor cursor, int columnIndex) {
            return columnIndex >= 0 && cursor != null && !cursor.isNull(columnIndex);
        }

        /**
         * Write input file data into output file.
         *
         * @param input  - source file.
         * @param output - where data will written.
         */
        public boolean writeFile(File input, File output) {

            try (InputStream in = new FileInputStream(input)) {  //input stream

                OutputStream out = new FileOutputStream(output); //output stream

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len); //write input file data to output file
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
