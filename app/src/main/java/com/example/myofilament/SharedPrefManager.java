package com.example.myofilament;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SharedPrefManager {
    private static final String SHARED_PREFS_FILE_NAME = "Cumulative_data";
    private static SharedPrefManager mInstance;
    private final SharedPreferences mSharedPrefs;
    private final SharedPreferences.Editor mEdit;

    public SharedPrefManager(Context context) {
        mSharedPrefs = context.getSharedPreferences(
                SHARED_PREFS_FILE_NAME,Context.MODE_PRIVATE
        );
        mEdit = mSharedPrefs.edit();
    }

    public static SharedPrefManager getInstance(Context context){
        if(mInstance==null){
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void setStand(int stand){
        mEdit.putInt("Stand",stand);
        mEdit.commit();
    }

    public int getStand(){
        return mSharedPrefs.getInt("Stand",0);
    }

    public void setList(ArrayList<Integer> list){
        JSONArray array = new JSONArray();
        for(int i = 0;i<list.size();i++){
            array.put(list.get(i));
        }
        if(!list.isEmpty()){
            mEdit.putString("List",array.toString());
        }
        else{
            mEdit.putString("List",null);
        }
        mEdit.commit();
    }

    public ArrayList<Integer> getList(){
        String List = mSharedPrefs.getString("List",null);
        ArrayList<Integer> Data = new ArrayList<>();
        if(List!=null){
            try {
                JSONArray array = new JSONArray(List);
                for(int i=0; i<array.length(); i++){
                    Data.add(array.optInt(i));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return Data;
    }
}
