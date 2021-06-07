package hk.ust.mtrec.multisensorcollector;

import org.junit.Test;

import hk.ust.mtrec.multisensorcollector.utils.GpsMappingUtils;
import hk.ust.mtrec.multisensorcollector.sensor.gps.GLocationSensorProxy;
/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }

    @Test
    public void test_GpsMapping(){
        GpsMappingUtils testSet = new GpsMappingUtils();
        testSet.addCalibData(114.263612,22.337691,0,0);
        testSet.addCalibData(114.263912,22.337691,33,0);
        testSet.addCalibData(114.263912,22.337428,33,12);
        double[] result = testSet.compute(114.263612,22.337428);
//        GLocationSensorProxy g = new GLocationSensorProxy(null,0);
//
//        System.out.println(g.getCurrentLocation());


    }
}