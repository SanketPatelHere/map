package com.example.mygooglemapscreen;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.annotation.RequiresApi;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    AutoCompleteTextView tvac2, tvac3;  //source, destination
    String s[] = {"c","c++","c#","java","jsp","android","php"};
    ArrayList<String> lst;
    MyData listener, listener2;

    //for source to destination
    private Polyline mPolyline;
    LatLng sourceLatLong, destinationLatLong;
    LatLng o11, o22;
    LatLng position;
    double latitude = 0;
    double longitude = 0;
    double latitude2 = 0;
    double longitude2 = 0;

    //second way      //current, souce, destination
    private GoogleMap mMap2, mMapSource, mMapDestination;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    public static AutocompleteSessionToken token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_to_destination2);
        final String apiKey = getString(R.string.google_maps_key);
        tvresult = (TextView) findViewById(R.id.tvresult);
        tvac = (AutoCompleteTextView)findViewById(R.id.tvac);
        tvac2 = (AutoCompleteTextView)findViewById(R.id.tvac2);
        tvac3 = (AutoCompleteTextView)findViewById(R.id.tvac3);


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

        listener = new MyData() {
            @Override
            public void getData(String description, Object o1) {

            }

            @Override
            public void getData(Object o1) {

            }
        };
        listener2 = new MyData() {
            @Override
            public void getData(String description, Object o2) {

            }

            @Override
            public void getData(Object o2) {

            }
        };
        token = AutocompleteSessionToken.newInstance();
        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        sp.edit().putString("token",token+"").commit();

        Places.initialize(this, getResources().getString(R.string.google_maps_key));
        //tvac2.setThreshold(3);
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(23.63936, 68.14712),
                new LatLng(28.20453, 97.34466));
        MyAdapter sourceadapter = new MyAdapter(this, R.layout.autocomplete_list_item, bounds, listener);
        tvac2.setAdapter(sourceadapter);
        tvac2.setTextColor(Color.RED);
        tvac2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sourceaddress = tvac2.getText().toString();
                tvac2.setSelection(0);
                sourceLatLong = getLocationFromAddress(getApplicationContext(), tvac2.getText().toString());
                Log.i("My Source Address = ",sourceaddress);
                Log.i("My Source latlong = ",sourceLatLong+"");
                Toast.makeText(SourceToDestination2.this, "sourceaddress = "+sourceaddress, Toast.LENGTH_SHORT).show();
                o11 = sourceLatLong;
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(sourceLatLong.latitude, sourceLatLong.longitude)).title("Source Marker"));
                MarkerOptions markerOptions = new MarkerOptions();
                //for remove old marker
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                markerOptions.position(sourceLatLong);
                markerOptions.title("Source Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                mCurrLocationMarker = mMapSource.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(o11));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        });

        //tvac3.setThreshold(3);
        RectangularBounds bounds2 = RectangularBounds.newInstance(
                new LatLng(23.63936, 68.14712),
                new LatLng(28.20453, 97.34466));
        MyAdapter destinationadapter = new MyAdapter(this, R.layout.autocomplete_list_item, bounds2, listener2);
        tvac3.setAdapter(destinationadapter);
        tvac3.setTextColor(Color.RED);
        tvac3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String destinationaddress = tvac3.getText().toString();
                tvac3.setSelection(0);
                destinationLatLong = getLocationFromAddress(getApplicationContext(), tvac3.getText().toString());

                Log.i("My Destination Address=",destinationaddress);
                Log.i("My Destination latlong=",destinationLatLong+"");
                Toast.makeText(SourceToDestination2.this, "destination = "+destinationaddress, Toast.LENGTH_SHORT).show();

                o22 = destinationLatLong;
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(destinationLatLong.latitude, destinationLatLong.longitude)).title("Destination Marker"));
                MarkerOptions markerOptions = new MarkerOptions();
                //for remove old marker
                /*if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }*/
                markerOptions.position(destinationLatLong);
                markerOptions.title("Destination Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mCurrLocationMarker = mMapDestination.addMarker(markerOptions);

            }
        });
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync((OnMapReadyCallback) this);
        //googleMap = supportMapFragment.getMap();


        btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.i("My mMapSource = ", mMapSource + "");
                Log.i("My mMapDestination = ", mMapDestination + "");
                Log.i("My mMapSource2 = ", o11 + "");
                Log.i("My mMapDestination2 = ", o22 + "");




                //getting url of google direction api
                drawRoute();
            }
        });


        configureCameraIdle();

    }

    public void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = mMap2.getCameraPosition().target;
                Log.i("My latLng = ",latLng+"");

                //LatLng latLng = myLatLng;
                Log.i("My Location = ", getClass().getSimpleName() + " = " + String.format("Drag from %f:%f", latLng.latitude, latLng.longitude));
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
        mMapSource = googleMap;
        mMapDestination = googleMap;

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
        Log.i("My mGoogleApiClient = ","connect");

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
            Log.i("My LocationServices = ","change");
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
        LatLng latLng2 = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i("My latLng2 = ",latLng2+"");

        if(o11!=null)
                {
                    latLng2 = (LatLng) o11;
                    Log.i("My new latLng2 = ",latLng2+"");
                }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng2);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap2.addMarker(markerOptions);

        //move map camera
        mMap2.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
        mMap2.animateCamera(CameraUpdateFactory.zoomTo(11));
        Log.i("My location = ","animatecamera");

        //stop location updates
        if (mGoogleApiClient != null) {
            Log.i("My location = ","update stop");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
















    private void drawRoute(){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(o11, o22);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&amp;"+str_dest+"&amp;"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.i("My url = ",url);
        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }
}