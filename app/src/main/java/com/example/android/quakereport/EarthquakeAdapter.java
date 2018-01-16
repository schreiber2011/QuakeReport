package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Criado por schreiber em 1 de jan de 2018.
 * Um objeto {@link EarthquakeAdapter} sabe como popular um item de uma lista
 * com os dodos de {@link Earthquake}
 */

class EarthquakeAdapter extends ArrayAdapter<Earthquake>{

    /**
     * Constrói um novo objeto {@link EarthquakeAdapter}
     * @param context do app
     * @param objects é a lista de terremoto, que é a fonte de dados para o adapter
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
     * Sobrescreve o método getView
     * @param position é a posição do item
     * @param convertView é a refer. da view
     * @param parent é a refer. do ViewGroup pai
     * @return um lista de views que mostra a informação sobre os terremotos
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        // Checa se existe uma list (convertView) que pode ser reciclada,
        // se for nula, então infla um novo item na lista do layout.
        //View listItemView = convertView;
        if (convertView == null) {
            convertView = mInflater(parent);//LayoutInflater.from(getContext()).inflate(
                    //R.layout.earthquake_list_item, parent, false);
            //Cria o holder, encontras as IDs e seta a tag
            holder = new ViewHolder();
            holder.mMag = convertView.findViewById(R.id.magnitude);
            holder.mLoc = convertView.findViewById(R.id.location);
            holder.mDir = convertView.findViewById(R.id.direction);
            holder.mDat = convertView.findViewById(R.id.date);
            holder.mHor = convertView.findViewById(R.id.hour);
            convertView.setTag(holder);
        } else { //Pega a tag to holder já criado
            holder = (ViewHolder) convertView.getTag();
        }

        //Encontra o terremoto na posição dada
        Earthquake currentEarthquake = getItem(position);

        //Seta os textos
        assert currentEarthquake != null;
        holder.mMag.setText(formatMagnitude(currentEarthquake.getmMagnitude()));
        holder.mLoc.setText(currentEarthquake.getmLocation());
        holder.mDir.setText(currentEarthquake.getmDirection());
        holder.mDat.setText(currentEarthquake.getmDate());
        holder.mHor.setText(currentEarthquake.getmHour());

        // Configure a cor de fundo apropriada no círculo de magnitude.
        // Busque o fundo do TextView, que é um GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.mMag.getBackground();

        // Obtenha a cor de fundo apropriada, baseada na magnitude do terremoto atual
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getmMagnitude());

        // Configure a cor no círculo de magnitude
        magnitudeCircle.setColor(magnitudeColor);

        return convertView;
    }

    /**
     * Retorna a string magnitude formatada mostrando 1 casa decimal (i.e. "3.2")
     * de um valor de magnitude decimal.
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
