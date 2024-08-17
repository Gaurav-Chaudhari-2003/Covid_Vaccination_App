package com.gaurav.covidvaccinationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<Map<String, Object>> userList;

    public UserAdapter(List<Map<String, Object>> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final LinearLayout detailsLayout;
        private final TextView emailTextView;
        private final TextView mobileTextView;
        private final TextView addressTextView;
        private final TextView pincodeTextView;
        private final TextView roleTextView;
        private final TextView covaxinStatusTextView;
        private final TextView covishieldStatusTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userNameTextView);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            emailTextView = itemView.findViewById(R.id.userEmailTextView);
            mobileTextView = itemView.findViewById(R.id.userMobileTextView);
            addressTextView = itemView.findViewById(R.id.userAddressTextView);
            pincodeTextView = itemView.findViewById(R.id.userPincodeTextView);
            roleTextView = itemView.findViewById(R.id.userRoleTextView);
            covaxinStatusTextView = itemView.findViewById(R.id.userCovaxinStatusTextView);
            covishieldStatusTextView = itemView.findViewById(R.id.userCovishieldStatusTextView);
        }

        public void bind(Map<String, Object> user) {
            nameTextView.setText((String) user.get("name"));
            emailTextView.setText((String) user.get("email"));
            mobileTextView.setText((String) user.get("mobile"));
            addressTextView.setText((String) user.get("address"));
            pincodeTextView.setText((String) user.get("pincode"));
            roleTextView.setText((String) user.get("role"));

            // Get vaccination status
            Map<String, Object> vaccinated = (Map<String, Object>) user.get("vaccinated");
            if (vaccinated != null) {
                Map<String, Object> covaxin = (Map<String, Object>) vaccinated.get("covaxin");
                if (covaxin != null) {
                    covaxinStatusTextView.setText("Covaxin: " + covaxin.get("status"));
                    covaxinStatusTextView.setTextColor(covaxin.get("status").equals("booked") ?
                            itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark) :
                            itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    covaxinStatusTextView.setText("Covaxin: Not vaccinated");
                    covaxinStatusTextView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                }

                Map<String, Object> covishield = (Map<String, Object>) vaccinated.get("covishield");
                if (covishield != null) {
                    covishieldStatusTextView.setText("Covishield: " + covishield.get("status"));
                    covishieldStatusTextView.setTextColor(covishield.get("status").equals("booked") ?
                            itemView.getContext().getResources().getColor(android.R.color.holo_green_dark) :
                            itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    covishieldStatusTextView.setText("Covishield: Not vaccinated");
                    covishieldStatusTextView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                }
            } else {
                covaxinStatusTextView.setText("Covaxin: Not vaccinated");
                covishieldStatusTextView.setText("Covishield: Not vaccinated");
                covaxinStatusTextView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                covishieldStatusTextView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

            itemView.setOnClickListener(v -> {
                // Toggle the visibility of the detailed information
                if (detailsLayout.getVisibility() == View.VISIBLE) {
                    detailsLayout.setVisibility(View.GONE);
                } else {
                    detailsLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
