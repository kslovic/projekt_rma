package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Activities.MenuActivity;
import hr.ferit.kslovic.petsmissingorfound.Adapters.UserAdapter;
import hr.ferit.kslovic.petsmissingorfound.Models.Users;
import hr.ferit.kslovic.petsmissingorfound.R;

public class UserList extends MenuActivity {

    private RecyclerView rvUsersList;
    private UserAdapter mUserAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Users> uList;
    private String activity = "AllList";
    private EditText etSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String intent =getIntent().getStringExtra("intent");
            if(intent.equals("admininterface"))
                loadUser(user.getUid());
        }

    }


    private void setUI() {

        uList = new ArrayList<>();
        this.rvUsersList = (RecyclerView) findViewById(R.id.rvUsersList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        rvUsersList.addItemDecoration(mItemDecoration);
        rvUsersList.setLayoutManager(mLayoutManager);

        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(watcher);


    }
    private final TextWatcher watcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        public void afterTextChanged(Editable s) {
            if (s.length() != 0) {
                String searchTerm = etSearch.getText().toString();
                mUserAdapter = new UserAdapter(loadUsers(searchTerm), getApplicationContext(),activity);
                rvUsersList.setAdapter(mUserAdapter);
            }
        }
    };

    public void loadUser(String uid){

        DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        uRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
    public ArrayList<Users>loadUsers(final String search){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
       userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                uList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if(user.getEmail().matches(".*?"+search+".*?")) {
                        uList.add(user);
                        mUserAdapter.notifyDataSetChanged();
                    }
            }}
            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value

            }
        });
    return uList;
    }
}
