package com.seakernel.stardroid.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Calvin on 2/27/2017.
 */

public class StardroidModel {

    // Static Variables
    @Retention(RetentionPolicy.SOURCE)
    @IntDef
    public @interface GameState {
        int BLANK = 0;
        int RUNNING = 1;
        int PAUSED = 2;
        int DIED = 3;
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

    private StardroidModel() {
        resetState();
    }

    public void resetState() {
        mState = GameState.BLANK;
        // TODO: Reset the rest of the game state
    }

    @GameState
    public int getState() {
        return mState;
    }

    public boolean isPaused() {
        return mState == GameState.PAUSED;
    }

    public void setPaused(boolean isPaused) {
        mState = isPaused ? GameState.PAUSED : GameState.RUNNING;
    }

    public boolean isGameRunning() {
        return mState == GameState.RUNNING;
    }

    public void startGame() {
        mState = GameState.RUNNING;
        // TODO: Any other setup
    }

    public void stopGame() {
        mState = GameState.BLANK;
        // TODO: Any other wrap up
    }
}
