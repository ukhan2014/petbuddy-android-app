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

public class WifiScanActivity extends Activity implements View.OnClickListener
{
    WifiManager wifi;
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);

        textStatus = (TextView) findViewById(R.id.textStatus);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
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
        this.adapter = new SimpleAdapter(WifiScanActivity.this, arraylist, R.layout.row,
                new String[] { ITEM_KEY }, new int[] { R.id.list_value });
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void onClick(View view)
    {
        arraylist.clear();
        wifi.startScan();

        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(ITEM_KEY, results.get(size).level + "," + results.get(size).SSID + ","
                        + results.get(size).capabilities);

                arraylist.add(item);
                Collections.reverse(arraylist);
//                Collections.sort(arraylist, new Comparator<String>() {
//                    public int compare(String s1, String s2) {
//                        int secondCommaIdx_s1 = s1.indexOf(',', 1 + s1.indexOf(','));
//                        int secondCommaIdx_s2 = s2.indexOf(',', 1 + s2.indexOf(','));
//
//                        int signalStrength_s1 = Integer.parseInt(s1.substring(secondCommaIdx_s1+1));
//                        int signalStrength_s2 = Integer.parseInt(s2.substring(secondCommaIdx_s2+1));
//
//                        if (signalStrength_s1 >= signalStrength_s2) {
//                            return -1;
//                        } else {
//                            return 1;
//                        }
//                    }
//                });
                size--;
                adapter.notifyDataSetChanged();

            }
        }
        catch (Exception e)
        { }
    }
}
