package hk.ust.mtrec.multisensorcollector.widget.map;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.Location;

import hk.ust.mtrec.multisensorcollector.R;
import hk.ust.mtrec.multisensorcollector.bean.LabelData;
import hk.ust.mtrec.multisensorcollector.exception.AccessResourceException;
import hk.ust.mtrec.multisensorcollector.persistence.PersistenceManager;
import hk.ust.mtrec.multisensorcollector.persistence.Persistencer;
import hk.ust.mtrec.multisensorcollector.utils.GeometryUtils;
import hk.ust.mtrec.multisensorcollector.sensor.gps.GLocationSensorProxy;


/**
 * Created by tanjiajie on 2/12/17.
 */
public class MapWidget extends FrameLayout {

    public static final String BROADCAST_INTENT_MAP_CHANGE = "map_changed";
    public static final String BROADCAST_INTENT_MAP_CLICK = "map_click";

    private static final int PIN_VIEW_WIDTH = 108;
    private static final int PIN_VIEW_HEIGHT = 108;

    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 0.5f;

    private static final int MODE_NONE = 0;
    private static final int MODE_MOVE = 1;
    private static final int MODE_ZOOM = 2;

    private static final int MOVE_THRESHLOD = 4;

    private boolean debug = true;

    /**
     * Map layer
     */
    private MapDisplayView mapDisplayView;

    /**
     * Label layer
     */
    private LabelDisplayView labelDisplayView;

    private ImageView pinView;

    private TextView coordTextView;

    private int mode;
    private int moveCount;

    private PointF[] pts = {new PointF(), new PointF()};
    private float dist;
    private float scale = 1f;

    private Map map;
    private PointF pinPts = new PointF();

    public MapWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MapWidget(Context context) {
        this(context, null);
    }

    public MapWidget(Context context, AttributeSet attrs, Map map) {
        this(context, attrs);
        loadMap(map);
    }

    private void initView() {
        this.mapDisplayView = new MapDisplayView(getContext());
        addView(mapDisplayView);

        this.labelDisplayView = new LabelDisplayView(getContext());
        addView(labelDisplayView);

        this.pinView = new ImageView(getContext());
        pinView.setImageResource(R.drawable.pin);
        pinView.setLayoutParams(new FrameLayout.LayoutParams(PIN_VIEW_WIDTH, PIN_VIEW_HEIGHT));

        ViewTreeObserver vtObserver = getViewTreeObserver();
        vtObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.v("measure", getHeight() + "," + getWidth());
//                    pinView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                pinView.setX((getWidth() - PIN_VIEW_WIDTH) / 2);
                pinView.setY(getHeight() / 2 - PIN_VIEW_HEIGHT);

                setPinPts(getWidth() / 2, getHeight() / 2);
                setCoordView();
            }
        });
        addView(pinView);

        this.coordTextView = new TextView(getContext());
        addView(coordTextView);
    }

    public void loadMap(Map map) {
        this.map = map;
        try {
            Bitmap bitmapMap = map.getBitmapMap(this.getContext());
            mapDisplayView.setTargetBitmap(bitmapMap);
            mapDisplayView.setScaleRate(1);
            mapDisplayView.setBitmap(bitmapMap);
            getContext().sendBroadcast(new Intent(BROADCAST_INTENT_MAP_CHANGE));
        } catch (AccessResourceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mode = MODE_MOVE;
                moveCount = 0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_ZOOM;
                moveCount = 0;
                pts[1].set(event.getX(1), event.getY(1));
                dist = GeometryUtils.distance(pts[0], pts[1]);
                break;
            case MotionEvent.ACTION_MOVE:
                moveCount++;
                if (mode == MODE_MOVE) {
                    float offX = event.getX() - pts[0].x;
                    float offY = event.getY() - pts[0].y;
                    moveAllViews(offX, offY);
                    // setBoundaryInfo();
                } else if (mode == MODE_ZOOM && event.getPointerCount() == 2) {
                    if (Math.abs(dist) > 0.000000001) {
                        zoomAllViews(this.scale * GeometryUtils.distance(pts[0], pts[1]) / dist);
                    }
                    dist = GeometryUtils.distance(pts[0], pts[1]);
                    pts[0].set(event.getX(0), event.getY(0));
                    pts[1].set(event.getX(1), event.getY(1));
                }
                mapDisplayView.postInvalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE_NONE;
                break;
            case MotionEvent.ACTION_UP:
                if (moveCount <= MOVE_THRESHLOD) {
                    mapDisplayView.postInvalidate();
                    getContext().sendBroadcast(new Intent(BROADCAST_INTENT_MAP_CLICK));
                }
                mode = MODE_NONE;
                break;
            default:
                break;
        }
        pts[0].set(event.getX(), event.getY());
        setCoordView();
        return true;
    }

    private void moveAllViews(float offX, float offY) {
        mapDisplayView.move(offX, offY);
        labelDisplayView.move(offX, offY);
        movePinPts(offX, offY);
    }

    private void zoomAllViews(float scale) {
        float priorScale = this.scale;
        scale = scale > MAX_SCALE ? MAX_SCALE : scale;
        scale = scale < MIN_SCALE ? MIN_SCALE : scale;
        if (Float.isNaN(scale) || Float.isInfinite(scale))
            scale = 1;
        this.scale = scale;
        mapDisplayView.zoom(scale);
        labelDisplayView.zoom(scale);

        // move to the center of screen
        float offX = -(pinPts.x / priorScale * scale - pinPts.x);
        float offY = -(pinPts.y / priorScale * scale - pinPts.y);
//        Log.i("off", "offX=" + offX + " offY=" + offY);
        moveAllViews(offX, offY);
    }

    private void setPinPts(float x, float y) {
        pinPts.x = x;
        pinPts.y = y;
    }

    private void movePinPts(float offX, float offY) {
        pinPts.x -= offX;
        pinPts.y -= offY;
    }

    public PointF toRealPinPts(PointF pts, float scale) {
        return new PointF(
                pts.x * map.getInSampleSize() / scale,
                pts.y * map.getInSampleSize() / scale);
    }

    public PointF getRealPinPts() {
        return toRealPinPts(pinPts, scale);
    }

    private void setCoordView() {
        PointF actPinPts = getRealPinPts();
        coordTextView.setText("(" + (int) actPinPts.x + ", " + (int) actPinPts.y + ")");
    }

    public Map getMap() {
        return map;
    }

    public void addMarkerAtPinPts(boolean started, boolean gpsMode, GLocationSensorProxy glocProxy) {
        //todo

        labelDisplayView.addDot(pinPts.x, pinPts.y, scale, gpsMode);
        if (gpsMode){
            glocProxy.start();
            PointF temp = getRealPinPts();
            glocProxy.addCalibData(temp.x,temp.y);
        }
        if (started) {
            PersistenceManager.getCurrentPersistencer().put(new LabelData(LabelData.OPERATION_ADD, map.getMapId(), getRealPinPts()));
        }
    }

    public boolean hasFocusedMarker() {
        return labelDisplayView.hasFocusedDot();
    }

    public void deleteFocusedMarker(boolean started) {
        labelDisplayView.deleteFocusedDot();
        if (started)
            PersistenceManager.getCurrentPersistencer().put(new LabelData(LabelData.OPERATION_REMOVE, map.getMapId(), toRealPinPts(labelDisplayView.getAbsFocusedPts(), 1)));
    }

    public void clearAllMarkers() {
        labelDisplayView.deleteAllDots();
    }

}