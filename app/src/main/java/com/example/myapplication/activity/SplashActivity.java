package com.example.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends Activity {

    //권한 코드
    private static final int PERMISSIONCODE = 100;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        checkPermission();
    }

    //권한 Check
    private void checkPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
           moveMain();
        } else if(Build.VERSION.SDK_INT <= 30){
            // Permission check

            int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionReadStroage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if(
                    permissionWriteStorage== PackageManager.PERMISSION_DENIED
                    ||permissionCoarseLocation== PackageManager.PERMISSION_DENIED
                    ||permissionReadStorage== PackageManager.PERMISSION_DENIED
                    ||permissionReadStroage == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONCODE);
            }else {

                moveMain();
            }
        }else {
            // Permission check
            int permissionBLUETOOTH_SCAN = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
            int permissionBLUETOOTH_CONNECT = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);

            int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if(
                    permissionWriteStorage== PackageManager.PERMISSION_DENIED
                    ||permissionBLUETOOTH_SCAN== PackageManager.PERMISSION_DENIED
                    ||permissionBLUETOOTH_CONNECT== PackageManager.PERMISSION_DENIED
                    ||permissionReadStorage== PackageManager.PERMISSION_DENIED
                    ){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                }, PERMISSIONCODE);
            }else {
                moveMain();

            }
        }
    }

    private void moveMain() {
        try{
            Thread.sleep(0000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONCODE :
                if(grantResults.length >0) {
                    moveMain();
                    // 권한 거부
                }else{
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_LONG);
                    finish();
                }
        }
    }
}
