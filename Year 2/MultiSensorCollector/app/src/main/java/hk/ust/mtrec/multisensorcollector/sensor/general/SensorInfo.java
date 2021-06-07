package hk.ust.mtrec.multisensorcollector.sensor.general;

/**
 * Created by tanjiajie on 2/21/17.
 */
public class SensorInfo {

    private final int type;
    private final String stringType;
    private final String[] keys;

    public SensorInfo(int type, String stringType, String[] keys) {
        this.type = type;
        this.stringType = stringType;
        this.keys = keys;
    }

    public int getType() {
        return type;
    }

    public String getStringType() {
        return stringType;
    }

    public String[] getKeys() {
        return keys;
    }

}
