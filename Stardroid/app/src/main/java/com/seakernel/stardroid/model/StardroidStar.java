package com.seakernel.stardroid.model;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Created by Calvin on 2/28/16.
 */
public class StardroidStar extends StardroidShape {
    // Fragment Shader keys
    public static final String COLOR_GLOWING_VARYING = "vColorGlowing";

    // Constants
    private static final int MIN_SIZE = 1;

    private static final double GLOW_MAX = 0.3;
    private static final double GLOW_CHANCE = 0.65;

    private static final float HALF_SIZE = 0.0075f;
    private static final float SIZE_SCALE = 1000.f;
    private static final float RAND_SIZE_SCALE = 3.5f;

    private static final float MAX_SIZE_RATIO = (RAND_SIZE_SCALE + MIN_SIZE) / SIZE_SCALE;

    private float mPositionX;
    private float mPositionY;
    private float mFloatingSpeed;
    private int mColorGlowingHandle;

    protected static final String COLOR_GLOWING_FRAGMENT_SHADER_SOURCE =
            "precision mediump float;"
                    + "uniform vec4 " + COLOR_VARYING + ";"
                    + "uniform vec4 " + COLOR_GLOWING_VARYING + ";"
                    + "void main() {"
                    + "  gl_FragColor = " + COLOR_VARYING + "*" + COLOR_GLOWING_VARYING + ";"
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
        double rand = Math.random();
        mFloatingSpeed = (float)((rand * RAND_SIZE_SCALE) + MIN_SIZE) / SIZE_SCALE;
    }

    @Override
    protected void onCreateProgramHandles(final int PROGRAM) {
        super.onCreateProgramHandles(PROGRAM);

        // get handle to fragment shader's vColor member
        mColorGlowingHandle = GLES20.glGetUniformLocation(PROGRAM, COLOR_GLOWING_VARYING);
    }

    @Override
    protected String getFragmentShader() {
        return COLOR_GLOWING_FRAGMENT_SHADER_SOURCE;
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

    @Override
    public void draw(float[] mvMatrix) {
        mMvMatrix = mvMatrix.clone();

        Matrix.translateM(mMvMatrix, 0, mPositionX, mPositionY, 0.0f);
//        Matrix.rotateM(mMvMatrix, 0, 0.0f, 0.0f, 0.0f, 1.0f);

        float offset = Math.random() < GLOW_CHANCE ? 1.f : 1 - (float)(Math.random() * GLOW_MAX);
        float[] glowOffset = new float[] {offset, offset, offset, 1.f};

        // Set mColor for drawing the triangle
        GLES20.glUniform4fv(mColorGlowingHandle, 1, glowOffset, 0);

        super.draw(mMvMatrix);
    }

    @Override
    protected float[] getCoordinates() {
        float size = HALF_SIZE * (mFloatingSpeed / MAX_SIZE_RATIO);
        return new float[] {
                -size, -size, 0.0f, // bottom left
                size, -size, 0.0f,  // bottom right
                -size,  size, 0.0f, // top left
                size,  size, 0.0f,  // top right
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
