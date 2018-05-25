package com.phobetor.promad;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by logan on 25/4/17.
 */

public class CustomAdapter extends ArrayAdapter<String> {


    public CustomAdapter(@NonNull Context context, String[] string ) {
        super(context,R.layout.custom_row, string);
    }

    public View getView(int position, View converView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_row, parent, false);

        String atPosition = getItem(position);
        TextView textView = (TextView) view.findViewById(R.id.rowName);
        textView.setText(atPosition);
        return view;

    }
}
