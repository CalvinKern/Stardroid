package com.seakernel.stardroid.legacy;

/**
 * This class represents a power up object in the Stardroid game.  Power ups have 4 types; upgrade
 *  engine speed, upgrade firing speed, upgrade firing output, and restore health.
 * Created by Calvin on 12/10/13.
 */
public class PowerUp
{
    // Enum for the type of the power up
    public enum PowerUpType
    {
        UPGRADE_ENGINE_SPEED,
        UPGRADE_FIRING_SPEED,
        UPGRADE_FIRING_OUTPUT,
        RESTORE_HEALTH
    }

    // Permanent field storing the power up type for this object
    private PowerUpType powerUp;

    // Variable fields for the bullet
    private float centerX = 0.0f;
    private float centerY = 0.0f;

    public PowerUp(float startX, float startY)
    {
        centerX = startX;
        centerY = startY;

        // Create with a random power up type
        int randPowerUp = (int)(Math.random()* PowerUpType.values().length);
        powerUp = PowerUpType.values()[randPowerUp];
    }

    // Getters
    public PowerUpType getPowerUpType()
    {
        return powerUp;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public float getFloatSpeed()
    {
        return 0.015f;
    }

    public float getWidth()
    {
        return 0.03f;
    }

    public float getHeight()
    {
        return 0.06f;
    }

    // Setters
    public void setCenterX(float newX)
    {
        centerX = newX;
    }

// Future Implementation (make getting power ups harder)
//    public void setCenterY(float newY)
//    {
//        centerY = newY;
//    }
}
