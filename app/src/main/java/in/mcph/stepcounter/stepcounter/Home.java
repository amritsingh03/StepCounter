package in.mcph.stepcounter.stepcounter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class Home extends AppCompatActivity {

    Button bt_next;
    Home thisclass;
    private static final String channelId ="step_counter_notification_channel";
    private final static long NOTIFICATION_TIME_HOUR = 3; // (24-H)
    private final static int NOTIFICATION_ID = 1001541;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        thisclass = Home.this;
        bt_next = findViewById(R.id.btnext);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });

        //check intent
        Intent i = getIntent();
        boolean f = i.getIntExtra("option",0) == 1;

        if(f){
            // launch next screen
            proceed();
        }

        else {

            // send a notification if time > set time and app opened not by notification intent

            Calendar cal = Calendar.getInstance();

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour >= NOTIFICATION_TIME_HOUR) {

                createNotificationChannel();
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("option", 1); // 1 - action from notification
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                // creating notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.notfico)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bigico))
                        .setContentTitle("Daily goal")
                        .setContentText("Tap to view your progress towards today's target")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Tap to view your progress towards today's target. " +
                                "This will take you to the application."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                // sending notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            }
        }

    }

    private void proceed() {
        // go to next page
//                Toast.makeText(Home.this, "NEXT pressed", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("usrinfo",MODE_PRIVATE);
        if(sp.contains("isDataEnterred")){
            // user has already enterred data
            // go to dashboard

            Intent i = new Intent(thisclass,DashBoard.class);
            thisclass.startActivity(i);
            thisclass.finish();

        }
        else{
            // goto data entry
            Intent i = new Intent(thisclass,SignUp.class);
            thisclass.startActivity(i);
            thisclass.finish();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "This channel is used to send notifications to the user.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelId, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
