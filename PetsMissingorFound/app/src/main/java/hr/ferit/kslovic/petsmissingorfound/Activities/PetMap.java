package hr.ferit.kslovic.petsmissingorfound.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;

import com.google.android.gms.location.LocationListener;

import hr.ferit.kslovic.petsmissingorfound.Models.Pet;
import hr.ferit.kslovic.petsmissingorfound.R;

import static android.view.View.VISIBLE;

public class PetMap extends MenuActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    MapFragment mMapFragment;
    private ArrayList<Pet> pList;
    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private Button bShow;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private boolean shown=false;
    private Marker marker;
    private HashMap<Marker, Pet> eventMarkerMap;
    private ValueEventListener locListener;
    private DatabaseReference locRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.petsmap_layout);
        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleMap!=null){
        mGoogleMap.clear();
        shown = false;}

    }

    @Override
    public void onStop() {
        super.onStop();
        if (locListener != null) {
            locRef.removeEventListener(locListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    private void setUI() {
        eventMarkerMap = new HashMap<Marker, Pet>();
        pList = new ArrayList<>();
        bShow = (Button) findViewById(R.id.bShow);
        this.mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fGoogleMap);
        this.mMapFragment.getMapAsync(this);
        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Kristina", "kilik");
                if(pList!=null){
                    Log.d("Kristina", pList.toString());
                    for(Pet l : pList){
                        setMarker(l);
                    }
                }
            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mGoogleMap = googleMap;
        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        UiSettings uiSettings = this.mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
                return;
            }
            else{
                buildGoogleApiClient();

            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }


        this.mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Pet pet = eventMarkerMap.get(marker);
                Intent intent = new Intent(getApplicationContext(), PetDetails.class);
                intent.putExtra("pid", pet.getPid());
                startActivity(intent);
            }
        });
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        if(!shown) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            loadLocations(location);
            shown = true;
        }
    }
    private void requestPermission(){
        String[] permissions = new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION };
        ActivityCompat.requestPermissions(PetMap.this, permissions, REQUEST_LOCATION_PERMISSION);
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
                PetMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
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
    public void setMarker(Pet pet){
    MarkerOptions newMarkerOptions = new MarkerOptions();
                newMarkerOptions.title(pet.getEtPbreed() + "\n\n" + pet.getsStatus());
                newMarkerOptions.snippet(pet.getPicture());
                if(pet.getsStatus().equals("Lost"))
                    newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.lost));
                else
                    newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.found));
                newMarkerOptions.position(new LatLng(pet.getLastLatitude(),pet.getLastLongitude()));

        Marker pMarker =  mGoogleMap.addMarker(newMarkerOptions);
        eventMarkerMap.put(pMarker, pet);


}
    private ArrayList<Pet> loadLocations(final Location location) {
            locRef = FirebaseDatabase.getInstance().getReference("pets");
            locListener = locRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pList.clear();

                    for (DataSnapshot PetSnapshot :dataSnapshot.getChildren()) {
                        Pet pet = PetSnapshot.getValue(Pet.class);
                        double lon1 =pet.getLastLongitude();
                        double lat1 =pet.getLastLatitude();
                        if(location!=null) {
                            double lon2 = location.getLongitude();
                            double lat2 = location.getLatitude();
                            Log.d("Kristina", lat1 + "" + lat2);
                            double theta = lon1 - lon2;
                            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
                            dist = Math.acos(dist);
                            dist = rad2deg(dist);
                            dist = dist * 60 * 1.1515;
                            dist = dist * 1.609344;
                            if (dist < 10) {
                                Log.d("Kristina", "distance"+dist);
                                pList.add(pet);
                            }
                        }
                    }
                    bShow.setVisibility(VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });
        return pList;

    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View view;
        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

        }
        @Override
        public View getInfoWindow(final Marker marker) {
            PetMap.this.marker = marker;

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.titleMarker));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }
            ImageView image = ((ImageView) view.findViewById(R.id.picMarker));
            final String pic = marker.getSnippet();
            if (pic != null) {
                Glide.with(getApplicationContext()).load(pic).listener(new RequestListener<Drawable>() {
                                                                           @Override
                                                                           public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                               return false;
                                                                           }

                                                                           @Override
                                                                           public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                               getInfoContents(marker);
                                                                               return false;
                                                                           }
                                                                       }
                ).into(image);


            }

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (PetMap.this.marker != null
                    && PetMap.this.marker.isInfoWindowShown()) {
                PetMap.this.marker.hideInfoWindow();
                PetMap.this.marker.showInfoWindow();
            }
            return null;
        }
    }

}
