package com.kodiakapps.petbuddy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.kodiakapps.petbuddy.barcode.BarcodeCaptureActivity;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by usman on 5/29/17.
 */

public class SearchSSIDs extends Activity {
    private static final String TAG = "PBD:SearchSSIDs";
    private String deviceName = "";
    private String serialno;

    WifiManager wifi;
    TextView textStatus;
    TextView connectNetworkTV;
    List<ScanResult> results;
    ImageView iw;
    Handler mHandler;
    boolean exitActivityFirstRun = false;
    int exitActivityRuns = 0;
    private BroadcastReceiver wifiReceiver;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ssid);
        mHandler = new Handler();

        Barcode barcode = (Barcode)(getIntent().getExtras().get("Barcode"));
        serialno = barcode.displayValue;

        deviceName = "PB" + serialno;

        Log.d(TAG, "deviceName is: " + deviceName);
        Log.d(TAG, "PSK is: " + serialno);

        textStatus = (TextView) findViewById(R.id.textStatus);
        textStatus.setVisibility(View.VISIBLE);
        connectNetworkTV = (TextView) findViewById(R.id.connectNetworkTV);
        connectNetworkTV.setVisibility(View.VISIBLE);
        iw = (ImageView) findViewById(R.id.status_indicator_light);
        iw.setImageResource(R.drawable.redlight);


        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        Log.d(TAG, "Starting WiFi scan");
        wifi.startScan();

        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
        textStatus.setText("Scanning");


        final Runnable exitActivityTask = new Runnable() {
            public void run() {
                if(exitActivityRuns != 10) {
                    exitActivityRuns++;
                    blinkConnectedText();
                    mHandler.postDelayed(this, 150);
                } else {
                    mHandler.removeCallbacksAndMessages(null);
                    Intent intent = new Intent(SearchSSIDs.this, RegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };


        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                Log.d(TAG, "onReceive()");
                Toast.makeText(getApplicationContext(), "got Scan Results", Toast.LENGTH_LONG).show();
                textStatus.setText("Receiving");
                results = wifi.getScanResults();
                if (results.toString().contains(deviceName)) {
                    Toast.makeText(getApplicationContext(), "PetBuddy Found!", Toast.LENGTH_LONG).show();

                    WifiConfiguration myWiFiConfig = new WifiConfiguration();
                    myWiFiConfig.preSharedKey = "\"" + serialno + "\"";
                    myWiFiConfig.SSID = "\"" + deviceName + "\"";
                    myWiFiConfig.status = WifiConfiguration.Status.ENABLED;
                    myWiFiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    myWiFiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    myWiFiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    myWiFiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    myWiFiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    myWiFiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    final int res = wifi.addNetwork(myWiFiConfig);

                    final ConnectivityManager connectivityManager  =
                            (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkRequest.Builder request = null;
                    if (android.os.Build.VERSION.SDK_INT >=
                            android.os.Build.VERSION_CODES.LOLLIPOP) {
                        request = new NetworkRequest.Builder();

                        request.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

                        connectivityManager.registerNetworkCallback(request.build(), new ConnectivityManager.NetworkCallback() {

                            @Override
                            public void onAvailable(Network network) {
                                //if (SDK_INT >= LOLLIPOP && SDK_INT <= M) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Log.d(TAG, "setting default network to WIFI");
                                    ConnectivityManager.setProcessDefaultNetwork(network);
                                    wifi.enableNetwork(res, true);
                                    wifi.reconnect();
                                }

                            }
                        });
                    }

                    Log.d(TAG, "addNetwork returned " + res);
                    boolean networkEnabled = wifi.enableNetwork(res, true);
                    wifi.reconnect();
                    Log.d(TAG, "enableNetwork returned " + networkEnabled);
                    if(networkEnabled) {
                        DhcpInfo dhcpInfo = wifi.getDhcpInfo();
                        byte[] ipAddress = convert2Bytes(dhcpInfo.serverAddress);
                        try {
                            String apIpAddr = InetAddress.getByAddress(ipAddress).getHostAddress();
                            Log.d(TAG, "IP address of AP = " + apIpAddr);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }



                        //if(isAPNEnabled(getApplicationContext())) {
//                            Log.d(TAG, "Disabling cell network Internet");
//                            updateAPN(getApplicationContext(), false);
                        //}

                        unregisterReceiver(wifiReceiver);
                        textStatus.setText("Connected!");
                        iw.setImageResource(R.drawable.greenlight);

                        if(!exitActivityFirstRun) {
                            exitActivityFirstRun = true;
                            mHandler.postDelayed(exitActivityTask, 250);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find your PetBuddy :(", Toast.LENGTH_LONG).show();
                }

            }
        };

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private static byte[] convert2Bytes(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };
        return addressBytes;
    }

    private static void updateAPN(Context paramContext, boolean enable) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    paramContext.getSystemService("connectivity");
            Method setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod(
                    "setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(connectivityManager, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isAPNEnabled(Context paramContext) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) paramContext.getSystemService(
                    "connectivity")).getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }



    public void blinkConnectedText() {
        if (textStatus.getVisibility() == View.VISIBLE)
            textStatus.setVisibility(View.INVISIBLE);
        else
            textStatus.setVisibility(View.VISIBLE);
    }
}
