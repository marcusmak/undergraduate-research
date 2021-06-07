package hk.ust.mtrec.multisensorcollector.bean;

import android.graphics.PointF;

import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.utils.TimeUtils;

/**
 * Created by tanjiajie on 2/13/17.
 */
public class LabelData extends SensorData {

    public static final int OPERATION_INFO_START = 100;
    public static final int OPERATION_INFO_END = 101;
    public static final int OPERATION_ADD = 1;
    public static final int OPERATION_REMOVE = -1;

    private final long elapsedTimeUs;
    private final long sysTimeMs;
    private final String map;
    private final float x;
    private final float y;
    private final int operation;

    public LabelData(int operation, String map, PointF pts) {
        this.elapsedTimeUs = AppSensorManager.getLatestElapsedTimeUs();
        this.sysTimeMs = TimeUtils.currentSystemTimeMs();
        this.operation = operation;
        this.map = map == null ? "" : map;
        this.x = pts == null ? 0 : pts.x;
        this.y = pts == null ? 0 : pts.y;
    }

    public LabelData(int operation, String map, float x, float y) {
//        this.elapsedTimeUs = TimeUtils.currentElapsedTimeUs();
        this.elapsedTimeUs = AppSensorManager.getLatestElapsedTimeUs();
        this.sysTimeMs = TimeUtils.currentSystemTimeMs();
        this.operation = operation;
        this.map = map;
        this.x = x;
        this.y = y;
    }

    public long getElapsedTimeUs() {
        return elapsedTimeUs;
    }

    public long getSysTimeMs() {
        return sysTimeMs;
    }

    public String getMap() {
        return map;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getOperation() {
        return operation;
    }

    @Override
    public int getType() {
        return LABEL_DATA;
    }

}
