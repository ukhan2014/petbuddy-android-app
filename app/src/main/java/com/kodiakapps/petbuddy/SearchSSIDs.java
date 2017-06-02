package com.kodiakapps.petbuddy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

/**
 * Created by usman on 5/29/17.
 */

public class SearchSSIDs extends Activity implements View.OnClickListener
{
    private static final String TAG = "PetBuddy";
    private static final String DEVICE_SERIAL = "serialno";
    private String deviceName = "";
    private String homeWifiSSID = "";
    private String homeWifiPSK = "";

    WifiManager wifi;
    TextView textStatus;
    TextView connectNetworkTV;
    Button buttonScan;
    Button buttonWifiInfoSubmit;
    EditText homeSsidEt;
    EditText homePskEt;
    List<ScanResult> results;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ssid);

        textStatus = (TextView) findViewById(R.id.textStatus);
        textStatus.setVisibility(View.INVISIBLE);
        connectNetworkTV = (TextView) findViewById(R.id.connectNetworkTV);
        connectNetworkTV.setVisibility(View.INVISIBLE);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        buttonScan.setVisibility(View.INVISIBLE);

        buttonWifiInfoSubmit = (Button) findViewById(R.id.submitWifiInfoButton);
        buttonWifiInfoSubmit.setOnClickListener(this);

        homeSsidEt = (EditText) findViewById(R.id.enterSsid);
        homePskEt = (EditText) findViewById(R.id.enterPsk);

        final String serialno;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                serialno= null;
            } else {
                serialno= extras.getString(DEVICE_SERIAL);
            }
        } else {
            serialno= (String) savedInstanceState.getSerializable(DEVICE_SERIAL);
        }

        deviceName = "PBD" + serialno;

        Log.d(TAG, "deviceName is: " + deviceName);
        Log.d(TAG, "PSK is: " + serialno);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                Toast.makeText(getApplicationContext(), "got Scan Results", Toast.LENGTH_LONG).show();
                textStatus.setText("Receiving");
                results = wifi.getScanResults();
                if(results.toString().contains(deviceName)) {
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
                    int res = wifi.addNetwork(myWiFiConfig);
                    Log.d(TAG, "addNetwork returned " + res );
                    boolean  networkEnabled = wifi.enableNetwork(res, true);
                    Log.d(TAG, "enableNetwork returned " + networkEnabled );
                }
                else {
                    Toast.makeText(getApplicationContext(), "Could not find your PetBuddy :(", Toast.LENGTH_LONG).show();
                }

            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.buttonScan:
                Log.d(TAG, "Starting WiFi scan");
                wifi.startScan();

                Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
                textStatus.setText("Scanning");
                break;

            case R.id.submitWifiInfoButton:


                homeWifiSSID = homeSsidEt.getText().toString();
                homeWifiPSK = homePskEt.getText().toString();

                homeSsidEt.setVisibility(View.GONE);
                homePskEt.setVisibility(View.GONE);
                buttonWifiInfoSubmit.setVisibility(View.GONE);
                TextView enterWifiInfo = (TextView) findViewById(R.id.enterWifiInfo);
                enterWifiInfo.setVisibility(View.GONE);

                textStatus.setVisibility(View.VISIBLE);
                connectNetworkTV.setVisibility(View.VISIBLE);
                buttonScan.setVisibility(View.VISIBLE);
                Log.d(TAG, "User input ssid=" + homeWifiSSID + "  psk=" + homeWifiPSK);
                break;
        }

    }
}
