package com.tao.Utile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedUtlis {

    Context context;
    String name;
    private SharedPreferences mPreferences;
    private Editor mEdit;


    public SharedUtlis(Context context, String name) {
        this.context = context;
        this.name = name;
        mPreferences = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        mEdit = mPreferences.edit();
    }

    public void putString(String key, String value) {
        mEdit.putString(key, value);
        mEdit.commit();
    }
    public void putBoolean(String key, boolean value) {
        mEdit.putBoolean(key, value);
        mEdit.commit();
    }

    public boolean getBoolean(String key, boolean def) {
        return mPreferences.getBoolean(key, def);
    }

    public String getString(String key, String def) {
        return mPreferences.getString(key, def);
    }
    
   public int getInt(String key,int defo) {
        return mPreferences.getInt(key,defo);
    }


    public void putInt(String key,int value) {
        mEdit.putInt(key, value);
        mEdit.commit();
    }

    public long getLong(String key,long defo) {
        return mPreferences.getLong(key,defo);
    }

    public void putLong(String key,long value) {
        mEdit.putLong(key, value);
        mEdit.commit();

    }

    public static void putString(Context context , String name,String key, String value) {
        new SharedUtlis(context,name).putString(key, value);

    }
    public static  void putBoolean(Context context , String name,String key, boolean value) {
        new SharedUtlis(context,name).putBoolean(key, value);

    }

    public  static boolean getBoolean(Context context , String name,String key, boolean def) {
        return    new SharedUtlis(context,name).getBoolean(key, def);
    }

    public  static String getString(Context context , String name,String key, String def) {
        return    new SharedUtlis(context,name).getString(key, def);
    }

   public static  int getInt(Context context , String name,String key,int defo) {
        return    new SharedUtlis(context,name).getInt(key,defo);
    }


    public  static void putInt(Context context , String name,String key,int value) {
        new SharedUtlis(context,name).putInt(key, value);

    }

    public  static long getLong(Context context , String name,String key,long defo) {
        return    new SharedUtlis(context,name).getLong(key,defo);
    }

    public  static void putLong(Context context , String name,String key,long value) {
        new SharedUtlis(context,name).putLong(key, value);


    }
}
