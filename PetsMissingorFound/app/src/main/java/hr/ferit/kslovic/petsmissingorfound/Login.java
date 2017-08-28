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
    private String email, uPsw, level;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        setUpUI();
    }
    void setUpUI(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Kristina", "onAuthStateChanged:signed_in:" + user.getUid());
                    if(level!=null&& level.equals("normal")){
                        Intent intent = new Intent(getApplicationContext(), Welcome.class);
                        startActivity(intent);}
                    else if(level!=null&&level.equals("admin")){
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);}
                }
                else {
                    // User is signed out
                    Log.d("Kristina", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        btnRegister = (Button) findViewById(R.id.bnotRegistered);
        btnLogin = (Button) findViewById(R.id.bLogin);
        etEmail = (EditText) findViewById(R.id.etUnameLog);
        etPsw = (EditText) findViewById(R.id.etPswLog);


        this.btnRegister.setOnClickListener(this);
        this.btnLogin.setOnClickListener(this);
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

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("Kristina", "signInWithEmail:failed", task.getException());
                                    //Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                                      //      Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Log.d("Kristina", "signInWithEmail:onComplete:" + task.isSuccessful());
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        loadUser(user.getUid());
                                    }
                                }


                                // ...
                            }
                        });
                break;

        }
    }
    public void loadUser(String uid){

        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                Users user =dataSnapshot.getValue(Users.class);
                level = user.getLevel();
                if (level != null && level.equals("normal")) {
                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
                    startActivity(intent);
                } else if (level != null && level.equals("admin")) {
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(intent);
                }


            }
            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value

            }
        });
    }
}
