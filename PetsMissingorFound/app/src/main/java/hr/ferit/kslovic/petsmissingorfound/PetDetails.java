package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import static android.view.View.VISIBLE;

public class PetDetails extends Activity implements OnMapReadyCallback{

    private TextView tvnameP;
    private TextView tvbreedP;
    private TextView tvdetailsP;
    private Button bcontactP;
    private Button bPrevious;
    private Button bNext;
    private TextView tvstatusP;
    private ImageButton ibPetDetails;
    private ImageButton ibNewLoc;
    GoogleMap mGoogleMap;
    MapFragment mMapFragment;
    private ArrayList<LatLng> lList;
    private ArrayList<String> pList;
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private int i=0;
    private String pid;
    private GoogleMap.OnMapClickListener mCustomOnMapClickListener;
    private LatLng newLocation;
    String pUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.petdetails_layout);
        setUI();
        loadPets();
        loadPictures();

    }

    private void loadPictures() {
        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("pets").child(pid);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pList.clear();

                for (DataSnapshot PicSnapshot :dataSnapshot.child("pictures").getChildren()) {
                    UploadPicture upPic = PicSnapshot.getValue(UploadPicture.class);

                    pList.add(upPic.getUrl());
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

    private void setUI() {
        tvnameP = (TextView) findViewById(R.id.tvnameP);
        tvbreedP = (TextView) findViewById(R.id.tvbreedP);
        tvdetailsP = (TextView) findViewById(R.id.tvdetailsP);
        bcontactP = (Button) findViewById(R.id.bcontactP);
        tvstatusP = (TextView) findViewById(R.id.tvstatusP);
        ibPetDetails = (ImageButton) findViewById(R.id.ibPetDetails);
        ibNewLoc = (ImageButton) findViewById(R.id.ibNewLoc);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        lList = new ArrayList<>();
        pList = new ArrayList<>();
        this.mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fGoogleMap);
        this.mMapFragment.getMapAsync(this);
        bcontactP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pUid!=null) {
                    Log.d("Kristina",pUid);
                    Intent contactIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    contactIntent.putExtra("pUid", pUid);
                    startActivity(contactIntent);
                }
            }
        });
        bPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i>0){
                    i--;
                    LatLng pLocation = lList.get(i);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pLocation, 16));

                }
            }
        });
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i<lList.size()-1){
                    i++;
                    LatLng pLocation = lList.get(i);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pLocation, 16));
                }
            }
        });
    ibNewLoc.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(newLocation!=null){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("pets");
                DatabaseReference locDatabase = mDatabase.child(pid).child("locations");
                String locid = locDatabase.push().getKey();
                PetLocation upLoc = new PetLocation(locid, newLocation);
                locDatabase.child(locid).setValue(upLoc);
            }

        }
    });
        this.mCustomOnMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions newMarkerOptions = new MarkerOptions();
                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.lost));
                newMarkerOptions.title("New pet location");
                newMarkerOptions.snippet("Pet was last seen here!");
                newMarkerOptions.position(latLng);
                mGoogleMap.addMarker(newMarkerOptions);
                newLocation = latLng;
                ibNewLoc.setVisibility(VISIBLE);
            }
        };
        ibPetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pList!=null) {
                    Intent imageIntent = new Intent(getApplicationContext(), ImageSwitch.class);
                    imageIntent.putExtra("pictureList",pList);
                    startActivity(imageIntent);
                }
            }
        });
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
        loadLocations();
    }

    private void loadLocations() {
        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("pets").child(pid);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lList.clear();
                Log.d("Kristina","uslo1");

                    for (DataSnapshot LocationSnapshot :dataSnapshot.child("locations").getChildren()) {
                        double lat = LocationSnapshot.child("petLocation").child("latitude").getValue(Double.class);
                        double lng = LocationSnapshot.child("petLocation").child("longitude").getValue(Double.class);
                        Log.d("Kristina","uslo3");
                        Log.d("Kristina",new LatLng(lat,lng).toString());
                        setMarker(new LatLng(lat,lng));
                        lList.add(new LatLng(lat,lng));
                    }
                if(lList.size()>0)
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lList.get(0), 16));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }

    private void requestPermission(){
        String[] permissions = new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION };
        ActivityCompat.requestPermissions(PetDetails.this, permissions, REQUEST_LOCATION_PERMISSION);
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
                PetDetails.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
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
    public void setMarker(LatLng location){
        Log.d("Kristina", location.toString());
        MarkerOptions newMarkerOptions = new MarkerOptions();
        newMarkerOptions.title("Pet Location");
        newMarkerOptions.snippet("Pet seen here");
        newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.found));
        newMarkerOptions.position(location);
        mGoogleMap.addMarker(newMarkerOptions);
    }
    private void  loadPets() {
        pid = getIntent().getStringExtra("pid");
        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("pets").child(pid);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    Pet pet = dataSnapshot.getValue(Pet.class);
                            tvnameP.setText(pet.getEtPname());
                            tvbreedP.setText(pet.getEtPbreed());
                            tvdetailsP.setText(pet.getEtPdetails());
                            tvstatusP.setText(pet.getsStatus());
                            pUid = pet.getUid();
                            Glide.with(getApplicationContext()).load(pet.getPicture()).into(ibPetDetails);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

    }

}
