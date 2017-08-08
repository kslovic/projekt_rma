package hr.ferit.kslovic.petsmissingorfound;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

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
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        setUpUI();
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

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Kristina", "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d("Kristina", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
            if(psw.equals(conPsw)){
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
                                } else{
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                                if (firebaseUser != null) {
                                    String uid = firebaseUser.getUid();
                                    Users user = new Users(etFname.getText().toString(), etLname.getText().toString(), etEmail.getText().toString(), etUname.getText().toString(), etPhone.getText().toString(), etPsw.getText().toString());
                                    mDatabase.child(uid).setValue(user);
                                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                    startActivity(intent);
                                }
                            }
                                // ...
                            }
                        });

            }
            else{
                Toast.makeText(getApplicationContext(), "Password wasn't confirmed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}