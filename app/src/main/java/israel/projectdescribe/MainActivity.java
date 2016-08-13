package israel.projectdescribe;

import android.Manifest;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import israel.projectdescribe.ai.ProjectDescribe;
import israel.projectdescribe.camera.CameraHelper;
import israel.projectdescribe.dialogs.PermissionDialog;
import israel.projectdescribe.toast.ToastHelper;

public class MainActivity extends AppCompatActivity {

    /**
     * Debug TAG
     */
    private static final String TAG = MainActivity.class.getName();

    /**
     * Dialog TAGs
     */
    private static final String CAMERA_PERMISSION_DIALOG = "Camera Permission";
    private static final String ERROR_DIALOG = "Error";

    /**
     * FrameLayout - Preview Layout
     */
    private FrameLayout previewLayout;
    private FrameLayout getPreviewLayout() {
        return previewLayout;
    }
    private void setPreviewLayout() {
        // Get the frame layout that the camera preview will reside in by ID.
        previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
    }
    private void addCameraPreview() {
        getPreviewLayout().addView(getCameraPreview());
    }

    /**
     * Camera Preview
     */
    private CameraPreview cameraPreview;
    private CameraPreview getCameraPreview() {
        return cameraPreview;
    }
    private void setCameraPreview() {
        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(this, getCamera());
    }


    /**
     * Camera
     */
    private Camera camera;
    private Camera getCamera() {
        return camera;
    }
    private void setCamera() {

        Log.d(TAG, "setCamera()");

        // Create an instance of Camera
        camera = CameraHelper.getCameraInstance(this);

        // Get rotation degrees
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        Log.d(TAG, "setCamera() - Display rotation is: " + rotation);

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        Log.d(TAG, "setCamera() - Camera Orientation: " + info.orientation);
        Log.d(TAG, "setCamera() - Degrees needed to rotate: " + degrees);
        Log.d(TAG, "setCamera() - Total rotation value: " + rotate);

        // Get camera parameters
        Camera.Parameters params = camera.getParameters();

        // Set camera rotation we've calculated earlier
        params.setRotation(rotate);

        // Set JPEG quality
        params.setJpegQuality(100);

        // Set picture size from supported sizes, currently just using an index hack, FIXME.
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(sizes.size()-3);

        params.setPictureSize(size.width, size.height);

        // Check if continuous focus mode for picture is supported
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            Log.d(TAG, "setCamera() - FOCUS_MODE_CONTINUOUS_PICTURE is supported!");

            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        } else {
            Log.w(TAG, "setCamera() - FOCUS_MODE_CONTINUOUS_PICTURE is not supported!");
        }

        // Setup camera parameters we assigned above
        camera.setParameters(params);

        Log.i(TAG, "setCamera() - Pictures are captured in resolution of: " + getCamera().getParameters().getPictureSize().width + "x" + getCamera().getParameters().getPictureSize().height);
    }

    /**
     * PROJECT DESCRIBE - Picture Callback for the camera picture capture
     */
    private ProjectDescribe projectDescribe;
    private ProjectDescribe getProjectDescribe() {
        return projectDescribe;
    }
    private void setProjectDescribe(ProjectDescribe projectDescribe) {
        this.projectDescribe = projectDescribe;
    }

    /**
     * Camera permission request code
     */
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static int getRequestCameraPermission() {
        return REQUEST_CAMERA_PERMISSION;
    }


    /**
     * Delays for the scheduling handler of picture capturing tasks.
     */
    private final int FIXED_DELAY = 3*1000;
    private final int INITIAL_DELAY = 8*1000;

    /**
     * Handler for scheduling picture capturing tasks.
     */
    private Handler pictureCaptureHandler;
    private Handler getPictureCaptureHandler() {
        return pictureCaptureHandler;
    }
    private void setupPictureCaptureHandler() {
        pictureCaptureHandler = new Handler();
        pictureCaptureHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Picture Capture Handler Run()");

                getCamera().takePicture(null, null, getProjectDescribe());
                getCamera().startPreview();

                pictureCaptureHandler.postDelayed(this, FIXED_DELAY);
            }
        }, INITIAL_DELAY);
    }


    /**
     * UI Components, Views
    */
    private TextView caption;
    private TextView getCaption() {
        return caption;
    }
    private void initCaption() {
        caption = (TextView) findViewById(R.id.caption);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        hideTitleBar();

        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        CameraHelper.checkCameraHardware(this);

        setup();

    }

    private void hideTitleBar() {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void applyCameraPermissionDialog() {
        Log.d(TAG, "applyCameraPermissionDialog() - Apply camera permission dialog");
        DialogFragment cameraPermissionDialog = PermissionDialog.newInstance(getApplicationContext().getString(R.string.camera_permission), Manifest.permission.CAMERA, getRequestCameraPermission());
        cameraPermissionDialog.show(this.getFragmentManager(), CAMERA_PERMISSION_DIALOG);
    }

    private void requestCameraPermission() {
        Log.d(TAG, "requestCameraPermission()");
        // If explanation needed we show one.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Log.d(TAG, "requestCameraPermission() - Explanation needed, provide camera permission dialog");
            applyCameraPermissionDialog();
            return;
        }

        Log.d(TAG, "requestCameraPermission() - Explanation not needed, request camera permission directly");
        // If explanation isn't need, then we request permissions right away.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        if (requestCode == getRequestCameraPermission()) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult() - Permission not granted, present camera permission dialog");
                requestCameraPermission();
            }
        } else {
            Log.d(TAG, "onRequestPermissionsResult() - Not a camera permission issue, let super handle that");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        Log.d(TAG, "onRequestPermissionsResult() - Camera permission granted");
        ToastHelper.showToast(this, "Camera permission granted..");
        setup();
    }

    public void setup() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate() - Request camera permission");
            requestCameraPermission();
            return;
        }

        setupViews();

        setupProjectDescribe();

        setupCamera();

        setupPictureCaptureHandler();
    }

    private void setupProjectDescribe() {
        ToastHelper.showToast(this, "Project Describe loading...");
        setProjectDescribe(new ProjectDescribe(getCaption()));
    }

    private void setupViews() {
        ToastHelper.showToast(this, "Setup view components..");
        initCaption();
    }

    private void setupCamera() {
        ToastHelper.showToast(this, "Initialize camera..");
        setCamera();

        setCameraPreview();
        getCameraPreview().makeSureIsValid(this);

        setPreviewLayout();

        addCameraPreview();

        ToastHelper.showToast(this, "2 hosts available");
        ToastHelper.showToast(this, "Connecting to host..");
    }

}
