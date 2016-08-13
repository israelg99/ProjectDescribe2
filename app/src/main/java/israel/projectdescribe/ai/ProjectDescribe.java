package israel.projectdescribe.ai;

import android.hardware.Camera;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.StringReader;

import israel.projectdescribe.services.image.caption.ImageCaptionArguments;
import israel.projectdescribe.services.image.caption.ImageCaptionRequest;
import israel.projectdescribe.services.processors.IStringProcessor;

/**
 * Created by Israel on 8/12/2016.
 */

public class ProjectDescribe implements Camera.PictureCallback, IStringProcessor {


    /**
     * Debug TAG
     */
    private static final String TAG = ProjectDescribe.class.getName();


    /**
     * HOST URL Management
     */
    private static final String[] HOST_URL = new String[] {"46.33.46.211", "138.68.7.183"};
    private static String getHostUrl(int index) {
        return HOST_URL[index];
    }
    private static final String PROTOCOL = "http";
    private static String getProtocol() {
        return PROTOCOL;
    }
    private static final int PORT = 8888;
    private static int getPort() {
        return PORT;
    }
    private static final String POSTFIX = "caption";
    private static String getPostfix() {
        return POSTFIX;
    }

    private static String getFullHostURL(int index) {
        return getProtocol() + "://" + getHostUrl(index) + ":" + getPort() + "/" + getPostfix();
    }
    private static String[] getFullHostURL() {
        String[] fullHostURLtemp = new String[HOST_URL.length];
        for(int i = 0; i < fullHostURLtemp.length; i++) {
            fullHostURLtemp[i] = getProtocol() + "://" + getHostUrl(i) + ":" + getPort() + "/" + getPostfix();
        }
        return fullHostURLtemp;
    }


    /**
     * Image Caption Request to send to the server
     */
    private ImageCaptionRequest imageCaptionRequest;
    private ImageCaptionRequest getImageCaptionRequest() {
        return imageCaptionRequest;
    }
    private void resetImageCaptionRequest() {
        imageCaptionRequest = new ImageCaptionRequest(getFullHostURL(), null, ImageCaptionRequest.getDefaultQuality());
    }


    /**
     * Caption View - TextView
     */
    private final TextView caption;
    private TextView getCaption() {
        return caption;
    }


    public ProjectDescribe(TextView caption) {
        Log.d(TAG, "ProjectDescribe()");

        this.caption = caption;
        resetImageCaptionRequest();
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Log.d(TAG, "onPictureTaken()");

        new ImageCaptionRequest(getFullHostURL()).execute(new ImageCaptionArguments(this, data, 100));
    }

    @Override
    public void processString(String string) {
        this.caption.setText(JSON_getCaption(string));
    }

    protected String JSON_getCaption(String JSON) {
        JsonReader jsonReader = new JsonReader(new StringReader(JSON));
        String desc = "Nothing..";

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals("caption")) {
                    desc = jsonReader.nextString();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "JSON_getCaption - Error while parsing the JSON file received from the server: " + e.getMessage());
        }

        return desc;
    }
}
