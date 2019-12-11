package com.example.mygooglemapscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;

public class newauto extends AppCompatActivity {
    public static final String TAG = "newauto";
    private static final int AUTO_COMP_REQ_CODE = 2;

    protected GeoDataClient geoDataClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newauto);

        Toolbar tb = findViewById(R.id.toolbar);
        //setSupportActionBar(tb);
        //tb.setSubtitle("Auto Complete");

        //set place types spinner data from array
        Spinner placeType = findViewById(R.id.place_type);
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.placeTypes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeType.setAdapter(spinnerAdapter);

        //Set adapter for autocomplete text view
        AutoCompleteTextView searchPlace = findViewById(R.id.search_place);

        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(this);
        searchPlace.setAdapter(adapter);
        searchPlace.setOnItemClickListener(onItemClickListener);

    }
    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i("My text = ",adapterView.getItemIdAtPosition(i)+"");
                    Toast.makeText(newauto.this,
                            "selected place "
                                    + (adapterView.
                                    getItemAtPosition(i)).getClass()
                            , Toast.LENGTH_SHORT).show();
                    //do something with the selection
                    searchScreen();
                }
            };

    public void searchScreen(){
        Intent i = new Intent();
        i.setClass(this, newauto.class);
        startActivity(i);
    }
}