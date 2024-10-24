package com.example.amity_1;
import android.content.Context;
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
    private List<Patient> patientListFull; // For holding the original data
    private Context context;

    public PatientAdapter(List<Patient> patientList, Context context) {
        this.patientList = patientList;
        this.context = context;
        this.patientListFull = new ArrayList<>(patientList); // Create a copy of the original list
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.nameTextView.setText(patient.getName()); // Adjust as needed for your Patient class
        holder.checkupDateTextView.setText(patient.getCheckupDate()); // Adjust for your data
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    @Override
    public Filter getFilter() {
        return patientFilter;
    }

    private Filter patientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Patient> filteredPatients = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredPatients.addAll(patientListFull); // No filter, return original list
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Filter the list based on name, phone number, or address
                for (Patient patient : patientListFull) {
                    if (patient.getName().toLowerCase().contains(filterPattern) ||
                            patient.getPhoneNumber().toLowerCase().contains(filterPattern) || // Assuming you have a getPhone method
                            patient.getAddress().toLowerCase().contains(filterPattern)) { // Assuming you have a getAddress method
                        filteredPatients.add(patient);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredPatients;
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            patientList.clear();
            patientList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, checkupDateTextView;

        PatientViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.patientNameTextView); // Adjust based on your layout
            checkupDateTextView = itemView.findViewById(R.id.checkupDateTextView); // Adjust based on your layout
        }
    }
}
