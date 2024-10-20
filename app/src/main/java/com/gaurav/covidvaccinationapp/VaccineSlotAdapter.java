package com.gaurav.covidvaccinationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class VaccineSlotAdapter extends RecyclerView.Adapter<VaccineSlotAdapter.VaccineSlotViewHolder> {

    private final List<VaccineSlotItem> slotList;
    private final FirebaseFirestore firestore;  // Add Firestore instance

    // Constructor to pass slot list and Firestore instance
    public VaccineSlotAdapter(List<VaccineSlotItem> slotList, FirebaseFirestore firestore) {
        this.slotList = slotList;
        this.firestore = firestore;  // Initialize Firestore
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

        // Show modify button logic
        holder.modifyButton.setVisibility(item.isShowModifyButton() ? View.VISIBLE : View.GONE);

        // Show delete button logic
        boolean canDelete = item.isShowModifyButton(); // Slot is outdated or current and not booked
        holder.deleteButton.setVisibility(canDelete ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });

        holder.modifyButton.setOnClickListener(v -> {
            ModifySlotDialogFragment dialogFragment = ModifySlotDialogFragment.newInstance(item.getSlotId(), item.getVaccineType());
            dialogFragment.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "ModifySlotDialog");
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteSlot(item.getSlotId(), item.getVaccineType(), position, holder);
        });
    }

    // Modify the deleteSlot method to accept the view holder as well
    private void deleteSlot(String slotId, String vaccineType, int position, VaccineSlotViewHolder holder) {
        // Get the reference to the collection based on the vaccine type
        CollectionReference slotRef = firestore.collection("slots").document("vaccine").collection(vaccineType);

        // Delete the document from Firestore
        slotRef.document(slotId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(holder.itemView.getContext(), "Slot deleted successfully", Toast.LENGTH_SHORT).show();
                    // Remove the item from the list and notify the adapter
                    slotList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(), "Error deleting slot", Toast.LENGTH_SHORT).show();
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
        Button deleteButton;

        public VaccineSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            modifyButton = itemView.findViewById(R.id.modifyButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
