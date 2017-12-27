package com.seakernel.stardroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seakernel.stardroid.model.StardroidModel;

/**
 * Created by Calvin on 12/17/17.
 * Copyright © 2017 SeaKernel. All rights reserved.
 */

public class EndOverlayFragment extends Fragment {

    public static EndOverlayFragment newInstance() {
        Bundle args = new Bundle();
        EndOverlayFragment fragment = new EndOverlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overlay_end, container, false);

        ((TextView) root.findViewById(R.id.end_score)).setText(getString(R.string.score, StardroidModel.getInstance().getScore()));
        return root;
    }
}
