package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPetsList extends Activity {

    private RecyclerView rvPetList;
    private PetAdapter mPetAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Pet> pList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myadds_layout);
        this.setUI();
    }

    private void setUI() {
        pList = new ArrayList<>();
        this.rvPetList = (RecyclerView) findViewById(R.id.rvPetList);

            this.mPetAdapter = new PetAdapter(this.loadPets(), this);
            this.mLayoutManager = new LinearLayoutManager(this);
            this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            this.rvPetList.addItemDecoration(this.mItemDecoration);
            this.rvPetList.setLayoutManager(this.mLayoutManager);
            this.rvPetList.setAdapter(this.mPetAdapter);



    }

    private ArrayList<Pet> loadPets() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            DatabaseReference addRef = FirebaseDatabase.getInstance().getReference("pets");
            Query query = addRef.orderByChild("pubTime");
            query.addValueEventListener(new ValueEventListener() {
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
            /*if(pid!=null) {
                Log.d("Kristina", "Uslo1" );
                DatabaseReference picRef = FirebaseDatabase.getInstance().getReference("pictures").child(pid);
                picRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dSnapshot) {
                        Log.d("Kristina", "Uslo2" );
                        for (DataSnapshot picSnapshot: dSnapshot.getChildren()) {

                            UploadPicture upPic = picSnapshot.getValue(UploadPicture.class);
                            if (upPic != null) {
                                Log.d("kristina", "uslo");
                                pPic = upPic.getUrl();

                            }
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value

                    }
                });
            }*/
            Log.d("Kristina", "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d("Kristina", "onAuthStateChanged:signed_out");
        }

        return pList;
    }

}
