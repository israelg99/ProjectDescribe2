package israel.projectdescribe.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import israel.projectdescribe.R;

/**
 * Created by Israel on 8/12/2016.
 */

public class PermissionDialog extends DialogFragment {

    /**
     * The names for the dialog bundle, which is accessed later in `onCreateDialog`.
     */
    private static final String REQUEST_PERMISSION = "REQUEST_PERMISSION";
    private static final String NAME_PERMISSION = "NAME_PERMISSION";
    private static final String MESSAGE = "MESSAGE";

    public static PermissionDialog newInstance(String message, String namePermission, int requestPermission) {
        PermissionDialog permissionDialog = new PermissionDialog();

        // We access the bundle later in `onCreateDialog()`
        Bundle bundle = new Bundle(3);
        bundle.putString(MESSAGE, message);
        bundle.putString(NAME_PERMISSION, namePermission);
        bundle.putInt(REQUEST_PERMISSION, requestPermission);

        permissionDialog.setArguments(bundle);

        return permissionDialog;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(savedInstanceState.getString(MESSAGE))
                .setPositiveButton(R.string.dialog_allow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{savedInstanceState.getString(NAME_PERMISSION)},
                                savedInstanceState.getInt(REQUEST_PERMISSION));
                    }
                })
                .setNegativeButton(R.string.dialog_deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, application exits.
                        getActivity().finish();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}