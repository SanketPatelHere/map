package com.example.mygooglemapscreen;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceToDestination2 extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GoogleMap googleMap;
    GoogleMap mMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    Button btnFind;
    String placeName = "";
    public TextView tvresult;
    public GoogleMap.OnCameraIdleListener onCameraIdleListener;
    LatLng myLatLng;
    AutoCompleteTextView tvac;
    AutoCompleteTextView tvac2;
    String s[] = {"c","c++","c#","java","jsp","android","php"};
    ArrayList<String> lst;

    //second way
    private GoogleMap mMap2;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_to_destination2);
        final String apiKey = getString(R.string.google_maps_key);
        tvresult = (TextView) findViewById(R.id.tvresult);
        tvac = (AutoCompleteTextView)findViewById(R.id.tvac);
        tvac2 = (AutoCompleteTextView)findViewById(R.id.tvac2);


        lst = new ArrayList<>();
        lst.add("c");
        lst.add("c++");
        lst.add("c#");
        lst.add("java");
        lst.add("jsp");
        lst.add("php");
        lst.add("android");
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, s);
        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.autocompleteitem, lst);
        tvac.setThreshold(1);
        tvac.setAdapter(adapter);
        tvac.setTextColor(Color.RED);

        Places.initialize(this, getResources().getString(R.string.google_maps_key));
        tvac2.setThreshold(3);
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));
        MyAdapter adapter2 = new MyAdapter(this, R.layout.autocomplete_list_item, bounds);
        tvac2.setAdapter(adapter2);
        tvac2.setTextColor(Color.RED);
        tvac2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sourceaddress = tvac2.getText().toString();
                Log.i("My Source Address = ",sourceaddress);
                Toast.makeText(SourceToDestination2.this, "sourceaddress = "+sourceaddress, Toast.LENGTH_SHORT).show();
            }
        });

        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();

        placeAutocompleteFragment.setFilter(autocompleteFilter);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new com.google.android.gms.location.places.ui.PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                Toast.makeText(getApplicationContext(),place.getName().toString(),Toast.LENGTH_SHORT).show();
                Log.i("My myLatLng1 = ", myLatLng+"");
                Log.i("My Place1 = ", place.getName() + ", " + place.getId());

            }

            @Override
            public void onError(Status status) {
                Log.i("My Error1 = ", status + "");
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });

        /*if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteSupportFragment1 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        AutocompleteSupportFragment autocompleteSupportFragment2 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        autocompleteSupportFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteSupportFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeName = place.toString();
                myLatLng = place.getLatLng();
                Log.i("My myLatLng1 = ", myLatLng+"");
                Log.i("My Place1 = ", place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i("My Error1 = ", status + "");
            }
        });*/

        /*autocompleteSupportFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteSupportFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("My Place2 = ",place.getName()+", "+place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i("My Error2 = ",status+"");
            }
        });*/
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync((OnMapReadyCallback) this);
        //googleMap = supportMapFragment.getMap();


        btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = placeName;
                if (location != null && !location.equals("")) {
                    //new GeocoderTask().execute(location);
                    //new Geocoder();
                }
            }
        });


        configureCameraIdle();

    }

    public void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {


                LatLng latLng = mMap.getCameraPosition().target;
                //LatLng latLng = myLatLng;

                Log.i("Location = ", getClass().getSimpleName() + " = " + String.format("Drag from %f:%f", latLng.latitude, latLng.longitude));
                Geocoder geocoder = new Geocoder(SourceToDestination2.this);
                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (!locality.isEmpty() && !country.isEmpty()) {
                            tvresult.setText("Location = " + locality + " , " + country);
                        }
                    }
                } catch (Exception e) {
                    Log.i("My Exception", e + "");
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap2 = googleMap;
        mMap2.setOnCameraIdleListener(onCameraIdleListener);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap2.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap2.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}