package hk.ust.mtrec.multisensorcollector.sensor.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import hk.ust.mtrec.multisensorcollector.sensor.SensorProxy;
import hk.ust.mtrec.multisensorcollector.sensor.datahandler.SensorDataHandler;

/**
 * Created by tanjiajie on 2/3/17.
 */
public class WifiSensorProxy extends BroadcastReceiver implements SensorProxy {

    private final Context context;
    private final WifiManager wifiManager;
    private SensorDataHandler dataHandler;
    private Thread scanThread;
    private final int type;

    public WifiSensorProxy(Context context, int type) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
        return "Wi-Fi";
    }

    @Override
    public void start() {
        context.registerReceiver(this,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (scanThread == null)
            this.scanThread = new Thread(new WifiScanner());
        if (!scanThread.isAlive())
            scanThread.start();
    }

    @Override
    public void stop() {
        context.unregisterReceiver(this);
        if (scanThread != null)
            scanThread.interrupt();
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
    public void onReceive(Context context, Intent intent) {
        Log.v("wifi", "Getting WiFi scan results ...");
        List<ScanResult> results = wifiManager.getScanResults();
        dataHandler.handle(WifiSensorData.fromWifiScanResults(results));
    }

    private class WifiScanner implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (this) {
                        wifiScan();
                        Thread.sleep(50);
//                        Log.v("wifi", "scanwifi()");
//                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void wifiScan() {
            if (wifiManager.isWifiEnabled()) {
                standardScan();
            } else {
                Log.e("wifi-sensor", "Fail to scan WiFi because Wi-Fi is disabled.");
            }
        }

        private void standardScan() {
            wifiManager.startScan();
        }

//        private void customizedScan() {
//            try {
//                Class<?> scanSettingsClass = Class.forName("android.net.wifi.ScanSettings");
//                Object scanSettingsObj = scanSettingsClass.newInstance();
//                Field channelSetField = scanSettingsClass.getField("channelSet");
//                List<Object> channelList = new ArrayList<Object>();
//
//                Class<?> wifiChannelClass = Class.forName("android.net.wifi.WifiChannel");
//                Object wifiChannelObj = wifiChannelClass.newInstance();
//                Field freqMHzField = wifiChannelClass.getField("freqMHz");
//                freqMHzField.setInt(wifiChannelObj, 2412);
//                Field channelNumField = wifiChannelClass.getField("channelNum");
//                freqMHzField.setInt(wifiChannelObj, 1);
//                channelList.add(wifiChannelObj);
//
//                Object wifiChannelObj2 = wifiChannelClass.newInstance();
//                freqMHzField.setInt(wifiChannelObj2, 2437);
//                freqMHzField.setInt(wifiChannelObj2, 6);
//                channelList.add(wifiChannelObj2);
//
//                Object wifiChannelObj3 = wifiChannelClass.newInstance();
//                freqMHzField.setInt(wifiChannelObj3, 2462);
//                freqMHzField.setInt(wifiChannelObj3, 11);
//                channelList.add(wifiChannelObj3);
//
//                channelSetField.set(scanSettingsObj, channelList);
//
//                Method scanMethod = wifiManager.getClass().getDeclaredMethod("startCustomizedScan", scanSettingsClass);
//                scanMethod.setAccessible(true);
//                scanMethod.invoke(wifiManager, scanSettingsObj);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//        }

    }

}
