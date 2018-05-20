package com.seakernel.stardroid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Calvin on 2/27/16.
 */
public class StartOverlayFragment extends Fragment implements View.OnClickListener {

    private static final int INTENT_SIGN_IN = 100;
    private static final int INTENT_LEADERBOARD = 101;
    private static final int INTENT_ACHIEVEMENTS = 102;

    public static StartOverlayFragment newInstance() {
        Bundle args = new Bundle();
        StartOverlayFragment fragment = new StartOverlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        signInPreviousUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_start, container, false);

        root.findViewById(R.id.start_settings).setOnClickListener(this);
        root.findViewById(R.id.start_settings_close).setOnClickListener(this);
        root.findViewById(R.id.settings_leaderboard).setOnClickListener(this);
        root.findViewById(R.id.settings_achievements).setOnClickListener(this);
        root.findViewById(R.id.start_google_services_sign_in).setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == INTENT_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onAccountConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onAccountDisconnected();

                new AlertDialog.Builder(getActivity()) // TODO: getActivity safety checks
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_settings: {
                showSettings();
                break;
            }
            case R.id.start_settings_close: {
                closeSettings();
                break;
            }
            case R.id.start_google_services_sign_in: {
                googlePlaySignIn();
                break;
            }
            case R.id.settings_leaderboard: {
                showLeaderboard();
                break;
            }
            case R.id.settings_achievements: {
                showAchievements();
                break;
            }
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

    private GoogleSignInClient getGoogleSignInClient() {
        return GoogleSignIn.getClient(getActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     *
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInPreviousUser() {
        if (getActivity() == null || !isResumed()) {
            return;
        }

        getGoogleSignInClient().silentSignIn().addOnCompleteListener(getActivity(),
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            onAccountConnected(task.getResult());
                        } else {
                            onAccountDisconnected();
                        }
                    }
                });
    }

    private void googlePlaySignIn() {
        if (getActivity() == null || !this.isResumed()) {
            return;
        }

        startActivityForResult(getGoogleSignInClient().getSignInIntent(), INTENT_SIGN_IN);
    }

    private void onAccountDisconnected() {
        getView().findViewById(R.id.start_google_services_sign_in).setVisibility(View.VISIBLE);
    }

    private void onAccountConnected(GoogleSignInAccount account) {
        GamesClient client = Games.getGamesClient(getActivity(), account);
        client.setViewForPopups(getView());

        getView().findViewById(R.id.start_google_services_sign_in).setVisibility(View.GONE);
    }

    private void showLeaderboard() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            return;
        }

        Games.getLeaderboardsClient(activity, account)
                .getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, INTENT_LEADERBOARD);
                    }
                });
    }

    private void showAchievements() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            return;
        }

        Games.getAchievementsClient(activity, account)
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, INTENT_ACHIEVEMENTS);
                    }
                });
    }
}
