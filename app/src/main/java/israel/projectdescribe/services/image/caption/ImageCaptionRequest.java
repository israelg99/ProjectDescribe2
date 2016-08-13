package israel.projectdescribe.services.image.caption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Israel on 8/12/2016.
 */

public class ImageCaptionRequest extends AsyncTask<ImageCaptionArguments, Void, ImageCaptionProcessor> {


    /**
     * Debug TAG
     */
    private static final String TAG = ImageCaptionRequest.class.getName();


    /**
     * METHOD
     */
    private static final String METHOD = "POST";
    private static String getMethod() {
        return METHOD;
    }

    /**
     * Image
     */
    private Bitmap bitmap;
    private Bitmap getBitmap() {
        return bitmap;
    }
    public void setImage(byte[] imageData) {
        this.bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    /**
     *  Image Quality
     */

    // Static
    private static int DEFAULT_QUALITY = 100;
    public static int getDefaultQuality() {
        return DEFAULT_QUALITY;
    }

    // Instance
    private int quality;
    private int getQuality() {
        return quality;
    }
    public void setQuality(int quality) {
        this.quality = quality;
    }
    public void setDefaultQuality() {
        this.quality = getDefaultQuality();
    }


    /**
     * Host URL
     */
    private int activeURL;
    private int getActiveURL() {
        return activeURL;
    }
    private void setActiveURL(int index) {
        if(getFullHostURL().length <= index) {
            Log.e(TAG, "setActiveURL() - URL OUT OF BOUNDS! index " + index + " expected to be smaller than " + getFullHostURL().length);
        }
        if(getActiveURL() == index) {
            Log.i(TAG, "setActiveURL() - Same URL cannot be assigned");
        }
        activeURL = index;
        openConnection(index);
    }
    private URL[] FullHostURL;
    private URL[] getFullHostURL() {
        return FullHostURL;
    }
    private URL getFullHostURL(int index) {
        return getFullHostURL()[index];
    }
    private void setFullHostURL(String[] urls) {
        FullHostURL = new URL[urls.length];
        try {
            for(int i = 0; i < getFullHostURL().length; i++) {
                Log.i(TAG, "setFullHostURL() - We assign URL: " + urls[i]);
                FullHostURL[i] = new URL(urls[i]);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    /**
     * HTTP URL Connection
     */
    private HttpURLConnection connection;
    private HttpURLConnection getConnection() {
        return connection;
    }
    private void openConnection(int index) {
        try {
            connection = (HttpURLConnection) getFullHostURL(index).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openConnection(URL url) {
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ImageCaptionRequest(String[] fullHostURL, byte[] imageData, int quality) {
        setQuality(quality);

        if(fullHostURL != null) {
            setFullHostURL(fullHostURL);
        } else {
            Log.e(TAG, "ImageCaptionRequest() - HOST URL IS NULL!");
        }

        if(imageData != null) {
            this.bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } else {
            this.bitmap = null;
        }
    }
    public ImageCaptionRequest(String fullHostURL, byte[] imageData, int quality) {
        this(new String[] {fullHostURL}, imageData, quality);
    }
    public ImageCaptionRequest(String[] fullHostURL, byte[] imageData) {
        this(fullHostURL, imageData, getDefaultQuality());
    }
    public ImageCaptionRequest(String fullHostURL, byte[] imageData) {
        this(fullHostURL, imageData, getDefaultQuality());
    }
    public ImageCaptionRequest(String fullHostURL) {
        this(fullHostURL, null, getDefaultQuality());
    }
    public ImageCaptionRequest(String[] fullHostURL) {
        this(fullHostURL, null, getDefaultQuality());
    }


    private String getStringImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBitmap().compress(Bitmap.CompressFormat.JPEG, getQuality(), baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void WriteImageToConnectionStream() {
        // Set up the output stream.
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(getConnection().getOutputStream());
        } catch (IOException e) {
            Log.e(TAG, "WriteImageToConnectionStream() - Error setting and getting connection's output stream: " + e.getMessage());
        }
        if(outputStream == null) {
            return;
        }

        // Compress the bitmap into the output stream.
        getBitmap().compress(Bitmap.CompressFormat.JPEG, getQuality(), outputStream);

        // Close the sending components.
        try {
            outputStream.flush();

            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "WriteImageToConnectionStream() - Unable to close response components: " + e.getMessage());
        }
    }

    public String uploadImage() {

        Log.d(TAG, "uploadImage()");

        if(getBitmap() == null) {
            Log.e(TAG, "uploadImage() - The image bitmap is null!");
            return "The image bitmap is null";
        }
        if(getFullHostURL() == null) {
            Log.e(TAG, "uploadImage() - The HOST URL is null!");
            return "The host url is null";
        }

        setActiveURL(0);

        getConnection().setDoOutput(true);
        getConnection().setUseCaches(false);
        //().setChunkedStreamingMode(0); Makes broken pipes.. issues. Yikes!
        getConnection().setRequestProperty("Connection", "keep-alive");
        getConnection().setRequestProperty("Content-Type", "image/jpeg");

        try {
            getConnection().setRequestMethod(getMethod());
        } catch (ProtocolException e) {
            Log.e(TAG, "uploadImage() - Error setting request method to " + getMethod() + ": " + e.getMessage());
        }

        // Connect to the server.
        try {
            getConnection().connect();
        } catch (IOException e) {
            Log.e(TAG, "uploadImage() - Error connecting to the server: " + e.getMessage());
        }


        WriteImageToConnectionStream();


        /* Relieve response from the server */
        Log.d(TAG, "uploadImage() - Server response incoming!");

        // Getting the actual response from the server, and the response code.
        BufferedInputStream responseStream = null;
        try {
            Log.d(TAG, "uploadImage() - Response code: " + getConnection().getResponseCode());
            responseStream = new BufferedInputStream(getConnection().getInputStream());
        } catch (IOException e) {
            Log.e(TAG, "uploadImage() - Error getting response from the connection's input stream: " + e.getMessage());
            e.printStackTrace();
        }
        if(responseStream == null) {
            Log.e(TAG, "uploadImage() - Response stream is null, abort.");
            return "Cannot relieve response from the server.";
        }

        // Decipher a string response from the server response.
        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

        String line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
        } catch (IOException e) {
            Log.e(TAG, "uploadImage() - Error building string response: " + e.getMessage());
        }

        Log.i(TAG, "uploadImage() - Final output string: " + stringBuilder.toString());
        return stringBuilder.toString();
    }
    public String uploadImage(byte[] imageData) {
        setImage(imageData);
        return uploadImage();
    }
    public String uploadImage(byte[] imageData, int quality) {
        setQuality(quality);
        return uploadImage(imageData);
    }
    public String uploadImage(int quality) {
        setQuality(quality);
        return uploadImage();
    }

    @Override
    protected ImageCaptionProcessor doInBackground(ImageCaptionArguments... params) {
        return new ImageCaptionProcessor(params[0].getStringProcessor(), uploadImage(params[0].getData(), params[0].getQuality()));
    }

    @Override
    protected void onPostExecute(ImageCaptionProcessor processor) {
        processor.getStringProcessor().processString(processor.getString());
    }

}