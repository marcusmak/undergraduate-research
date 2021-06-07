package hk.ust.mtrec.multisensorcollector.sensor.gps;

import java.util.Vector;

import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnSuccessListener;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.os.Looper;
import android.content.Context;

import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.sensor.SensorProxy;
import hk.ust.mtrec.multisensorcollector.sensor.datahandler.SensorDataHandler;
import hk.ust.mtrec.multisensorcollector.utils.GpsMappingUtils;
import hk.ust.mtrec.multisensorcollector.utils.file.SerializerUtils;

public class GLocationSensorProxy implements SensorProxy{
//    implements
// GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private FusedLocationProviderClient fusedLocationClient;
    private int type;
    private Context context;
    private SensorDataHandler dataHandler;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isStarted = false;
    //    private GoogleApiClient googleApiClient;
    @Override
    public String getSensorName(){
        return "Location " + type;
    }
    private Location CurrentLocation = null;

    public void addCalibData(final double x,final double y){

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity)context, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Vector<Double> temp = new Vector<Double>();
                            temp.add(x);
                            temp.add(y);
                            GpsMappingUtils.addCalibData(location,temp);


                        }else{

                        }
                    }
                } );





    }







//    public Location getCurrentLocation(){
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener((Activity)context, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            // Logic to handle location object
//                            System.out.println("current location = "+location.getLatitude() +","
//                                    +location.getLongitude() );
//                            setCurrentLocation(location);
//
//                        }else{
//
//                        }
//                    }
//                } );
//
//
//        return CurrentLocation;
//    }
//
//
//    public void setCurrentLocation(Location location){
//        CurrentLocation = location;
//    }
    public GLocationSensorProxy(Context context, int type){

        this.type = type;
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity)context);
        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if (dataHandler != null){
                        LocationSensorData data;
                        data = LocationSensorData.generalLocationWrap(location,AppSensorManager.TYPE_GLOCATION);
                        Log.i("qwerrewq","glocation"+ data.getType());
                        dataHandler.handle(data);
//                        Log.i("qwerrewq","glocation"+ data.getType());
                    }
                }
            };
        };


    };

    @Override
    public int getType(){ return type;};

    @Override
    public void start() {
       if (!isStarted) {
           System.out.println("Glocation:" + "start");
           fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
           isStarted = true;
       }
    };

    @Override
    public void stop(){
        fusedLocationClient.removeLocationUpdates(locationCallback);
        isStarted = false;
    };


    @Override
    public boolean isEnabled() { return true;}




    @Override
    public int status(){ return 0;};


    @Override
    public void setHandler(SensorDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }


}
