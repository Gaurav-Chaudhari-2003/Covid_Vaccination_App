package com.gaurav.covidvaccinationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private List<Slot> slotList;
    private OnSlotClickListener onSlotClickListener;

    public interface OnSlotClickListener {
        void onSlotClick(Slot slot);
    }

    public SlotAdapter(List<Slot> slotList, OnSlotClickListener onSlotClickListener) {
        this.slotList = slotList;
        this.onSlotClickListener = onSlotClickListener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot slot = slotList.get(position);
        holder.vaccineNameTextView.setText(slot.getVaccineType());
        holder.dateTextView.setText(slot.getDate());
        holder.timeTextView.setText(slot.getTime());
        holder.locationTextView.setText(slot.getLocation());

        holder.itemView.setOnClickListener(v -> onSlotClickListener.onSlotClick(slot));
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView vaccineNameTextView, dateTextView, timeTextView, locationTextView;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            vaccineNameTextView = itemView.findViewById(R.id.vaccineNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
        }
    }
}