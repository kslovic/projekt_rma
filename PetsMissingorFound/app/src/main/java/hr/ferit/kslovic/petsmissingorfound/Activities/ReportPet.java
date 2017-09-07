package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hr.ferit.kslovic.petsmissingorfound.Models.Pet;
import hr.ferit.kslovic.petsmissingorfound.Models.PetLocation;
import hr.ferit.kslovic.petsmissingorfound.Models.UploadPicture;
import hr.ferit.kslovic.petsmissingorfound.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReportPet extends MenuActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 1;
    private final int REQUEST_STORAGE_PERMISSION = 1234;
    private EditText etPname;
    private EditText etPbreed;
    private EditText etPdetails;
    private ImageButton ibAddPic;
    private EditText etPcontact;
    private Spinner sStatus;
    private Button bReport;
    private String statusSpinner;
    private StorageReference mStorageRef;
    private  Uri pictureUri;
    private ImageView ivUpload;
    private Button bUpload;
    private String sDownloadUrl;
    private ImageButton ibAddPicCam;
    GoogleMap mGoogleMap;
    MapFragment mMapFragment;
    private LatLng location;
    private TextView tvLocation;
    private ArrayList<String> pList;
    private GoogleMap.OnMapClickListener mCustomOnMapClickListener;
    private ArrayAdapter<CharSequence> adapter;
    private  String pid;
    private double pLatitude;
    private double pLongitude;
    private String pPicture;
    private Bitmap bmp;
    private ByteArrayOutputStream bos;
    private Marker newMarker;
    private File photoFile=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);
        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pid = getIntent().getStringExtra("pid");
        if(pid!=null&&pictureUri==null)
        setData();
    }

    private void setUI() {
        pList = new ArrayList<>();
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
        ibAddPicCam = (ImageButton) findViewById(R.id.ibAddPicCam);
        ibAddPic.setOnClickListener(this);
        bReport.setOnClickListener(this);
        bUpload.setOnClickListener(this);
        ibAddPicCam.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sStatus.setAdapter(adapter);
        sStatus.setOnItemSelectedListener(this);

        this.mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fGoogleMap);
        this.mMapFragment.getMapAsync(this);
        this.mCustomOnMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(newMarker!=null)
                    newMarker.remove();
                MarkerOptions newMarkerOptions = new MarkerOptions();
                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                newMarkerOptions.title("Pet Location");
                newMarkerOptions.snippet("Pet was last seen here!");
                newMarkerOptions.position(latLng);
                newMarker = mGoogleMap.addMarker(newMarkerOptions);
                location = latLng;
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
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("pets");
                    String pName = etPname.getText().toString();
                    String pBreed = etPbreed.getText().toString();
                    String pDetails = etPdetails.getText().toString();
                    if (pList.size()>0)
                    pPicture = pList.get(0);
                    if(location!=null) {
                        pLatitude = location.latitude;
                        pLongitude = location.longitude;
                    }
                    String pContact = etPcontact.getText().toString();
                    String pStatus = statusSpinner;
                    if(pid==null)
                        pid = mDatabase.push().getKey();
                        if (TextUtils.isEmpty(pName) || TextUtils.isEmpty(pBreed) || TextUtils.isEmpty(pDetails) || TextUtils.isEmpty(pContact)) {
                            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

                        } else {
                            /*Pet pet = new Pet(pid, pName, pBreed, pDetails, pContact, pStatus, uid, pLatitude, pLongitude, pPicture);
                            mDatabase.child(pid).setValue(pet);*/
                            mDatabase.child(pid).child("pid").setValue(pid);
                            mDatabase.child(pid).child("etPname").setValue(pName);
                            mDatabase.child(pid).child("etPbreed").setValue(pBreed);
                            mDatabase.child(pid).child("etPdetails").setValue(pDetails);
                            mDatabase.child(pid).child("etPcontact").setValue(pContact);
                            mDatabase.child(pid).child("sStatus").setValue(pStatus);
                            mDatabase.child(pid).child("uid").setValue(uid);
                            mDatabase.child(pid).child("lastLatitude").setValue(pLatitude);
                            mDatabase.child(pid).child("lastLongitude").setValue(pLongitude);
                            mDatabase.child(pid).child("picture").setValue(pPicture);
                            Long tsLong = System.currentTimeMillis() * (-1) / 1000;
                            mDatabase.child(pid).child("pubTime").setValue(tsLong);
                            if (pList.size() > 0) {
                                DatabaseReference picDatabase = mDatabase.child(pid).child("pictures");
                                for (String pPic : pList) {
                                    String picid = picDatabase.push().getKey();
                                    UploadPicture upPic = new UploadPicture(picid, pPic);
                                    picDatabase.child(picid).setValue(upPic);
                                }
                            }
                            if (location != null) {
                                DatabaseReference locDatabase = mDatabase.child(pid).child("locations");
                                String locid = locDatabase.push().getKey();
                                PetLocation upLoc = new PetLocation(locid, location, firebaseUser.getEmail());
                                locDatabase.child(locid).setValue(upLoc);
                            }

                            Intent menuIntent = new Intent(getApplicationContext(), Welcome.class);
                            startActivity(menuIntent);
                        }


                }

                break;
            case R.id.ibAddPic:
                boolean hasStoragePermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

                if (!hasStoragePermission) {
                    requestPermission(REQUEST_STORAGE_PERMISSION);
                } else {
                    openStorage();
                }
                break;
            case R.id.bUpload:
                if(bmp!=null){
                    ivUpload.setVisibility(GONE);
                    bUpload.setVisibility(GONE);
                    RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    r.addRule(RelativeLayout.BELOW, R.id.llReport);
                    tvLocation.setLayoutParams(r);
                    final ProgressDialog pDialog = new ProgressDialog(this);
                    pDialog.setTitle("Uploading...");
                    pDialog.show();
                    Log.d("Kristina","bUpload");
                    byte[] data = bos.toByteArray();
                    FirebaseUser fUser =FirebaseAuth.getInstance().getCurrentUser();
                    String key = fUser.getUid();
                    StorageReference refStorage = mStorageRef.child("images/"+ System.currentTimeMillis()+ key +getPictureExt(pictureUri));
                    refStorage.putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                   Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    pDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Picture uploaded",Toast.LENGTH_SHORT).show();

                                    if(downloadUrl!=null) {
                                        sDownloadUrl = downloadUrl.toString();
                                        pList.add(sDownloadUrl);
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
            case R.id.ibAddPicCam:
                boolean hasCameraPermission = (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

                if (!hasCameraPermission) {
                    requestPermission(REQUEST_CAMERA_PERMISSION);
                } else{
                    openCamera();
                }
                break;
        }
    }
    private void setPictue() {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivUpload.setVisibility(VISIBLE);
        bUpload.setVisibility(VISIBLE);
        p.addRule(RelativeLayout.BELOW, R.id.bUpload);
        tvLocation.setLayoutParams(p);

    }
    private void openStorage() {
        Intent intentPic = new Intent();
        intentPic.setType("image/*");
        intentPic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentPic,"Select image"),REQUEST_STORAGE_PERMISSION);
       setPictue();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean hasWriteStoragePermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (intent.resolveActivity(getPackageManager()) != null) {

                if (!hasWriteStoragePermission) {
                    requestPermission(REQUEST_WRITE_STORAGE_PERMISSION);
                } else {
                    try {
                    photoFile = createImageFile();
                    } catch (IOException ex) {

                    }
                }

            // Continue only if the File  if (photoFile != null) {
                if (photoFile != null) {
                   pictureUri = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                   intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                startActivityForResult(intent, REQUEST_CAMERA_PERMISSION);
            }

            setPictue();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Kristina","result");
            switch (requestCode) {
                case REQUEST_STORAGE_PERMISSION:
                    if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                        super.onActivityResult(requestCode, resultCode, data);
                        pictureUri = data.getData();
                        savePicture();

                    }
                    break;
                case REQUEST_CAMERA_PERMISSION:
                    if (resultCode == RESULT_OK){
                        savePicture();
                    }
                    break;
            }
    }
    public String getPictureExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void savePicture() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            AssetFileDescriptor fileDescriptor = null;
            fileDescriptor =
                    this.getContentResolver().openAssetFileDescriptor(pictureUri, "r");

            bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            Log.d("Kristina", pictureUri.toString());
            Glide.with(this)
                    .load(pictureUri)
                    .into(ivUpload);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      //  ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
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
            requestPermission(REQUEST_LOCATION_PERMISSION);
            return;
        }
        this.mGoogleMap.setMyLocationEnabled(true);
    }
    private void requestPermission(int requestCode){
        switch(requestCode) {
            case REQUEST_LOCATION_PERMISSION:
            String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(ReportPet.this, permissions, REQUEST_LOCATION_PERMISSION);
              break;
            case REQUEST_CAMERA_PERMISSION:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                break;
            case REQUEST_STORAGE_PERMISSION:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                break;
            case REQUEST_WRITE_STORAGE_PERMISSION:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

                if(grantResults.length >0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Log.d("Permission","Permission granted. User pressed allow.");
                        switch (requestCode) {
                            case REQUEST_LOCATION_PERMISSION:
                                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    mGoogleMap.setMyLocationEnabled(true);
                                }
                                break;
                            case REQUEST_CAMERA_PERMISSION:
                                openCamera();
                                break;
                            case REQUEST_STORAGE_PERMISSION:
                                openStorage();
                                break;
                            case REQUEST_WRITE_STORAGE_PERMISSION:
                                try{
                                photoFile = createImageFile();
                                break;
                                } catch (IOException ex) {

                                }
                        }
                    }
                    else{
                        Log.d("Permission","Permission not granted. User pressed deny.");
                        askForPermission(requestCode);
                    }
                }

    }
    private void askForPermission( int requestCode){
        boolean shouldExplain = false;
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(ReportPet.this, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case REQUEST_CAMERA_PERMISSION:
                shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(ReportPet.this, Manifest.permission.CAMERA);
                break;
            case REQUEST_STORAGE_PERMISSION:
                shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(ReportPet.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case REQUEST_WRITE_STORAGE_PERMISSION:
                shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(ReportPet.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }

        if(shouldExplain){
            Log.d("Permission","Permission should be explained, - don't show again not clicked.");
            this.displayDialog(requestCode);
        }
        else{
            Log.d("Permission","Permission not granted. User pressed deny and don't show again.");
            Toast.makeText(getApplicationContext(),"Sorry, we really need that permission",Toast.LENGTH_SHORT).show();
        }
    }
    private void displayDialog(final int requestCode) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                dialogBuilder.setTitle("Location permission")
                .setMessage("We display your location and need your permission");
                break;
            case REQUEST_CAMERA_PERMISSION:
                dialogBuilder.setTitle("Camera permission")
                .setMessage("We use your camera and need your permission");
                break;
            case REQUEST_STORAGE_PERMISSION:
                dialogBuilder.setTitle("Read storage permission")
                        .setMessage("We read your storage and need your permission");
                break;
            case REQUEST_WRITE_STORAGE_PERMISSION:
                dialogBuilder.setTitle("Write storage permission")
                        .setMessage("We write in your storage and need your permission");
                break;
        }
        dialogBuilder
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
                        requestPermission(requestCode);
                        dialog.cancel();
                    }
                })
                .show();
    }
    public void setData(){
        if(pid!=null){
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("pets").child(pid);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Pet pet = dataSnapshot.getValue(Pet.class);
                    etPname.setText(pet.getEtPname());
                    etPbreed.setText(pet.getEtPbreed());
                    etPdetails.setText(pet.getEtPdetails());
                    etPcontact.setText(pet.getEtPcontact());
                    sStatus.setSelection(((adapter.getPosition(pet.getsStatus()))));
                    pLatitude = pet.getLastLatitude();
                    pLongitude = pet.getLastLongitude();
                    pPicture = pet.getPicture();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });

        }
    }
}
