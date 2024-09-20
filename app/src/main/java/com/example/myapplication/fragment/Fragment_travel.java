package com.example.myapplication.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.NaviActivity;
import com.example.myapplication.activity.TravelDetailActivity;
import com.example.myapplication.activity.TravelWriteActivity;
import com.example.myapplication.adapter.Travel_list_adapter;
import com.example.myapplication.adapter.Travel_pic_adapter;
import com.example.myapplication.model.Travel_list_item;
import com.example.myapplication.tool.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Fragment_travel extends Fragment {
    //    private String TAG = "";
    private static final String TAG = Fragment_travel.class.getName() + " 로그";

    ListView listView;
    FloatingActionButton fab_add;

    Travel_list_adapter travel_list_adapter;
    ArrayList<Travel_list_item> travel_items;
    Spinner sp_local;
    List<Travel_list_item> filterList;

    Context context;
    // 파이어베이스 스토어 객체 생성
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_travel, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();

        listView = (ListView) view.findViewById(R.id.lv_travel_list);
        fab_add = (FloatingActionButton) view.findViewById(R.id.fab_travel_add);
        sp_local = (Spinner) view.findViewById(R.id.sp_local_travel);


        travel_items = new ArrayList<Travel_list_item>();
        travel_list_adapter = new Travel_list_adapter(travel_items, getContext());
        listView.setAdapter(travel_list_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, TravelDetailActivity.class);
                intent.putExtra("data", filterList.get(i));
                startActivity(intent);
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TravelWriteActivity.class);
                startActivity(intent);
                Activity activity = (Activity) context;
                activity.finish();
            }
        });

        dataLoad();
        initSpinner();
    }

    public void dataLoad() {
        db.collection("travel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Log.d("GET_DATA", document.getData().toString());
                                    JSONObject jsonObject = new JSONObject(document.getData().toString());
                                    String title = jsonObject.getString("title");
                                    String content = jsonObject.getString("content");
                                    String id = jsonObject.getString("email");
                                    String img_url = jsonObject.getString("img_url");
                                    String local = jsonObject.getString("local");
                                    Log.i(TAG, "onComplete: Test local = "+local);
                                    String get_document = document.getId();

                                    travel_items = new ArrayList<>();
                                    db.collection("users")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            try {
                                                                JSONObject jsonObject = new JSONObject(document.getData().toString());
                                                                String get_id = jsonObject.getString("Id");
                                                                String get_name = jsonObject.getString("Nickname");
                                                                if (get_id.equals(id)) {
                                                                    travel_list_adapter.addItem(img_url, get_document, title, content, get_name, local);
                                                                    Travel_list_item tData = new Travel_list_item();
                                                                    tData.setLocal(local);
                                                                    tData.setContent(content);
                                                                    tData.setTitle(title);
                                                                    tData.setDocument_id(get_document);
                                                                    tData.setWriter(get_id);
                                                                    tData.setImg_url(img_url);

                                                                    travel_items.add(tData);
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    } else {
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                    }
                                                }
                                            });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void initSpinner() {
        String[] items = getResources().getStringArray(R.array.local_list);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
        sp_local.setAdapter(spinnerAdapter);

        sp_local.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String local = items[position];

                filterList = new ArrayList<>();
                filterList = travel_items.stream().filter(travel_items -> travel_items.getLocal().equals(local)).collect(Collectors.toList());
                travel_list_adapter.filterItem((ArrayList<Travel_list_item>) filterList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}