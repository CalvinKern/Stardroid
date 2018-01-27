package com.seakernel.stardroid.model;

/**
 * Created by Calvin on 7/9/2017.
 */

public class OpenGlColors {
    public static float[] STAR_BLUE                 = {0.6f, 0.6f, 1.f, 1.f}; // Blue is the hottest
    public static float[] STAR_WHITE_BLUE           = {0.8f, 0.8f, 1.f, 1.f}; // White Blue is next
    public static float[] STAR_LIGHTER_WHITE_BLUE   = {0.875f, 0.875f, 1.f, 1.f}; // White Blue is next
    public static float[] STAR_WHITE_YELLOW         = {1.f, 0.95f, 0.7f, 1.f}; // White Yellow
    public static float[] STAR_YELLOW_ORANGE        = {1.f, 0.75f, 0.5f, 1.f}; // Yellow Orange
    public static float[] STAR_ORANGE_RED           = {1.f, 0.75f, 0.7f, 1.f}; // Orange Red
    public static float[] STAR_WHITE                = {1.f, 1.f, 1.f, 1.f}; // Return a white star default

    public static float[] ENEMY_RED = {0.9568627f, 0.2627451f, 0.2117647f, 1.f}; // Yellow Orange rgb(244,67,54)
    public static float[] PLAYER_BLUE = {0.2588235f, 0.6470588f, 0.9607843f, 1.f}; // rgb(66,165,245)

    public static float[] randColor() {
        return randColor(1.0f);
    }

    public static float[] randColor(final float opacity) {
        return new float[] {
                rand(), rand(), rand(), opacity
        };
    }

    private static float rand() {
        //noinspection NumericCastThatLosesPrecision
        return (float) Math.random();
    }
}
