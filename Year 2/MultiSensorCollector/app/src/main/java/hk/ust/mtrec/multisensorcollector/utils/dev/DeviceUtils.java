package hk.ust.mtrec.multisensorcollector.utils.dev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.lang.NullPointerException;

/**
 * Created by tanjiajie on 2/20/17.
 */
public class DeviceUtils {

    private static String androidId;
    private static String macAddr;

    public static String getAndroidId(Context context) {
        if (androidId == null) {
            androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("devUtils", "androidId = " + androidId);
        }
        return androidId;
    }

    public static String getMacAddr(Context context) {
        if (macAddr == null) {
            // 02:00:00:00:00:00
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String _macAddr = wifiManager.getConnectionInfo().getMacAddress();
            try{
                if (_macAddr.equals("02:00:00:00:00:00")) {
                    Log.e("qwertyuio","hi");

                    try {
                        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                        while (interfaces.hasMoreElements()) {
                            NetworkInterface netIf = interfaces.nextElement();
                            if (netIf.getName().equals("wlan0")) {
                                StringBuilder buf = new StringBuilder();
                                for (byte b : netIf.getHardwareAddress()) {
                                    buf.append(String.format("%02X:", b));
                                }
                                if (buf.length() > 0) {
                                    buf.deleteCharAt(buf.length() - 1);
                                }
                                _macAddr = buf.toString();
                                break;
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
            }catch(NullPointerException e){
                Toast.makeText(context,"Enable Wifi",Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                ((Activity) context).startActivityForResult(intent,0);
            }

            macAddr = _macAddr;
            Log.d("devUtils", "macAddr = " + macAddr);
        }
        return macAddr;
    }


}
