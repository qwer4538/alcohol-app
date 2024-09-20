package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Travel_pic_adapter extends RecyclerView.Adapter<Travel_pic_adapter.ViewHolder> {

    ArrayList<Uri> arrayList;
    Context mContext;
    View.OnClickListener addEvent;
    boolean add_flag = true;
    public Travel_pic_adapter(ArrayList<Uri> arrayList, Context context, View.OnClickListener add_event){
        this.arrayList  = arrayList;
        this.mContext   = context;
        this.addEvent   = add_event;
    }

    public Travel_pic_adapter(ArrayList<Uri> arrayList, Context context){
        this.arrayList  = arrayList;
        this.mContext   = context;
        add_flag = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.travel_write_pic_item, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        Travel_pic_adapter.ViewHolder vh = new Travel_pic_adapter.ViewHolder(view) ;

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {



        if(position == getItemCount() -1){
            if(add_flag){
                holder.iv_add.setVisibility(View.VISIBLE);
            }
            holder.iv_pic.setVisibility(View.GONE);
            holder.fab_del.setVisibility(View.GONE);
            holder.iv_add.setOnClickListener(addEvent);
        }else {
            Uri image_uri = arrayList.get(position) ;
            Glide.with(mContext)
                    .load(image_uri)
                    .into(holder.iv_pic);

            holder.fab_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    arrayList.remove(position);
                    notifyDataSetChanged();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size()+1;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        ImageView iv_add;
        FloatingActionButton fab_del;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_travel_write_pic);
            iv_add = (ImageView) itemView.findViewById(R.id.iv_travel_writer_add_pic);
            fab_del= (FloatingActionButton) itemView.findViewById(R.id.fab_travel_write_pic_del);
        }
    }

}
