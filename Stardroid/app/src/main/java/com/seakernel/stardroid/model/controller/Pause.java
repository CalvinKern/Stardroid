package com.seakernel.stardroid.model.controller;

import android.opengl.Matrix;

import com.seakernel.stardroid.model.StardroidModel;

/**
 * Created by Calvin on 2/28/16.
 */
public class Pause extends StardroidShape { // TODO: Implement StardroidDrawable rather than inherit from the shape and have all the extra cruft

    private static final float POS = 0.9f;
    private static final float WIDTH = 0.05f;
    private static final float HEIGHT_SCALE = (2.0f / 3.0f) * (3.5f);
    private static final float MINI_SIZE_SCALE = 0.5f;

    private final float[] mBounds;
    private final PauseBar mLeftBar = new PauseBar(0);
    private final PauseBar mRightBar = new PauseBar(3.0f * WIDTH);

    /**
     * @param bounds left, top, right, bottom
     */
    public Pause(final float[] bounds) {
        super();

        mBounds = bounds;
    }

    @Override
    protected void initialize() {}

    @Override
    protected float[] getCoordinates() {
        return new float[] {
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f
        };
    }

    @Override
    protected void draw(final float[] mvpMatrix, final float dt) {
        final int gameState = StardroidModel.getInstance().getState();
        if (gameState != StardroidModel.GameState.RUNNING && gameState != StardroidModel.GameState.PAUSED) {
            return;
        }

        // If running, draw in the top right
        if (gameState == StardroidModel.GameState.RUNNING) {
            Matrix.translateM(mvpMatrix, 0, mBounds[2] - (WIDTH * 4), mBounds[1] - (WIDTH * HEIGHT_SCALE), 0);
            Matrix.scaleM(mvpMatrix, 0, MINI_SIZE_SCALE, MINI_SIZE_SCALE, 0.0f);
        }

        mRightBar.doDraw(mvpMatrix, dt);
        mLeftBar.doDraw(mvpMatrix, dt);
    }

    private static class PauseBar extends StardroidShape {

        private final float mTranslationX;

        PauseBar(final float translationX) {
            super();
            mTranslationX = translationX;
        }

        @Override
        protected void initialize() {
            mColor = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
        }

        @Override
        protected float[] getCoordinates() {
            final float width = WIDTH;
            final float height = WIDTH * HEIGHT_SCALE;
            return new float[] {
                    -width, -height, 0.0f, // bottom left
                    width, -height, 0.0f,  // bottom right
                    -width, height, 0.0f, // top left
                    width, height, 0.0f,  // top right
            };
        }

        @Override
        protected void draw(final float[] mvpMatrix, final float dt) {
            // TODO: Get the right width that should be between the pause bars by considering the eye position?
            Matrix.translateM(mvpMatrix, 0, mTranslationX, 0.0F, 0.0f);
        }
    }
}
