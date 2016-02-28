package edu.utah.cs4962.stardroid;

/**
 * This class represents a bullet.
 * Created by Calvin on 12/1/13.
 */
public class Bullet
{
    // Variable bullet properties
    private float centerX = 0.0f;
    private float centerY = 0.0f;
    private float distanceTraveled = 0.0f;
//    private float rotation = 0.0f;        // Future Implementation

    public Bullet(float startX, float startY)
    {
        centerX = startX;
        centerY = startY;
    }

    // Getters
    public float getBulletSpeed()
    {
        return 0.035f;
    }

    public float getDistanceTraveled()
    {
        return distanceTraveled;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public float getBulletWidth()
    {
        return 0.03f;
    }

    public float getBulletHeight()
    {
        return 0.03f;
    }

//    Future Implementation
//    public float getRotation()
//    {
//        return rotation;
//    }

    // Setters
    public void setCenterX(float centerX)
    {
        distanceTraveled += Math.abs(this.centerX - centerX);
        this.centerX = centerX;
    }

    public void setCenterY(float centerY)
    {
        this.centerY = centerY;
    }

//    Future Implementation
//    public void setRotation(float rotation)
//    {
//        this.rotation = rotation;
//    }
}
