/**
 * Copyright (C) 2016 Christoph Hennemann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.androidbytes.adbconnect.presentation.view.dialogs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import de.androidbytes.adbconnect.R;
import de.androidbytes.adbconnect.presentation.utils.Sku;

import java.util.ArrayList;
import java.util.List;



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
