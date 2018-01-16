package com.example.android.quakereport;

/**
 * Criado por schreiber em 1 de jan de 2018.
 * Um objeto {@link Earthquake} contém informação sobre um único terremoto.
 */

class Earthquake {

    //Magnitude do terremoto
    private final double mMagnitude;

    //Localização do terremoto
    private final String mLocation;

    //Direção do terremoto
    private final String mDirection;

    //Data do terremoto
    private final String mDate;

    //Hora do terremoto
    private final String mHour;

    //Url do terremoto
    private final String mUrl;

    /**
     * Constrói um novo objeto {@link Earthquake}
     * @param magnitude é a magnitude do terremoto
     * @param location é a localização do terremoto
     * @param date é a data do terremoto
     */ Earthquake(double magnitude, String location, String direction, String date, String hour, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mDirection = direction;
        mDate = date;
        mHour = hour;
        mUrl = url;
    }

    /** Retorna a magnitude do terr.
     * @return mMagnitude */ double getmMagnitude() {return mMagnitude;}

    /** Retorna a localização do terr.
     *  @return mLocation */ String getmLocation() {return mLocation;}

    /** Retorna a direção do terr.
     *  @return mDirection */ String getmDirection() {return mDirection;}

    /** Retorna a data do terr.
     * @return mDate */ String getmDate() {return mDate;}

    /** Retorna a hora do terr.
     * @return mHour */ String getmHour() {return mHour;}

    /** Retorna a url do terr.
     * @return mUrl */ String getmUrl() {return mUrl;}
}