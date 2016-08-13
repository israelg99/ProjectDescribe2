package israel.projectdescribe.toast;

import android.app.Activity;

/**
 * Created by Israel on 8/12/2016.
 */

public class ToastHelper {

    /**
     * Shows a {@link android.widget.Toast} on the UI thread.
     *
     * @param text The message to show
     */
    public static void showToast(final Activity activity, final String text) {
        if (activity != null/*fragment compat*/) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    android.widget.Toast.makeText(activity, text, android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
