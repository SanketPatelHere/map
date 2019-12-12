package com.example.mygooglemapscreen;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.RectangularBounds;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback//, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
{

    public GoogleMap mMap;
    public TextView tvresult;
    ArrayList markerPoints = new ArrayList();
    public GoogleMap.OnCameraIdleListener onCameraIdleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvresult = (TextView)findViewById(R.id.tvresult);

        configureCameraIdle();

    }

    public void configureCameraIdle()
    {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {


                LatLng latLng = mMap.getCameraPosition().target;
                Log.i("Location = ",getClass().getSimpleName() +" = "+ String.format("Drag from %f:%f",latLng.latitude,latLng.longitude));
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try
                {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(addressList!=null && addressList.size()>0)
                    {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if(!locality.isEmpty() && !country.isEmpty())
                        {
                            tvresult.setText("Location = "+locality+" , "+country);
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.i("My Exception",e+"");
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(onCameraIdleListener);
        RectangularBounds bounds2 = RectangularBounds.newInstance(
                new LatLng(23.63936, 68.14712),
                new LatLng(28.20453, 97.34466));
        LatLng india = new LatLng(20.5937, 78.9629);

        //only show mnarker
        //mMap.addMarker(new MarkerOptions().position(india).title("Marker in india"));
        //or
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(india);
        markerOptions.title("india");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        Marker mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move camera = go to map
        LatLng rajkot = new LatLng(22.3039, 70.8022);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rajkot, 12));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Toast.makeText(MapsActivity.this, "map click", Toast.LENGTH_SHORT).show();
                if(markerPoints.size()>1)
                {
                    markerPoints.clear();
                    mMap.clear();
                }
                markerPoints.add(rajkot);//array ma add  //size check karva

                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(rajkot);
                if(markerPoints.size()==1)  //source  //first
                {
                    markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                }
                else if(markerPoints.size()==2)  //destination  //second
                {
                    markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }
                mMap.addMarker(markerOptions1);

                //souce-destination both present
                if(markerPoints.size()>=2)
                {
                    LatLng jamnagar = new LatLng(22.4707, 70.0577);
                    LatLng kalavad = new LatLng(22.2082, 70.3807);
                    MarkerOptions sourceMarkeroptions = new MarkerOptions();
                    MarkerOptions destinationMarkerOptions = new MarkerOptions();
                    sourceMarkeroptions.position(jamnagar);
                    destinationMarkerOptions.position(kalavad);
                        sourceMarkeroptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        destinationMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    mMap.addMarker(sourceMarkeroptions);
                    mMap.addMarker(destinationMarkerOptions);

                    //for getting map url
                    /*String url = getDirectionsUrl(jamnagar, kalavad);
                    DownloadTask downloadTask = new DownloadTask();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);*/
                }
            }
        });

    }



    /////////////////////////////////////////
    //extra code = for show route path
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }


    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            //List<List<HashMap>> routes = null;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        //protected void onPostExecute(List<List<HashMap>> result) {
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
