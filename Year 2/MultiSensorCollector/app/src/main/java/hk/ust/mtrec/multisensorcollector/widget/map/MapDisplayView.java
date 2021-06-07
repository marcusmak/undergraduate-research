package hk.ust.mtrec.multisensorcollector.widget.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MapDisplayView extends View {

    private Bitmap mBitmap;
    private float mLeft;
    private float mTop;

    private final static String TAG = "mapView";
    private final static float RATE_MAX = 3;
    private final static float RATE_MIN = 0.5f;


    public final static int TYPE_SELECT_TARGET = 0;
    public final static int TYPE_SELECT_MIDDLE = 1;
    public final static int TYPE_CORRECT_POSITION = 2;

    private Bitmap mTargetBitmap;
    private float mScaleRate;
    private float mPrevScaleRate;

    private PointF mTargetPointF;
    private List<PointF> mCollectedFinishPointFs;
    private Paint mCirclePaint;

    public MapDisplayView(Context context) {
        super(context);
        mCollectedFinishPointFs = new ArrayList<PointF>();
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.RED);
    }

    public MapDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCollectedFinishPointFs = new ArrayList<PointF>();
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.RED);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mLeft = 0;
        mTop = 0;
        mScaleRate = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mLeft, mTop);
        Log.v("position", "mLeft:" + mLeft + " mTop:" + mTop);
        canvas.scale(mScaleRate, mScaleRate);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // move?
        if (mTargetPointF != null)
            canvas.drawBitmap(mTargetBitmap,
                    mTargetPointF.x - mTargetBitmap.getWidth() / 4.0f,
                    mTargetPointF.y - mTargetBitmap.getHeight(), null);
    }

//    public void setScaleRate(float rate) {
//        float scaleRate = correctScaleRate(rate);
//        if (mPrevScaleRate == 0 || mScaleRate == 0)
//            mPrevScaleRate = mScaleRate = scaleRate;
//        else {
//            mLeft = -((-mLeft + getWidth() / 2) * mScaleRate / mPrevScaleRate - getWidth() / 2);
//            mTop = -((-mTop + getHeight() / 2) * mScaleRate / mPrevScaleRate - getHeight() / 2);
//            mPrevScaleRate = mScaleRate;
//            mScaleRate = scaleRate;
//        }
//
//        postInvalidate();
//    }

    public void setTargetBitmap(Bitmap bitmap) {
        mTargetBitmap = bitmap;
    }

    public void drawCircle(PointF pointF) {
        mCollectedFinishPointFs.add(pointF);
        postInvalidate();
    }

    public void setTargetLocation(float x, float y) {
        if (mTargetPointF == null)
            mTargetPointF = new PointF(x, y);
        else
            mTargetPointF.set(x, y);

        postInvalidate();
    }

    public void setMapPositionMiddle(float x, float y) {
        mLeft = getWidth() / 2 - x * mScaleRate;
        mTop = getHeight() / 2 - y * mScaleRate;
    }

    public void clear() {
        mCollectedFinishPointFs.clear();
    }

    public void setMapPosition(float left, float top) {
        mLeft = left;
        mTop = top;
    }

    public float setScaleRate(float scaleRate) {
        return mScaleRate;
    }

    public float getScaleRate() {
        return mScaleRate;
    }

    public void move(final float offX, final float offY) {
        this.mLeft += offX;
        this.mTop += offY;
        postInvalidate();
    }

    public void zoom(final float scale) {
        this.mScaleRate = scale;
        postInvalidate();
    }

    public void update() {
        postInvalidate();
    }
}