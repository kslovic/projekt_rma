package hr.ferit.kslovic.petsmissingorfound;

import android.provider.Settings;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import hr.ferit.kslovic.petsmissingorfound.Models.Notifications;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<Message> mList;
    private RecyclerView rvMessageList;
    private EditText etMessage;
    private FloatingActionButton fbChat;
    String pUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pUid = getIntent().getStringExtra("pUid");
        Log.d("Kristina",pUid);
        setUI();
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
        fbChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mText = etMessage.getText().toString();
                etMessage.setText("");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String currentDateandTime = sdf.format(new Date());
                String mTime = currentDateandTime;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String mUname=null;
                if(user!=null){
                 mUname= user.getEmail();

                DatabaseReference cDatabase = FirebaseDatabase.getInstance().getReference("messages").child(user.getUid()).child(pUid);
                DatabaseReference cDatabase2 = FirebaseDatabase.getInstance().getReference("messages").child(pUid).child(user.getUid());
                String mid = cDatabase.push().getKey();
                Message message = new Message(mUname,mText,mTime,pUid);
                Message message2 = new Message(mUname,mText,mTime,user.getUid());
                cDatabase.child(mid).setValue(message);
                cDatabase2.child(mid).setValue(message2);
                    Notifications notifications = new Notifications(pUid,user.getUid(),"message",false);
                    DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference("notifications");
                    String nid = nDatabase.push().getKey();
                    nDatabase.child(nid).setValue(notifications);
                }
            }
        });

    }

    private ArrayList<Message> loadMessages() {
        mList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid;
        if (user != null) {
            // User is signed in
           uid = user.getUid();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("messages").child(uid).child(pUid);
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    mList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);

                        String uname = message.getUname();
                        String time  = message.getTime();
                        String text = message.getText();

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
}
