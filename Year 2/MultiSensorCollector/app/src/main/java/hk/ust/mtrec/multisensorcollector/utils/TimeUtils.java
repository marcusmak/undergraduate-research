package hk.ust.mtrec.multisensorcollector.utils;

import android.os.SystemClock;

/**
 * Created by tanjiajie on 2/21/17.
 */
public class TimeUtils {

    public static long currentElapsedTimeUs() {
        return SystemClock.elapsedRealtimeNanos() / 1000L;
    }

    public static long currentSystemTimeMs() {
        return System.currentTimeMillis();
    }

}
