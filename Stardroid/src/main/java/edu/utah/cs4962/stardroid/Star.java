package edu.utah.cs4962.stardroid;

/**
 * This class represents a star object for the background of the Stardroid game
 * Created by Calvin on 12/12/13.
 */
public class Star
{
    private float xPosition;
    private float yPosition;

    public Star(float startX, float startY)
    {
        xPosition = startX;
        yPosition = startY;
    }

    public void setStarX(float x)
    {
        xPosition = x;
    }

    public float getStarX()
    {
        return xPosition;
    }

    // Future Implementation (adding move with ship functionality)
//    public void setStarY(float y)
//    {
//        yPosition = y;
//    }

    public float getStarY()
    {
        return yPosition;
    }

    public float getStarWidth()
    {
        return 0.005f;
    }

    public float getStarHeight()
    {
        return 0.01f;
    }

    public float getStarFloatingSpeed()
    {
        return 0.005f;
    }
}
