package de.androidbytes.adbconnect.presentation.view.dialogs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.utils.Sku;


/**
 * Created by Christoph on 10.10.2015.
 */
public class PurchaseItemAdapter extends ArrayAdapter<Sku> {

    List<RadioButton> radioButtons = new ArrayList<>();
    List<Sku> availableSkus;
    int selectedPosition = -1;
    int maxPosition = -1;

    public PurchaseItemAdapter(Context context, int resource, List<Sku> items) {
        super(context, resource, items);
        availableSkus = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alertdialog_purchase_item_list_item_layout, null);
        }

        final RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radiobutton_purchase_item_selected_indicator);
        radioButton.setClickable(false);

        boolean alreadyContainsRadioButton = false;

        for (RadioButton rb : radioButtons) {
            if(rb.hashCode() == radioButton.hashCode()) {
                alreadyContainsRadioButton = true;
            }
        }

        if(!alreadyContainsRadioButton) {
            if (radioButtons.size() <= position || radioButtons.get(position) == null) {
                radioButtons.add(position, radioButton);
            }
        }



        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                for (RadioButton rb : radioButtons) {
                    rb.setChecked(false);
                }

                radioButtons.get(position).setChecked(true);
                selectedPosition = position;
            }
        });

        Sku sku = getItem(position);

        if(sku != null) {
            TextView title = (TextView) convertView.findViewById(R.id.textview_purchase_item_title);
            TextView description = (TextView) convertView.findViewById(R.id.textview_purchase_item_description);

            if(title != null) {
                title.setText(sku.getTitle());
            }

            if(description != null) {
                description.setText(sku.getDescription());
            }
        }

        return convertView;
    }

    public int getSelectedIndex() {
        return selectedPosition;
    }
}
