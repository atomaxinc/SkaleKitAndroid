package com.atomax.android.skaleexample;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.atomax.android.skaleutils.Controller.WeightStableChecker;
import com.atomax.android.skaleutils.SkaleHelper;

public class SkaleActivity extends AppCompatActivity implements SkaleHelper.Listener {

    private static final int REQUEST_BT_ENABLE = 2;
    private static final int REQUEST_BT_PERMISSION = 1;

    private SkaleHelper mSkaleHelper;

    private TextView mStatusTextView;
    private TextView mWeightTextView;
    private TextView mBatteryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skale);


        mSkaleHelper = new SkaleHelper(this);
        mSkaleHelper.setListener(this);

        mStatusTextView = (TextView) findViewById(R.id.text_status);
        mWeightTextView = (TextView) findViewById(R.id.text_weight);
        mBatteryTextView = (TextView) findViewById(R.id.text_battery);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mSkaleHelper.isBluetoothEnable()){
            boolean hasPermission = SkaleHelper.hasPermission(this);
            if(hasPermission){
                mSkaleHelper.resume();
                mStatusTextView.setText("finding skale...");
            }else{
                SkaleHelper.requestBluetoothPermission(this, REQUEST_BT_PERMISSION);
            }
        }else{
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_BT_ENABLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSkaleHelper.pause();
    }

    @Override
    public void onButtonClicked(int id) {
        Toast.makeText(this, "button " + id + " is clicked", Toast.LENGTH_SHORT).show();


        if(id == 1){
            mSkaleHelper.tare();
        }else{
            // TODO invoke some function here for squre button
        }
    }

    @Override
    public void onWeightUpdate(float weight) {

        mWeightTextView.setText(String.format("%1.1f g", weight));
    }

    @Override
    public void onBindRequest() {

        mStatusTextView.setText("New skale found, paring with it.");
    }

    @Override
    public void onBond() {
        mStatusTextView.setText("Paring done, Connecting");
    }

    @Override
    public void onConnectResult(boolean success) {

        if(success){
            mStatusTextView.setText("connected");
        }

    }

    @Override
    public void onDisconnected() {
        mStatusTextView.setText("disconnected");
    }

    @Override
    public void onBatteryLevelUpdate(int level) {
        mBatteryTextView.setText(String.format("battery: %02d", level));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_BT_PERMISSION) {

            boolean result = SkaleHelper.checkPermissionRequest(requestCode, permissions, grantResults);

            if(result){
                mSkaleHelper.resume();
            }else{
                Toast.makeText(this, "No bluetooth permission", Toast.LENGTH_SHORT).show();
            }

            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
