package com.example.mygooglemapscreen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter {
    ArrayList<String> lst;
    Context context;
    int layout;

    ListFilter listFilter = new ListFilter();
    List<String> dataListAllItems;

    public CustomListAdapter(Context context, int layout, ArrayList<String> lst) {
        super(context, layout, lst);
        this.context = context;
        this.layout = layout;
        this.lst = lst;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);
        }
        TextView tvname = (TextView)convertView.findViewById(R.id.tvname);
        tvname.setText(getItem(position)+"");
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    @Override
    public Object getItem(int position) {
        Log.i("My CustomListA getItem", lst.get(position));
        return lst.get(position);
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<String>(lst);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<String> matchValues = new ArrayList<String>();

                for (String dataItem : dataListAllItems) {
                    if (dataItem.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                lst = (ArrayList<String>)results.values;
            } else {
                lst = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
