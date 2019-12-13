package com.example.mygooglemapscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static android.content.Context.MODE_PRIVATE;
public class MyAdapter extends ArrayAdapter<MyAdapter.PlaceAutocomplete> implements Filterable {

    private static final String TAG = "MyAdapter";
    public MyData listener;
    private final PlacesClient placesClient;
    private RectangularBounds mBounds;
    private ArrayList<MyAdapter.PlaceAutocomplete> mResultList = new ArrayList<>();
    public Context context;
    ArrayList<MyAdapter.PlaceAutocomplete> resultList;

    public MyAdapter(Context context, int resource, RectangularBounds bounds) {
        super(context, resource);
        this.context = context;
        mBounds = bounds;
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }
    public MyAdapter(Context context, int resource, RectangularBounds bounds, MyData listener) {
        super(context, resource);
        this.context = context;
        mBounds = bounds;
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
        this.listener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position!=(mResultList.size()-1))
        {
            view = inflater.inflate(R.layout.autocomplete_list_item, null);
        }
        else
        {
            //view = inflater.inflate(R.layout.place_autocomplete_item_powered_by_google, null);
            view = inflater.inflate(R.layout.autocomplete_list_item, null);
        }
        if (position != (mResultList.size() - 1)) {
            TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
            autocompleteTextView.setText(mResultList.get(position)+"");
            listener.getData(mResultList.get(position).l1);
            //listener.getData(mResultList.get(position).description+"", mResultList.get(position).l1);
            Log.i("My location1 = ",mResultList.get(position)+"");
        }
        else {
            //ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            // not sure what to do :D
        }

        return view;
    }

    @Override
    public int getCount() {
        if (mResultList == null)
            return 0;
        else
            return mResultList.size();
    }

    @Override
    public MyAdapter.PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    private ArrayList<MyAdapter.PlaceAutocomplete> getPredictions(CharSequence constraint) {

        resultList = new ArrayList<>();

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        //AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        SharedPreferences sp = context.getSharedPreferences("data", MODE_PRIVATE);
        //or =get token
        //String token = sp.getString("token","null");
        AutocompleteSessionToken token1 = SourceToDestination2.token;
        Log.i("My token = ",token1+"");
        //AutocompleteSessionToken token1 = token;
                // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                // .setLocationBias(bounds)
                .setLocationBias(mBounds)
                //.setCountry("au")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token1)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            //Tasks.await(autocompletePredictions);
            Tasks.await(autocompletePredictions, 30, TimeUnit.SECONDS);

        }
        catch (Exception e){
        //catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.i("My Error = ","in prediction "+e);
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (com.google.android.libraries.places.api.model.AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i("My placeid = ", prediction.getPlaceId());
                    Log.i("My primarytext ", prediction.getPrimaryText(null).toString());
                    resultList.add(new MyAdapter.PlaceAutocomplete(prediction.getPlaceId(), prediction.getFullText(null).toString()));
                    //resultList.add(new MyAdapter.PlaceAutocomplete(prediction.getPlaceId(), prediction.getFullText(null).toString(), latlong));
                }

            return resultList;
        } else {
            Log.i("My failure = ",resultList+"");
            return resultList;
        }

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    Log.d(TAG, "Before Prediction");
                    mResultList = getPredictions(constraint);
                    Log.d(TAG, "After Prediction");
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                    //Log.i("My Results = ",results.values+"");
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;
        public LatLng l1;
        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }
        PlaceAutocomplete(CharSequence placeId, CharSequence description, LatLng l1) {
            this.placeId = placeId;
            this.description = description;
            this.l1 = l1;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


}