package com.seakernel.stardroid.model;

import android.support.annotation.IntDef;

import com.seakernel.stardroid.StardroidEngine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 2/27/2017.
 */

public class StardroidModel {

    public interface GameStateChangeWatcher {
        void onStateChanged(@GameState int oldState, @GameState int newState);
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
    private int mEnemiesDestroyed;

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

    private void updateGameStateChangeWatchers(int oldState, int newState) {
        for (GameStateChangeWatcher watcher : mGameStateChangeWatchers) {
            watcher.onStateChanged(oldState, newState); // TODO: Check for null watcher?
        }
    }

    private void setState(@GameState int state) {
        setState(mState, state);
    }

    private void setState(@GameState int oldState, @GameState int newState) {
        if (oldState != newState) {
            mState = newState;
            updateGameStateChangeWatchers(oldState, newState);
        }
    }

    @GameState
    public int getState() {
        return mState;
    }

    public void resetState() {
        // TODO: Reset the rest of the game state

        setState(GameState.BLANK);
    }

    public boolean isPaused() {
        return mState == GameState.PAUSED;
    }

    public void setPaused(boolean isPaused) {
        setState(isPaused ? GameState.PAUSED : GameState.RUNNING);
    }

    public boolean isGameRunning() {
        return mState == GameState.RUNNING;
    }

    public void startGame() {
        // TODO: Any other setup

        setState(mState, GameState.RUNNING);
    }

    public void endGame() {
        // TODO: Any other wrap up

        setState(mState, GameState.END);
    }

    public void resetScore() {
        mEnemiesDestroyed = 0;
    }

    public int getScore() {
        return mEnemiesDestroyed;
    }

    public void addScore(int toAdd) {
        mEnemiesDestroyed += toAdd;
    }
}
