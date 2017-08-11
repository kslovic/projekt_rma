package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReportPet extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {

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
    private final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private  Uri pictureUri;
    private ImageView ivUpload;
    private Button bUpload;
    private String sDownloadUrl;
    GoogleMap mGoogleMap;
    MapFragment mMapFragment;
    private String location;
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    TextView tvLocation;

    private GoogleMap.OnMapClickListener mCustomOnMapClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);

        setUI();
    }

    private void setUI() {
        etPname = (EditText) findViewById(R.id.etPname);
        etPbreed = (EditText) findViewById(R.id.etPbreed);
        etPdetails = (EditText) findViewById(R.id.etPdetails);
        ibAddPic = (ImageButton) findViewById(R.id.ibAddPic);
        etPcontact = (EditText) findViewById(R.id.etPcontact);
        sStatus = (Spinner) findViewById(R.id.sStatus);
        bReport = (Button) findViewById(R.id.bReport);
        ivUpload = (ImageView) findViewById(R.id.ivUpload);
        bUpload = (Button) findViewById(R.id.bUpload);
        tvLocation = (TextView) findViewById(R.id.tvLlocation);
        ibAddPic.setOnClickListener(this);
        bReport.setOnClickListener(this);
        bUpload.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();

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

        this.mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fGoogleMap);
        this.mMapFragment.getMapAsync(this);
        this.mCustomOnMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions newMarkerOptions = new MarkerOptions();
                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
                newMarkerOptions.title("Pet Location");
                newMarkerOptions.snippet("Pet was last seen here!");
                newMarkerOptions.position(latLng);
                mGoogleMap.addMarker(newMarkerOptions);
                location = latLng.toString();
            }
        };
    }
    @SuppressWarnings("VisibleForTests")
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bReport:
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("pets").child(uid);
                    String pName = etPname.getText().toString();
                    String pBreed = etPbreed.getText().toString();
                    String pDetails = etPdetails.getText().toString();
                    String pPicture = sDownloadUrl;
                    String pLocation=location;
                    String pContact = etPcontact.getText().toString();
                    String pStatus = statusSpinner;
                    String pid = mDatabase.push().getKey();
                    if(TextUtils.isEmpty(pName)||TextUtils.isEmpty(pBreed)||TextUtils.isEmpty(pDetails)||TextUtils.isEmpty(pContact)) {
                        Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Pet pet = new Pet(pid, pName, pBreed, pDetails, pContact, pStatus);
                        mDatabase.child(pid).setValue(pet);
                        if (pPicture != null) {
                             DatabaseReference picDatabase = mDatabase.child(pid).child("pictures");
                            String picid = picDatabase.push().getKey();
                            UploadPicture upPic = new UploadPicture(picid, pPicture);
                            picDatabase.child(picid).setValue(upPic);
                        }
                        if (pLocation != null) {
                            DatabaseReference locDatabase = mDatabase.child(pid).child("locations");
                            String locid = locDatabase.push().getKey();
                            PetLocation upLoc = new PetLocation(locid, pLocation);
                            locDatabase.child(locid).setValue(upLoc);
                        }
                        Intent menuIntent = new Intent(getApplicationContext(), Welcome.class);
                        startActivity(menuIntent);
                    }
                }

                break;
            case R.id.ibAddPic:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select image"),REQUEST_CODE);
                ivUpload.setVisibility(VISIBLE);
                bUpload.setVisibility(VISIBLE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                p.addRule(RelativeLayout.BELOW, R.id.bUpload);
                tvLocation.setLayoutParams(p);
                break;
            case R.id.bUpload:
                if(pictureUri!=null){
                    ivUpload.setVisibility(GONE);
                    bUpload.setVisibility(GONE);
                    RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    r.addRule(RelativeLayout.BELOW, R.id.ibAddPic);
                    tvLocation.setLayoutParams(r);
                    final ProgressDialog pDialog = new ProgressDialog(this);
                    pDialog.setTitle("Uploading...");
                    pDialog.show();

                    StorageReference refStorage = mStorageRef.child("images/"+ System.currentTimeMillis()+"."+getPictureExt(pictureUri));
                    refStorage.putFile(pictureUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                   Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Picture uploaded",Toast.LENGTH_SHORT).show();
                                    /*FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("pictures");
                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();

                                        mDatabase.child(uid).child("System.currentTimeMillis()").setValue(taskSnapshot.getDownloadUrl().toString());
                                    }*/
                                    if(downloadUrl!=null) {
                                        sDownloadUrl = downloadUrl.toString();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), exception.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                    pDialog.setMessage("Uploaded:"+(int)progress+"%");
                                }
                            });

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            pictureUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(),pictureUri);
                ivUpload.setImageBitmap(bm);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    public String getPictureExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        statusSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        UiSettings uiSettings = this.mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        this.mGoogleMap.setOnMapClickListener(this.mCustomOnMapClickListener);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        this.mGoogleMap.setMyLocationEnabled(true);
    }
    private void requestPermission(){
        String[] permissions = new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION };
        ActivityCompat.requestPermissions(ReportPet.this, permissions, REQUEST_LOCATION_PERMISSION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length >0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Log.d("Permission","Permission granted. User pressed allow.");
                    }
                    else{
                        Log.d("Permission","Permission not granted. User pressed deny.");
                        askForPermission();
                    }
                }
        }
    }
    private void askForPermission(){
        boolean shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(
                ReportPet.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(shouldExplain){
            Log.d("Permission","Permission should be explained, - don't show again not clicked.");
            this.displayDialog();
        }
        else{
            Log.d("Permission","Permission not granted. User pressed deny and don't show again.");
            Toast.makeText(getApplicationContext(),"Sorry, we really need that permission",Toast.LENGTH_SHORT).show();
        }
    }
    private void displayDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Location permission")
                .setMessage("We display your location and need your permission")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Permission", "User declined and won't be asked again.");
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Permission","Permission requested because of the explanation.");
                        requestPermission();
                        dialog.cancel();
                    }
                })
                .show();
    }
}
