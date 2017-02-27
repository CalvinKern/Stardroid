package com.seakernel.stardroid.model;

/**
 * Created by Calvin on 2/27/2017.
 */

public class StardroidModel {

    // Static Variables

    private static StardroidModel mModel;

    public static StardroidModel getInstance() {
        if (mModel == null) {
            mModel = new StardroidModel();
        }
        return mModel;
    }

    // Instance Variables

    private boolean mIsPaused;
    private boolean mIsGameRunning;

    private StardroidModel() {
        resetState();
    }

    public void resetState() {
        mIsPaused = false;
        mIsGameRunning = false;
        // TODO: Reset the rest of the game state
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    public void setPaused(boolean isPaused) {
        mIsPaused = isPaused;
    }

    public boolean isGameRunning() {
        return mIsGameRunning;
    }

    public void startGame() {
        mIsGameRunning = true;
        // TODO: Any other setup
    }

    public void stopGame() {
        mIsGameRunning = false;
        // TODO: Any other wrap up
    }
}
