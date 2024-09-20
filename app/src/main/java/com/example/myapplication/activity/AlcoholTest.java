package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.Random;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class AlcoholTest extends AppCompatActivity {

    public static final String TAG = "AlcholTest";

    public enum Status {
        IDLE, DETECTED, PASS
    }

    TextView statusText;
    TextView msgText;
    TextView valueText;
    LinearLayout circleView;
    View backView;

    private BluetoothSPP bt;

    boolean isConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_test);

        statusText = findViewById(R.id.status);
        msgText = findViewById(R.id.message);
        valueText = findViewById(R.id.value);
        circleView = findViewById(R.id.centerCircle);
        backView = findViewById(R.id.backgroundTop);

        bt = new BluetoothSPP(this);



        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(this, "블루투스 지원기기가 아닙니다.", Toast.LENGTH_SHORT).show();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {

            TextView value = findViewById(R.id.value);

            @SuppressLint("SetTextI18n")
            public void onDataReceived(byte[] data, String message) {
                value.setText(message);
                CheckAlcol();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "블루투스 연결됨.", Toast.LENGTH_SHORT).show();
            //    btnConnect.setText("연결 해제");
             ///   Log.e(TAG, "connect");
                isConnect = true;
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "블루투스 해제됨.", Toast.LENGTH_SHORT).show();
             //   btnConnect.setText("측정기 연결");
              //  btnSend.setText("측정");
           //     Log.e(TAG, "disconnect");
                isConnect = false;
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "블루투스 연결 실패.", Toast.LENGTH_SHORT).show();
              //  Log.e(TAG, "faile");
                isConnect = false;
            }
        });


        circleView.setOnClickListener(v->{
            if(!isConnect){
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }else{
                bt.send("T", true);
            }
            /*Status status = Status.values()[new Random().nextInt(3)];
            setValue(new Random().nextFloat()/10);
            setStatus(status);*/
        });


    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Log.e(TAG, "OK");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                Log.e(TAG,"NO");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                //setup();
            }
        }
    }

    void CheckAlcol() {
        float alcol = Float.parseFloat((String)valueText.getText());
        if(alcol < 0.03) {
            setStatus(Status.PASS);
        } else {
            setStatus(Status.DETECTED);
        }
    }

    void setValue(float v) {
        valueText.setText(String.format("%.3f", v));
    }

    void setStatus(Status status) {
        switch (status) {
            case IDLE:
                statusText.setText("미측정");
                msgText.setText("음주상태를 확인해주세요.");
                circleView.setBackgroundResource(R.drawable.circle_center_black);
                backView.setBackgroundResource(R.drawable.top_round_background_black);
                break;
            case DETECTED:
                statusText.setText("음주 상태");
                msgText.setText("알콜 성분이 감지되었습니다.");
                circleView.setBackgroundResource(R.drawable.circle_center_red);
                backView.setBackgroundResource(R.drawable.top_round_background_red);
                break;
            case PASS:
                statusText.setText("정상");
                msgText.setText("알콜 성분이 감지되지 않았습니다.");
                circleView.setBackgroundResource(R.drawable.circle_center_blue);
                backView.setBackgroundResource(R.drawable.top_round_background_blue);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else { // 사용불가
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}