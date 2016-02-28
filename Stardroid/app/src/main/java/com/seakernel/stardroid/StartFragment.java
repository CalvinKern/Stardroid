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
public class StartFragment extends Fragment implements View.OnClickListener {

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

        root.findViewById(R.id.start_settings).setOnClickListener(this);
        root.findViewById(R.id.start_settings_close).setOnClickListener(this);

        LinearLayout titleContainer = (LinearLayout) root.findViewById(R.id.start_title_container);

        // Get the player records to display
        List<Utilities.PlayerRecord> records = Utilities.readPlayerInfo(getActivity());
        if (records != null) {
            TextView playerRecord;
            for (int i = 0; i < records.size(); i++) {
                // Create the player record view and display the records information
                playerRecord = (TextView) inflater.inflate(R.layout.view_record_player, titleContainer, false);
                playerRecord.setText(records.get(i).toString());

                // Add the record to the container
                titleContainer.addView(playerRecord);
            }
        }
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_settings:
                showSettings();
                break;
            case R.id.start_settings_close:
                closeSettings();
                break;
        }
    }

    private void showSettings() {
        changeSettingsVisibility(View.VISIBLE);
    }

    private void closeSettings() {
        changeSettingsVisibility(View.GONE);
    }

    /**
     * Changes the visibility of the settings pane by animating it in or out.
     *
     * @param visibility {@link View#VISIBLE} or {@link View#INVISIBLE} or {@link View#GONE}
     */
    private void changeSettingsVisibility(final int visibility) {
        // If we don't have an activity or we are detached, return early
        if (getActivity() == null || isDetached()) {
            return;
        }

        // Find the view
        View view = getActivity().findViewById(R.id.start_settings_container);
        if (view.getVisibility() == visibility) {
            // If the view is already the requested visibility, we're done
            return;
        }

        // Determine the new visibility
        if (visibility == View.VISIBLE) {
            // Animate the view in
            view.setAlpha(0);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1);
        } else {
            // Animate the view out
            view.animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    // Once the view has animated alpha out, set the visibility to gone or invisible (as requested)
                    if (getActivity() == null || isDetached()) {
                        return;
                    }

                    View view = getActivity().findViewById(R.id.start_settings_container);
                    view.setVisibility(visibility);
                }
            });
        }
    }
}
