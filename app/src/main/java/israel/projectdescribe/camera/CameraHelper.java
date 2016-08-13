package israel.projectdescribe.camera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import israel.projectdescribe.dialogs.ErrorDialog;
import israel.projectdescribe.toast.ToastHelper;

/**
 * Created by Israel on 8/12/2016.
 */

public class CameraHelper {

    private static String TAG = CameraHelper.class.getName();

    /**
     * Check if the device has camera hardware.
     */
    public static boolean checkCameraHardware(Activity activity) {
        Log.d(TAG, "checkCameraHardware()");
        ToastHelper.showToast(activity, "Checking for camera hardware..");
        if (activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // This device has a camera
            Log.d(TAG, "checkCameraHardware() - Camera hardware found");
            ToastHelper.showToast(activity, "Camera hardware found");
            return true;
        } else {
            // No camera found on this device
            Log.d(TAG, "checkCameraHardware() - No camera hardware found");

            // Display error dialog
            ErrorDialog errorDialog = ErrorDialog.newInstance("No camera hardware found");
            errorDialog.show(activity.getFragmentManager(), "error-dialog");

            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(Activity activity){
        Log.d(TAG, "getCameraInstance()");

        Camera camera = null;
        try {
            camera = Camera.open(); // Attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "getCameraInstance() - Camera is not available");

            // Display error dialog
            ErrorDialog errorDialog = ErrorDialog.newInstance("Camera is not available");
            errorDialog.show(activity.getFragmentManager(), "error-dialog");

            // Return null if camera is unavailable
            return null;
        }

        Log.d(TAG, "getCameraInstance() - Camera is available");
        return camera;
    }
}
