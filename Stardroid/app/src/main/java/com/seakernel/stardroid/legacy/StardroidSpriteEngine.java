package com.seakernel.stardroid.legacy;

import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This Class performs as the sprite engine that draws all required elements for the Stardroid game
 * Created by Calvin on 11/30/13.
 */
public class StardroidSpriteEngine
{
    // Listener Interfaces
    public interface OnEnemyShipDestroyedListener
    {
        public void onEnemyShipDestroyed(boolean isSpecial, SpaceShip enemy);
    }

    public interface OnEnemyShipPassedListener
    {
        public void onEnemyShipPassed(boolean isSpecial, SpaceShip enemy);
    }

    // Listener variables
    private OnEnemyShipDestroyedListener onEnemyShipDestroyedListener = null;
    private OnEnemyShipPassedListener onEnemyShipPassedListener = null;

    // Member variables
    private HashMap<SpaceShip, Bullet> enemyCollisionCourseBullets = null;
    private ArrayList<Star> backgroundStars = null;

    // Texture variables
    private int userShipTexture;
    private int enemyShipTexture;
    private int specialEnemyShipTexture;
    private int bulletTexture;
    private int starTexture;
    private int healthTexture;
    private int enginePowerTexture;
    private int bulletStreamsTexture;
    private int bulletSpeedTexture;
    private int fullHealthTexture;
    private int halfHealthTexture;
    private int lowHealthTexture;
    private int pauseTexture;

    // Open GL variables
    private int program = -1;
    private FloatBuffer geometryBuffer = null;

    public void setTextures(int userShipTexture,
                            int enemyShipTexture,
                            int specialEnemyShipTexture,
                            int bulletTexture,
                            int starTexture,
                            int healthTexture,
                            int enginePowerTexture,
                            int bulletStreamsTexture,
                            int bulletSpeedTexture,
                            int fullHealthTexture,
                            int halfHealthTexture,
                            int lowHealthTexture,
                            int pauseTexture)
    {
        this.userShipTexture = userShipTexture;
        this.enemyShipTexture = enemyShipTexture;
        this.specialEnemyShipTexture = specialEnemyShipTexture;
        this.bulletTexture = bulletTexture;
        this.starTexture = starTexture;
        this.healthTexture = healthTexture;
        this.enginePowerTexture = enginePowerTexture;
        this.bulletStreamsTexture = bulletStreamsTexture;
        this.bulletSpeedTexture = bulletSpeedTexture;
        this.fullHealthTexture = fullHealthTexture;
        this.halfHealthTexture = halfHealthTexture;
        this.lowHealthTexture = lowHealthTexture;
        this.pauseTexture = pauseTexture;
    }

    /**
     * This method initializes all neccessary data structures and GLES20
     */
    private void initialize()
    {
        enemyCollisionCourseBullets = new HashMap<SpaceShip, Bullet>();
        backgroundStars = new ArrayList<Star>();

        // Create a vertex shader
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, getVertexShaderSource());
        GLES20.glCompileShader(vertexShader);
//        String vertexShaderCompileLog = GLES20.glGetShaderInfoLog(vertexShader); // LOG

        // Create a fragment shader
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, getFragmentShaderSource());
        GLES20.glCompileShader(fragmentShader);
//        String fragmentShaderCompileLog = GLES20.glGetShaderInfoLog(fragmentShader); // LOG

        // Initialize the program
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glBindAttribLocation(program, 0, "position");
        GLES20.glBindAttribLocation(program, 1, "textureCoordinate");
        GLES20.glLinkProgram(program);
