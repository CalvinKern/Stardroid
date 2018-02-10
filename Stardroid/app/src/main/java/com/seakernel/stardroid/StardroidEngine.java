package com.seakernel.stardroid;

import com.seakernel.stardroid.model.EnemyController;
import com.seakernel.stardroid.model.StardroidModel;
import com.seakernel.stardroid.model.controller.Pause;
import com.seakernel.stardroid.model.controller.StardroidShape;
import com.seakernel.stardroid.model.controller.effect.Explosion;
import com.seakernel.stardroid.model.controller.effect.Star;
import com.seakernel.stardroid.model.controller.ship.BaseShip;
import com.seakernel.stardroid.model.controller.ship.UserShip;
import com.seakernel.stardroid.model.controller.weapon.Projectile;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class performs as the sprite engine that draws all required elements for the Stardroid game
 *
 * Created by Calvin on 2/27/16.
 */
public class StardroidEngine {

    private final static float MAGIC_FINGER_OFFSET = 0.25f;
    private static final int MAGIC_MAX_COUNT_STAR = 169; // REDUCE this to improve FPS ;)
    private static final int MAGIC_MAX_COUNT_OBJECTS = 800; // This is the max for 60 fps on a good phone (Nexus 6P)

    // Member Variables
    private float mAspectRatio;
    private Pause mPauseSprite = null;
    private ArrayList<Star> mStars = null;
    public BaseShip mUserShip = null;
    private float mElapsedTime;
    private List<Explosion> mExplosions;
    private boolean mResetGame;
    private EnemyController mEnemyController;

    private final StardroidModel.GameStateChangeWatcher mGameStateChangeWatcher = new StardroidModel.GameStateChangeWatcher() {
        @Override
        public void onStateChanged(int oldState, int newState) {
            if (oldState != StardroidModel.GameState.RUNNING && oldState != StardroidModel.GameState.PAUSED &&
                    newState == StardroidModel.GameState.RUNNING) {
                mResetGame = true;
            }
        }
    };

    public StardroidEngine() {
        StardroidModel.getInstance().addGameStateChangeWatcher(mGameStateChangeWatcher); // TODO: Remove this somehow...
        mEnemyController = new EnemyController();
    }

    /**
     * @return total number of objects tracked for drawing
     */
    public int getObjectCount() {
        return mStars.size();
    }

    public void receiveTouch(final float normX, final float normY) {
        final float x = (( 2 * normX) - 1) * mAspectRatio;
        final float y = (-2 * normY) + 1;

        if (mPauseSprite.receiveTouch(x, y) || StardroidModel.getInstance().isPaused()) {
            return;
        }

        mUserShip.moveToPosition(x + MAGIC_FINGER_OFFSET, y);

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
        mEnemyController.setBounds(new float[]{ -aspectRatio, 1.0f, aspectRatio, -1.0f});
    }

    public void resetGame() {
        mUserShip = new UserShip();
        mPauseSprite = new Pause(new float[]{ -mAspectRatio, 1.0f, mAspectRatio, -1.0f});

        mEnemyController.resetState();
        StardroidModel.getInstance().resetScore();
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
                Star star = new Star(getRandomPointOnScreen(), (float)(Math.random() * 2 - 1));
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

        if (mResetGame) {
            mResetGame = false;
            resetGame();
        }

        drawPause(mvpMatrix, dt);
        drawGame(mvpMatrix, dt);
    }

    private boolean isShapeOutOfBounds(final StardroidShape shape) {
        return shape.getBounds()[2] < -mAspectRatio || shape.getBounds()[0] > mAspectRatio;
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

        int enemiesDestroyedPoints = 0;
        for (StardroidShape shape : list) {

            if (isShapeOutOfBounds(shape)) {
//                Log.d("StardroidEngine", "Shape leaving (" + shape.getPositionX() + "," + shape.getPositionY() + ")");
                shapesLeaving.add(shape);
                continue;
            }

            shape.doDraw(mvpMatrix, dt);
            if (shape instanceof BaseShip) {
                BaseShip ship = (BaseShip) shape;
                ship.destroyProjectiles(drawAndCheckCollisions(ship.getProjectiles(), mvpMatrix, dt));

                List<Projectile> hitProjectiles = new ArrayList<>();

                // Simple collision detection
                for (Projectile projectile : mUserShip.getProjectiles()) {

                    if (ship.hasCollided(projectile.getBounds())) {
                        hitProjectiles.add(projectile);
                        mExplosions.add(new Explosion(ship));
                        enemiesDestroyedPoints += ship.getPoints();
                        ship.destroy();
                        shapesLeaving.add(ship);
                    }
                }

                if (StardroidModel.getInstance().isGameRunning() && mUserShip.hasCollided(ship.getBounds())) {
                    Explosion enemyExplosion = ship.shipHit();
                    if (enemyExplosion != null) {
                        mExplosions.add(enemyExplosion);
                        enemiesDestroyedPoints += ship.getPoints();
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

        StardroidModel.getInstance().addScore(enemiesDestroyedPoints);

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
    private void drawPause(float[] mvpMatrix, float dt) {
        mPauseSprite.doDraw(mvpMatrix, dt); // TODO: Change this to doDraw the resume button (that also states paused)
    }

    private void drawGame(float[] mvpMatrix, float dt) {
        if (StardroidModel.getInstance().isPaused()) {
            return;
        }

        drawUser(mvpMatrix, dt);
        drawEnemyShips(mvpMatrix, dt);
    }

    private void drawUser(float[] mvpMatrix, float dt) {
        // TODO: figure out a cleaner way to draw projectiles while checking out of bounds (should be in ship class, but it doesn't have aspect ratio...)
        mUserShip.destroyProjectiles(drawAndCheckCollisions(mUserShip.getProjectiles(), mvpMatrix, dt));
        if (StardroidModel.getInstance().isGameRunning()) {
            mUserShip.doDraw(mvpMatrix, dt);
        }
    }

    /**
     * Draws and creates enemy ships as well as their projectiles
     *
     * @param mvpMatrix
     * @param dt
     */
    private void drawEnemyShips(float[] mvpMatrix, float dt) {
        mEnemyController.destroyEnemies(drawAndCheckCollisions(mEnemyController.getEnemies(), mvpMatrix, dt));
        if (StardroidModel.getInstance().isGameRunning()) {
            mEnemyController.addNewEnemySet(dt);
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
}
