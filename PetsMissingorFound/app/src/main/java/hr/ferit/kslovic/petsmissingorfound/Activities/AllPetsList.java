package hr.ferit.kslovic.petsmissingorfound.Activities;

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

import hr.ferit.kslovic.petsmissingorfound.Models.Pet;
import hr.ferit.kslovic.petsmissingorfound.Adapters.PetAdapter;
import hr.ferit.kslovic.petsmissingorfound.R;
import hr.ferit.kslovic.petsmissingorfound.Models.Users;

public class AllPetsList extends MenuActivity implements AdapterView.OnItemSelectedListener {

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
    private ValueEventListener qListener;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.petlist_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
          this.setUI();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(qListener!=null)
            query.removeEventListener(qListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPetAdapter!=null){
        this.mPetAdapter = new PetAdapter(this.loadAllPets(), this,activity);
        this.rvPetList.setAdapter(this.mPetAdapter);
        }
    }


    private void setUI() {
        pList = new ArrayList<>();
        this.rvPetList = (RecyclerView) findViewById(R.id.rvAddsList);
        this.mLayoutManager = new LinearLayoutManager(this);
        this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        this.rvPetList.addItemDecoration(this.mItemDecoration);
        this.rvPetList.setLayoutManager(this.mLayoutManager);

        sSearch = (Spinner) findViewById(R.id.sSearch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sSearch.setAdapter(adapter);
        sSearch.setOnItemSelectedListener(this);


    }

    private ArrayList<Pet> loadAllPets() {
        if (user != null) {
            // User is signed in
            DatabaseReference addRef = FirebaseDatabase.getInstance().getReference("pets");
            query = addRef.orderByChild("pubTime");
           qListener = query.addValueEventListener(new ValueEventListener() {
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

                                rvPetList.setAdapter(mPetAdapter);

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
        if(qListener!=null)
            query.removeEventListener(qListener);
        statusSpinner = parent.getItemAtPosition(position).toString();
        Log.d("Kristina",statusSpinner);
            this.mPetAdapter = new PetAdapter(this.loadAllPets(), this,activity);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
