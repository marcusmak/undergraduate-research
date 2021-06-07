package hk.ust.mtrec.multisensorcollector.sensor.datahandler;

import hk.ust.mtrec.multisensorcollector.bean.SensorData;
import hk.ust.mtrec.multisensorcollector.persistence.Persistencer;

/**
 * Created by tanjiajie on 2/7/17.
 */
public class PersistenceSensorDataHandler implements SensorDataHandler {

    private Persistencer persistencer;

    public PersistenceSensorDataHandler(Persistencer persistencer) {
        this.persistencer = persistencer;
    }

    @Override
    public void handle(SensorData sensorData) {
        persistencer.put(sensorData);
    }

}
