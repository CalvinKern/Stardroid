package edu.utah.cs4962.stardroid;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class represents the model of the game.  All necessary information about the game is stored here.
 * Created by Calvin on 12/2/13.
 */
public class StardroidModel
{
    // Member variables
    private static StardroidModel model = null;

    // Game items
    private SpaceShip userShip = null;
    private ArrayList<Bullet> bullets = null;
    private ArrayList<SpaceShip> enemyList = null;
    private ArrayList<SpaceShip> specialEnemyList = null;
    private ArrayList<PowerUp> powerUpList = null;
    private ArrayList<Bullet> collidingBullets = null;    // Not using

    private int enemiesKilled = 0;
    private boolean canGenerateEnemy = false;
    private boolean canGenerateSpecial = false;
    private boolean canRaiseDifficultyLevel = false;
    private int difficultyLevel = 1;
    private Calendar startTime = null;

    private StardroidModel()
    {
        bullets = new ArrayList<Bullet>();
        collidingBullets = new ArrayList<Bullet>();
        enemyList = new ArrayList<SpaceShip>();
        specialEnemyList = new ArrayList<SpaceShip>();
        powerUpList = new ArrayList<PowerUp>();
        startTime = Calendar.getInstance();
    }

    public static StardroidModel getInstance()
    {
        if (model == null)
            model = new StardroidModel();

        return model;
    }

    public SpaceShip addUserSpaceship(float shipX, float shipY)
    {
        if (userShip != null)
            return userShip;

        userShip = new SpaceShip(shipX, shipY);
        return userShip;
    }

    public void moveUserShip(float shipX, float shipY, float rotation)
    {
        userShip.setRotationAngle(rotation);
        userShip.moveShipToX(shipX);
        userShip.moveShipToY(shipY);
    }

    public void addUserBullet(float bulletX, float bulletY)
    {
        int bulletCount = bullets.size();

        // Check if there are no bullets or if the last bullet has traveled enough to fire another
        if (bulletCount == 0 || bullets.get(bulletCount - 1).getDistanceTraveled() >= userShip.getFiringRate())
        {
            int weaponStreams = userShip.getWeaponStreams();

            // Draw the bullet at the correct y position
            for (int streams = 1; streams <= weaponStreams; streams++)
            {
                if (weaponStreams % 2 == 0) // Deal with even streams (no center stream)
                {
                    if (streams <= weaponStreams / 2) // Add the top bullet
                    {
                        Bullet bullet = new Bullet(bulletX, 0);
                        float newY = bulletY + 2 * bullet.getBulletHeight();
                        bullet.setCenterY(newY);
                        bullets.add(bullet);
                    }
                    else // Add the bottom bullet
                    {
                        Bullet bullet = new Bullet(bulletX, 0);
                        float newY = bulletY - 2 * bullet.getBulletHeight();
                        bullet.setCenterY(newY);
                        bullets.add(bullet);
                    }
                }
                else // Deal with odd streams here (has center stream)
                {
                    if (streams <= weaponStreams / 2) // Add the top bullet
                    {
                        Bullet bullet = new Bullet(bulletX, 0);
                        float newY = bulletY + 4 * bullet.getBulletHeight();
                        bullet.setCenterY(newY);
                        bullets.add(bullet);
                    }
                    else if (streams == weaponStreams / 2 + 1) // Add the center stream
                    {
                        bullets.add(new Bullet(bulletX, bulletY));
                    }
                    else // Add the bottom bullet
                    {
                        Bullet bullet = new Bullet(bulletX, 0);
                        float newY = bulletY - 4 * bullet.getBulletHeight();
                        bullet.setCenterY(newY);
                        bullets.add(bullet);
                    }
                }
            }
        }

        // Check for old bullets that are past the screen
        for (int i = 0; i < userShip.getWeaponStreams(); i++)
            if (bullets.get(0).getCenterX() >= 2.0f) bullets.remove(0);
    }

