package com.seakernel.stardroid.model;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Created by Calvin on 2/28/16.
 */
public class StardroidStar extends StardroidShape {
    // Fragment Shader keys
    private static final String COLOR_TWINKLE_VARYING = "vColorTwinkle";

    // Constants

    private static final double TWINKLE_MAX = 0.3;
    private static final double TWINKLE_CHANCE = 0.65;

    private static final float HALF_SIZE = 0.0075f;
    private static final float SPEED_SCALE = 10000.f;

    private float mPositionX;
    private float mPositionY;
    private float mFloatingSpeed;
    private int mColorTwinkleHandle;

    protected static final String COLOR_TWINKLE_FRAGMENT_SHADER_SOURCE =
            "precision mediump float;"
                    + "uniform vec4 " + COLOR_VARYING + ";"
                    + "uniform vec4 " + COLOR_TWINKLE_VARYING + ";"
                    + "void main() {"
                    + "  gl_FragColor = " + COLOR_VARYING + "*" + COLOR_TWINKLE_VARYING + ";"
                    + "}";

    public StardroidStar (float x, float y) {
        super();

        // TODO: This could be internally monitored in the background to speed up drawing \0/ for multi-threading
        mPositionX = x;
        mPositionY = y;
    }

    @Override
    protected void initialize() {
        mColor = getStarColor();
        double rand = Math.random(); // TODO: Bias the random number to be smaller
        mFloatingSpeed = (float)(rand / SPEED_SCALE);
    }

    @Override
    protected void onCreateProgramHandles(final int PROGRAM) {
        super.onCreateProgramHandles(PROGRAM);

        // get handle to fragment shader's vColor member
        mColorTwinkleHandle = GLES20.glGetUniformLocation(PROGRAM, COLOR_TWINKLE_VARYING);
    }

    @Override
    protected String getFragmentShader() {
        return COLOR_TWINKLE_FRAGMENT_SHADER_SOURCE;
    }

    /**
     * Makes the color for the star an applicable star color
     *
     * http://oneminuteastronomer.com/708/star-colors-explained/
     *
     * @return a 16 byte color as a 4 part float array
     */
    private float[] getStarColor() {
        int colorClass = (int)(Math.random() * 15);

        // Stars range between around 7 colors, hottest to coldest
        switch (colorClass) {
            case 0:
                return OpenGlColors.STAR_BLUE; // Blue is the hottest
            case 1:
            case 2:
                return OpenGlColors.STAR_WHITE_BLUE; // White Blue is next
            case 3:
            case 4:
                return OpenGlColors.STAR_LIGHTER_WHITE_BLUE; // White Blue is next
            case 5:
            case 6:
            case 7:
            case 8:
            default:
                return OpenGlColors.STAR_WHITE; // Return a white star default
            case 9:
            case 10:
                return OpenGlColors.STAR_WHITE_YELLOW; // White Yellow
            case 11:
            case 12:
                return OpenGlColors.STAR_YELLOW_ORANGE; // Yellow Orange
            case 13:
            case 14:
                return OpenGlColors.STAR_ORANGE_RED; // Orange Red
        }
    }

    public void update(float dt) {
        // Fixed time step
//        setPositionX(getPositionX() + (getStarFloatingSpeed() * 10));

        // Variable time step
        setPositionX(getPositionX() + (getStarFloatingSpeed() * dt));
    }

    @Override
    public void draw(float[] mvpMatrix) {
        mMvpMatrix = mvpMatrix.clone();

        Matrix.translateM(mMvpMatrix, 0, mPositionX, mPositionY, 0.0f);
//        Matrix.rotateM(mMvpMatrix, 0, 0.0f, 0.0f, 0.0f, 1.0f);

        float offset = Math.random() < TWINKLE_CHANCE ? 1.f : 1 - (float)(Math.random() * TWINKLE_MAX);
        float[] twinkleOffset = new float[] {offset, offset, offset, 1.f};

        // Set mColor for drawing the triangle
        GLES20.glUniform4fv(mColorTwinkleHandle, 1, twinkleOffset, 0);

        super.draw(mMvpMatrix);
    }

    @Override
    protected float[] getCoordinates() {
        float halfSize = HALF_SIZE * (mFloatingSpeed * SPEED_SCALE);
        return new float[] {
                -halfSize, -halfSize, 0.0f, // bottom left
                halfSize, -halfSize, 0.0f,  // bottom right
                -halfSize,  halfSize, 0.0f, // top left
                halfSize,  halfSize, 0.0f,  // top right
        };
    }

    public float getStarFloatingSpeed() {
        return mFloatingSpeed;
    }

    public float getPositionX() {
        return mPositionX;
    }

    public float getPositionY() {
        return mPositionY;
    }

    public void setPositionX(float x) {
        mPositionX = x;
    }
}
