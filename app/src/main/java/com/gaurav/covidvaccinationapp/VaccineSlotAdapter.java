package com.gaurav.covidvaccinationapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VaccineSlotAdapter extends RecyclerView.Adapter<VaccineSlotAdapter.VaccineSlotViewHolder> {

    private final List<VaccineSlotItem> slotList;

    public VaccineSlotAdapter(List<VaccineSlotItem> slotList) {
        this.slotList = slotList;
    }

    @NonNull
    @Override
    public VaccineSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaccine_slot, parent, false);
        return new VaccineSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineSlotViewHolder holder, int position) {
        VaccineSlotItem item = slotList.get(position);
        holder.summaryTextView.setText(item.getSummary());
        holder.detailsTextView.setText(item.getDetails());
        holder.detailsTextView.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
        holder.modifyButton.setVisibility(item.isShowModifyButton() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });

        holder.modifyButton.setOnClickListener(v -> {
            ModifySlotDialogFragment dialogFragment = ModifySlotDialogFragment.newInstance(item.getSlotId(), item.getVaccineType());
            dialogFragment.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "ModifySlotDialog");
        });
    }


    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class VaccineSlotViewHolder extends RecyclerView.ViewHolder {
        TextView summaryTextView;
        TextView detailsTextView;
        Button modifyButton;

        public VaccineSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            modifyButton = itemView.findViewById(R.id.modifyButton);
        }
    }
}
