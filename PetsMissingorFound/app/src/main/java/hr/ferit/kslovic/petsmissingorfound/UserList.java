package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserList extends Activity {

    private RecyclerView rvUsersList;
    private UserAdapter mUserAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Users> uList;
    private String activity = "AllList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String intent =getIntent().getStringExtra("intent");
            if(intent.equals("admininterface"))
                loadUser(user.getUid());
            else{this.setUI();}
        }

    }

    private void setUI() {

        uList = new ArrayList<>();
        this.rvUsersList = (RecyclerView) findViewById(R.id.rvUsersList);
        this.mUserAdapter = new UserAdapter(this.loadUsers(), this,activity);
        this.mLayoutManager = new LinearLayoutManager(this);
        this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        this.rvUsersList.addItemDecoration(this.mItemDecoration);
        this.rvUsersList.setLayoutManager(this.mLayoutManager);
        this.rvUsersList.setAdapter(this.mUserAdapter);

    }


    public void loadUser(String uid){

        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                Users user =dataSnapshot.getValue(Users.class);
                activity = user.getLevel();
                if(activity.equals("admin"))
                    setUI();
                else
                    Toast.makeText(getApplicationContext(),"You are not allowded to preform this action!!!",Toast.LENGTH_LONG);

            }
            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value

            }
        });
    }
    public ArrayList<Users>loadUsers(){
        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("users");
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                uList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    uList.add(user);
                    mUserAdapter.notifyDataSetChanged();
            }}
            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value

            }
        });
    return uList;
    }
}
