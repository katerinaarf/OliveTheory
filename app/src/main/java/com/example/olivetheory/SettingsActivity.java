package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SettingsActivity extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        ImageButton user = view.findViewById(R.id.user);
        ImageButton settingButton = view.findViewById(R.id.settings);
        ImageButton user2 = view.findViewById(R.id.user2);
        ImageButton loc = view.findViewById(R.id.loc);
        ImageButton history = view.findViewById(R.id.history);
        ImageButton info = view.findViewById(R.id.info);
        TextView usertext = view.findViewById(R.id.usertext);
        TextView location = view.findViewById(R.id.location);
        TextView history_work = view.findViewById(R.id.history_work);
        TextView center_info = view.findViewById(R.id.center_info);
        TextView back = view.findViewById(R.id.back);

        user.setOnClickListener(v -> startNewActivity(UserProfile.class));
        user2.setOnClickListener(v -> startNewActivity(UserProfile.class));
        settingButton.setOnClickListener(v -> startNewActivity(SettingsActivity.class));
        loc.setOnClickListener(v -> startNewActivity(Location.class));
        history.setOnClickListener(v -> startNewActivity(HistoryWorkActivity.class));
        info.setOnClickListener(v -> startNewActivity(InfoActivity.class));
        usertext.setOnClickListener(v -> startNewActivity(UserProfile.class));
        location.setOnClickListener(v -> startNewActivity(Location.class));
        history_work.setOnClickListener(v -> startNewActivity(HistoryWorkActivity.class));
        center_info.setOnClickListener(v -> startNewActivity(InfoActivity.class));
        back.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }
}


