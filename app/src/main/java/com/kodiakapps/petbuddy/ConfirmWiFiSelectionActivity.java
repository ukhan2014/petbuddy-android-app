package com.kodiakapps.petbuddy;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfirmWiFiSelectionActivity extends AppCompatActivity {

    private final String TAG = "PBD:ConfirmWiFiSelectionActivity";
    private Resources res;
    private TextView selectedWifiTextView;
    private Button yesBtn;
    private Button noBtn;
    private TextView wifiPwdTextView;
    private Button submitBtn;
    private String ssid = "";
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_wi_fi_selection);


        res = getResources();
        ssid = (String) (getIntent().getExtras().get("ssid"));

        wifiPwdTextView = (TextView) findViewById(R.id.wifiPwd);
        wifiPwdTextView.setVisibility(View.INVISIBLE);

        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setVisibility(View.INVISIBLE);

        selectedWifiTextView = (TextView) findViewById(R.id.selected_wifi_textview);
        yesBtn = (Button) findViewById(R.id.yesBtn);
        noBtn = (Button) findViewById(R.id.noBtn);

        text = String.format(res.getString(R.string.selectedWifi_str), ssid);
        selectedWifiTextView.setText(text);
        yesBtn.setText(R.string.confirm_wifi_btn);
        noBtn.setText(R.string.reselect_wifi_btn);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                text = String.format(res.getString(R.string.enterWifiPwd_str), ssid);
                selectedWifiTextView.setText(text);
                ViewGroup.LayoutParams lop = yesBtn.getLayoutParams();
                yesBtn.setVisibility(View.GONE);
                noBtn.setVisibility(View.GONE);
                wifiPwdTextView.setVisibility(View.VISIBLE);
                wifiPwdTextView.setLayoutParams(lop);
                submitBtn.setVisibility(View.VISIBLE);
                submitBtn.setText(R.string.submit_btn);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(getApplicationContext(), WifiScanActivity.class);
                startActivity(myIntent);
                finish();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String psk = wifiPwdTextView.getText().toString();
                String wifiinfo = "ssid=".concat(ssid).concat("\npsk=").concat(psk);

                Log.d(TAG, "Built wifi info str:\n" + wifiinfo);

                Intent myIntent = new Intent(getApplicationContext(), SendToPbdNew.class);
                myIntent.putExtra("type", "wifiinfo");
                myIntent.putExtra("wifiinfo", wifiinfo);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
