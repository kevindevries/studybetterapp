package com.example.kevdevries.studybetter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by KevdeVries on 10/03/2018.
 *
 * Model to create a custom Info Window
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowAdapter(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow, null);

        TextView info_name = view.findViewById(R.id.name);
        TextView info_year = view.findViewById(R.id.year);
        TextView info_subject1 = view.findViewById(R.id.subject1);
        TextView info_subject2 = view.findViewById(R.id.subject2);
        TextView info_subject3 = view.findViewById(R.id.subject3);

        info_name.setText(marker.getTitle());
        info_year.setText(marker.getSnippet());

        InfoWindowModel infoWindowModel = (InfoWindowModel) marker.getTag();

        info_subject1.setText(infoWindowModel.getSubject1());
        info_subject2.setText(infoWindowModel.getSubject2());
        info_subject3.setText(infoWindowModel.getSubject3());

        return view;
    }
}