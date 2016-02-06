package de.androidbytes.adbconnect.presentation.utils;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.androidbytes.adbconnect.R;


/**
 * Created by Christoph on 09.10.2015.
 */
public class InAppBillingUtility {

    private static List<Sku> mAvailableSkus = new ArrayList<>();

    public static List<Sku> getAvailableSkus(Context context) {

        if (mAvailableSkus.size() > 0) {
            return mAvailableSkus;
        } else {
            return fillAvailableSkus(context);
        }
    }

    public static Sku getSkuAtIndex(int index) {
        if(index >= 0 && index < mAvailableSkus.size()) {
            return mAvailableSkus.get(index);
        } else {
            return null;
        }
    }

    private static List<Sku> fillAvailableSkus(Context context) {

        mAvailableSkus.add(new Sku(context.getString(R.string.donation_single_level_one_title), context.getString(R.string.donation_single_level_one_description), context.getString(R.string.donation_single_level_one_sku)));
        mAvailableSkus.add(new Sku(context.getString(R.string.donation_single_level_two_title), context.getString(R.string.donation_single_level_two_description), context.getString(R.string.donation_single_level_two_sku)));
        mAvailableSkus.add(new Sku(context.getString(R.string.donation_single_level_three_title), context.getString(R.string.donation_single_level_three_description), context.getString(R.string.donation_single_level_three_sku)));

        return mAvailableSkus;
    }

}
