package com.seakernel.stardroid;

import com.seakernel.stardroid.model.Explosion;
import com.seakernel.stardroid.model.Projectile;
import com.seakernel.stardroid.model.SpaceShip;
import com.seakernel.stardroid.model.StardroidModel;
import com.seakernel.stardroid.model.StardroidPause;
import com.seakernel.stardroid.model.StardroidShape;
import com.seakernel.stardroid.model.StardroidStar;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class performs as the sprite engine that draws all required elements for the Stardroid game
 *
 * Created by Calvin on 2/27/16.
 */
public class StardroidEngine {

    private static final int MAGIC_MAX_COUNT_STAR = 169; // REDUCE this to improve FPS ;)
    private static final int MAGIC_MAX_COUNT_OBJECTS = 800; // This is the max for 60 fps on a good phone (Nexus 6P)

    // Member Variables
    private float mAspectRatio;
    private StardroidPause mPauseSprite = null;
    private ArrayList<StardroidStar> mStars = null;
    public SpaceShip mUserShip = null;
    private ArrayList<SpaceShip> mEnemyShips;
    private float mElapsedTime;
    private float mMillisecondsBetweenEnemyCreation = 1500;
    private List<Explosion> mExplosions;
    private StardroidModel mModel;
    private int mEnemiesDestroyed; // TODO: Keep track of in model (probably via callback on enemies being destroyed))

    /**
     * @return total number of objects tracked for drawing
     */
    public int getObjectCount() {
        return mStars.size();
    }

    public void receiveTouch(float normX, float normY) {
        normX = ( 2 * normX) - 1;
        normY = (-2 * normY) + 1;
        mUserShip.moveToPosition(normX * mAspectRatio, normY);

//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.d("GameFragment", String.format("Touch down at (%f, %f)", shipX, shipY));
//        }
    }

    /**
     * This method initializes all necessary data structures and GLES20
     */
    public void initializeScreen(float aspectRatio) {
        mAspectRatio = aspectRatio;

        // initialize the stars in the background for the start of the game
        generateBackground();
        resetGame();
    }

    public void resetGame() {
        mUserShip = new SpaceShip();
        mEnemyShips = new ArrayList<>();
        mPauseSprite = new StardroidPause();

        mUserShip.setCanShoot(true);
        mUserShip.setEngineSpeed(20f);
    }

    private void generateBackground() {
        mExplosions = new ArrayList<>();

        generateStars();
    }

    /**
     * This initializes the list of stars for the game
     */
    private void generateStars() {
        mStars = new ArrayList<>();
        if (mStars.size() == 0) {
            for (int i = 0; i < MAGIC_MAX_COUNT_STAR; i++) {
                StardroidStar star = new StardroidStar(getRandomPointOnScreen(), getRandomPointOnScreen());
                mStars.add(star);
            }
        }
    }

    private float getRandomPointOnScreen() {
        return (float) (Math.random() * mAspectRatio * 2) - mAspectRatio;
    }

    public void setTextures() { /*TODO:...Once I have something to texture*/ }

    public float getUserEngineSpeed() {
        return mUserShip.getEngineSpeed();
    }

    // -- Drawing Methods

    public void draw(float[] mvpMatrix, float dt) {
        drawStars(mvpMatrix, dt); // First so it can go in the background

        drawExplosions(mvpMatrix, dt);
        mModel = StardroidModel.getInstance();

        if (mModel.isGameRunning()) {
            drawGameRunning(mvpMatrix, dt);
        } else if (mModel.isPaused()) {
            drawPause(mvpMatrix, dt);
        } else if (mModel.getState() == StardroidModel.GameState.END) {
            resetGame();
        }

        mModel = null; // Clean up reference
    }

    private boolean isShapeOutOfBounds(StardroidShape shape) {
        float[] screenBounds = new float[]{-mAspectRatio, 1.f, mAspectRatio, -1.f};
        return !shape.hasCollided(screenBounds);
    }

