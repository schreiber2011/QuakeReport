package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

    static class ViewHolder {TextView mMag; TextView mLoc; TextView mDat;}

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
            holder.mDat = convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else { //Pega a tag to holder já criado
            holder = (ViewHolder) convertView.getTag();
        }

        //Encontra o terremoto na posição dada
        Earthquake currentEarthquake = getItem(position);

        //Seta os textos
        assert currentEarthquake != null;
        holder.mMag.setText(currentEarthquake.getmMagnitude());
        holder.mLoc.setText(currentEarthquake.getmLocation());
        holder.mDat.setText(currentEarthquake.getmDate());

        return convertView;
    }
}
