package israel.projectdescribe.dialogs;

/**
 * Created by Israel on 8/12/2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Shows an error message dialog.
 */
public class ErrorDialog extends DialogFragment {

    /**
     * String error message argument for bundling.
     */
    private static final String ARG_MESSAGE = "MESSAGE";
    private static String getArgMessage() {
        return ARG_MESSAGE;
    }

    public static ErrorDialog newInstance(String message) {
        ErrorDialog dialog = new ErrorDialog();

        // Bundle all the parameters, in this case just an error message.
        Bundle args = new Bundle();
        args.putString(getArgMessage(), message);

        // Set the bundle arguments as the dialog's arguments.
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(getArguments().getString(getArgMessage()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // After the user confirms the error the activity finishes.
                        activity.finish();
                    }
                })
                .create();
    }

}