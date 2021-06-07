package hk.ust.mtrec.multisensorcollector.sensor.gps;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.widget.Toast;
import android.provider.Settings;
import android.util.Log;
import android.os.Bundle;


import java.lang.String;


import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.sensor.SensorProxy;
import hk.ust.mtrec.multisensorcollector.sensor.datahandler.SensorDataHandler;
import hk.ust.mtrec.multisensorcollector.sensor.gps.LocationSensorData;




//import hk.ust.mtrec.multisensorcollector.AppSensorManager.TYPE_GOOGLE_LOCATION;

public class LocationSensorProxy implements SensorProxy,LocationListener {
    private Context context;
    protected LocationManager locationManager;
    private int type;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute


    private SensorDataHandler dataHandler;


    public LocationSensorProxy (Context context,int type){
        this.context = context;
        this.type = type;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }




    @Override
    public int getType(){ return type;};

    @Override
    public String getSensorName(){
        return "Location " + type;
    }


    @Override
    public boolean isEnabled() {
        if ((!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER) && type == AppSensorManager.TYPE_GPS)||
                (!locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER) && type == AppSensorManager.TYPE_NETWORK)){ //GPS_PROVIDER
            Toast.makeText(context, "Enable GPS before entering！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            ((Activity) context).startActivityForResult(intent,0);
        }
        return ((locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER) && type == AppSensorManager.TYPE_GPS)||
                (locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER) && type == AppSensorManager.TYPE_NETWORK));

    };



    @Override
    public void start() {
//
        if (type == AppSensorManager.TYPE_GPS) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Log.i("GPS", "GPS [" + LocationManager.GPS_PROVIDER + "] is started.");

        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, this);
            Log.i("GPS", "GPS [" + LocationManager.NETWORK_PROVIDER + "] is started.");

        }
    };

    @Override
    public void stop(){
        locationManager.removeUpdates(this);
    };

    @Override
    public int status(){ return 0;};

    @Override
    public void setHandler(SensorDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void onProviderDisabled(String arg){
        Toast.makeText(context, "Enable GPS before entering!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }

    @Override
    public void onProviderEnabled(String arg){

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2){
    }

    @Override
    public void onLocationChanged(Location location) {
        if (dataHandler != null){

        LocationSensorData data;
        if (type == AppSensorManager.TYPE_GPS)
        {
            data = LocationSensorData.fromGPS(location, locationManager.getGpsStatus(null).getSatellites(),type);
        }else{
            data = LocationSensorData.generalLocationWrap(location,type);
        }

            System.out.println("testingtesting");
            dataHandler.handle(data);
    }
//        else
//            Log.i("gpsnull","gpsnull");
    }
}