    /**
     * Draws the list of shapes and returns a sublist of any shapes that have gone out of bounds
     *
     * @param list the list of shapes to draw and check for out of bounds
     * @param mvpMatrix
     * @param dt
     * @return the sublist of shapes that are out of bounds
     */
    private List<StardroidShape> drawAndCheckCollisions(List<? extends StardroidShape> list, float[] mvpMatrix, float dt) {
        ArrayList<StardroidShape> shapesLeaving = new ArrayList<>();

        for (StardroidShape shape : list) {

            if (isShapeOutOfBounds(shape)) {
//                Log.d("StardroidEngine", "Shape leaving (" + shape.getPositionX() + "," + shape.getPositionY() + ")");
                shapesLeaving.add(shape);
                continue;
            }

            shape.doDraw(mvpMatrix, dt);
            if (shape instanceof SpaceShip) {
                SpaceShip ship = (SpaceShip) shape;
                ship.destroyProjectiles(drawAndCheckCollisions(ship.getProjectiles(), mvpMatrix, dt));

                List<Projectile> hitProjectiles = new ArrayList<>();

                // Simple collision detection
                for (Projectile projectile : mUserShip.getProjectiles()) {

                    if (ship.hasCollided(projectile.getBounds())) {
                        hitProjectiles.add(projectile);
                        mExplosions.add(new Explosion(ship));
                        mEnemiesDestroyed++;
                        ship.destroy();
                        shapesLeaving.add(ship);
                    }
                }

                if (mUserShip.hasCollided(ship.getBounds())) {
                    Explosion enemyExplosion = ship.shipHit();
                    if (enemyExplosion != null) {
                        mExplosions.add(enemyExplosion);
                        mEnemiesDestroyed++;
                        ship.destroy();
                        shapesLeaving.add(ship);
                    }

                    Explosion explosion = mUserShip.shipHit();
                    if (explosion != null) {
                        mExplosions.add(explosion);
                        StardroidModel.getInstance().endGame();
                    }
                }

                mUserShip.destroyProjectiles(hitProjectiles);
            }
        }

        return shapesLeaving;
    }

    private void drawStars(float[] mvpMatrix, float dt) {
        List<StardroidShape> passed = drawAndCheckCollisions(mStars, mvpMatrix, dt);

        for (StardroidShape starToReset : passed) {
            // FIXME: 7/23/2017 instead of setting from mAspectRatio, get it from mvp so that it can scale to the size of the actual play area, or maybe that's just how the cookie crumbles
            starToReset.setPositionX(mAspectRatio);
        }
    }

    private void drawExplosions(float[] mvpMatrix, float dt) {
        for (Explosion explosion : new ArrayList<>(mExplosions)) {
            explosion.draw(mvpMatrix, dt);
            if (explosion.isFinished()) {
                mExplosions.remove(explosion);
            }
        }
    }

    /**
     * @param mvpMatrix
     * @return true if the game is paused
     */
    private boolean drawPause(float[] mvpMatrix, float dt) {
        // If paused, only doDraw the
        if (StardroidModel.getInstance().isPaused()) {
            mPauseSprite.doDraw(mvpMatrix, dt); // TODO: Change this to doDraw the resume button (that also states paused)
            return true; // Return here if we are paused so we don't keep drawing everything else
        } else {
            // TODO: Modify mvp to doDraw pause in the (top-right?) corner.
//            final float[] copyMvp = new float[mvpMatrix.length];
//            System.arraycopy(mvpMatrix, 0, copyMvp, 0, mvpMatrix.length); // TODO: in place state saving of mvpMatrix for efficiency?

            // TODO: doDraw once the mvp is correct
//            mPauseSprite.doDraw(copyMvp);
            return false;
        }
    }

    private void drawGameRunning(float[] mvpMatrix, float dt) {
        drawUser(mvpMatrix, dt);
        drawEnemyShips(mvpMatrix, dt);
    }

    private void drawUser(float[] mvpMatrix, float dt) {
        // TODO: figure out a cleaner way to draw projectiles while checking out of bounds (should be in ship class, but it doesn't have aspect ratio...)
        mUserShip.destroyProjectiles(drawAndCheckCollisions(mUserShip.getProjectiles(), mvpMatrix, dt));
        mUserShip.doDraw(mvpMatrix, dt);
    }

    /**
     * Draws and creates enemy ships as well as their projectiles
     *
     * @param mvpMatrix
     * @param dt
     */
    private void drawEnemyShips(float[] mvpMatrix, float dt) {
        mEnemyShips.removeAll(drawAndCheckCollisions(mEnemyShips, mvpMatrix, dt));
        createEnemy(dt);
    }

    public void createEnemy(float dt) {
        mElapsedTime += dt;
        if (mElapsedTime >= mMillisecondsBetweenEnemyCreation) {
            mElapsedTime = 0;
            SpaceShip ship = new SpaceShip(mAspectRatio, (float)Math.random() * 1.8f - 0.9f);
            ship.setEngineSpeed(5);
            ship.setCanShoot(false);
            ship.moveToPosition(-mAspectRatio * 2, ship.getPositionY());

            mEnemyShips.add(ship);
//            Log.d("StardroidEngine", "Total Enemies: " + mEnemyShips.size());
        }
    }

    public boolean setUserEngineSpeed(float engineSpeed) {
        return mUserShip.setEngineSpeed(engineSpeed);
    }

    public boolean incrementEngineSpeed() {
        return mUserShip.incrementEngineSpeed();
    }

    public void resetUserEngineSpeed() {
        mUserShip.resetEngineSpeed();
    }

    public int getScore() {
        return mEnemiesDestroyed;
    }
}
