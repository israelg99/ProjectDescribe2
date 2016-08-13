package israel.projectdescribe.services.image.caption;

import israel.projectdescribe.services.processors.IStringProcessor;

/**
 * Created by Israel on 8/12/2016.
 */

public class ImageCaptionArguments {
    private final IStringProcessor stringProcessor;
    public IStringProcessor getStringProcessor() {
        return stringProcessor;
    }

    private final byte[] data;
    public byte[] getData() {
        return data;
    }

    private final int quality;
    public int getQuality() {
        return quality;
    }

    public ImageCaptionArguments(IStringProcessor stringProcessor, byte[] data, int quality) {
        this.stringProcessor = stringProcessor;
        this.data = data;
        this.quality = quality;
    }
}
