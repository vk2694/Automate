package com.lsv.automate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class AutomationViewHolder extends RecyclerView.ViewHolder {
    private final TextView total_items, label_title, bluethooth_status, donotdistrub_status;
    private final Button editBtn;
    private Context context;

    private AutomationViewHolder(View itemView) {
        super(itemView);
        label_title = itemView.findViewById(R.id.title_label);
        total_items = itemView.findViewById(R.id.enabledSettings);
        bluethooth_status = itemView.findViewById(R.id.bluethooth);
        donotdistrub_status = itemView.findViewById(R.id.doNotDistrube);
        editBtn = itemView.findViewById(R.id.editBtn);
    }

    public void bind(String label, String setting, String bluethooth, String donotdistrub, Context context) {
        label_title.setText(label);
        total_items.setText("Total Enabled: "+ setting);
        bluethooth_status.setText("Bluetooth Enabled: "+ bluethooth);
        donotdistrub_status.setText("Do Not Disturb: "+ donotdistrub);
        this.context = context;
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).editBtnClicked();
            }
        });
    }

    static AutomationViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cards, parent, false);
        return new AutomationViewHolder(view);
    }
}