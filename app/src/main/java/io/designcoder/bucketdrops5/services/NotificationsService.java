package io.designcoder.bucketdrops5.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.designcoder.bucketdrops5.MainActivity;
import io.designcoder.bucketdrops5.R;
import io.designcoder.bucketdrops5.beans.Drop;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationsService extends IntentService {
public static final String TAG = "CHRIST";

    public NotificationsService() {
        super("NotificationsService");
        Log.d(TAG,"NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Realm realm = null;
        try{
            Realm.init(getApplicationContext());
            realm = Realm.getDefaultInstance();

            RealmResults<Drop> results = realm.where(Drop.class).equalTo("completed",false).findAll();
            for (Drop current : results){
                if (isNotificationNeeded(current.getAdded(),current.getWhen())){
                     fireNotification(current);
                }
            }
        } finally {
            if (realm != null){
                realm.close();
            }
        }
    }

    private void fireNotification(Drop drop) {
        String message = getString(R.string.notif_message) + "\"" + drop.getWhat() + "\"";
        PugNotification.with(this).load()
                .smallIcon(R.drawable.ic_drop)
                .largeIcon(R.drawable.ic_drop)
                .title(R.string.notif_title)
                .message(message)
                .bigTextStyle(R.string.notif_long_message)
                .flags(Notification.DEFAULT_ALL)
                .autoCancel(true)
                .click(MainActivity.class)
                .simple()
                .build();
    }

    private boolean isNotificationNeeded(long added, long when){
        long now = System.currentTimeMillis();
        if (now > when){
            return  false;
        } else{
            long difference90  = (long)(0.9 * (when - added));
            return (now > (added + difference90))? true : false;
        }
    }

}
