package hk.ust.mtrec.multisensorcollector.sensor.general;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.sensor.SensorProxy;
import hk.ust.mtrec.multisensorcollector.sensor.datahandler.SensorDataHandler;

/**
 * Created by tanjiajie on 2/8/17.
 */
public class StandardSensorProxy implements SensorProxy, SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor sensor;
    private final int type;

    private SensorDataHandler dataHandler;

    public StandardSensorProxy(SensorManager sensorManager, Sensor sensor, int type) {
        this.sensorManager = sensorManager;
        this.sensor = sensor;
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getSensorName() {
        return sensor.getName();
    }

    @Override
    public void start() {
        if (dataHandler == null)
            Log.w("sensor", "There is no data handler defined in the sensor " + getSensorName());
        sensorManager.registerListener(this, sensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
        Log.i("sensor", "Sensor [" + sensor.getName() + "] is started.");
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(this);
        Log.i("sensor", "Sensor [" + sensor.getName() + "] is stopped.");
    }

    @Override
    public int status() {
        return 0;
    }

    @Override
    public void setHandler(SensorDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        AppSensorManager.setLatestElapsedTimeUs(event.timestamp / 1000L);
        if (dataHandler != null)
            dataHandler.handle(StandardSensorData.fromSensorEvent(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
