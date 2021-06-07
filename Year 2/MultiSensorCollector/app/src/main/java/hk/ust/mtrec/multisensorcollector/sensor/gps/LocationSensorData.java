package hk.ust.mtrec.multisensorcollector.sensor.gps;

import android.location.GpsSatellite;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import hk.ust.mtrec.multisensorcollector.bean.SensorData;
import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.utils.TimeUtils;



public class LocationSensorData extends SensorData {

    private double longitude;
    private double latitude;
    private double altitude;
    private float accuracy;
    private long sysTimeMs;
    private long elapsedTimeUs;
    private int type;
    private ArrayList<GpsSatellite> satellites;

    @Override
    public int getType(){
        return type;
    }


    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    };

    public double getAccuracy(){
        return accuracy;
    };

    public double getAltitude(){ return altitude;};


    public long getSysTimeMs(){return sysTimeMs;};
    public long getElapsedTimeUs(){return elapsedTimeUs;};


    public ArrayList<GpsSatellite> getSatellites(){ return satellites;};


    private void locationWrap(Location location) {

        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.altitude = location.getAltitude();
        this.accuracy = location.getAccuracy();
        this.sysTimeMs = TimeUtils.currentSystemTimeMs();
        this.elapsedTimeUs = location.getElapsedRealtimeNanos();

    }

    static LocationSensorData generalLocationWrap(Location location,int type){
        LocationSensorData networkSensorData = new LocationSensorData();
        networkSensorData.type = type;
        networkSensorData.locationWrap(location);
        return networkSensorData;

    }





    static LocationSensorData fromGPS(Location location, Iterable<GpsSatellite> it_satellites, int type){

        LocationSensorData gpsSensorData = new LocationSensorData();
        gpsSensorData.locationWrap(location);
        gpsSensorData.type =  type;
        String strGpsStats ="";
        Iterator<GpsSatellite>sat = it_satellites.iterator();
        gpsSensorData.satellites = new ArrayList<GpsSatellite>();

        int i=0;
        Log.i("qwerqwer","//");
        while (sat.hasNext()) {
            GpsSatellite satellite = sat.next();
            gpsSensorData.satellites.add(satellite);
            strGpsStats+= (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()+ "\n\n";
        };
        System.out.println("qwerqwer this->" +strGpsStats);
        Log.i("qwerqwer","/-/");
        return gpsSensorData;
    }




    public LocationSensorData ()
    {


    }



}
