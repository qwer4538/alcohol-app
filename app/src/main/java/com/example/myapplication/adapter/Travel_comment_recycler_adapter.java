package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.NaviActivity;
import com.example.myapplication.model.Travel_comment_list_item;
import com.example.myapplication.tool.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Travel_comment_recycler_adapter extends RecyclerView.Adapter<Travel_comment_recycler_adapter.ViewHolder>{
    ArrayList<Travel_comment_list_item> arrayList;
    Context mContext;

    // 파이어베이스 스토어 객체 생성
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public Travel_comment_recycler_adapter(ArrayList<Travel_comment_list_item> arrayList, Context context){
        this.arrayList =arrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.travel_comment_item, parent, false) ;
        Travel_comment_recycler_adapter.ViewHolder vh = new Travel_comment_recycler_adapter.ViewHolder(view) ;

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Travel_comment_list_item travel_comment_list_item = arrayList.get(position);

        holder.tv_writer.setText(travel_comment_list_item.getName());
        holder.tv_time.setText(travel_comment_list_item.getTime());
        holder.tv_comment.setText(travel_comment_list_item.getComment());

        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(mContext);
        String login_id = sharedPreferenceHelper.get_id();

        if(!travel_comment_list_item.getId().equals(login_id)){
            holder.layout_btn.setVisibility(View.GONE);
        }else {
            holder.layout_btn.setVisibility(View.VISIBLE);
        }

        holder.btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.layout_btn.setVisibility(View.GONE);
                holder.layout_edit.setVisibility(View.VISIBLE);

                holder.tv_comment.setVisibility(View.GONE);
                holder.et_comment.setVisibility(View.VISIBLE);


                holder.et_comment.setText(holder.tv_comment.getText().toString());
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("comment")
                        .document(travel_comment_list_item.getDocument_id())
                        .delete()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "오류발생",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                arrayList.remove(position);
                                notifyDataSetChanged();
                            }
                        });
            }
        });

        holder.btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                long mNow = System.currentTimeMillis();
                Date mDate = new Date(mNow);


                Map<String, Object> comment = new HashMap<>();

                comment.put("document_id", travel_comment_list_item.getDocument_id());
                comment.put("id", travel_comment_list_item.getId());
                comment.put("name", travel_comment_list_item.getName());
                comment.put("comment", holder.et_comment.getText().toString());
                comment.put("time", mFormat.format(mDate));


                db.collection("comment")
                        .document(travel_comment_list_item.getDocument_id())
                        .update(comment)  // 해당 map 객체를 삽입
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                travel_comment_list_item.setComment(holder.et_comment.getText().toString());
                                holder.layout_edit.setVisibility(View.GONE);
                                holder.layout_btn.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        holder.btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.layout_btn.setVisibility(View.VISIBLE);
                holder.layout_edit.setVisibility(View.GONE);

                holder.tv_comment.setVisibility(View.VISIBLE);
                holder.et_comment.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_writer;
        TextView tv_time;
        TextView tv_comment;
        EditText et_comment;

        LinearLayout layout_btn;
        LinearLayout layout_edit;

        Button btn_submit;
        Button btn_cancle;
        Button btn_delete;
        Button btn_modify;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_writer       = (TextView)    itemView.findViewById(R.id.tv_travel_comment_item_writer);
            tv_time         = (TextView)    itemView.findViewById(R.id.tv_travel_comment_item_time);
            tv_comment      = (TextView)    itemView.findViewById(R.id.tv_travel_comment_item_content);

            et_comment      = (EditText)    itemView.findViewById(R.id.et_travel_comment_item_edit_content);

            btn_modify      = (Button)      itemView.findViewById(R.id.btn_travel_comment_item_edit);
            btn_delete      = (Button)      itemView.findViewById(R.id.btn_travel_comment_item_delete);
            btn_submit      = (Button)      itemView.findViewById(R.id.btn_travel_comment_item_btn_edit_submit);
            btn_cancle      = (Button)      itemView.findViewById(R.id.btn_travel_comment_item_edit_cancle);

            layout_btn      = (LinearLayout) itemView.findViewById(R.id.layout_travel_comment_item_button);
            layout_edit     = (LinearLayout) itemView.findViewById(R.id.layout_travel_comment_item_edit);
        }
    }
}