    public ArrayList<SpaceShip> generateEnemies()
    {
        if (canRaiseDifficultyLevel && ((enemiesKilled)% 10 == 0))
        {
            canRaiseDifficultyLevel = false;
            difficultyLevel++;
        }

        if (enemyList.size() >= difficultyLevel)
        {
            canGenerateEnemy = false;
            return enemyList;
        }

        long previousEnemyTime = startTime.getTimeInMillis();
        long currentTime = Calendar.getInstance().getTimeInMillis();

        long timePassed = Math.abs(currentTime - previousEnemyTime);
        long canSpawnTime = 1500 - difficultyLevel*5;
        long spawnEnemyTime = 100;

        if (!canGenerateEnemy && timePassed >= canSpawnTime || canGenerateEnemy && timePassed >= spawnEnemyTime) // Correct (but no WALL)
        {
            canGenerateEnemy = true;

            float MAX = 0.9f - 2 * userShip.getShipHeight();
            float MIN = -1.0f + 2 * userShip.getShipWidth();

            int difficultyRand = (int)(Math.random() * difficultyLevel) + 1;

            for (int i = 0; i < difficultyRand; i++)
            {
                float rand = (float)(Math.random() * (MAX - MIN) + MIN);
                enemyList.add(new SpaceShip(1.0f, rand));
            }

            startTime = Calendar.getInstance();
        }

        if (enemyList.size() > 0 && enemyList.get(0).getShipX() <= -1.1f)
            enemyList.remove(0);

        //TODO: gradually increase THE WALL

//        if (currentSecond - startSecond >= 2 || startSecond >= 57 && currentSecond >= 3) // Buggy (but has THE WALL)
//        if (timePassed >= canSpawnTime) // Correct (but no WALL)
/*
        if (timePassed >= canSpawnTime*difficulty)
        {
            float rand = (float)(Math.random() * 2 - 1);
            enemyList.add(new SpaceShip(1.0f, rand));
            startTime = Calendar.getInstance();
        }
*/
        return new ArrayList<SpaceShip>(enemyList);
    }

    public ArrayList<SpaceShip> generateSpecialEnemies()
    {
        if (specialEnemyList.size() == 0)
        {
            if (canGenerateSpecial && (enemiesKilled) % (10 + difficultyLevel/5) == 0)
            {
                float MAX = 0.9f - 2 * userShip.getShipHeight();
                float MIN = -1.0f + 2 * userShip.getShipWidth();

                float rand = (float)(Math.random() * (MAX - MIN) + MIN);
                SpaceShip special = new SpaceShip(1.0f, rand);
                specialEnemyList.add(special);
                canGenerateSpecial = false;
            }
        }

        return specialEnemyList;
    }

    public SpaceShip getUserShip()
    {
        return userShip;
    }

    public ArrayList<Bullet> getUserBullets()
    {
        return bullets;
    }

    // No longer using colliding bullets
    public ArrayList<Bullet> getCollidingBullets()
    {
        return collidingBullets;
    }

    public ArrayList<PowerUp> getPowerUps()
    {
        return powerUpList;
    }

    public void enemyKilled(SpaceShip enemyShip)
    {
        enemiesKilled++;
        canGenerateSpecial = true;
        canRaiseDifficultyLevel = true;

        enemyList.remove(enemyShip);
    }

    public void enemyPassed(SpaceShip enemyShip)
    {
        enemyList.remove(enemyShip);
    }

    public void specialKilled(SpaceShip specialEnemy)
    {
        enemiesKilled++;
        canGenerateSpecial = true;

        PowerUp powerUp = new PowerUp(specialEnemy.getShipX(), specialEnemy.getShipY());
        powerUpList.add(powerUp);

        specialEnemyList.remove(specialEnemy);
    }

    public void specialPassed(SpaceShip specialEnemy)
    {
        specialEnemyList.remove(specialEnemy);
    }

    public int getScore()
    {
        return enemiesKilled;
    }

    public void resetModel()
    {
        userShip = null;
        specialEnemyList = new ArrayList<SpaceShip>();
        bullets = new ArrayList<Bullet>();
        collidingBullets = new ArrayList<Bullet>();
        enemyList = new ArrayList<SpaceShip>();
        powerUpList = new ArrayList<PowerUp>();
        enemiesKilled = 0;
        canGenerateEnemy = false;
        canGenerateSpecial = false;
        canRaiseDifficultyLevel = false;
        difficultyLevel = 1;
        startTime = Calendar.getInstance();
    }
}
