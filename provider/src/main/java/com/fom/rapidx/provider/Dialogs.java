package com.fom.rapidx.provider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 18th Sept 2022.
 * Purpose of this class is less coding, save time and work anywhere in the app.
 *
 * @author <a ref="https://github.com/fiftyonemoon">hardkgosai</a>.
 * @since 1.0
 */
public class Dialogs {

    private static Alert alertInstance;
    private static Progress progressInstance;

    /**
     * {@link Alert} class constructor.
     */
    public Alert alert() {
        return alertInstance == null
                ? alertInstance = new Alert()
                : alertInstance;
    }

    /**
     * A class to show type of alert dialog.
     */
    public static class Alert {

        public Dialog delete() {
            return new Dialog(Type.delete);
        }

        public Dialog exit() {
            return new Dialog(Type.exit);
        }

        public Dialog save() {
            return new Dialog(Type.save);
        }

        public static class Dialog {

            private AlertDialog alertDialog;
            private final Type type;
            private boolean cancelable;

            public Dialog(Type type) {
                this.type = type;
            }

            public Dialog cancelable(boolean cancelable) {
                this.cancelable = cancelable;
                return this;
            }

            public void show(Context context, DialogInterface.OnClickListener listener) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(cancelable);
                builder.setTitle(type.getTitle());
                builder.setMessage(type.getMessage());
                builder.setPositiveButton(type.getTitle(), listener);
                builder.setNegativeButton(type.getCancel(), listener);
                alertDialog = builder.create();
                alertDialog.show();
            }

            public void dismiss() {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                    alertInstance = null;
                }
            }

            public AlertDialog getAlertDialog() {
                return alertDialog;
            }
        }
    }

    /**
     * Enum class for type of alert dialog.
     */
    public enum Type {

        delete(R.string.delete, R.string.are_you_sure_delete),
        exit(R.string.exit, R.string.are_you_sure_exit),
        save(R.string.save, R.string.are_you_sure_save);

        private final int title;
        private final int message;

        Type(int title, int message) {
            this.title = title;
            this.message = message;
        }

        public int getTitle() {
            return title;
        }

        public int getMessage() {
            return message;
        }

        public int getCancel() {
            return R.string.cancel;
        }
    }

    /**
     * {@link Progress} class constructor.
     */
    public Progress progress() {
        return progressInstance == null
                ? progressInstance = new Progress()
                : progressInstance;
    }

    /**
     * A class to show progress dialog in runtime.
     */
    public static class Progress {

        private ProgressDialog progressDialog;
        private String message;
        private boolean cancelable;

        public Progress cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Progress message(String message) {
            this.message = message;
            return this;
        }

        public void show(Context context) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(cancelable);
            progressDialog.setMessage(message != null
                    ? message
                    : context.getString(R.string.please_wait));
            progressDialog.show();
        }

        public void dismiss() {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
                progressInstance = null;
            }
        }

        public ProgressDialog getProgressDialog() {
            return progressDialog;
        }
    }
}
