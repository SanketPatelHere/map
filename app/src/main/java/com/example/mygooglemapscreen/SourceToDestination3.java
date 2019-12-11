package com.example.mygooglemapscreen;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;


public class SourceToDestination3 extends AppCompatActivity
        //implements PlacesAutoCompleteAdapter.ClickListener{
{

    PlacesAutoCompleteAdapter  mAutoCompleteAdapter;
    PlaceArrayAdapter3 pa;
    PlacesAutoCompleteAdapter pa2;
    private RecyclerView recyclerView;
    //EditText place_search;
    AutoCompleteTextView place_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_to_destination3);

        Places.initialize(this, getResources().getString(R.string.google_maps_key));

        recyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        place_search = (AutoCompleteTextView) findViewById(R.id.place_search);



        ((AutoCompleteTextView) findViewById(R.id.place_search)).addTextChangedListener(filterTextWatcher);


        /*mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        //recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();*/


        place_search.setThreshold(3);
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));
        //place_search = new PlaceArrayAdapter3(this, R.layout.autocomplete_list_item, CURRENT_LOCATION_BONDS);
        pa = new PlaceArrayAdapter3(this, R.layout.autocomplete_list_item, bounds);
        //pa2 = new PlacesAutoCompleteAdapter(this);
        place_search.setAdapter(pa);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                //mAutoCompleteAdapter.getFilter().filter(s.toString());
                //pa.getFilter().filter(s.toString());
                /*if (recyclerView.getVisibility() == View.GONE)
                {
                    recyclerView.setVisibility(View.VISIBLE);
                }*/
            }
            else
            {
                /*if (recyclerView.getVisibility() == View.VISIBLE)
                {
                    recyclerView.setVisibility(View.GONE);
                }*/
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }
    };



    /*@Override
    public void click(Place place) {
        Log.i("My Search Text = ",place.getAddress()+", "+place.getLatLng().latitude+" "+place.getLatLng().longitude);
        Toast.makeText(this, place.getAddress()+", "+place.getLatLng().latitude+place.getLatLng().longitude, Toast.LENGTH_SHORT).show();

    }*/
}