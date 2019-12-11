package com.example.mygooglemapscreen;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class PlacesAutoCompleteAdapter2 extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> resultList;
    Context mContext;
    int mResource;
    PlaceAPI placeAPI = new PlaceAPI();

    private static String TAG = MainActivity.class.getSimpleName();

    private PlacesAutoCompleteAdapter2 mAdapter;



    public PlacesAutoCompleteAdapter2(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position!=(resultList.size()-1))
        {
            view = inflater.inflate(R.layout.autocomplete_list_item, null);
        }
        else
        {
            //view = inflater.inflate(R.layout.place_autocomplete_item_powered_by_google, null);
            view = inflater.inflate(R.layout.autocomplete_list_item, null);
        }
        if (position != (resultList.size() - 1)) {
            TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
            autocompleteTextView.setText(resultList.get(position));
        }
        else {
            //ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            // not sure what to do :D
        }

        return view;
    }

    public Filter getFilter()
    {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint!=null)
                {
                    resultList = placeAPI.autocomplete(constraint.toString());
                    //resultList.add("footer");
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results!=null && results.count>0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


}
