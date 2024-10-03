package com.example.amity_1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> implements Filterable {
    private List<Patient> patientList;
    private List<Patient> patientListFull; // Full list for filtering
    private Context context;

    public PatientAdapter(List<Patient> patientList, Context context) {
        this.patientList = patientList;
        this.context = context;
        patientListFull = new ArrayList<>(patientList);
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient currentPatient = patientList.get(position);
        holder.nameTextView.setText(currentPatient.getName());
        holder.phoneTextView.setText(currentPatient.getPhone());

        // Set up click listener to view patient file
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientFileActivity.class);
            intent.putExtra("FILE_ID", currentPatient.getFileId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Patient> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(patientListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Patient patient : patientListFull) {
                        if (patient.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(patient);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                patientList.clear();
                patientList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;

        PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.patientNameTextView);
            phoneTextView = itemView.findViewById(R.id.patientPhoneTextView);
        }
    }
}
