package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportPet extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText etPname;
    private EditText etPbreed;
    private EditText etPdetails;
    private ImageButton ibAddPic;
    private ImageButton ibAddLocation;
    private EditText etPcontact;
    private Spinner sStatus;
    private Button bReport;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUI();
    }

    private void setUI() {
        etPname = (EditText) findViewById(R.id.etPname);
        etPbreed = (EditText) findViewById(R.id.etPbreed);
        etPdetails = (EditText) findViewById(R.id.etPdetails);
        ibAddPic = (ImageButton) findViewById(R.id.ibAddPic);
        ibAddLocation = (ImageButton) findViewById(R.id.ibAddLocation);
        etPcontact = (EditText) findViewById(R.id.etPcontact);
        sStatus = (Spinner) findViewById(R.id.sStatus);
        bReport = (Button) findViewById(R.id.bReport);
        ibAddPic.setOnClickListener(this);
        ibAddLocation.setOnClickListener(this);
        bReport.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sStatus.setAdapter(adapter);
        sStatus.setOnItemSelectedListener(this);
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
        switch(v.getId()){
            case R.id.bReport:
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String pName = etPname.getText().toString();
                    String pBreed = etPbreed.getText().toString();
                    String pDetails = etPdetails.getText().toString();
                    String pPicture = null;
                    String pLocation = null;
                    String pContact = etPcontact.getText().toString();
                    String pStatus = statusSpinner;
                    Pet pet = new Pet(pName,pBreed,pDetails,pPicture,pLocation,pContact,pStatus);
                    mDatabase.child(uid).setValue(pet);
                }

                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        statusSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
