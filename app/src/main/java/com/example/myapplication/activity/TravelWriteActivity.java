package com.example.myapplication.activity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Travel_pic_adapter;
import com.example.myapplication.tool.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TravelWriteActivity extends AppCompatActivity {
    private static final String TAG = "";
    RecyclerView recyclerView;
    EditText et_title;
    EditText et_content;
    Button btn_submit;
    Spinner sp_local;

    String userLocal;

    ArrayList<Uri> arrayList;
    Travel_pic_adapter travel_pic_adapter;
    View.OnClickListener onClickListener;

    // 파이어베이스 스토어 객체 생성
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // 파이어베이스 저장소 객체 생성
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_write);

        recyclerView = (RecyclerView) findViewById(R.id.rv_travel_write_pic);
        et_title = (EditText) findViewById(R.id.et_travel_write_title);
        et_content = (EditText) findViewById(R.id.et_travel_write_content);
        btn_submit = (Button) findViewById(R.id.btn_travel_write_submit);
        sp_local = (Spinner) findViewById(R.id.sp_local_write);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);

            }
        };

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        arrayList = new ArrayList<Uri>();
        travel_pic_adapter = new Travel_pic_adapter(arrayList, getApplicationContext(), onClickListener);

        recyclerView.setAdapter(travel_pic_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        initSpinner();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        } else {   // 이미지를 하나라도 선택한 경우
            if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                arrayList.add(0, imageUri);

                travel_pic_adapter = new Travel_pic_adapter(arrayList, getApplicationContext(), onClickListener);
                recyclerView.setAdapter(travel_pic_adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            } else {      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if (clipData.getItemCount() > 10) {   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            arrayList.add(0, imageUri);  //uri를 list에 담는다.
                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    travel_pic_adapter = new Travel_pic_adapter(arrayList, getApplicationContext(), onClickListener);
                    recyclerView.setAdapter(travel_pic_adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                }
            }
        }
    }

    public void submit() {
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
        String login_id = sharedPreferenceHelper.get_id();
        Map<String, Object> travel_data = new HashMap<>();

        travel_data.put("email", login_id);
        travel_data.put("title", et_title.getText().toString());
        travel_data.put("content", et_content.getText().toString());
        travel_data.put("img_url", "null");
        travel_data.put("local", userLocal);

        // user 컬렉션에
        db.collection("travel")
                .add(travel_data)  // 해당 map 객체를 삽입
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {    // 삽입 성공시,
                        String document_id = documentReference.getId();
                        travel_data.put("document_id", document_id);

                        // 업로드할 이미지가 없으면,
                        if (arrayList.size() == 0) {
                            Toast.makeText(getApplicationContext(), "게시물 작성완료..", Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            Intent intent = new Intent(getApplicationContext(), NaviActivity.class);
                            intent.putExtra("REQUEST", "SHOW_TRAVEL");
                            startActivity(intent);
                            finish();
                        } else {
                            // 이미지 업로드
                            ArrayList<String> img_url = new ArrayList<String>();
                            for (int i = 0; i < arrayList.size(); i++) {
                                int cur = i;
                                Uri file = arrayList.get(i);
                                String imgUrlStr = "travel/" + document_id + "/" + String.valueOf(i) + ".png";
                                img_url.add(imgUrlStr);


                                StorageReference storageRef = storage.getReference();
                                StorageReference imgRef = storageRef.child(imgUrlStr);
                                UploadTask uploadTask = imgRef.putFile(file);

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if (cur == arrayList.size() - 1) {
                                            String imgUrlsString = "";
                                            JSONObject obj = new JSONObject();
                                            try {
                                                JSONArray jArray = new JSONArray();
                                                for (int j = 0; j < img_url.size(); j++) {
                                                    jArray.put(img_url.get(j).toString());
                                                }
                                                obj.put("item", jArray);
                                                System.out.println();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            imgUrlsString = obj.toString();

                                            travel_data.put("img_url", imgUrlsString);
                                            travel_data.put("document_id", document_id);
                                            // img_url String 업로드
                                            db.collection("travel")
                                                    .document(document_id)
                                                    .update(travel_data)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "오류발생", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "게시물 작성완료..", Toast.LENGTH_LONG).show();

                                                            Intent intent = new Intent(getApplicationContext(), NaviActivity.class);
                                                            intent.putExtra("REQUEST", "SHOW_TRAVEL");
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {     // 삽입 실패 시,
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "게시물 작성 실패 \n 관리자에게 문의하세요.", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void initSpinner() {
        String[] items = getResources().getStringArray(R.array.local_list);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        sp_local.setAdapter(spinnerAdapter);

        sp_local.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userLocal = items[position];
                Log.i(TAG, "onItemSelected: userLocal = "+userLocal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userLocal = items[0];
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NaviActivity.class);
        intent.putExtra("REQUEST", "SHOW_TRAVEL");
        startActivity(intent);
        finish();
    }
}