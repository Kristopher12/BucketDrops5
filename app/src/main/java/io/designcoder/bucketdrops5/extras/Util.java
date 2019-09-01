package io.designcoder.bucketdrops5.extras;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import io.designcoder.bucketdrops5.services.NotificationsService;

public class Util {
    public static void showViews(List<View> views){
        for(View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }
    public static void hideViews(List<View> views){
        for(View view : views){
            view.setVisibility(View.GONE);
        }
    }

    public static  void scheduleAlarm(Context context){
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent =  new Intent(context, NotificationsService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        aManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,1000,240000 ,pendingIntent);
    }
}
