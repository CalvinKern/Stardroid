package com.seakernel.stardroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Calvin on 2/27/16.
 */
public class StartFragment extends Fragment {

    public static StartFragment newInstance() {
        Bundle args = new Bundle();
        StartFragment fragment = new StartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_start, container, false);
        LinearLayout titleContainer = (LinearLayout) root.findViewById(R.id.start_title_container);

        List<Utilities.PlayerRecord> records = Utilities.readPlayerInfo(getActivity());
        TextView playerRecord;
        if (records != null) {
            for (int i = 0; i < records.size(); i++) {
                playerRecord = (TextView) inflater.inflate(R.layout.view_record_player, titleContainer, false);
                playerRecord.setText(records.get(i).toString());

                titleContainer.addView(playerRecord);
            }
        }
        return root;
    }
}
