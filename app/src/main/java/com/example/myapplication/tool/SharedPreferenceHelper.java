package com.example.myapplication.tool;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    Context context;

    public SharedPreferenceHelper(Context context){
        this.context = context;
    }

    public void set_id(String email){
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", email);
        editor.commit();
    }

    public void set_name(String name){
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", name);
        editor.commit();
    }

    public String get_id(){
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString("id", "");
    }

    public String get_name(){
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString("name", "");
    }

}
