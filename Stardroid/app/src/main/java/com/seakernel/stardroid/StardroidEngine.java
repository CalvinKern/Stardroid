package com.seakernel.stardroid;

import com.seakernel.stardroid.model.SpaceShip;
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
    public SpaceShip mUserShip = null;

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
        mUserShip = new SpaceShip();
    }

    public void receiveTouch(float normX, float normY) {
        normX = ( 2 * normX) - 1;
        normY = (-2 * normY) + 1;
        mUserShip.moveToPosition(normX * mAspectRatio, normY);

//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.d("GameFragment", String.format("Touch down at (%f, %f)", shipX, shipY));
//        }
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

    public void draw(float[] mvpMatrix, float dt) {
        drawStars(mvpMatrix, dt); // First so it can go in the background

        drawPause(mvpMatrix, dt);

        mUserShip.doDraw(mvpMatrix, dt);

        // TODO: Draw the rest of the game
    }

    private void drawStars(float[] mvpMatrix, float dt) {
        // Create a new ArrayList to put all of the passed stars
        ArrayList<StardroidStar> passedStars = new ArrayList<>();

        // Loop through all the stars in the background
        for (StardroidStar star : mStars) {

            // Determine if the star has passed the screen
            if (star.getPositionX() <= -mAspectRatio) {
//                Log.d("StardroidEngine", "Star dying (" + star.getPositionX() + "," + star.getPositionY() + ")");
                passedStars.add(star);
                continue;
            }

            star.doDraw(mvpMatrix, dt);
        }

        // Remove stars that have passed
        for (StardroidStar oldStar : passedStars) {
            mStars.remove(oldStar);
        }

        createStars();
    }

    private void createStars() {
        // TODO: Speed up by moving create/destroy to a background thread (in model?)
        // randomly add new stars to the background
        if (getObjectCount() < MAGIC_MAX_COUNT_OBJECTS && mStars.size() < MAGIC_MAX_COUNT_STAR && Math.random() * 100 <= 25) {
            // FIXME: 7/23/2017 instead of getting a random point from mAspectRatio, get it from mvp so that it can scale to the size of the actual play area, or maybe that's just how the cookie crumbles
            StardroidStar newStar = new StardroidStar(mAspectRatio, getRandomPointOnScreen());
            mStars.add(newStar);
        }
    }

    /**
     * @param mvpMatrix
     * @return true if the game is paused
     */
    private boolean drawPause(float[] mvpMatrix, float dt) {
        // If paused, only doDraw the
        if (StardroidModel.getInstance().isPaused()) {
            mPauseSprite.doDraw(mvpMatrix, dt); // TODO: Change this to doDraw the resume button (that also states paused)
            return true; // Return here if we are paused so we don't keep drawing everything else
        } else {
            // TODO: Modify mvp to doDraw pause in the (top-right?) corner.
//            final float[] copyMvp = new float[mvpMatrix.length];
//            System.arraycopy(mvpMatrix, 0, copyMvp, 0, mvpMatrix.length); // TODO: in place state saving of mvpMatrix for efficiency?

            // TODO: doDraw once the mvp is correct
//            mPauseSprite.doDraw(copyMvp);
            return false;
        }
    }
}
