package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hr.ferit.kslovic.petsmissingorfound.Models.Notifications;
import hr.ferit.kslovic.petsmissingorfound.R;

public class MenuActivity extends AppCompatActivity {
    DatabaseReference nRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ValueEventListener nListener;
    private int notificationId = 1;
    private String oldPuid = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {

                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                }
                // ...
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        loadNotifications();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if(nListener!=null){
            nRef.removeEventListener(nListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.iSignOut:
                FirebaseAuth.getInstance().signOut();
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
    public void loadNotifications() {


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)

        {
            nRef =FirebaseDatabase.getInstance().getReference("notifications");
            nListener = nRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Notifications notification = snapshot.getValue(Notifications.class);

                        if (notification.getUid().equals(user.getUid()) && !notification.getRead()) {
                            sendNotification(notification.getType(), notification.getId());
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


    public void sendNotification(String type, String id) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if(type.equals("location")) {
            Intent intentNotification = new Intent(getApplicationContext(), PetDetails.class);
            intentNotification.putExtra("pid",id);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setAutoCancel(true)
                    .setContentTitle("New Location")
                    .setContentText("Pet seen in new location")
                    .setContentIntent(notificationPendingIntent)
                    .setSmallIcon(R.drawable.addlocation)
                    .setLights(Color.BLUE, 2000, 1000)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = notificationBuilder.build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
        else{
            if(oldPuid!=null&&!oldPuid.equals(id))
                notificationId ++;
            oldPuid = id;
            Intent intentNotification = new Intent(getApplicationContext(), ChatActivity.class);
            intentNotification.putExtra("pUid",id);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setAutoCancel(true)
                    .setContentTitle("New message")
                    .setContentText("You have new message")
                    .setSmallIcon(R.drawable.message)
                    .setContentIntent(notificationPendingIntent)
                    .setLights(Color.GREEN, 2000, 1000)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = notificationBuilder.build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);
        }

    }
}
