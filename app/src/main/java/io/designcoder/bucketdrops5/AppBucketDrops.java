package io.designcoder.bucketdrops5;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import io.designcoder.bucketdrops5.adapters.Filter;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppBucketDrops extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //get the configuration
        Realm.init(getApplicationContext());
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();

        //set the real configuration
        Realm.setDefaultConfiguration(configuration);
        //get an instance of realm
        Realm realm = Realm.getDefaultInstance();
    }

    public static void save(Context context, int filterOption) {
       SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("filter", filterOption);
        editor.apply();
    }

    public static int load(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int filterOption = pref.getInt("filter", Filter.NONE);
        return filterOption;
    }
}
