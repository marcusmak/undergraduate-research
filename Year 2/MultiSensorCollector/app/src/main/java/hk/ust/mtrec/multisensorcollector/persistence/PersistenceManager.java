package hk.ust.mtrec.multisensorcollector.persistence;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.sensor.general.SensorInfo;
import hk.ust.mtrec.multisensorcollector.utils.file.FileUtils;

/**
 * Created by tanjiajie on 2/10/17.
 */
public class PersistenceManager {

    public static final String BROADCAST_INTENT_PERSISTENCER_ENABLED_CHANGED = "persistencer_enabled_changed";

    private static final PersistenceManager INSTANCE = new PersistenceManager();

    private File persistenceBaseDir;
    private Persistencer curPersistencer;

    private PersistenceManager() {
        this.persistenceBaseDir = FileUtils.getDataCollectionDirectory();
        if (!persistenceBaseDir.exists()) {
            persistenceBaseDir.mkdirs();
        }
    }

    public static PersistenceManager getInstance() {
        return INSTANCE;
    }

    public static Persistencer getCurrentPersistencer() {
        return INSTANCE.curPersistencer;
    }

//    public Persistencer getPersistencer() {
//        if (persistencer == null)
//            persistencer = new Persistencer(handlerMap);
//        return persistencer;
//    }

    public boolean isTaskNameAvailable(String taskName) {
        File file = new File(persistenceBaseDir, taskName);
        return !file.exists();
    }

    public Persistencer initNewPersistencer(String taskName, List<Integer> supportedTypes) {
        File pDir = new File(persistenceBaseDir, taskName);
        if (pDir == null || pDir.exists()) {
            Log.e("Persistence", "Fail to create persistencer for task " + taskName);
            return null;
        }
        pDir.mkdirs();
        Map<Integer, PersistenceHandler> handlerMap = new HashMap<>();
        handlerMap.put(
                Persistable.LABEL_DATA,
                new ContinuousPersistenceHandler(new File(pDir, "location.dat")));
        for (Integer type : supportedTypes) {
            SensorInfo sensorInfo = AppSensorManager.SENSOR_INFO_MAP.get(type);
            if (sensorInfo != null) {
                String filename = sensorInfo.getStringType() + ".dat";
                PersistenceHandler handler = new ContinuousPersistenceHandler(new File(pDir, filename));
                handlerMap.put(type, handler);
                Log.i("Persistence", "PersistenceHandler for " + sensorInfo.getStringType() + " is loaded.");
            }
        }
        this.curPersistencer = new Persistencer(taskName, handlerMap);
        return curPersistencer;
    }

    //    public void enablePersistencer(Context context) {
//        if (persistencer != null) {
//            persistencer.setEnabled(true);
//            context.sendBroadcast(new Intent(BROADCAST_INTENT_PERSISTENCER_ENABLED_CHANGED));
//        }
//    }
//
//    public void disablePersistencer(Context context) {
//        if (persistencer != null) {
//            persistencer.setEnabled(false);
//            context.sendBroadcast(new Intent(BROADCAST_INTENT_PERSISTENCER_ENABLED_CHANGED));
//        }
//    }

}