//        String programLinkLog = GLES20.glGetProgramInfoLog(program);  // LOG
        GLES20.glUseProgram(program);

        // BASIC GEOMETRY COORDINATES
        float[] geometry =
                {
                        -1f, -1f, 0.0f,
                        1f, -1f, 0.0f,
                        -1f,  1f, 0.0f,
                        1f,  1f, 0.0f,
                };

        ByteBuffer geometryByteBuffer = ByteBuffer.allocateDirect(geometry.length * 4);
        geometryByteBuffer.order(ByteOrder.nativeOrder());
        geometryBuffer = geometryByteBuffer.asFloatBuffer();
        geometryBuffer.put(geometry);
        geometryBuffer.rewind();

        // initialize the stars in the background for the start of the game
        initializeStars();
    }

    /**
     * This method contains the vertex shader source code required by GLES to draw
     *
     * @return The vertex shader source code
     */
    private String getVertexShaderSource()
    {
        return "precision mediump float;\n"
                + "uniform mat4 modelView;\n"
                + "attribute vec3 position;\n"
                + "attribute vec2 textureCoordinate;\n"

                + "varying vec2 textureCoordinateVarying;\n"
                + "\n"
                + "void main()\n"
                + "{\n"
                + "   gl_Position = modelView * vec4(position, 1.0);\n"
                + "   textureCoordinateVarying = textureCoordinate;\n"
                + "}";
    }

    /**
     * This method contains the fragment shader source code required for GLES to draw
     *
     * @return The fragment shader source code
     */
    private String getFragmentShaderSource()
    {
        return ""
                + "precision mediump float;\n"
                + "uniform sampler2D textureUnit;\n"
                + "varying vec2 textureCoordinateVarying;\n"
                + "\n"
                + "void main()\n"
                + "{\n"
                + "   vec4 color = texture2D(textureUnit, textureCoordinateVarying);\n"
                + "   gl_FragColor = color;\n"
                + "}";
    }

    /**
     * This helper method initializes the list of stars for the beginning of the game
     */
    private void initializeStars()
    {
        if (backgroundStars.size() == 0)
        {
            int rand = (int)(Math.random() * 50) + 50;

            for (int i = 0; i < rand; i++)
            {
                float randX = (float)(Math.random()*2) - 1;
                float randY = (float)(Math.random()*2) - 1;

                Star star = new Star(randX,randY);
                backgroundStars.add(star);
            }
        }
    }

    /**
     * This method will draw all the necessary views to the screen
     *
     * @param userShip The Ship that the user controls
     * @param bulletList The list of bullets fired by the user
     * @param enemyList The list of enemies spawned
     * @param specialEnemyShips The list of special enemies spawned
     * @param powerUps The list of power ups dropped by special enemies
     */
    public void draw(SpaceShip userShip, ArrayList<Bullet> bulletList, ArrayList<Bullet> collidingBullets, ArrayList<SpaceShip> enemyList, ArrayList<SpaceShip> specialEnemyShips, ArrayList<PowerUp> powerUps) // No longer requires colliding bullets
    {
        // Check if the program hasn't been initialized
        if (program < 0)
            initialize();

        // initialize variables for use
        float[] modelView = new float[16];

        // Draw the stars in the background
        drawStars(modelView);

        // Draw life bars
        drawLifeBars(userShip.getRemainingLife(), modelView);

        // Draw the enemies
        drawEnemies(userShip, bulletList, collidingBullets, enemyList, modelView);  // No longer requires colliding bullets

        // Draw the special enemies
        drawSpecialEnemies(userShip, bulletList, specialEnemyShips, modelView);

        // Draw the ship
        drawUserShip(userShip, modelView);

        // Draw the bullets
        drawBullets(bulletList, modelView);

        // Draw the power ups
        drawPowerUps(userShip, powerUps, modelView);
    }

    /**
     * This method will draw the pause screen to inform the user the game is paused
     */
    public void drawPause()
    {
        float[] modelView = new float[16];
        int elements = 3;

        // Modify the matrix and set it
        Matrix.setIdentityM(modelView, 0);
        Matrix.translateM(modelView, 0, 0.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelView, 0, 90.0f, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(modelView, 0, 1.0f, 1.0f, 1.0f);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "modelView"), 1, false, modelView, 0);

        // Enable the coordinates in the program
        GLES20.glVertexAttribPointer(0, elements, GLES20.GL_FLOAT, false, 0, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);

        // Enable the texture location in the program
        int textureUnitLocation = GLES20.glGetUniformLocation(program, "textureUnit");
        GLES20.glUniform1i(textureUnitLocation, 0);

        float triangleTextureCoordinates[] =
                {
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                };

        // Set up the buffer
        ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(triangleTextureCoordinates.length * 4);
        textureByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer textureCoordinateBuffer = textureByteBuffer.asFloatBuffer();
        textureCoordinateBuffer.put(triangleTextureCoordinates);
        textureCoordinateBuffer.position(0);

        // Bind the texture and draw
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, pauseTexture);
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 4 * 2, textureCoordinateBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, geometryBuffer.capacity() / elements);
    }

    // HELPER METHODS FOR DRAWING ==================================================================

    /**
     *
     * @param modelView The Matrix to hold the coordinates to draw with
     */
    private void drawStars(float[] modelView)
    {
        // randomly add stars to the background
        if (Math.random() * 100 <= 15)
        {
            float randY = (float)(Math.random() * 2 - 1);
            Star newStar = new Star(1.0f, randY);
            backgroundStars.add(newStar);
        }

        // Create a new ArrayList to put all of the passed stars
        ArrayList<Star> passedStars = new ArrayList<Star>();

        // Loop through all the stars in the background
        for (Star star : backgroundStars)
        {
            star.setStarX(star.getStarX() - star.getStarFloatingSpeed());

            // Determine if the star has passed the screen
            if (star.getStarX() <= -1.0f)
            {
                passedStars.add(star);
                continue;
            }

            // Modify the matrix that contains the coordinates
            Matrix.setIdentityM(modelView, 0);
            Matrix.translateM(modelView, 0, star.getStarX(), star.getStarY(), 0.0f);
            Matrix.rotateM(modelView, 0, 0.0f, 0.0f, 0.0f, 1.0f);
            Matrix.scaleM(modelView, 0, star.getStarWidth(), star.getStarHeight(), 1.0f);

            drawWithTexture(modelView, starTexture);
        }

        // Remove stars that have passed
        for (Star oldStar : passedStars)
        {
            backgroundStars.remove(oldStar);
        }
    }

    // TODO: MVC cleanup
    private void drawUserShip(SpaceShip userShip, float[] modelView)
    {
        // TODO: Animate ship in X and Y direction in Controller
        // Animate ship in the X direction
        float moveToX = userShip.getMoveToX();
        float xDifference = Math.abs(moveToX - userShip.getShipX());

        if (xDifference != 0)
        {
            float xMoveAmount = userShip.getEngineSpeed() % xDifference;

            if (moveToX > userShip.getShipX())
            {
                if (userShip.getShipX() < 0.98f)
                {
                    userShip.setShipX(userShip.getShipX() + xMoveAmount);
                }
            }
            else if (moveToX < userShip.getShipX())
            {
                if (userShip.getShipX() > -0.98f)
                {
                    userShip.setShipX(userShip.getShipX() - xMoveAmount);
                }
            }
        }

        // Animate ship in the Y direction
        float moveToY = userShip.getMoveToY();
        float yDifference = Math.abs(moveToY - userShip.getShipY());

        if (yDifference != 0)
        {
            float yMoveAmount = userShip.getEngineSpeed() % yDifference;

            if (moveToY > userShip.getShipY())
            {
                if (userShip.getShipY() < 0.98f)
                {
                    userShip.setShipY(userShip.getShipY() + yMoveAmount);
                }
            }
            else if (moveToY < userShip.getShipY())
            {
                if (userShip.getShipY() > -0.98f)
                {
                    userShip.setShipY(userShip.getShipY() - yMoveAmount);
                }
            }
        }

        // Modify matrix that has the position of the ship
        Matrix.setIdentityM(modelView, 0);
        Matrix.translateM(modelView, 0, userShip.getShipX(), userShip.getShipY(), 0.0f);
        Matrix.rotateM(modelView, 0, userShip.getRotationAngle() - 90, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(modelView, 0, userShip.getShipWidth(), userShip.getShipHeight(), 1.0f);

        drawWithTexture(modelView, userShipTexture);
    }

    // TODO: MVC cleanup
    private void drawBullets(ArrayList<Bullet> bulletList, float[] modelView)
    {
        int bulletCount = bulletList.size();

        if (bulletCount > 0)
        {
            ArrayList<Bullet> passedBullets = new ArrayList<Bullet>();

            for (Bullet bullet : bulletList)
            {
                bullet.setCenterX(bullet.getCenterX() + bullet.getBulletSpeed());

                // TODO: put in model/controller
                if (bullet.getCenterX() >= 1.5f)
                    passedBullets.add(bullet);

                // Modify matrix that contains coordinates
                Matrix.setIdentityM(modelView, 0);
                Matrix.translateM(modelView, 0, bullet.getCenterX(), bullet.getCenterY(), 0.0f);
                Matrix.rotateM(modelView, 0, 0.0f, 0.0f, 0.0f, 1.0f);
                Matrix.scaleM(modelView, 0, bullet.getBulletWidth(), bullet.getBulletHeight(), 1.0f);

                drawWithTexture(modelView, bulletTexture);
            }

            // TODO: put in model/controller
            for (Bullet bullet : passedBullets)
                bulletList.remove(bullet);
        }
    }

    // TODO: MVC cleanup
    private void drawEnemies(SpaceShip userShip, ArrayList<Bullet> bulletList, ArrayList<Bullet> collidingBullets, ArrayList<SpaceShip> enemyList, float[] modelView)
    {
        int enemyCount = enemyList.size();

        if (enemyCount > 0)
        {
            ArrayList<SpaceShip> passedShips = new ArrayList<SpaceShip>();
            ArrayList<SpaceShip> destroyedShips = new ArrayList<SpaceShip>();
            ArrayList<Bullet> hitBullets = new ArrayList<Bullet>();

            for (SpaceShip enemyShip : enemyList)
            {
                enemyShip.setShipX(enemyShip.getShipX() - enemyShip.getEngineSpeed());

                // TODO: put in model/controller
                if (enemyShip.getShipX() < -1.0f)
                {
                    passedShips.add(enemyShip);
                    continue;
                }

                // TODO: put in model/controller
                if (Math.random() < 0.5)
                    enemyShip.setShipY(enemyShip.getShipY() + enemyShip.getEngineSpeed()*1);
                else
                    enemyShip.setShipY(enemyShip.getShipY() - enemyShip.getEngineSpeed()*1);

                // Modify matrix containing coordinates
                Matrix.setIdentityM(modelView, 0);
                Matrix.translateM(modelView, 0, enemyShip.getShipX(), enemyShip.getShipY(), 0.0f);
                Matrix.rotateM(modelView, 0, 90.0f, 0.0f, 0.0f, 1.0f);
                Matrix.scaleM(modelView, 0, enemyShip.getShipWidth(), enemyShip.getShipHeight(), 1.0f);

                drawWithTexture(modelView, enemyShipTexture);

                // Accelerator for collision detection (Old way where enemies don't move in Y; buggy as well)
                // If the ship is already on a collision course && still on a collision course with that bullet
/*
                if (enemyCollisionCourseBullets.containsKey(enemyShip))
                    bulletCollisionCourseDetection(enemyShip, enemyCollisionCourseBullets.get(enemyShip));

                if (enemyCollisionCourseBullets.containsKey(enemyShip) && bulletCollisionCourseDetection(enemyShip, enemyCollisionCourseBullets.get(enemyShip)))
                {
                    // Get the bullet that it will collide with
                    Bullet bullet = enemyCollisionCourseBullets.get(enemyShip);

                    // If the bullet collided
                    if (bulletCollisionDetection(enemyShip, bullet))
                    {
                        hitBullets.add(bullet);
                        destroyedShips.add(enemyShip);
//                        onEnemyShipDestroyedListener.onEnemyShipDestroyed(false, enemyShip);

                        enemyCollisionCourseBullets.remove(enemyShip);
                    }
                }
                else
                {
                    if (enemyCollisionCourseBullets.containsKey(enemyShip))
                    {
                        enemyCollisionCourseBullets.remove(enemyShip);
                    }

                    for (Bullet bullet : bulletList)
                    {
                        if (bulletCollisionCourseDetection(enemyShip, bullet))
                        {
                            collidingBullets.add(bullet);
//                            hitBullets.add(bullet);
                            enemyCollisionCourseBullets.put(enemyShip, bullet);
                            break;
                        }
                    }
                }
*/

                // Simple collision detection
                for (Bullet bullet : bulletList)
                {
                    if (bulletCollisionDetection(enemyShip, bullet))
                    {
                        hitBullets.add(bullet);
                        destroyedShips.add(enemyShip);
                    }
                }

                if (userCollisionDetection(userShip, enemyShip))
                {
                    destroyedShips.add(enemyShip);
                    userShip.shipHit();
                }
            }

            for (SpaceShip ship : destroyedShips)
            {
                onEnemyShipDestroyedListener.onEnemyShipDestroyed(false, ship);
            }

            for (SpaceShip ship : passedShips)
            {
                onEnemyShipPassedListener.onEnemyShipPassed(false, ship);
            }

            // TODO: put in model/controller
            for (Bullet bullet : hitBullets)
                bulletList.remove(bullet);
        }
    }

    // TODO: MVC cleanup
    private void drawSpecialEnemies(SpaceShip userShip, ArrayList<Bullet> bulletList, ArrayList<SpaceShip> specialEnemies, float[] modelView)
    {
        int specialEnemyCount = specialEnemies.size();

        if (specialEnemyCount > 0)
        {
            ArrayList<SpaceShip> passedShips = new ArrayList<SpaceShip>();
            ArrayList<SpaceShip> destroyedShips = new ArrayList<SpaceShip>();
            ArrayList<Bullet> hitBullets = new ArrayList<Bullet>();

            for (SpaceShip specialEnemyShip : specialEnemies)
            {
                specialEnemyShip.setShipX(specialEnemyShip.getShipX() - specialEnemyShip.getEngineSpeed());

                // TODO: put in model
                if (specialEnemyShip.getShipX() < -1.0f)
                {
                    passedShips.add(specialEnemyShip);
                    continue;
                }

                // Modify the matrix containing the coordinates for the special enemy ship
                Matrix.setIdentityM(modelView, 0);
                Matrix.translateM(modelView, 0, specialEnemyShip.getShipX(), specialEnemyShip.getShipY(), 0.0f);
                Matrix.rotateM(modelView, 0, 90.0f, 0.0f, 0.0f, 1.0f);
                Matrix.scaleM(modelView, 0, specialEnemyShip.getShipWidth(), specialEnemyShip.getShipHeight(), 1.0f);

                drawWithTexture(modelView, specialEnemyShipTexture);

                // Simple collision detection against every bullet
                for (Bullet bullet : bulletList)
                    if (bulletCollisionDetection(specialEnemyShip, bullet))
                    {
                        destroyedShips.add(specialEnemyShip);
                        hitBullets.add(bullet);
                    }

                // Accelerator for collision detection (Old way where enemies don't move in Y; buggy as well)
/*
                if (enemyCollisionCourseBullets.containsKey(specialEnemyShip) && bulletCollisionCourseDetection(specialEnemyShip, enemyCollisionCourseBullets.get(specialEnemyShip)))
                {
                    Bullet bullet = enemyCollisionCourseBullets.get(specialEnemyShip);

                    if (bulletCollisionDetection(specialEnemyShip, bullet))
                    {
                        destroyedShips.add(specialEnemyShip);
//                        onEnemyShipDestroyedListener.onEnemyShipDestroyed(true, specialEnemyShip);
                        hitBullets.add(bullet);
                    }
                }
                else
                {
                    if (enemyCollisionCourseBullets.containsKey(specialEnemyShip))
                        enemyCollisionCourseBullets.remove(specialEnemyShip);

                    for (Bullet bullet : bulletList)
                    {
                        if (bulletCollisionCourseDetection(specialEnemyShip, bullet))
                        {
                            enemyCollisionCourseBullets.put(specialEnemyShip, bullet);
                            break;
                        }
                    }
                }

                if (userCollisionDetection(userShip, specialEnemyShip))
                {
                    destroyedShips.add(specialEnemyShip);
                    userShip.shipHit();
                }
*/
            }

            for (SpaceShip ship : destroyedShips)
                    onEnemyShipDestroyedListener.onEnemyShipDestroyed(true, ship);

            for (SpaceShip ship : passedShips)
                onEnemyShipPassedListener.onEnemyShipPassed(true, ship);

            // TODO: set up listeners for removing colliding ships and bullets in model
            for (Bullet bullet : hitBullets)
                bulletList.remove(bullet);
        }
    }

    /**
     *  This is a helper method for draw() that will display the life bars that the user ship
     *   has remaining onto the screen.
     *
     * @param lifeCount The remaining amount of life the ship has
     * @param modelView The coordinates to draw the life bars within
     */
    private void drawLifeBars(int lifeCount, float[] modelView)
    {
        // Life Bar variables
        float firstBar = -0.6f;
        float secondBar = -0.2f;
        float thirdBar = 0.2f;
        float fourthBar = 0.6f;

        float barWidth = 0.2f;
        float barHeight = 0.025f;

        // Loop for every life bar
        for (int lifeBar = 1; lifeBar <= lifeCount; lifeBar++)
        {
            // Set the center X coordinate for each life bar
            float lifeBarCenterX;
            if (lifeBar == 1)
                lifeBarCenterX = firstBar;
            else if (lifeBar == 2)
                lifeBarCenterX = secondBar;
            else if (lifeBar == 3)
                lifeBarCenterX = thirdBar;
            else
                lifeBarCenterX = fourthBar;

            // Modify the geometric matrix that contains the life bar coordinates
            Matrix.setIdentityM(modelView, 0);
            Matrix.translateM(modelView, 0, lifeBarCenterX, -0.9f, 0.0f);
            Matrix.rotateM(modelView, 0, 0.0f, 0.0f, 0.0f, 1.0f);
            Matrix.scaleM(modelView, 0, barWidth, barHeight, 1.0f);

            // Draw the life bars with the correct texture
            if (lifeCount == 1)
                drawWithTexture(modelView, lowHealthTexture);
            else if (lifeCount == 2)
                drawWithTexture(modelView, halfHealthTexture);
            else
                drawWithTexture(modelView, fullHealthTexture);
        }
    }

    // TODO: MVC cleanup
    private void drawPowerUps(SpaceShip userShip, ArrayList<PowerUp> powerUps, float[] modelView)
    {
        int powerUpCount = powerUps.size();

        if (powerUpCount > 0)
        {
            ArrayList<PowerUp> takenPowerUps = new ArrayList<PowerUp>();

            for (PowerUp powerUp : powerUps)
            {
                // TODO: put in controller
                powerUp.setCenterX(powerUp.getCenterX() - powerUp.getFloatSpeed());

                // TODO: do in model/controller
                if (powerUp.getCenterX() < -1.0f)
                {
                    takenPowerUps.add(powerUp);
                    continue;
                }

                // Change color for power-ups
                // Engine Speed = WHITE
                // Firing Speed = YELLOW
                // Firing Output = CYAN
                // Restore Health = GREEN

                Matrix.setIdentityM(modelView, 0);
                Matrix.translateM(modelView, 0, powerUp.getCenterX(), powerUp.getCenterY(), 0.0f);
                Matrix.rotateM(modelView, 0, 0.0f, 0.0f, 0.0f, 1.0f);
                Matrix.scaleM(modelView, 0, powerUp.getWidth(), powerUp.getHeight(), 1.0f);

                if (powerUp.getPowerUpType() == PowerUp.PowerUpType.UPGRADE_FIRING_SPEED)// YELLOW (BLUE)
                    drawWithTexture(modelView, bulletSpeedTexture);
                else if (powerUp.getPowerUpType() == PowerUp.PowerUpType.UPGRADE_FIRING_OUTPUT)// CYAN
                    drawWithTexture(modelView, bulletStreamsTexture);
                else if (powerUp.getPowerUpType() == PowerUp.PowerUpType.UPGRADE_ENGINE_SPEED)// RAINBOW
                    drawWithTexture(modelView, enginePowerTexture);
                else if (powerUp.getPowerUpType() == PowerUp.PowerUpType.RESTORE_HEALTH)// GREEN
                    drawWithTexture(modelView, healthTexture);

                if(powerUpCollisionDetection(userShip, powerUp))
                {
                    //TODO: Set up listener for received power up

                    if (powerUp.getPowerUpType() == PowerUp.PowerUpType.UPGRADE_FIRING_SPEED)
                        userShip.setFiringRate(userShip.getFiringRate() - 0.05f);
                    else if (powerUp.getPowerUpType() == PowerUp.PowerUpType.UPGRADE_FIRING_OUTPUT)
                        userShip.setWeaponStreams(userShip.getWeaponStreams() + 1);
                    else if (powerUp.getPowerUpType() == PowerUp.PowerUpType.UPGRADE_ENGINE_SPEED)
                        userShip.setEngineSpeed(userShip.getEngineSpeed() + 0.01f);
                    else if (powerUp.getPowerUpType() == PowerUp.PowerUpType.RESTORE_HEALTH)
                        userShip.restoreHealth();

                    takenPowerUps.add(powerUp);
                }
            }

            // TODO: Set up listener for missed power ups
            for (PowerUp power : takenPowerUps)
            {
                powerUps.remove(power);
            }
        }
    }

    private void drawWithTexture(float[] modelView, int texture)
    {
        int elements = 3;

        // get the uniform locations
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "modelView"), 1, false, modelView, 0);
        int textureUnitLocation = GLES20.glGetUniformLocation(program, "textureUnit");
        GLES20.glUniform1i(textureUnitLocation, 0);

        float triangleTextureCoordinates[] =
                {
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                };

        // Set up the buffer
        ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(triangleTextureCoordinates.length * 4);
        textureByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer textureCoordinateBuffer = textureByteBuffer.asFloatBuffer();
        textureCoordinateBuffer.put(triangleTextureCoordinates);
        textureCoordinateBuffer.position(0);

        // Bind the texture
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 4 * 2, textureCoordinateBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        // Draw using the coordinates
        GLES20.glVertexAttribPointer(0, elements, GLES20.GL_FLOAT, false, 0, geometryBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, geometryBuffer.capacity() / elements);

        // Old way (direct color)
