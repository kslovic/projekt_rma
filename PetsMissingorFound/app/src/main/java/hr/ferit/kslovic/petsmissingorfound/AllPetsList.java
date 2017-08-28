package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Collections;
import java.util.Objects;

public class AllPetsList extends Activity implements AdapterView.OnItemSelectedListener {

    private RecyclerView rvPetList;
    private PetAdapter mPetAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private String pPic;
    private ArrayList<Pet> pList;
    private Spinner sSearch;
    private String statusSpinner;
    private String activity = "AllList";
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.petlist_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String intent =getIntent().getStringExtra("intent");
            if(intent!=null&&intent.equals("admininterface"))
            loadUser(user.getUid());
            else{this.setUI();}
        }

    }

    private void setUI() {
        sSearch = (Spinner) findViewById(R.id.sSearch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sSearch.setAdapter(adapter);
        sSearch.setOnItemSelectedListener(this);
        pList = new ArrayList<>();
        this.rvPetList = (RecyclerView) findViewById(R.id.rvAddsList);


    }

    private ArrayList<Pet> loadAllPets() {
        if (user != null) {
            // User is signed in
            String uid = user.getUid();
            DatabaseReference addRef = FirebaseDatabase.getInstance().getReference("pets");
            Query query = addRef.orderByChild("pubTime");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pList.clear();
                            for (DataSnapshot PetSnapshot :dataSnapshot.getChildren()) {
                                Pet pet = PetSnapshot.getValue(Pet.class);
                                String pStatus = pet.getsStatus();
                                if(pStatus.equals(statusSpinner)) {
                                    Log.d("Kristina", pet.toString());
                                    pList.add(pet);
                                    mPetAdapter.notifyDataSetChanged();

                                }



                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });
        }
            return pList;

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        statusSpinner = parent.getItemAtPosition(position).toString();
        this.mPetAdapter = new PetAdapter(this.loadAllPets(), this,activity);
        this.mLayoutManager = new LinearLayoutManager(this);
        this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        this.rvPetList.addItemDecoration(this.mItemDecoration);
        this.rvPetList.setLayoutManager(this.mLayoutManager);
        this.rvPetList.setAdapter(this.mPetAdapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
}
