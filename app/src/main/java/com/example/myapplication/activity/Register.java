package com.example.myapplication.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    EditText et_name,et_id,et_pw,et_pw2,et_birth,et_nick,et_phone;
    Button btn_pwcheck, btn_submit;
    private AlertDialog dialog;

    // 파이어베이스 스토어 객체 생성 및 선언
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // 태그 선언
    private final String TAG = "Register";
    Spinner spinner;

    // 스피너 선택값읋 저장할 변수
    String spinner_text = "선택필수";
    // 비밀번호 확인여부 검증 변수
    boolean pwd_checker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ArrayList<String> arrayList;
        ArrayAdapter<String> arrayAdapter;

        arrayList = new ArrayList<>();
        arrayList.add("선택필수");
        arrayList.add("쏘카");
        arrayList.add("그린카");
        arrayList.add("카플랫");
        arrayList.add("SK렌터카");
        arrayList.add("딜카");
        arrayList.add("카모아");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO 하위 버전 텍스트 색상 지원하기 위해 선언
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextSize(13);

                // 선택한 메뉴 목록 확인 실시
                //Toast.makeText(getApplicationContext(), arrayList.get(i)+" 선택", Toast.LENGTH_SHORT).show();
                String spinnerMenu = String.valueOf(arrayList.get(i));
                spinner_text = String.valueOf(arrayList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });




        //기입 항목
        // 위젯 바인딩
        et_name     = findViewById(R.id.edit_name);
        et_id       = findViewById(R.id.edit_id);
        et_pw       = findViewById(R.id.edit_pw);
        et_pw2      = findViewById(R.id.edit_pwre);
        et_birth    = findViewById(R.id.edit_birth);
        et_nick     = findViewById(R.id.edit_nick);
        et_phone    = findViewById(R.id.edit_phone);


        // 비밀번호 선택시
        // 현재 입력한 비밀번호값 초기화
        et_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_pw.setText("");
                et_pw2.setText("");
                pwd_checker = false;
            }
        });

        // 비밀번호 터치시
        // 현재 입력한 비밀번호값 초기화
        et_pw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                et_pw.setText("");
                et_pw2.setText("");
                pwd_checker = false;
                return false;
            }
        });

        //비밀번호 확인 버튼
        btn_pwcheck = findViewById(R.id.pwcheckbutton);
        btn_pwcheck.setOnClickListener(v -> {
            if (et_pw.getText().toString().equals(et_pw2.getText().toString())) {
                Toast.makeText(Register.this, "비밀번호 일치", Toast.LENGTH_SHORT).show();
                pwd_checker = true;
            } else {
                Toast.makeText(Register.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //회원가입 완료 버튼
        btn_submit = findViewById(R.id.signupbutton);
        btn_submit.setOnClickListener(v -> {
            String UserName   = et_name.getText().toString();
            String UserId     = et_id.getText().toString();
            String UserPw     = et_pw.getText().toString();
            String UserPw2    = et_pw2.getText().toString();
            String UserBirth  = et_birth.getText().toString();
            String UserPhone  = et_phone.getText().toString();
            String UserNick   = et_nick.getText().toString();

            if (UserName.equals("") || UserId.equals("") || UserPw.equals("") || UserPw2.equals("")
                    || UserBirth.equals("") || UserPhone.equals("") || UserNick.equals("") || spinner_text.equals("선택필수")){
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                dialog.show();
            }else if(!pwd_checker) {        // 비밀번호 확인을 하지 않았을 경우,
                Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요",Toast.LENGTH_LONG).show();
            }else {
                // 회원가입 함수 호출
                registUser(UserId,UserPw,UserName,UserNick,UserPhone,UserBirth);
            }
        });


    }
    // 회원가입 함수
    private void registUser(String id, String pwd, String name,String nickname, String phone, String birth){
        // 파이어 스토어의 user 컬렉션 선택
        db.collection("users")
                .get()                                                              // 레퍼런스 반환
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {    // 완료 리스너 추가
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {  // 파이어 스토어 조회 성공시,
                            // 중복 확인을 위한 Flag 변수
                            boolean ck_flag = true;

                            // 파이어 스토어의 모든 문서 순회
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    // 문서의 값을 JSON형으로 변경
                                    JSONObject jsonObject =  new JSONObject(document.getData().toString());
                                    // JSON 객체를 파싱
                                    String get_id = jsonObject.getString("Id");
                                    String get_nick = jsonObject.getString("Nickname");
                                    Log.d(TAG,document.getData().toString());


                                    if(get_id.equals(id)){  // 같은 아이디가 존재할 경우,
                                        Toast.makeText(getApplicationContext(), "중복된 아이디가 있습니다.",Toast.LENGTH_LONG).show();
                                        ck_flag = false;
                                    }else if(get_nick.equals(nickname)) {   // 같은 닉네임이 존재할 경우,
                                        Toast.makeText(getApplicationContext(), "중복된 닉네임이 있습니다.",Toast.LENGTH_LONG).show();
                                        ck_flag = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // 중복 확인을 위한 Flag 변수 값이
                            // True일 경우, 중복값이 없는것 이므로, 회원가입 실행
                            if (ck_flag){
                                // Map형 객체에 회원가입정보 삽입
                                Map<String, Object> user = new HashMap<>();

                                user.put("Id", id);
                                user.put("Pwd", pwd);
                                user.put("Nickname", nickname);
                                user.put("Name", name);
                                user.put("Phone", phone);
                                user.put("Birth", birth);
                                user.put("category", spinner_text);

                                // user 컬렉션에
                                db.collection("users")
                                        .add(user)  // 해당 map 객체를 삽입
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {    // 삽입 성공시,
                                                Toast.makeText(getApplicationContext(), "회원가입에 성공했습니다.",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {     // 삽입 실패 시,
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다. \n 관리자에게 문의하세요.",Toast.LENGTH_LONG).show();
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        } else {        // 파이어 스토어 조회 실패 시,
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}