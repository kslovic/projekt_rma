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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPetsList extends Activity {

    private RecyclerView rvPetList;
    private PetAdapter mPetAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private String pPic;
    private ArrayList<PetAdd> picList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myadds_layout);
        this.setUI();
    }

    private void setUI() {
        picList = new ArrayList<>();
        this.rvPetList = (RecyclerView) findViewById(R.id.rvPetList);

            this.mPetAdapter = new PetAdapter(this.loadPets(), this);
            this.mLayoutManager = new LinearLayoutManager(this);
            this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            this.rvPetList.addItemDecoration(this.mItemDecoration);
            this.rvPetList.setLayoutManager(this.mLayoutManager);
            this.rvPetList.setAdapter(this.mPetAdapter);



    }

    private ArrayList<PetAdd> loadPets() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String uid = user.getUid();
            DatabaseReference addRef = FirebaseDatabase.getInstance().getReference("pets").child(uid);
            addRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    picList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Pet pet = snapshot.getValue(Pet.class);
                        String pName = pet.getEtPname();
                        String pBreed = pet.getEtPbreed();
                        String pStatus = pet.getsStatus();
                        String pid = pet.getPid();
                        pPic = null;
                        for (DataSnapshot picSnapshot: snapshot.child("pictures").getChildren()) {
                            UploadPicture upPic = picSnapshot.getValue(UploadPicture.class);
                            if(upPic!=null)
                                pPic = upPic.getUrl();

                        }

                    PetAdd petAdd = new PetAdd(pid,pName, pBreed, pStatus, pPic);
                        Log.d("Kristina", pName + pBreed + pStatus +pPic );
                        picList.add(petAdd);
                        mPetAdapter.notifyDataSetChanged();

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

        return picList;
    }

}
