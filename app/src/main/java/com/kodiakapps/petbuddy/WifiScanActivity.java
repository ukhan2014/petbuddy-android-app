package com.kodiakapps.petbuddy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WifiScanActivity extends Activity
{
    private final String TAG = "PBD:WiFiScanActivity";

    WifiManager wifi;
    ListView lv;

    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    String ITEM_KEY = "key";

    SimpleAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);

        lv = (ListView)findViewById(R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) lv.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),"You selected : " + item,Toast.LENGTH_SHORT).show();
            }
        });

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        wifi.startScan();
        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();

        this.adapter = new SimpleAdapter(WifiScanActivity.this, arraylist, R.layout.row,
                new String[] { ITEM_KEY }, new int[] { R.id.list_value });
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                int size = 0;
                List<ScanResult> results;

                // Create Temporary HashMap
                HashMap<String, ScanResult> resultHashMap =
                        new HashMap<String, ScanResult>();
                // Add to new List
                List<ScanResult> sortedWifiList = new ArrayList<>();
                resultHashMap.clear();

                results = wifi.getScanResults();
                Log.d(TAG, "size of results is " + results.size());
                // Add ScanResults to Map to remove duplicates
                for (ScanResult scanResult : results) {
                    if (scanResult.SSID != null &&
                            !scanResult.SSID.isEmpty()) {
                        resultHashMap.put(scanResult.SSID, scanResult);
                        sortedWifiList.clear();
                        sortedWifiList.addAll(resultHashMap.values());

                        size = sortedWifiList.size();
                        // Create Comparator to sort by level
                        Comparator<ScanResult> comparator =
                                new Comparator<ScanResult>() {
                                    @Override
                                    public int compare(ScanResult lhs, ScanResult rhs) {
                                        //Log.d(TAG, lhs.SSID+":"+lhs.level+"  "+rhs.SSID+":"+rhs.level);
                                        if(lhs.level < rhs.level) {
                                            return -1;
                                        } else if(lhs.level == rhs.level) {
                                            return 0;
                                        } else {
                                            return 1;
                                        }
                                    }
                                };

                        // Apply Comparator and sort
                        Collections.sort(sortedWifiList, comparator);

                        Log.d(TAG, "got new wifi scan results");
                    }

                    size = size - 1;
                    arraylist.clear();
                    while (size >= 0) {
                        String ssid = sortedWifiList.get(size).SSID;
                        int level = sortedWifiList.get(size).level;

                        Log.d(TAG, "size="+size+" ssid="+ssid+" lvl="+level);
                        HashMap<String, String> item = new HashMap<>();

                        item.put(ITEM_KEY, ssid + " " + level);
                        arraylist.add(item);

                        size--;

                    }

                    adapter.notifyDataSetChanged();
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


}
