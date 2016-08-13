package israel.projectdescribe.services.image.caption;

import israel.projectdescribe.services.processors.IStringProcessor;

/**
 * Created by Israel on 8/12/2016.
 */

public class ImageCaptionProcessor {
    private final IStringProcessor stringProcessor;
    public IStringProcessor getStringProcessor() {
        return stringProcessor;
    }

    private final String string;
    public String getString() {
        return string;
    }

    public ImageCaptionProcessor(IStringProcessor stringProcessor, String string) {
        this.stringProcessor = stringProcessor;
        this.string = string;
    }
}
