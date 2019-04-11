package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter<SingleRowItemHolder> {

    private Context context;
    private ArrayList<SingleRowItemHolder> rowItemHolders;

    private TextView title, details;
    private ImageView icon;

    public CustomAdapter(Context context, ArrayList<SingleRowItemHolder> rowItemHolders){
        super(context, 0, rowItemHolders);
        this.context = context;
        this.rowItemHolders = rowItemHolders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.single_row_items, parent, false);
            icon = view.findViewById(R.id.row_image);
            title = view.findViewById(R.id.row_name);
            details = view.findViewById(R.id.row_details);
        }

        if(rowItemHolders.get(position).getImageId()!=-1){
            icon.setImageResource(rowItemHolders.get(position).getImageId());
        }

        title.setText(rowItemHolders.get(position).getName());

        details.setText(rowItemHolders.get(position).getDetails());

        return view;
    }
}
