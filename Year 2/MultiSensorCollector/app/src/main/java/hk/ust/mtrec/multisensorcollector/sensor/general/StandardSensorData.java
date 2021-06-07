package hk.ust.mtrec.multisensorcollector.sensor.general;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

import hk.ust.mtrec.multisensorcollector.bean.SensorData;
import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.utils.TimeUtils;

/**
 * Created by tanjiajie on 2/8/17.
 */
public class StandardSensorData extends SensorData {

    private final Sensor sensor;
    private long sysTimeMs;
    private long elapsedTimeUs;
    private int accuracy;
    private Map<String, Float> data;

    static StandardSensorData fromSensorEvent(SensorEvent event) {
        StandardSensorData standardSensorData = new StandardSensorData(event.sensor);
        standardSensorData.sysTimeMs = TimeUtils.currentSystemTimeMs();
        standardSensorData.elapsedTimeUs = event.timestamp / 1000L;
        standardSensorData.accuracy = event.accuracy;
        Map<String, Float> data = new HashMap<>();
        // determine keys
        String[] keys = new String[event.values.length];
        String[] predefinedKeys = SensorDataHelper.findDataKeys(standardSensorData.getSensorType());
        if (predefinedKeys != null) {
            for (int i = 0; i < Math.min(keys.length, predefinedKeys.length); i++)
                keys[i] = predefinedKeys[i];
        }
        for (int i = 0; i <keys.length; i++) {
            keys[i] = keys[i] == null ? "undefined_" + i : keys[i];
        }
        // put data
//        for (int i = 0; i < event.values.length; i++) {
        for (int i = 0; i < predefinedKeys.length; i++) {
            data.put(keys[i], event.values[i]);
        }
        standardSensorData.data = data;
        return standardSensorData;
    }

    public StandardSensorData(Sensor sensor) {
        this.sensor = sensor;
    }

    public String getSensorName() {
        return sensor.getName();
    }

    @JsonIgnore
    public int getSensorType() {
        return AppSensorManager.ANDROID_SENSOR_PREFIX + sensor.getType();
    }

    public long getSysTimeMs() {
        return sysTimeMs;
    }

    public void setSysTimeMs(long sysTimeMs) {
        this.sysTimeMs = sysTimeMs;
    }

    public long getElapsedTimeUs() {
        return elapsedTimeUs;
    }

    public void setElapsedTimeUs(long elapsedTimeUs) {
        this.elapsedTimeUs = elapsedTimeUs;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public Map<String, Float> getData() {
        return data;
    }

    public void setData(Map<String, Float> data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return getSensorType();
    }

}
