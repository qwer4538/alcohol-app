package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.tool.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    // 위젯 선언
    EditText et_id;
    EditText et_pwd;
    TextView btn_register;
    TextView btn_login;

    // 파이어베이스 스토어 객체 생성
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // 태그 선언
    private final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위젯 바인딩
        et_id   = findViewById(R.id.edit_id);
        et_pwd  = findViewById(R.id.edit_pw);

        //회원가입 버튼
        btn_register = findViewById(R.id.btn_register);

        //회원가입 버튼 클릭시, 회원가입 페이지로 이동
        btn_register.setOnClickListener(v -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });

        //로그인 버튼
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            login();
        });
    }

    private void login(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // 아이디와 비밀번호를 변수에 삽입
                            String str_id   = et_id.getText().toString();
                            String str_pwd  = et_pwd.getText().toString();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    JSONObject jsonObject =  new JSONObject(document.getData().toString());
                                    String get_id   = jsonObject.getString("Id");
                                    String get_pwd  = jsonObject.getString("Pwd");
                                    String get_name = jsonObject.getString("Nickname");
                                    Log.d(TAG,document.getData().toString());
                                    if(get_id.equals(str_id)){
                                        if(!get_pwd.equals(str_pwd)) {
                                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                                            return;
                                        }else {
                                            SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
                                            sharedPreferenceHelper.set_id(get_id);
                                            sharedPreferenceHelper.set_name(get_name);

                                            Toast.makeText(getApplicationContext(), get_name+ "님 환영합니다.",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), NaviActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            Toast.makeText(getApplicationContext(), "일치하는 아이디가 없습니다.",Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            finish();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}