package com.seakernel.stardroid;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.seakernel.stardroid.model.StardroidModel;

/**
 * A full-screen activity that shows and hides the system UI (i.e. status bar and navigation/system
 * bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener {

    // Fragment Tags
    private static final String START_FRAGMENT_TAG = "start";
    private static final String GAME_FRAGMENT_TAG = "game";
    private static final String OVER_FRAGMENT_TAG = "over";

    // Animation Constants
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int UI_INITIAL_ANIMATION_DELAY = 100;

    // Member variables
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

    // =============================================================================================
    // Activity Methods
    // =============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register for system control UI changes (Necessary for Now on Tap)
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);

        // Set the view for the activity
        setContentView(R.layout.activity_game);

        // Load the view with the start fragment on top of the game fragment (so we can see the cool stars)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.game_surface, GameFragment.newInstance(), GAME_FRAGMENT_TAG);
        transaction.add(R.id.game_overlay, StartFragment.newInstance(), START_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hideSystemControls() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(UI_INITIAL_ANIMATION_DELAY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        // TODO: Serialize the model and save it

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        // TODO: Read in the serialized model data
    }

    @Override
    public void onBackPressed() {
        StardroidModel model = StardroidModel.getInstance();
        if (model.isGameRunning()) {
            if (model.isPaused()) {
                // If the game is running and paused, then we're going back to the start screen
                model.resetState();
            } else {
                // If we're in the game and not paused, pause the game
                model.setPaused(true);
            }
        } else {
            // TODO: Popup asking 'are you sure exit' if at the top of the back stack?
            super.onBackPressed();
        }
    }

    // =============================================================================================
    // OnSystemUiVisibilityChangeListener Methods
    // =============================================================================================

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        // On system visibility change, we need to see if we are displaying system controls
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            // If system controls are visible (status bar, home, back, recent) then hideSystemControls them
            hideSystemControls();
        }
    }

    // =============================================================================================
    // Helper Methods
    // =============================================================================================

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

    public void onPlayClicked(View view) {
        // Hide the start fragment so that the game can show unobstructed
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(manager.findFragmentByTag(START_FRAGMENT_TAG));
        transaction.addToBackStack(null);
        transaction.commit();

        // Start the game model
        StardroidModel.getInstance().startGame();
    }
}