//        GLES20.glVertexAttribPointer(1,elements, GLES20.GL_FLOAT, false, 0, shipColorBuffer);
//        GLES20.glEnableVertexAttribArray(1);

    }

    // COLLISION DETECTION =========================================================================

    // Accelerator for collision detection (Best for old way where enemies don't move in Y; buggy as well)

    private boolean bulletCollisionCourseDetection(SpaceShip ship, Bullet bullet)
    {
        float shipRight = ship.getShipX() + ship.getShipWidth();
        float bulletLeft = bullet.getCenterX() - bullet.getBulletWidth();

        if (bulletLeft > shipRight)
            return false;

        float bulletTop = bullet.getCenterY() + bullet.getBulletHeight();
        float shipBottom = ship.getShipY() - ship.getShipHeight();

        if (bulletTop >= shipBottom)
        {
            float bulletBottom = bullet.getCenterY() - bullet.getBulletHeight();
            float shipTop = ship.getShipY() + ship.getShipHeight();

            if (bulletBottom <= shipTop)
                return true;
        }
        return false;
    }

    private boolean bulletCollisionDetection(SpaceShip ship, Bullet bullet)
    {
        // Compute the coordinates of the ship
        RectF shipCoord = new RectF();
        shipCoord.left = ship.getShipX() - ship.getShipWidth();

        // Compute the coordinates of the bullet
        RectF bulletCoord = new RectF();
        bulletCoord.right = bullet.getCenterX() + bullet.getBulletWidth();

        // Check if they are contained within each other (overlap)
        if (shipCoord.left <= bulletCoord.right)
        {
            shipCoord.right = ship.getShipX() + ship.getShipWidth();
            bulletCoord.left = bullet.getCenterX() - bullet.getBulletWidth();

            if (shipCoord.right >= bulletCoord.left)
            {
                shipCoord.top = ship.getShipY() + ship.getShipHeight();
                bulletCoord.bottom = bullet.getCenterY() - bullet.getBulletHeight();

                if (shipCoord.top >= bulletCoord.bottom)
                {
                    shipCoord.bottom = ship.getShipY() - ship.getShipHeight();
                    bulletCoord.top = bullet.getCenterY() + bullet.getBulletHeight();

                    if (shipCoord.bottom <= bulletCoord.top)
                        return true;
                }
            }
        }
        return false;
    }

    private boolean userCollisionDetection(SpaceShip userShip, SpaceShip enemyShip)
    {
        // Compute the coordinates of the user ship
        RectF userShipCoord = new RectF();
        userShipCoord.left = userShip.getShipX() - userShip.getShipWidth();
        userShipCoord.right = userShip.getShipX() + userShip.getShipWidth();
        userShipCoord.top = userShip.getShipY() + userShip.getShipHeight();
        userShipCoord.bottom = userShip.getShipY() - userShip.getShipHeight();

        // Compute the coordinates of the enemy ship
        RectF enemyShipCoord = new RectF();
        enemyShipCoord.left = enemyShip.getShipX() - enemyShip.getShipWidth();
        enemyShipCoord.right = enemyShip.getShipX() + enemyShip.getShipWidth();
        enemyShipCoord.top = enemyShip.getShipY() + enemyShip.getShipHeight();
        enemyShipCoord.bottom = enemyShip.getShipY() - enemyShip.getShipHeight();

        // Check if they are contained within each other (overlap)
        return (userShipCoord.left <= enemyShipCoord.right && userShipCoord.right >= enemyShipCoord.left
                && userShipCoord.top >= enemyShipCoord.bottom && userShipCoord.bottom <= enemyShipCoord.top);
    }

    private boolean powerUpCollisionDetection(SpaceShip userShip, PowerUp powerUp)
    {
        // Compute the coordinates of the user ship
        RectF userShipCoord = new RectF();
        userShipCoord.left = userShip.getShipX() - userShip.getShipWidth();
        userShipCoord.right = userShip.getShipX() + userShip.getShipWidth();
        userShipCoord.top = userShip.getShipY() + userShip.getShipHeight();
        userShipCoord.bottom = userShip.getShipY() - userShip.getShipHeight();

        // Compute the coordinates of the enemy ship
        RectF powerUpCoord = new RectF();
        powerUpCoord.left = powerUp.getCenterX() - powerUp.getWidth();
        powerUpCoord.right = powerUp.getCenterX() + powerUp.getWidth();
        powerUpCoord.top = powerUp.getCenterY() + powerUp.getHeight();
        powerUpCoord.bottom = powerUp.getCenterY() - powerUp.getHeight();

        // Check if they are contained within each other (overlap)
        return (userShipCoord.left <= powerUpCoord.right && userShipCoord.right >= powerUpCoord.left
                && userShipCoord.top >= powerUpCoord.bottom && userShipCoord.bottom <= powerUpCoord.top);
    }

    // SETTERS FOR LISTENERS =======================================================================
    public void setOnEnemyShipDestroyedListener(OnEnemyShipDestroyedListener listener)
    {
        this.onEnemyShipDestroyedListener = listener;
    }

    public void setOnEnemyShipPassedListener(OnEnemyShipPassedListener listener) {  this.onEnemyShipPassedListener = listener;  }
}
