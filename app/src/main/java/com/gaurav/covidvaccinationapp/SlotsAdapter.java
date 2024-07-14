package com.gaurav.covidvaccinationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.SlotViewHolder> {

    private List<Slot> slotList;

    public SlotsAdapter(List<Slot> slotList) {
        this.slotList = slotList;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_item, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot slot = slotList.get(position);
        holder.bind(slot);
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotId, tvDate, tvTime;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotId = itemView.findViewById(R.id.tvSlotId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Slot slot) {
            tvSlotId.setText(slot.getSlotId());
            tvDate.setText(slot.getDate());
            tvTime.setText(slot.getTime());
        }
    }
}
