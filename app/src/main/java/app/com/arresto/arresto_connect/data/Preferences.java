/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.constants.PrefernceConstants;

public class Preferences {
    private static Preferences MySingleton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    public static Preferences getInstance(Context context) {
        if (MySingleton == null) {
            MySingleton = new Preferences(context);
        }
        return MySingleton;
    }

    private Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PrefernceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }


    public String getData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

 public void saveBoolean(String key, boolean value) {
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }


    public boolean getBoolean(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, false);
        }
        return false;
    }

    public void clearData() {
        sharedPreferences.edit().clear().apply();

    }

    public void remove_key(String key) {
        sharedPreferences.edit().remove(key).apply();

    }

    public void saveArray_Data(String key, List<String> lists) {
        Gson gson = new Gson();
        String json = gson.toJson(lists);
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    public List<String> getArray_Data(String key) {
        if (sharedPreferences != null) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(key, "");
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> items = gson.fromJson(json, type);
            if (items == null) {
                items = new ArrayList<>();
            }
            return items;
        }

        return null;
    }

}