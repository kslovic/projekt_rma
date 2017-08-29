package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Models.Notifications;

public class Welcome extends Activity  {

    private ImageButton ibRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        loadNotifications();
        ibRefresh =(ImageButton) findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNotifications();
            }
        });


    }

    private void loadNotifications() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            final DatabaseReference nRef = FirebaseDatabase.getInstance().getReference("notifications");
            nRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key =snapshot.getKey();
                        Notifications notification = snapshot.getValue(Notifications.class);

                        if (notification.getUid().equals(user.getUid())&&!notification.getRead()) {
                                sendNotification(notification.getType(),notification.getId());
                                nRef.child(key).child("read").setValue(true);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.iSignOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                return true;
            case R.id.iReport:
                Intent intentReport = new Intent(getApplicationContext(), ReportPet.class);
                startActivity(intentReport);
                return true;
            case R.id.iMyAdds:
                Intent intentMyAdds = new Intent(getApplicationContext(), MyPetsList.class);
                startActivity(intentMyAdds);
                return true;
            case R.id.iSearchP:
                Intent intentAllAdds = new Intent(getApplicationContext(), AllPetsList.class);
                startActivity(intentAllAdds);
                return true;
            case R.id.iShow:
                Intent intentLocations = new Intent(getApplicationContext(), PetMap.class);
                startActivity(intentLocations);
                return true;
            case R.id.iInbox:
                Intent intentInbox = new Intent(getApplicationContext(), InboxActivity.class);
                startActivity(intentInbox);
                return true;
            case R.id.iProfile:
                FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {
                    Intent intentProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                    intentProfile.putExtra("uid", user.getUid());
                    startActivity(intentProfile);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void sendNotification(String type, String id) {


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if(type.equals("location")) {
            Intent intentNotification = new Intent(getApplicationContext(), PetDetails.class);
            intentNotification.putExtra("pid",id);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setAutoCancel(true)
                    .setContentTitle("New Location")
                    .setContentText("Pet seen in new location")
                    .setContentIntent(notificationPendingIntent)
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setLights(Color.BLUE, 2000, 1000)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        else{
            Intent intentNotification = new Intent(getApplicationContext(), ChatActivity.class);
            intentNotification.putExtra("pUid",id);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setAutoCancel(true)
                    .setContentTitle("New message")
                    .setContentText("You have new message")
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setContentIntent(notificationPendingIntent)
                    .setLights(Color.BLUE, 2000, 1000)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        this.finish();
    }
}
