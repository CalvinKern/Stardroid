package com.seakernel.stardroid;

import com.seakernel.stardroid.model.StardroidModel;
import com.seakernel.stardroid.model.StardroidPause;
import com.seakernel.stardroid.model.StardroidStar;

import java.util.ArrayList;

/**
 * This Class performs as the sprite engine that draws all required elements for the Stardroid game
 *
 * Created by Calvin on 2/27/16.
 */
public class StardroidEngine {

    private static final int STAR_MAX_COUNT_MAGIC = 169; // REDUCE this to improve FPS ;)

    // Member Variables
    private ArrayList<StardroidStar> mStars = null;
    private StardroidPause mPauseSprite = null;

    /**
     * This method initializes all necessary data structures and GLES20
     */
    public void initializeScreen(float screenRatio) {

        // initialize the stars in the background for the start of the game
        initializeStars(screenRatio);
        mPauseSprite = new StardroidPause();
    }

    /**
     * This helper method initializes the list of stars for the beginning of the game
     */
    private void initializeStars(float screenRatio) {
        mStars = new ArrayList<>();
        if (mStars.size() == 0) {
            int randCount = STAR_MAX_COUNT_MAGIC;

            for (int i = 0; i < randCount; i++) {
                float randX = (float)(Math.random() * screenRatio * 2) - screenRatio;
                float randY = (float)(Math.random() * screenRatio * 2) - screenRatio;

                StardroidStar star = new StardroidStar(randX,randY);
                mStars.add(star);
            }
        }
    }

    public void setTextures() { /*TODO:...Once I have something to texture*/ }

    public void draw(float[] mvpMatrix, float screenRatio) {
        drawStars(mvpMatrix, screenRatio); // First so it can go in the background

        drawPause(mvpMatrix);

        // TODO: Draw the rest of the game
    }

    private void drawStars(float[] mvpMatrix, float screenRatio) {
        // TODO: Speed up by moving create/destroy to a background thread (in model?)

        // randomly add new stars to the background
        if (mStars.size() < STAR_MAX_COUNT_MAGIC && Math.random() * 100 <= 25) {
            float halfRatio = screenRatio/2;
            float randY = (float)(Math.random() * screenRatio) - halfRatio;
            StardroidStar newStar = new StardroidStar(-screenRatio, randY);
            mStars.add(newStar);
        }

        // Create a new ArrayList to put all of the passed stars
        ArrayList<StardroidStar> passedStars = new ArrayList<>();

        // Loop through all the stars in the background
        for (StardroidStar star : mStars) {
            star.setPositionX(star.getPositionX() + star.getStarFloatingSpeed());

            // Determine if the star has passed the screen
            if (star.getPositionX() >= screenRatio) {
                passedStars.add(star);
                continue;
            }

            star.draw(mvpMatrix);
        }

        // Remove stars that have passed
        for (StardroidStar oldStar : passedStars) {
            mStars.remove(oldStar);
        }
    }

    /**
     * @param mvpMatrix
     * @return true if the game is paused
     */
    private boolean drawPause(float[] mvpMatrix) {
        // If paused, only draw the
        if (StardroidModel.getInstance().isPaused()) {
            mPauseSprite.draw(mvpMatrix); // TODO: Change this to draw the resume button (that also states paused)
            return true; // Return here if we are paused so we don't keep drawing everything else
        } else {
            // TODO: Modify mvp to draw pause in the top right? corner.
            final float[] copyMvp = new float[mvpMatrix.length];
            System.arraycopy(mvpMatrix, 0, copyMvp, 0, mvpMatrix.length); // TODO: in place state saving of mvpMatrix for efficiency?

            // TODO: draw once the mvp is correct
//            mPauseSprite.draw(copyMvp);
            return false;
        }
    }

    /**
     * @return total number of objects tracked for drawing
     */
    public int getObjectCount() {
        return mStars.size();
    }
}
