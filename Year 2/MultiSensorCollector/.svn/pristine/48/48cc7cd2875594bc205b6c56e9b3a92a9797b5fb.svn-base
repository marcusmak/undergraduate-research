package hk.ust.mtrec.multisensorcollector.sensor.general;

import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;

/**
 * Created by tanjiajie on 2/13/17.
 */
public class SensorDataHelper {

    public static String[] findDataKeys(int type) {
        SensorInfo sensorInfo = AppSensorManager.SENSOR_INFO_MAP.get(type);
        if (sensorInfo != null)
            return sensorInfo.getKeys();
        return null;
    }
}
