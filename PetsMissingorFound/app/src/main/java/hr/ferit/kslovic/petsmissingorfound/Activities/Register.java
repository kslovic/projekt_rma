package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hr.ferit.kslovic.petsmissingorfound.R;
import hr.ferit.kslovic.petsmissingorfound.Models.Users;

public class Register extends Activity implements View.OnClickListener {

    private Button btnRegister;
    private EditText etFname;
    private EditText etLname;
    private EditText etEmail;
    private EditText etUname;
    private EditText etPhone;
    private EditText etPsw;
    private EditText etConPsw;
    private String fName, lName, email, uName, phone ,psw, conPsw;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String level = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        mAuth = FirebaseAuth.getInstance();
        setUpUI();
        setData();
    }


    void setUpUI(){
        btnRegister = (Button) findViewById(R.id.bRegister);
        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUname = (EditText) findViewById(R.id.etUname);
        etPhone = (EditText) findViewById(R.id.etPnumber);
        etPsw = (EditText) findViewById(R.id.etPsw);
        etConPsw = (EditText) findViewById(R.id.etPswCon);
        this.btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        fName = etFname.getText().toString();
        lName = etLname.getText().toString();
        email = etEmail.getText().toString();
        uName = etUname.getText().toString();
        phone = etPhone.getText().toString();
        psw = etPsw.getText().toString();
        conPsw = etConPsw.getText().toString();

        Log.e("Kristina", fName);
        Log.e("Kristina", lName);
        Log.e("Kristina", uName);
        Log.e("Kristina", email);
        Log.e("Kristina", phone);
        Log.e("Kristina", psw);
        Log.e("Kristina", conPsw);
        if(TextUtils.isEmpty(fName)||TextUtils.isEmpty(lName)||TextUtils.isEmpty(uName)||TextUtils.isEmpty(phone)||TextUtils.isEmpty(email)||TextUtils.isEmpty(psw)||TextUtils.isEmpty(conPsw)) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

        }
        else {
            if(psw.equals(conPsw)) {
                mDatabase = FirebaseDatabase.getInstance().getReference("users");
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    firebaseUser.updateEmail(email);
                    firebaseUser.updatePassword(psw);
                    Users user = new Users(uid,fName, lName, uName, email, phone, psw,level);
                    mDatabase.child(uid).setValue(user);
                    if(!level.equals("admin")) {
                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);
                    }
                } else {
                    mAuth.createUserWithEmailAndPassword(email, psw)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("Kristina", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        // Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                                        //       Toast.LENGTH_SHORT).show();
                                    } else {
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if (firebaseUser != null) {
                                            String uid = firebaseUser.getUid();
                                            Users user = new Users(uid,fName, lName, uName, email, phone, psw,level);
                                            mDatabase.child(uid).setValue(user);
                                            Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                            startActivity(intent);
                                        }
                                    }
                                    // ...
                                }
                            });

                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Password wasn't confirmed", Toast.LENGTH_SHORT).show();
            }
        }
    }
public void setData(){
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    if(firebaseUser!=null){
    String uid = firebaseUser.getUid();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);
                etFname.setText(user.getFname());
                etLname.setText(user.getLname());
                etUname.setText(user.getUname());
                etPhone.setText(user.getPhone());
                etEmail.setText(user.getEmail());
                etPsw.setText(user.getPsw());
                etConPsw.setText(user.getPsw());
                level = user.getLevel();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }
}
}
