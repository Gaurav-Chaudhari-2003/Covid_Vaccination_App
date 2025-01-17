package com.gaurav.covidvaccinationapp;

import android.content.Context;
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

    private final Context context;
    private final List<Map<String, Object>> userList;

    public UserAdapter(Context context, List<Map<String, Object>> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
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
                    String covaxinStatus = (String) covaxin.get("status");
                    covaxinStatusTextView.setText("Covaxin: " + covaxinStatus);

                    if ("booked".equals(covaxinStatus)) {
                        covaxinStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    } else if ("vaccinated".equals(covaxinStatus)) {
                        covaxinStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                    } else {
                        covaxinStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    }
                } else {
                    covaxinStatusTextView.setText("Covaxin: Not vaccinated");
                    covaxinStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                }

                Map<String, Object> covishield = (Map<String, Object>) vaccinated.get("covishield");
                if (covishield != null) {
                    String covishieldStatus = (String) covishield.get("status");
                    covishieldStatusTextView.setText("Covishield: " + covishieldStatus);

                    if ("booked".equals(covishieldStatus)) {
                        covishieldStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    } else if ("vaccinated".equals(covishieldStatus)) {
                        covishieldStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                    } else {
                        covishieldStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    }
                } else {
                    covishieldStatusTextView.setText("Covishield: Not vaccinated");
                    covishieldStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                }
            } else {
                covaxinStatusTextView.setText("Covaxin: Not vaccinated");
                covishieldStatusTextView.setText("Covishield: Not vaccinated");
                covaxinStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                covishieldStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
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
