package hk.ust.mtrec.multisensorcollector;

import android.graphics.Color;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import hk.ust.mtrec.multisensorcollector.bean.LabelData;
import hk.ust.mtrec.multisensorcollector.persistence.PersistenceManager;
import hk.ust.mtrec.multisensorcollector.persistence.Persistencer;
import hk.ust.mtrec.multisensorcollector.persistence.remove.FileRemover;
import hk.ust.mtrec.multisensorcollector.persistence.upload.Uploader;
import hk.ust.mtrec.multisensorcollector.sensor.AppSensorManager;
import hk.ust.mtrec.multisensorcollector.sensor.SensorProxy;
import hk.ust.mtrec.multisensorcollector.sensor.datahandler.PersistenceSensorDataHandler;
import hk.ust.mtrec.multisensorcollector.utils.GpsMappingUtils;
import hk.ust.mtrec.multisensorcollector.utils.dev.DeviceUtils;
import hk.ust.mtrec.multisensorcollector.utils.file.SerializerUtils;
import hk.ust.mtrec.multisensorcollector.widget.map.Map;
import hk.ust.mtrec.multisensorcollector.widget.map.MapWidget;
import hk.ust.mtrec.multisensorcollector.sensor.gps.GLocationSensorProxy;

public class MultiSensorCollector extends AppCompatActivity {

    //    private static final String MAP = "default";
//    private static final String MAP = "mall";
    private static final String MAP = "epc";

    private AppSensorManager appSensorManager;
    private PersistenceManager persistenceManager;

    private Toolbar toolbar;
    private MapWidget mapWidget;
    private FloatingActionButton labelBtn;
    private Switch sensorSwitch;
    private Dialog newTraceDialog;
    private EditText traceNameField;
    private boolean gpsMode;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_multi_sensor_collector);

        initToolbar();

        initMapWidget();

        initLabelBtn();

        initSwitch();

        appSensorManager = new AppSensorManager(this);
        appSensorManager.loadStandardSensors();
        appSensorManager.loadWifiSensor();
        appSensorManager.loadLocationSensors();

        persistenceManager = PersistenceManager.getInstance();

        DeviceUtils.getAndroidId(this);
        DeviceUtils.getMacAddr(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorSwitch.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void initToolbar() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.gps:
                        boolean temp = item.isChecked();
                        gpsMode = !temp;
                        item.setChecked(!temp);
                        if (!started)
                            labelBtn.setEnabled(!temp);
                        if(!temp)
                            labelBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF4081")));
                        else {
                            if(!started){
                                ((GLocationSensorProxy)appSensorManager.getSensor(AppSensorManager.TYPE_GLOCATION)).stop();
                            }
                            labelBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00BBFF")));
                            try {
                                try{
                                    SerializerUtils.deserialize();
                                }catch(Exception e){};
                                SerializerUtils.serialize(new GpsMappingUtils());
                            }catch(Exception e){
                                e.printStackTrace();
                            };

                        }
                        break;
                    case R.id.upload:
                        Uploader uploader = new Uploader(MultiSensorCollector.this);
                        uploader.doUploadJob();
                        break;
                    case R.id.settings:
                        break;
                    case R.id.empty:
                        FileRemover remover = new FileRemover(MultiSensorCollector.this);
                        remover.doRemoveJob();
                        break;
                }
                return true;
            }
        });

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                toolbar.setSubtitle(mapWidget.getMap().getMapId());
            }
        }, new IntentFilter(MapWidget.BROADCAST_INTENT_MAP_CHANGE));
    }

    private void initMapWidget() {
        this.mapWidget = (MapWidget) findViewById(R.id.map_widget);
        Map map = new Map();
        map.setEmbedded(true);
        map.setMapId(MAP);
        mapWidget.losadMap(map);
    }

    private void initLabelBtn() {
        this.labelBtn = (FloatingActionButton) findViewById(R.id.label_btn);
        labelBtn.setEnabled(false);
        labelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapWidget.addMarkerAtPinPts(started, gpsMode,
                        (GLocationSensorProxy)appSensorManager.getSensor(AppSensorManager.TYPE_GLOCATION));
            }
        });
        labelBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mapWidget.hasFocusedMarker()) {
                    Toast.makeText(MultiSensorCollector.this, "Please select a marker first.", Toast.LENGTH_SHORT)
                            .show();
                    return true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MultiSensorCollector.this);
                builder.setItems(new String[] {"Delete"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mapWidget.deleteFocusedMarker(started);
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    private void initSwitch() {
        initNewTraceDialog();
        this.sensorSwitch = (Switch) findViewById(R.id.sensor_switch_btn);
        sensorSwitch.setChecked(false);
        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    traceNameField.setText("");
                    newTraceDialog.show();
                } else {



                    PersistenceManager.getCurrentPersistencer().put(
                            new LabelData(LabelData.OPERATION_INFO_END, null, null));
                    appSensorManager.stopAllSensors(gpsMode);
                    closeAllSensors();
                    if (!gpsMode)
                        labelBtn.setEnabled(false);
                    started = false;
                    Toast.makeText(MultiSensorCollector.this, "All sensors are disabled.", Toast.LENGTH_SHORT)
                            .show();

                }
            }
        });
    }




    private void initNewTraceDialog() {
        this.traceNameField = new EditText(MultiSensorCollector.this);
        traceNameField.setSingleLine();
        this.newTraceDialog = new AlertDialog.Builder(MultiSensorCollector.this)
                .setView(traceNameField, 60, 20, 60, 0)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String traceName = traceNameField.getText().toString();
                        if (!traceName.equals("") && persistenceManager.isTaskNameAvailable(traceName)) {
                            startAllSensorsWithPersistenceHandler(traceName);
                            labelBtn.setEnabled(true);
                            started = true;
                            Toast.makeText(MultiSensorCollector.this, "All sensors are enabled.", Toast.LENGTH_SHORT)
                                    .show();
                            mapWidget.clearAllMarkers();
                            PersistenceManager.getCurrentPersistencer().put(
                                    new LabelData(LabelData.OPERATION_INFO_START, null, null));
                        } else {
                            Dialog pDialog = new AlertDialog.Builder(MultiSensorCollector.this)
                                    .setMessage("The trace name is not available.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            newTraceDialog.show();
                                        }
                                    })
                                    .create();
                            pDialog.show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sensorSwitch.setChecked(false);
                        dialog.dismiss();
                    }
                })
                .setTitle("Create new trace")
                .setCancelable(false)
                .create();
    }

    private void startAllSensorsWithPersistenceHandler(String traceName) {
        Persistencer persistencer = persistenceManager.initNewPersistencer(traceName, appSensorManager.getAllSupportedSensorTypes());
        for (SensorProxy sensor : appSensorManager.getSensors()) {
            sensor.setHandler(new PersistenceSensorDataHandler(persistencer));
        }
        appSensorManager.startAllSensors();
    }

    private void closeAllSensors() {
        PersistenceManager.getCurrentPersistencer().close();
    }

}