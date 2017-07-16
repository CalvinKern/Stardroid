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

    private static final int MAGIC_MAX_COUNT_STAR = 169; // REDUCE this to improve FPS ;)
    private static final int MAGIC_MAX_COUNT_OBJECTS = 800; // This is the max for 60 fps on a good phone

    // Member Variables

    private float mAspectRatio;
    private StardroidPause mPauseSprite = null;
    private ArrayList<StardroidStar> mStars = null;

    /**
     * @return total number of objects tracked for drawing
     */
    public int getObjectCount() {
        return mStars.size();
    }

    /**
     * This method initializes all necessary data structures and GLES20
     */
    public void initializeScreen(float aspectRatio) {
        mAspectRatio = aspectRatio;

        // initialize the stars in the background for the start of the game
        initializeStars();
        mPauseSprite = new StardroidPause();
    }

    /**
     * This helper method initializes the list of stars for the beginning of the game
     */
    private void initializeStars() {
        mStars = new ArrayList<>();
        if (mStars.size() == 0) {
            for (int i = 0; i < MAGIC_MAX_COUNT_STAR; i++) {
                StardroidStar star = new StardroidStar(getRandomPointOnScreen(), getRandomPointOnScreen());
                mStars.add(star);
            }
        }
    }

    private float getRandomPointOnScreen() {
        return (float) (Math.random() * mAspectRatio * 2) - mAspectRatio;
    }

    public void setTextures() { /*TODO:...Once I have something to texture*/ }

    public void draw(float[] mvMatrix) {
        drawStars(mvMatrix); // First so it can go in the background

        drawPause(mvMatrix);

        // TODO: Draw the rest of the game
    }

    private void drawStars(float[] mvMatrix) {
        // TODO: Speed up by moving create/destroy to a background thread (in model?)

        // randomly add new stars to the background
        if (mStars.size() < MAGIC_MAX_COUNT_STAR && Math.random() * 100 <= 25) {
            float halfRatio = mAspectRatio / 2;
            float randY = (float)(Math.random() * mAspectRatio) - halfRatio;
            StardroidStar newStar = new StardroidStar(-mAspectRatio, randY);
            mStars.add(newStar);
        }

        // Create a new ArrayList to put all of the passed stars
        ArrayList<StardroidStar> passedStars = new ArrayList<>();

        // Loop through all the stars in the background
        for (StardroidStar star : mStars) {
            star.update(0);

            // Determine if the star has passed the screen
            if (star.getPositionX() >= mAspectRatio) {
                passedStars.add(star);
                continue;
            }

            star.draw(mvMatrix);
        }

        // Remove stars that have passed
        for (StardroidStar oldStar : passedStars) {
            mStars.remove(oldStar);
        }
    }

    /**
     * @param mvMatrix
     * @return true if the game is paused
     */
    private boolean drawPause(float[] mvMatrix) {
        // If paused, only draw the
        if (StardroidModel.getInstance().isPaused()) {
            mPauseSprite.draw(mvMatrix); // TODO: Change this to draw the resume button (that also states paused)
            return true; // Return here if we are paused so we don't keep drawing everything else
        } else {
            // TODO: Modify mvp to draw pause in the (top-right?) corner.
            final float[] copyMv = new float[mvMatrix.length];
            System.arraycopy(mvMatrix, 0, copyMv, 0, mvMatrix.length); // TODO: in place state saving of mvMatrix for efficiency?

            // TODO: draw once the mvp is correct
//            mPauseSprite.draw(copyMv);
            return false;
        }
    }
}
