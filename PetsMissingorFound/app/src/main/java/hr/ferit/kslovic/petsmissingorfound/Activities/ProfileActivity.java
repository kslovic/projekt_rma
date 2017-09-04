package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hr.ferit.kslovic.petsmissingorfound.R;
import hr.ferit.kslovic.petsmissingorfound.Models.Users;

public class ProfileActivity extends MenuActivity {

    private Button bContact;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvEmail;
    private TextView tvUserName;
    private TextView tvPhoneNum;
    private String uid;
    private  FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        uid =getIntent().getStringExtra("uid");
        if(uid!=null) {
            setUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        loadUser();
    }

    private void loadUser() {
        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                    Users user =dataSnapshot.getValue(Users.class);
                if(user!=null) {
                    tvFirstName.setText(user.getFname());
                    tvLastName.setText(user.getLname());
                    tvUserName.setText(user.getUname());
                    tvEmail.setText(user.getEmail());
                    tvPhoneNum.setText(user.getPhone());
                }



            }
            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value

            }
            });
    }

    private void setUI() {
        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvLastName = (TextView) findViewById(R.id.tvLastName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);
        bContact = (Button) findViewById(R.id.bContactEdit);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            if (uid.equals(fUser.getUid())){
                bContact.setText("Edit");
                bContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (uid != null) {
                            Log.d("Kristina", uid);
                            Intent editIntent = new Intent(getApplicationContext(), Register.class);
                            editIntent.putExtra("uid", uid);
                            startActivity(editIntent);
                        }
                    }
                });
            }
            else {
                bContact.setText("Contact");
                bContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (uid != null) {
                            Log.d("Kristina", uid);
                            Intent contactIntent = new Intent(getApplicationContext(), ChatActivity.class);
                            contactIntent.putExtra("pUid", uid);
                            startActivity(contactIntent);
                        }
                    }
                });
            }
        }
    }
}
