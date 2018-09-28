package com.learner.backgroundservicefirebasedatachangenotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "Notification";
    public static final String EXTRA_NOTIFICATION_ID = "id";

    private Thread mThread;
    private DatabaseReference mUserRef;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null){
                        String content = "Hello Mr. "+user.getName();
                        showDataChangeNotification(1,content);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mUserRef = FirebaseDatabase.getInstance().getReference(MainActivity.USER_REF);
        mThread = new Thread(runnable);
    }

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread.start();
        return START_STICKY;
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showDataChangeNotification(int notificationId, String content) {
        // Hide the progress notification


        // Make Intent to MainActivity
        Intent intent = new Intent(this, MainActivity.class)
                .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        showNotification(content, intent);
    }

    /**
     * Show notification that the activity finished.
     */
    protected void showNotification(String caption, Intent intent) {
        // Make PendingIntent for notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);
        createDefaultChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(caption)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(notificationId, builder.build());
    }

    private void createDefaultChannel() {
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Default",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }
    }
}
