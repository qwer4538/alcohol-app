package com.example.myapplication.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.AlcoholTest;


import java.util.Random;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class Fragment_home extends Fragment {

    public static final String TAG = "Fragment_home";

    enum Status {
        IDLE, DETECTED, PASS
    }

    TextView statusText;
    TextView msgText;
    TextView valueText;
    LinearLayout circleView;
    View backView;

    private BluetoothSPP bt;

    boolean isConnect = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_alcohol_test, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        statusText  = view.findViewById(R.id.status);
        msgText     = view.findViewById(R.id.message);
        valueText   = view.findViewById(R.id.value);
        circleView  = view.findViewById(R.id.centerCircle);
        backView    = view.findViewById(R.id.backgroundTop);


        bt = new BluetoothSPP(getActivity());



        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getActivity(), "블루투스 지원기기가 아닙니다.", Toast.LENGTH_SHORT).show();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {



            @SuppressLint("SetTextI18n")
            public void onDataReceived(byte[] data, String message) {
                valueText.setText(message);
                CheckAlcol();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getActivity(), "블루투스 연결됨.", Toast.LENGTH_SHORT).show();
                //    btnConnect.setText("연결 해제");
                ///   Log.e(TAG, "connect");
                isConnect = true;
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getActivity(), "블루투스 해제됨.", Toast.LENGTH_SHORT).show();
                //   btnConnect.setText("측정기 연결");
                //  btnSend.setText("측정");
                //     Log.e(TAG, "disconnect");
                isConnect = false;
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getActivity(), "블루투스 연결 실패.", Toast.LENGTH_SHORT).show();
                //  Log.e(TAG, "faile");
                isConnect = false;
            }
        });


        circleView.setOnClickListener(v->{
            if(!isConnect){
                Intent intent = new Intent(getActivity(), DeviceList.class);
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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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
            setStatus(AlcoholTest.Status.PASS);
        } else {
            setStatus(AlcoholTest.Status.DETECTED);
        }
    }

    void setValue(float v) {
        valueText.setText(String.format("%.3f", v));
    }

    void setStatus(AlcoholTest.Status status) {
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
                Toast.makeText(getActivity()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

}