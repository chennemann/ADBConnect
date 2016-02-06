package de.androidbytes.adbconnect.presentation.utils;


import lombok.Getter;
import lombok.experimental.Accessors;


/**
 * Created by Christoph on 09.10.2015.
 */
@Accessors(prefix = "m")
@Getter
public class Sku {

    private String mTitle;
    private String mDescription;
    private String mSku;

    public Sku(String title, String description, String sku) {
        mTitle = title;
        mDescription = description;
        mSku = sku;
    }

    @Override
    public String toString() {

        return mTitle;
    }
}