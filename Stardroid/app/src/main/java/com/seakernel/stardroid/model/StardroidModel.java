package com.seakernel.stardroid.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 2/27/2017.
 */

public class StardroidModel {

    public interface GameStateChangeWatcher {
        void onStateChanged(@GameState int newState);
    }

    // Static Variables
    @Retention(RetentionPolicy.SOURCE)
    @IntDef
    public @interface GameState {
        int BLANK = 0;
        int RUNNING = 1;
        int PAUSED = 2;
        int END = 3;
    }

    private static StardroidModel mModel;

    public static StardroidModel getInstance() {
        if (mModel == null) {
            mModel = new StardroidModel();
        }
        return mModel;
    }

    // Instance Variables

    @GameState
    private int mState;
    private List<GameStateChangeWatcher> mGameStateChangeWatchers;

    private StardroidModel() {
        mGameStateChangeWatchers = new ArrayList<>();

        resetState();
    }

    public boolean addGameStateChangeWatcher(GameStateChangeWatcher watcher) {
        return mGameStateChangeWatchers.add(watcher);
    }

    public boolean removeGameStateChangeWatcher(GameStateChangeWatcher watcher) {
        return mGameStateChangeWatchers.remove(watcher);
    }

    private void updateGameStateChangeWatchers() {
        for (GameStateChangeWatcher watcher : mGameStateChangeWatchers) {
            watcher.onStateChanged(mState); // TODO: Check for null watcher?
        }
    }

    @GameState
    public int getState() {
        return mState;
    }

    public void resetState() {
        mState = GameState.BLANK;

        // TODO: Reset the rest of the game state

        updateGameStateChangeWatchers();
    }

    public boolean isPaused() {
        return mState == GameState.PAUSED;
    }

    public void setPaused(boolean isPaused) {
        @GameState int oldState = mState;
        mState = isPaused ? GameState.PAUSED : GameState.RUNNING;

        if (oldState != mState) {
            updateGameStateChangeWatchers();
        }
    }

    public boolean isGameRunning() {
        return mState == GameState.RUNNING;
    }

    public void startGame() {
        mState = GameState.RUNNING;

        // TODO: Any other setup

        updateGameStateChangeWatchers();
    }

    public void endGame() {
        mState = GameState.END;

        // TODO: Any other wrap up

        updateGameStateChangeWatchers();
    }
}
