package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Schreiber em 1 de jan de 2018.
 * An objet {@link EarthquakeAdapter} knows how to create a list item layout for each earthquake
 * in the data source (a list of {@link Earthquake} objects).
 */

class EarthquakeAdapter extends ArrayAdapter<Earthquake>{

    /**
     * Construct a new object {@link EarthquakeAdapter}
     * @param context from app
     * @param objects is the list of earthquakes, which is the data source of the adapter
     */
    EarthquakeAdapter(@NonNull Context context, @NonNull List<Earthquake> objects) {
        super(context, 0, objects);
    }

    private View mInflater(ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(
                R.layout.earthquake_list_item, parent, false);
    }

    static class ViewHolder {TextView mMag; TextView mLoc; TextView mDir; TextView mDat; TextView mHor;}

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     * @param position is the position in the list of earthquakes
     * @param convertView is a recycled view to populate
     * @param parent is the parent view
     * @return a list item view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        // Checks if there is an existing list item view (called convertView) that we can reuse,
        // if not, then inflate a new list item layout.
        if (convertView == null) {
            convertView = mInflater(parent);

            // Create a new ViewHolder object we can reuse a row
            holder = new ViewHolder();
            holder.mMag = convertView.findViewById(R.id.magnitude);
            holder.mLoc = convertView.findViewById(R.id.location);
            holder.mDir = convertView.findViewById(R.id.direction);
            holder.mDat = convertView.findViewById(R.id.date);
            holder.mHor = convertView.findViewById(R.id.hour);
            convertView.setTag(holder);
        } else { // Gets the ViewHolder object from the holder field
            holder = (ViewHolder) convertView.getTag();
        }

        // Find the earthquake at the given position in the list of earthquakes
        Earthquake currentEarthquake = getItem(position);

        // Set the proper background color on the magnitude circle.
        assert currentEarthquake != null;
        holder.mMag.setText(formatMagnitude(currentEarthquake.getmMagnitude()));
        holder.mLoc.setText(currentEarthquake.getmLocation());
        holder.mDir.setText(currentEarthquake.getmDirection());
        holder.mDat.setText(currentEarthquake.getmDate());
        holder.mHor.setText(currentEarthquake.getmHour());

        // Configures the background color of the magnitude circle
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.mMag.getBackground();

        // Obtains the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getmMagnitude());

        // Configures the background color of the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        return convertView;
    }

    /**
     * Returns a magnitude string with one decimal (i.e. "3.2")
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
