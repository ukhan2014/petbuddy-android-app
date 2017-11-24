package com.kodiakapps.petbuddy;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ConfirmWiFiSelectionActivity extends AppCompatActivity {

    private TextView selectedWifiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_wi_fi_selection);

        Resources res = getResources();

        String ssid = (String)(getIntent().getExtras().get("ssid"));

        selectedWifiTextView = (TextView) findViewById(R.id.selected_wifi_textview);
        String text = String.format(res.getString(R.string.selectedWifi_str), ssid);
        selectedWifiTextView.setText(text);
    }
}
