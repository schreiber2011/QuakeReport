package com.example.android.quakereport;

/**
 * Criado por schreiber em 1 de jan de 2018.
 * Um objeto {@link Earthquake} contém informação sobre um único terremoto.
 */

class Earthquake {

    //Magnitude do terremoto
    private final String mMagnitude;

    //Localização do terremoto
    private final String mLocation;

    //Data do terremoto
    private final String mDate;

    /**
     * Constrói um novo objeto {@link Earthquake}
     * @param magnitude é a magnitude do terremoto
     * @param location é a localização do terremoto
     * @param date é a data do terremoto
     */ Earthquake(String magnitude, String location, String date) {
        mMagnitude = magnitude;
        mLocation = location;
        mDate = date;
    }

    /** Retorna a magnitude do terr.
     * @return mMagnitude */ String getmMagnitude() {return mMagnitude;}

    /** Retorna a localização do terr.
     *  @return mLocation */ String getmLocation() {return mLocation;}

    /** Retorna a data do terr.
     * @return mDate */ String getmDate() {return mDate;}

}