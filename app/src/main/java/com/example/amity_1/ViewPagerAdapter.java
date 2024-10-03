package com.example.amity_1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyGraphFragment(); // Fragment for daily graph
            case 1:
                return new WeeklyGraphFragment(); // Fragment for weekly graph
            case 2:
                return new MonthlyGraphFragment(); // Fragment for monthly graph
            default:
                return new DailyGraphFragment(); // Default to daily graph
        }
    }

    @Override
    public int getItemCount() {
        return 3; // We have 3 graphs (daily, weekly, monthly)
    }
}
