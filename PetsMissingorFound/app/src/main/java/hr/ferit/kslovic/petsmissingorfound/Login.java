package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class Login extends Activity implements View.OnClickListener {

    private Button btnRegister;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPsw;
    String email, uPsw;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        setUpUI();
    }
    void setUpUI(){
        btnRegister = (Button) findViewById(R.id.bnotRegistered);
        btnLogin = (Button) findViewById(R.id.bLogin);
        etEmail = (EditText) findViewById(R.id.etUnameLog);
        etPsw = (EditText) findViewById(R.id.etPswLog);
        this.btnRegister.setOnClickListener(this);
        this.btnLogin.setOnClickListener(this);

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
    public void onClick(View v) {
        email = etEmail.getText().toString();
        uPsw = etPsw.getText().toString();
        switch(v.getId()) {

            case R.id.bnotRegistered:
                Log.d("Kristina","uslo1");
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), Register.class);
                this.startActivity(intent);
                break;
            case R.id.bLogin:
                Log.d("Kristina","uslo2");
                mAuth.signInWithEmailAndPassword(email, uPsw)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("Kristina", "signInWithEmail:onComplete:" + task.isSuccessful());
                                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                startActivity(intent);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("Kristina", "signInWithEmail:failed", task.getException());
                                    //Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                                      //      Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
                break;
                /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                mDatabase.child(email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                       Users user = dataSnapshot.getValue(Users.class);

                        if(uName.equals(user.getUname())&&uPsw.equals(user.getPsw())){
                            //user logged in
                            Toast.makeText(getApplicationContext(), "You are successfully logged in!!!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Wrong username or password!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Kiki", "Failed to read value.", error.toException());
                    }
                });*/
        }
    }
}
