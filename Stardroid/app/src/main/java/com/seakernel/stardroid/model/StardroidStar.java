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

    private static final double TWINKLE_MAX = 0.00001;
    private static final double TWINKLE_CHANCE = 0.65;

    private static final float SIZE_SCALE = 0.0075f;
    private static final float SPEED_SCALE = 5000.f;

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
        // FIXME: 7/23/2017 need to create the star with its start side at the end of the screen instead of its left side
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

    private void update(float dt) {
        // Fixed time step
//        setPositionX(getPositionX() -(getStarFloatingSpeed() * 10));

        // Variable time step
        setPositionX(getPositionX() - (getStarFloatingSpeed() * dt));
    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
        update(dt);

        mMvpMatrix = mvpMatrix.clone();

        Matrix.translateM(mMvpMatrix, 0, mPositionX, mPositionY, 0.0f);
//        Matrix.rotateM(mMvpMatrix, 0, 0.0f, 0.0f, 0.0f, 1.0f);

        //TODO: Make glow not so epileptic
        float offset = Math.random() < TWINKLE_CHANCE ? 1.f : 1 - (float)(Math.random() * TWINKLE_MAX);
        float[] twinkleOffset = new float[] {offset, offset, offset, 1.f};

        // Set mColor for drawing the triangle
        GLES20.glUniform4fv(mColorTwinkleHandle, 1, twinkleOffset, 0);

        super.draw(mMvpMatrix, dt);
    }

    @Override
    protected int getDrawMode() {
        return GLES20.GL_TRIANGLE_FAN;
    }

    @Override
    protected float[] getCoordinates() {
        float size = SIZE_SCALE * (mFloatingSpeed * SPEED_SCALE);

        int triangleAmount = 10; //# of triangles used to draw circle

        float twicePi = (float) (2.0f * Math.PI);

        float[] coords = new float[(triangleAmount * 3)];
        coords[0] = 0;
        coords[1] = 0;
        coords[2] = 0;
        for(int i = 3; i < coords.length; i+=3) {
            coords[i] =     (float) (size * Math.cos(i * twicePi / (coords.length - 6)));
            coords[i + 1] = (float) (size * Math.sin(i * twicePi / (coords.length - 6)));
            coords[i + 2] = 0;
        }

        return coords;
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
