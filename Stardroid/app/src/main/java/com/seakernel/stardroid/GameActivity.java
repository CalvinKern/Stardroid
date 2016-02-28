package com.seakernel.stardroid;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener, GameFragment.OnPauseStateChangeListener {

    // Fragment Tags
    private static final String START_FRAGMENT_TAG = "start";
    private static final String GAME_FRAGMENT_TAG = "game";
    private static final String OVER_FRAGMENT_TAG = "over";

    // Save Keys
    private static final String IN_GAME_KEY = "game";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private boolean mInGame; // Keep track of game state, may switch to find view by id
    private boolean mIsPaused;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideSystemControls();
        }
    };
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            if (getWindow() == null || getWindow().getDecorView() == null) {
                return;
            }

            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register for system control UI changes (Necessary for Now on Tap)
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);

        // Set the view for the activity
        setContentView(R.layout.activity_game);

        // Pull out any saved state
        if (savedInstanceState != null) {
            mInGame = savedInstanceState.getBoolean(IN_GAME_KEY, mInGame);
        }

        // Load the view with the start fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.activity_frame, StartFragment.newInstance(), START_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hideSystemControls() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(IN_GAME_KEY, mInGame);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        mInGame = savedInstanceState.getBoolean(IN_GAME_KEY, mInGame);
    }

    @Override
    public void onBackPressed() {
        // If we're in the game or not paused, pause the game
        if (mInGame && !mIsPaused) {
            GameFragment fragment = (GameFragment) getFragmentManager().findFragmentByTag(GAME_FRAGMENT_TAG);
            if (fragment != null) {
                fragment.onPauseClick();
            }
        } else {
            // TODO: Popup asking 'are you sure exit'?
            mInGame = false; // We are no longer in a game state if we call super on back press
            super.onBackPressed();
        }
    }

    private void hideSystemControls() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hideSystemControls() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        // On system visibility change, we need to see if we are displaying system controls
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            // If system controls are visible (status bar, home, back, recent) then hideSystemControls them
            hideSystemControls();
        }
    }

    public void onPlayClicked(View view) {
        mInGame = true; // We are starting the game

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_frame, GameFragment.newInstance(), GAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPauseStateChanged(boolean paused) {
        mIsPaused = paused;
    }
}
