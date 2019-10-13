package de.kai_morich.simple_bluetooth_terminal;

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

    public void setDate(ArrayList<String> date){
        JSONArray array = new JSONArray();
        for(int i = 0;i<date.size();i++){
            array.put(date.get(i));
        }
        if(date.isEmpty()){
            mEdit.putString("Date",array.toString());
        }
        else{
            mEdit.putString("Date",null);
        }
        mEdit.commit();
    }

    public ArrayList<String> getDate(){
        String List = mSharedPrefs.getString("Date",null);
        ArrayList<String> Data = new ArrayList<>();
        if(List!=null){
            try {
                JSONArray array = new JSONArray(List);
                for(int i=0; i<array.length(); i++){
                    Data.add(array.optString(i));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return Data;
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
