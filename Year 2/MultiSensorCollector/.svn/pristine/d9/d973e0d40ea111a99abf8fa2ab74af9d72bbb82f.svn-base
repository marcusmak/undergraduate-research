package hk.ust.mtrec.multisensorcollector.widget.map;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.InputStream;

import hk.ust.mtrec.multisensorcollector.exception.AccessResourceException;
import hk.ust.mtrec.multisensorcollector.utils.file.BitmapUtils;
import hk.ust.mtrec.multisensorcollector.utils.file.FileUtils;

/**
 * Created by tanjiajie on 2/20/17.
 */
public class Map {

    private static final int DEFAULT_IN_SAMPLE_SIZE = 1;

    private String mapId;
    private boolean embedded;   // in assets
    private int inSampleSize = DEFAULT_IN_SAMPLE_SIZE;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public int getInSampleSize() {
        return inSampleSize;
    }

    public void setInSampleSize(int inSampleSize) {
        this.inSampleSize = inSampleSize;
    }

    public Bitmap getBitmapMap(Context context) throws AccessResourceException {
        InputStream bitmapStream = null;
        if (embedded)
            bitmapStream = FileUtils.getStreamFromAssets(context, "map/" + mapId + "/map.jpg");
        else
            bitmapStream = FileUtils.getStreamFromAppDirectory("map/" + mapId + "/map.jpg");
        this.inSampleSize = BitmapUtils.calculateInSampleSize(bitmapStream, 1024, 1024);
        return BitmapUtils.getBitmap(bitmapStream, 1024, 1024);
    }

}
