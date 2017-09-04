package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import hr.ferit.kslovic.petsmissingorfound.Adapters.ChatAdapter;
import hr.ferit.kslovic.petsmissingorfound.Models.Message;
import hr.ferit.kslovic.petsmissingorfound.Models.Notifications;
import hr.ferit.kslovic.petsmissingorfound.R;
import hr.ferit.kslovic.petsmissingorfound.Models.Users;

public class ChatActivity extends MenuActivity {
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Message> mList;
    private RecyclerView rvMessageList;
    private EditText etMessage;
    private FloatingActionButton fbChat;
    private String pUid;
    private String email;
    private  ValueEventListener mListener;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pUid = getIntent().getStringExtra("pUid");
        loadEmail(pUid);
        Log.d("Kristina",pUid);
        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatAdapter.notifyDataSetChanged();
    }

    private void setUI() {
        etMessage = (EditText) findViewById(R.id.etMessage);
        fbChat = (FloatingActionButton) findViewById(R.id.fbChat);
        rvMessageList =(RecyclerView) findViewById(R.id.rvMessageList);
        mList = new ArrayList<>();
            this.mChatAdapter = new ChatAdapter(this.loadMessages(), this);
            this.mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);
        this.mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            this.rvMessageList.addItemDecoration(this.mItemDecoration);

            this.rvMessageList.setLayoutManager(this.mLayoutManager);
            this.rvMessageList.setAdapter(this.mChatAdapter);


    }

    @Override
    public void onStop() {
        super.onStop();
        mRef.removeEventListener(mListener);
    }

    private ArrayList<Message> loadMessages() {
        mList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid;
        if (user != null) {
            // User is signed in
           uid = user.getUid();
            mRef = FirebaseDatabase.getInstance().getReference("messages").child(uid).child(pUid);
            mListener = mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);
                        String key = snapshot.getKey();
                        message.setRead(true);
                        mRef.child(key).setValue(message);
                            mList.add(message);
                            mChatAdapter.notifyDataSetChanged();
                            scrollToBottom();

                    }
                    Collections.reverse(mList);

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
    public void scrollToBottom(){
        rvMessageList.scrollToPosition(0);
    }
    public void loadEmail(String uid){
        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                Users user =dataSnapshot.getValue(Users.class);
                email = user.getEmail();
                fbChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String mText = etMessage.getText().toString();
                        etMessage.setText("");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String currentDateandTime = sdf.format(new Date());
                        String mTime = currentDateandTime;
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String mUname=null;
                        if(firebaseUser!=null&&email!=null){
                            mUname= firebaseUser.getEmail();

                            DatabaseReference cDatabase = FirebaseDatabase.getInstance().getReference("messages").child(firebaseUser.getUid()).child(pUid);
                            DatabaseReference cDatabase2 = FirebaseDatabase.getInstance().getReference("messages").child(pUid).child(firebaseUser.getUid());
                            String mid = cDatabase.push().getKey();
                            Message message = new Message(mUname,mText,mTime,pUid,email,true);
                            Message message2 = new Message(mUname,mText,mTime,firebaseUser.getUid(),firebaseUser.getEmail(),false);
                            cDatabase.child(mid).setValue(message);
                            cDatabase2.child(mid).setValue(message2);
                            Notifications notifications = new Notifications(pUid,firebaseUser.getUid(),"message",false);
                            DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference("notifications");
                            String nid = nDatabase.push().getKey();
                            nDatabase.child(nid).setValue(notifications);
                        }
                    }
                });
                fbChat.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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
                            if(!pUid.equals(notification.getId()))
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
}
