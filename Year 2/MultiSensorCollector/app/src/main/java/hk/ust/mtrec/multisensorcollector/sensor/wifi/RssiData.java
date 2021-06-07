package hk.ust.mtrec.multisensorcollector.sensor.wifi;

import hk.ust.mtrec.multisensorcollector.persistence.Persistable;
import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;

/**
 * Created by tanjiajie on 2/13/17.
 */
public class RssiData implements Persistable {

    private long sysTimeMs;
    private long elapsedTimeUs;
    private String bssid;
    private String ssid;
    private int freq;
    private int rssi;

    public long getElapsedTimeUs() {
        return elapsedTimeUs;
    }

    public void setElapsedTimeUs(long elapsedTimeUs) {
        this.elapsedTimeUs = elapsedTimeUs;
    }

    public long getSysTimeMs() {
        return sysTimeMs;
    }

    public void setSysTimeMs(long sysTimeMs) {
        this.sysTimeMs = sysTimeMs;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public int getType() {
        return AppSensorManager.TYPE_WIFI;
    }

}

