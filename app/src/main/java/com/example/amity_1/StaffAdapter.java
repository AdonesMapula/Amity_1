package com.example.amity_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {
    private List<Staff> staffList;

    public StaffAdapter(List<Staff> staffList) {
        this.staffList = staffList;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_item, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.nameTextView.setText(staff.getName());
        holder.positionTextView.setText(staff.getPosition());
        holder.imageView.setImageResource(staff.getImageResource());
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView positionTextView;
        ImageView imageView;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.staff_name);
            positionTextView = itemView.findViewById(R.id.staff_position);
            imageView = itemView.findViewById(R.id.staff_image);
        }
    }
}
