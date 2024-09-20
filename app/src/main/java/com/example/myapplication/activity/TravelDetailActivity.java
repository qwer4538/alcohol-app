package com.example.myapplication.activity;

import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Travel_comment_recycler_adapter;
import com.example.myapplication.adapter.Travel_detial_pic_adapter;
import com.example.myapplication.model.Travel_comment_list_item;
import com.example.myapplication.model.Travel_list_item;
import com.example.myapplication.tool.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TravelDetailActivity extends AppCompatActivity {
    private static final String TAG = TravelDetailActivity.class.getName() +" 로그";
    TextView tv_title;
    TextView tv_content;

    EditText et_title;
    EditText et_content;
    EditText et_comment;
    TextView tv_writer;
    Button btn_modify;
    Button btn_delete;
    Button btn_cancle;
    Button btn_submit;
    Button btn_comment_submit;

    RecyclerView rv_comment;

    LinearLayout layout_modify;
    LinearLayout layout_button;

    RecyclerView recyclerView;

    ArrayList<Uri> arrayList;
    ArrayList<Travel_comment_list_item> comment_arrayList;

    Travel_detial_pic_adapter travel_pic_adapter;
    Travel_comment_recycler_adapter travel_comment_adapter;
    Travel_list_item data;

    String name;
    String id;

    // 파이어베이스 스토어 객체 생성
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

        Intent intent = getIntent();
        data = (Travel_list_item)intent.getSerializableExtra("data");
        Log.d("Detail_get_data", data.toString());



        tv_title        = (TextView)        findViewById(R.id.tv_travel_detail_title);
        tv_content      = (TextView)        findViewById(R.id.tv_travel_detail_content);
        et_title        = (EditText)        findViewById(R.id.et_travel_detail_title);
        et_content      = (EditText)        findViewById(R.id.et_travel_detail_content);
        et_comment      = (EditText)        findViewById(R.id.et_travel_detail_comment);
        tv_writer       = (TextView)        findViewById(R.id.tv_travel_detail_writer);
        recyclerView    = (RecyclerView)    findViewById(R.id.rv_travel_detail_pic);
        btn_modify      = (Button)          findViewById(R.id.btn_travel_detail_modify);
        btn_delete      = (Button)          findViewById(R.id.btn_travel_detail_delete);
        btn_submit      = (Button)          findViewById(R.id.btn_travel_detail_submit);
        btn_cancle      = (Button)          findViewById(R.id.btn_travel_detail_cancle);
        btn_comment_submit  = (Button)      findViewById(R.id.btn_travel_detail_comment_submit);
        layout_button   = (LinearLayout)    findViewById(R.id.layout_taravel_detail_button);
        layout_modify   = (LinearLayout)    findViewById(R.id.layout_taravel_detail_modify);

        rv_comment      = (RecyclerView)        findViewById(R.id.rv_travel_detial_comment);

        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
        name    = sharedPreferenceHelper.get_name();
        id      = sharedPreferenceHelper.get_id();

        Log.i(TAG, "onCreate: name = "+ name);
        Log.i(TAG, "onCreate: id = "+ id);

        if(!name.equals(data.getWriter())){
            layout_button.setVisibility(View.GONE);
        }else{
            layout_button.setVisibility(View.VISIBLE);
        }


        tv_title.setText(data.getTitle());
        tv_content.setText(data.getContent());
        tv_writer.setText(data.getWriter());

        arrayList = new ArrayList<Uri>();
        travel_pic_adapter = new Travel_detial_pic_adapter(arrayList,getApplicationContext());

        recyclerView.setAdapter(travel_pic_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        comment_arrayList = new ArrayList<Travel_comment_list_item>();
        travel_comment_adapter = new Travel_comment_recycler_adapter(comment_arrayList,getApplicationContext());
        rv_comment.setAdapter(travel_comment_adapter);
        rv_comment.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ;
        commentLoad();

        String img_url = data.getImg_url();
        Log.d("img_url", img_url);
        if(img_url.length() > 2) {
            try {
                JSONObject jsonObject = new JSONObject(img_url);
                JSONArray jsonArray = jsonObject.getJSONArray("item");
                for (int i = 0 ; i < jsonArray.length(); i++){
                    arrayList.add(Uri.parse(jsonArray.get(i).toString()));
                    travel_pic_adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_title.setVisibility(View.GONE);
                tv_content.setVisibility(View.GONE);

                et_title.setVisibility(View.VISIBLE);
                et_content.setVisibility(View.VISIBLE);

                et_title.setText(tv_title.getText().toString());
                et_content.setText(tv_content.getText().toString());

                layout_button.setVisibility(View.GONE);
                layout_modify.setVisibility(View.VISIBLE);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(TravelDetailActivity.this);
                dlg.setTitle("삭제"); //제목
                dlg.setMessage("삭제 하시겠습니까?"); // 메시지
                dlg.setPositiveButton("삭제",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        travelDelete();
                    }
                });

                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dlg.show();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travelUpdate();

                tv_title.setVisibility(View.VISIBLE);
                tv_content.setVisibility(View.VISIBLE);

                et_title.setVisibility(View.GONE);
                et_content.setVisibility(View.GONE);

                tv_title.setText(et_title.getText().toString());
                tv_content.setText(et_content.getText().toString());

                layout_button.setVisibility(View.VISIBLE);
                layout_modify.setVisibility(View.GONE);
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_button.setVisibility(View.VISIBLE);
                layout_modify.setVisibility(View.GONE);
            }
        });

        btn_comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_comment.getText().toString().equals("")){
                    commentAdd();
                }
            }
        });
    }

    public void travelUpdate(){
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getApplicationContext());
        String login_id = sharedPreferenceHelper.get_id();
        Map<String, Object> travel_data = new HashMap<>();

        travel_data.put("email",    login_id);
        travel_data.put("title",    et_title.getText().toString());
        travel_data.put("content",  et_content.getText().toString());
        travel_data.put("img_url",  data.getImg_url());

        db.collection("travel")
                .document(data.getDocument_id())
                .update(travel_data)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "오류발생",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "수정완료..",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void travelDelete(){
        db.collection("travel")
                .document(data.getDocument_id())
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "오류발생",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "삭제완료..",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),NaviActivity.class);
                        intent.putExtra("REQUEST","SHOW_TRAVEL");
                        startActivity(intent);
                        finish();
                    }
                });
    }

    public void commentLoad(){
        comment_arrayList.clear();
        db.collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    JSONObject jsonObject =  new JSONObject(document.getData().toString());
                                    String get_travel       = jsonObject.getString("travel_id");
                                    String get_id           = jsonObject.getString("id");
                                    String get_name         = jsonObject.getString("name");
                                    String get_comment      = jsonObject.getString("comment");
                                    String get_time         = jsonObject.getString("time");
                                    String get_document_id  = jsonObject.getString("document_id");


                                    if(get_travel.equals(data.getDocument_id())){
                                        Travel_comment_list_item travel_comment_list_item = new Travel_comment_list_item(get_document_id,get_id,get_name,get_time,get_comment);
                                        Log.d("Travel_comment", travel_comment_list_item.toString());
                                        comment_arrayList.add(travel_comment_list_item);
                                        travel_comment_adapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                        }
                    }
                });
    }

    public void commentAdd(){
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);


        Map<String, Object> comment = new HashMap<>();

        comment.put("travel_id", data.getDocument_id());
        comment.put("id", id);
        comment.put("name", name);
        comment.put("comment", et_comment.getText().toString());
        comment.put("time", mFormat.format(mDate));


        db.collection("comment")
                .add(comment)  // 해당 map 객체를 삽입
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {    // 삽입 성공시,
                        String document_id = documentReference.getId();
                        Log.d("comment_add_couemnt_id", document_id);
                        comment.put("document_id", document_id);
                        db.collection("comment")
                                .document(document_id)
                                .update(comment)  // 해당 map 객체를 삽입
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "댓글 작성",Toast.LENGTH_LONG).show();
                                        et_comment.setText("");
                                        commentLoad();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {     // 삽입 실패 시,
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "댓글 작성 실패 \n 관리자에게 문의하세요.",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {     // 삽입 실패 시,
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "댓글 작성 실패 \n 관리자에게 문의하세요.",Toast.LENGTH_LONG).show();
                    }
                });
    }
}