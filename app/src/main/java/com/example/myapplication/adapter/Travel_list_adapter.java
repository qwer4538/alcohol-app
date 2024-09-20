package com.example.myapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Travel_list_item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Travel_list_adapter extends BaseAdapter {
    private static final String TAG = Travel_list_adapter.class.getName() +" 로그";
    ArrayList<Travel_list_item> arrayList;
    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public Travel_list_adapter(ArrayList<Travel_list_item> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Travel_list_item getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();
        Travel_list_item travel_list_item = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.travel_list_item, viewGroup, false);
        }

        final TextView tv_local = (TextView) view.findViewById(R.id.tv_local_item);
        final TextView tv_title = (TextView) view.findViewById(R.id.tv_travel_item_title);

        tv_local.setText("["+travel_list_item.getLocal()+"]");
        tv_title.setText(travel_list_item.getTitle());


        if (!arrayList.get(i).getImg_url().equals("null")) {
            String img_url = arrayList.get(i).getImg_url();
            Log.d("img_url", img_url);
            if (img_url.length() > 2) {
                String thumbnail_url = "";
                try {
                    JSONObject jsonObject = new JSONObject(img_url);
                    JSONArray jsonArray = jsonObject.getJSONArray("item");
                    thumbnail_url = jsonArray.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StorageReference thumbnailRef = storageRef.child(thumbnail_url);
                if (thumbnailRef == null) {
                    Log.d("Travel_list_adapter", "이미지가 경로에 없음");
                } else {
                    thumbnailRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

        }
        return view;
    }

        public void addItem(String img_url,String document_id, String title,String content, String writer,String local){

        Travel_list_item travel_list_item = new Travel_list_item();
        travel_list_item.setDocument_id(document_id);
        travel_list_item.setImg_url(img_url);
        travel_list_item.setTitle(title);
        travel_list_item.setContent(content);
        travel_list_item.setWriter(writer);
        travel_list_item.setLocal(local);

        arrayList.add(travel_list_item);
        notifyDataSetChanged();
    }

    public void filterItem(ArrayList<Travel_list_item> list) {
        arrayList = list;
        notifyDataSetChanged();
    }
}
