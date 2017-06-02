package com.kodiakapps.petbuddy;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.kodiakapps.petbuddy.barcode.BarcodeCaptureActivity;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static final String DEVICE_SERIAL = "serialno";

    private TextView mResultTextView;
    private String mPetBuddyDeviceSerial = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mResultTextView = (TextView) findViewById(R.id.result_textview);

        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        Button findPetBuddyButton = (Button) findViewById(R.id.buttonFind);
        findPetBuddyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(mPetBuddyDeviceSerial == "") {
                    Toast.makeText(getApplicationContext(), "First scan your PetBuddy QR Code",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent myIntent = new Intent(MainActivity.this, SearchSSIDs.class);
                    myIntent.putExtra(DEVICE_SERIAL, mPetBuddyDeviceSerial);
                    startActivity(myIntent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    mPetBuddyDeviceSerial = barcode.displayValue;
                    mResultTextView.setText("Your PetBuddy WiFi is: " + mPetBuddyDeviceSerial);
                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
