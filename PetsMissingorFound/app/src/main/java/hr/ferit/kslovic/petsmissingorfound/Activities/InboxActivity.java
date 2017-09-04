package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Adapters.InboxAdapter;
import hr.ferit.kslovic.petsmissingorfound.Models.Message;
import hr.ferit.kslovic.petsmissingorfound.R;

public class InboxActivity extends MenuActivity {

    private RecyclerView rvInbox;
    private InboxAdapter mInboxAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Message> mList;
    private long fetched;
    private  ValueEventListener mListener;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRef.removeEventListener(mListener);
    }

    private void setUI() {
        rvInbox = (RecyclerView) findViewById(R.id.rvInbox);
        mList = new ArrayList<>();
        this.mInboxAdapter = new InboxAdapter(this.loadMessages(), this);
        this.mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        this.rvInbox.addItemDecoration(this.mItemDecoration);

        this.rvInbox.setLayoutManager(this.mLayoutManager);
        this.rvInbox.setAdapter(this.mInboxAdapter);
    }

    private ArrayList<Message> loadMessages() {
        mList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid;
        if (user != null) {
            // User is signed in
            uid = user.getUid();

            mRef = FirebaseDatabase.getInstance().getReference("messages").child(uid);
            mListener = mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                        String key =snapshot.getKey();
                        int i=0;
                        fetched=dataSnapshot.child(key).getChildrenCount();
                        for (DataSnapshot mSnapshot: dataSnapshot.child(key).getChildren()) {
                            if(i==fetched-1) {
                                Message conversation = mSnapshot.getValue(Message.class);
                                mList.add(conversation);
                                Log.d("Kristina", conversation.getUname());
                                mInboxAdapter.notifyDataSetChanged();
                            }
                                i++;
                        }



                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });

            Log.d("Kristina", "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            Log.d("Kristina", "onAuthStateChanged:signed_out");
        }
        return mList;
    }

}
