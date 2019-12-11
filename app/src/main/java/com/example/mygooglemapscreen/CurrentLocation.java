package com.example.mygooglemapscreen;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;

public class CurrentLocation extends AppCompatActivity {
    AutoCompleteTextView autocompleteView;
    private PlacesAutoCompleteAdapter2 mAdapter;
    PlaceAPI mPlaceAPI;

    HandlerThread mHandlerThread;
    Handler mThreadHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        //PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        autocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter2(getApplicationContext(), R.layout.autocomplete_list_item));



        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(CurrentLocation.this, description, Toast.LENGTH_SHORT).show();
                Log.i("My text = ",description+"");
            }
        });

        /*autocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String value = s.toString();

                // Remove all callbacks and messages
                mThreadHandler.removeCallbacksAndMessages(null);

                // Now add a new one
                mThreadHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // Background thread

                        mAdapter.resultList = mPlaceAPI.autocomplete(value);

                        // Footer
                        if (mAdapter.resultList.size() > 0)
                            mAdapter.resultList.add("footer");

                        // Post to Main Thread
                        mThreadHandler.sendEmptyMessage(1);
                    }
                }, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //doAfterTextChanged();
            }
        });*/

    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        // Get rid of our Place API Handlers
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
    }*/
}
