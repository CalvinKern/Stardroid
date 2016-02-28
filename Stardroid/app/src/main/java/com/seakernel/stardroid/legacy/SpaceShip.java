package com.seakernel.stardroid.legacy;

/**
 * This class represents a spaceship object to be used in the Stardroid game.
 * Created by Calvin on 12/2/13.
 */
public class SpaceShip
{
    public interface OnShipSpeedChangedListener
    {
        public void onShipSpeedChanged();
    }

    public interface OnFiringRateChangedListener
    {
        public void onFiringRateChanged();
    }

    public interface OnShipDestroyedListener
    {
        public void onShipDestroyed();
    }

    // Listeners
    private OnShipSpeedChangedListener onShipSpeedChangedListener = null;
    private OnFiringRateChangedListener onFiringRateChangedListener = null;
    private OnShipDestroyedListener onShipDestroyedListener = null;

    // Physical ship properties
//    private float shipWidth = 0.1f;
    private float shipHeight = 0.1f;

    // Variable ship properties
    private float shipX;
    private float shipY;
    private float moveToX;
    private float moveToY;
    private float rotation = 0.0f;       // For future updates
    private float engineSpeed = 0.02f;   // starts at .02, increment by x, final is y
    private float firingRate = 0.75f;    // (seconds / bullet) Start at .75, decrement by .05, final is .15
    private int weaponStreams = 1;       // Start at 1, increment by 1, final is 3
    private int shipLife = 4;

    public SpaceShip(float startX, float startY)
    {
        this.shipX = startX;
        this.shipY = startY;
    }

    // Accessor methods ============================================================================
    public float getRotationAngle()
    {
        return rotation;
    }

    public float getShipX()
    {
        return shipX;
    }

    public float getShipY()
    {
        return shipY;
    }

    public float getMoveToX()
    {
        return moveToX;
    }

    public float getMoveToY()
    {
        return moveToY;
    }

    public float getShipWidth()
    {
        return 0.1f;
    }

    public float getShipHeight()
    {
        return shipHeight;
    }

    public float getEngineSpeed()
    {
        return engineSpeed;
    }

    public float getFiringRate()
    {
        return firingRate;
    }

    public int getWeaponStreams()
    {
        return weaponStreams;
    }

    public int getRemainingLife()
    {
        return shipLife;
    }

    // Setter methods ==============================================================================
    public void setRotationAngle(float angle)
    {
        rotation = angle;
    }

    public void setShipX(float newShipX)
    {
        shipX = newShipX;
    }

    public void setShipY(float newShipY)
    {
        if (newShipY < (0.9f - 2*shipHeight) && newShipY > (-1.0f + 2*shipHeight))
            shipY = newShipY;
    }

    public void setEngineSpeed(float speed)
    {
        if (speed >= 0.0f) // change
        {
            engineSpeed = speed;

            if (onShipSpeedChangedListener != null)
                onShipSpeedChangedListener.onShipSpeedChanged();
        }
    }

    public void setFiringRate(float rate)
    {
        if (rate >= 0.15f)
        {
            firingRate = rate;

            if (onFiringRateChangedListener != null)
                onFiringRateChangedListener.onFiringRateChanged();
        }
    }

    public void setWeaponStreams(int streams)
    {
        if (streams < 4)
            weaponStreams = streams;
    }

    // Extra Ship methods ==========================================================================
    public void moveShipToX(float toX)
    {
        moveToX = toX;
    }

    public void moveShipToY(float toY)
    {
        moveToY = toY;
    }

    public void restoreHealth()
    {
        if (shipLife < 4)
           shipLife++;
    }

    public void shipHit()
    {
        if (shipLife > 1)
            shipLife--;
        else
            onShipDestroyedListener.onShipDestroyed();
    }

    // Listener Setters ============================================================================
    public void setOnShipSpeedChangedListener(OnShipSpeedChangedListener listener)
    {
        onShipSpeedChangedListener = listener;
    }

    public void setOnFiringRateChangedListener(OnFiringRateChangedListener listener)
    {
        onFiringRateChangedListener = listener;
    }

    public void setOnShipDestroyedListener(OnShipDestroyedListener listener)
    {
        onShipDestroyedListener = listener;
    }
}
