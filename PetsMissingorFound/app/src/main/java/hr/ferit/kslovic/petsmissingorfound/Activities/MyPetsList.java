package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

public class MyPetsList extends MenuActivity {

    private RecyclerView rvPetList;
    private PetAdapter mPetAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Pet> pList;
    private ValueEventListener mListener;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myadds_layout);
        this.setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mPetAdapter = new PetAdapter(this.loadPets(), this,"mylist");
        this.rvPetList.setAdapter(this.mPetAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            query.removeEventListener(mListener);
        }
    }

    private void setUI() {
        pList = new ArrayList<>();
        this.rvPetList = (RecyclerView) findViewById(R.id.rvPetList);
            this.mLayoutManager = new LinearLayoutManager(this);
            this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            this.rvPetList.addItemDecoration(this.mItemDecoration);
            this.rvPetList.setLayoutManager(this.mLayoutManager);



    }

    private ArrayList<Pet> loadPets() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            DatabaseReference addRef = FirebaseDatabase.getInstance().getReference("pets");
            query = addRef.orderByChild("pubTime");
            mListener =query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    pList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Pet pet = snapshot.getValue(Pet.class);

                        String pUid = pet.getUid();
                        String uid = user.getUid();
                        if(pUid.equals(uid)) {

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


}
