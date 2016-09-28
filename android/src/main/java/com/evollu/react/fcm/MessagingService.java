package com.evollu.react.fcm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Remote message received");

        if (!applicationIsRunning(this)) {
            Bundle b = new Bundle();
            Map data = remoteMessage.getData();
            Set<String> keysIterator = data.keySet();
            for(String key: keysIterator){
                b.putString(key, (String) data.get(key));
            }
            FIRLocalMessagingHelper h = new FIRLocalMessagingHelper(getApplication());
            h.sendNotification(b);

        } else {
            Intent i = new Intent("com.evollu.react.fcm.ReceiveNotification");
            i.putExtra("data", remoteMessage);
            Map<String, String> d = remoteMessage.getData();

            sendOrderedBroadcast(i, null);
        }



        
    }

    // from react-native-system-notification
    private boolean applicationIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.processName.equals(context.getApplicationContext().getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String d: processInfo.pkgList) {
                            Log.v("ReactSystemNotification", "NotificationEventReceiver: ok: " + d);
                            return true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

}
