package israel.projectdescribe;

/**
 * Created by Israel on 8/12/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import israel.projectdescribe.dialogs.ErrorDialog;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


    /**
     * Debug TAG
     */
    private static final String TAG = CameraPreview.class.getName();


    /**
     * Orientation
     */
    private static final int ORIENTATION_PORTRAIT = 90;
    private static int getOrientationPortrait() {
        return ORIENTATION_PORTRAIT;
    }

    private static final int ORIENTATION_LANDSCAPE = 180;
    private static int getOrientationLandscape() {
        return ORIENTATION_LANDSCAPE;
    }

    private static final int ORIENTATION_DEFAULT = 0;
    private static int getOrientationDefault() {
        return ORIENTATION_DEFAULT;
    }


    /**
     * Fail checking system, used to later crash and display error dialogs if failed.
     */
    private boolean isFailed;
    public boolean isFailed() {
        return isFailed;
    }
    private void fail() {
        isFailed = true;
    }
    private void init() {
        isFailed = false;
    }


    /**
     * The SurfaceHolder which hold the camera preview.
     */
    private SurfaceHolder previewHolder;
    private SurfaceHolder getPreviewHolder() {
        return previewHolder;
    }
    private void setupPreviewHolder() {
        previewHolder = getHolder();
    }


    /**
     * Camera (1)
     */
    private Camera camera;
    private Camera getCamera() {
        return camera;
    }
    private void setCamera(Camera camera) {
        this.camera = camera;
    }


    public CameraPreview(Context context, Camera camera) {
        super(context);

        Log.d(TAG, "CameraPreview()");

        // Sets the boolean isFailed to false.
        init();

        setCamera(camera);

        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        setupPreviewHolder();
        getPreviewHolder().addCallback(this);

        // Deprecated setting, but required on Android versions prior to 3.0
        getPreviewHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "surfaceCreated()");

        // Surface has been created, (try) setup camera preview here.
        try {
            getCamera().setPreviewDisplay(holder);
            getCamera().setDisplayOrientation(getOrientationDefault());
            getCamera().startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            fail();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");

        // Empty. Release the Camera preview in your activity.
        destroy();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        /*Log.d(TAG, "surfaceChanged()");

        // Take care of events such as preview rotation, resizing and such, here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (getPreviewHolder().getSurface() == null){
            Log.d(TAG, "surfaceChanged() - Preview surface does not exist");
            // Preview surface does not exist.

            return;
        }

        // Stop preview before making changes.
        try {
            Log.d(TAG, "surfaceChanged() - Stop preview before making changes");
            getCamera().stopPreview();
        } catch (Exception e){
            Log.d(TAG, "surfaceChanged() - Tried to stop a non-existent preview");
            // Ignore: Tried to stop a non-existent preview.
        }

        // Set preview size and make resize, rotate or reformatting changes here.

        // Start preview with new settings
        try {
            Log.d(TAG, "surfaceChanged() - Start preview with new settings");
            getCamera().setPreviewDisplay(getPreviewHolder());
            getCamera().startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            fail();
        }*/
    }

    /**
     * Makes sure the camera preview is valid and didn't fail.
     * @param activity Needs the activity in order to notify it in the form of error dialogs and such...
     */
    public void makeSureIsValid(Activity activity) {
        Log.d(TAG, "makeSureIsValid()");

        if(isFailed()) {
            Log.e(TAG, "makeSureIsValid() - CameraPreview is invalid!");
            ErrorDialog errorDialog = ErrorDialog.newInstance("Failed to create a camera preview");
            errorDialog.show(activity.getFragmentManager(), "error-dialog");
        }
    }


    /** Release the camera for other applications */
    private void releaseCamera() {
        Log.w(TAG, "releaseCamera()");
        if (getCamera() != null) {
            getCamera().stopPreview();
            getCamera().release();
            setCamera(null);
        }
    }

    private void destroy() {
        Log.w(TAG, "destroy()");

        // This is coupling, but ok for our scope meanwhile, setting up callbacks is too much of a hassle atm.
        //releaseCamera();
    }
}