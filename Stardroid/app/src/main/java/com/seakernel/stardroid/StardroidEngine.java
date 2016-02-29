package com.seakernel.stardroid;

import com.seakernel.stardroid.model.StardroidShape;
import com.seakernel.stardroid.model.StardroidStar;

import java.util.ArrayList;

/**
 * This Class performs as the sprite engine that draws all required elements for the Stardroid game
 *
 * Created by Calvin on 2/27/16.
 */
public class StardroidEngine {

    // Member Variables
    private ArrayList<StardroidStar> mStars = null;

    /**
     * This method initializes all necessary data structures and GLES20
     */
    public void initializeScreen(float screenRatio) {

        // initialize the stars in the background for the start of the game
        initializeStars(screenRatio);
    }

    /**
     * This helper method initializes the list of stars for the beginning of the game
     */
    private void initializeStars(float screenRatio) {
        mStars = new ArrayList<>();
        if (mStars.size() == 0) {
//            int randCount = 1;
            int randCount = 300;//(int)(Math.random() * 20) + 280;

            for (int i = 0; i < randCount; i++) {
                float randX = (float)(Math.random() * screenRatio * 2) - screenRatio;
                float randY = (float)(Math.random() * screenRatio * 2) - screenRatio;

                StardroidStar star = new StardroidStar(randX,randY);
                mStars.add(star);
            }
        }
    }

    public void setTextures() {
    }

    private StardroidShape shape;

    public void draw(float[] mvpMatrix, float screenRatio) {
        // Draw the stars in the background
        drawStars(mvpMatrix, screenRatio);
    }

    private void drawStars(float[] mvpMatrix, float screenRatio) {
        // randomly add new stars to the background
        if (Math.random() * 100 <= 15) {
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
}