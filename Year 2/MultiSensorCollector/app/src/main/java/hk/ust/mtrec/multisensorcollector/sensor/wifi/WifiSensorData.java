package hk.ust.mtrec.multisensorcollector.sensor.wifi;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

import hk.ust.mtrec.multisensorcollector.bean.SensorData;
import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.utils.TimeUtils;

/**
 * Created by tanjiajie on 2/13/17.
 */
public class WifiSensorData extends SensorData {

    private long sysTimeMs;
    private long elapsedTimeUs;
    private List<RssiData> data;

    public static WifiSensorData fromWifiScanResults(List<ScanResult> scanResults) {
        WifiSensorData wifiData = new WifiSensorData();
        wifiData.sysTimeMs = TimeUtils.currentSystemTimeMs();
        wifiData.data = new ArrayList<>();
        long elapsedTimeUsSum = 0;
        if (scanResults.size() != 0) {
            for (ScanResult scanResult : scanResults) {
                RssiData rssiData = new RssiData();
                rssiData.setSysTimeMs(wifiData.sysTimeMs);
                rssiData.setElapsedTimeUs(scanResult.timestamp);
                rssiData.setBssid(scanResult.BSSID);
                rssiData.setSsid(scanResult.SSID);
                rssiData.setFreq(scanResult.frequency);
                rssiData.setRssi(scanResult.level);
                wifiData.data.add(rssiData);
                elapsedTimeUsSum += scanResult.timestamp;
            }
            wifiData.elapsedTimeUs = elapsedTimeUsSum / scanResults.size();
        }
        return wifiData;
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

    public List<RssiData> getData() {
        return data;
    }

    public void setData(List<RssiData> data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return AppSensorManager.TYPE_WIFI;
    }

}
