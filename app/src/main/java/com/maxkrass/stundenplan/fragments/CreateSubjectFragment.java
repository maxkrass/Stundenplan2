package com.maxkrass.stundenplan.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.R;

public class CreateSubjectFragment extends Fragment {

    public View colorIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_subject, container, false);
        colorIcon = v.findViewById(R.id.color_icon);
        return v;
    }
}
