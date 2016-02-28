package edu.utah.cs4962.stardroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameActivity extends Activity implements GLSurfaceView.Renderer
{
    // Start location of the user's ship
    private float startX = -0.7f;
    private float startY = 0.0f;

    // Current location of the user's ship
    private float shipX = startX;
    private float shipY = startY;

    // Screen properties
    private float screenWidth = 0.0f;
    private float screenHeight = 0.0f;
    ImageView pauseButton = null;

    // Member variables
    private String playerName = "";
    private StardroidSpriteEngine spriteEngine = null;
    private StardroidModel stardroidModel = null;

    // Variables for displaying ship properties
    private TextView scoreText = null;
    private TextView speedText = null;
    private TextView firingRateText = null;
    private int shipSpeed = 1;
    private int firingRate = 1;

    // Boolean flags for game state
    private boolean isActive = false;
    private boolean isPaused = true;
    private boolean inGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Show the start screen
//        startScreen = new Intent(this, StartScreenActivity.class);
//        startActivityForResult(startScreen, 1);
//        initializeVariables(); // Do at end!

//        gameOverScreen = new Intent(this, GameOverActivity.class);

        // Create the renderer
        GLSurfaceView glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.setRenderer(this);

        scoreText = new TextView(this);
        scoreText.setText("Score = 0");

        speedText = new TextView(this);
        speedText.setText("Ship Speed = " + shipSpeed);

        firingRateText = new TextView(this);
        firingRateText.setText("Firing Rate = " + firingRate);

        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);

        infoLayout.addView(scoreText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        infoLayout.addView(speedText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        infoLayout.addView(firingRateText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        pauseButton = new ImageView(this);
        pauseButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_pause));

        pauseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isPaused = !isPaused;

                if (!isPaused)
                    pauseButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_pause));
                else
                    pauseButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play));
            }
        });
        infoLayout.addView(pauseButton);
//        infoGroup.addView(pauseButton);

//        pauseButton.setImageBitmap(R.drawable.pauseButton);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        mainLayout.addView(infoLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
//        mainLayout.addView(infoGroup, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        mainLayout.addView(glView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0));

        setContentView(mainLayout);

        // Initialize Model
        stardroidModel = StardroidModel.getInstance();
        SpaceShip userShip = stardroidModel.addUserSpaceship(shipX, shipY);
        userShip.setEngineSpeed(0.05f);
        userShip.setOnShipSpeedChangedListener(new SpaceShip.OnShipSpeedChangedListener()
        {
            @Override
            public void onShipSpeedChanged()
            {
                Runnable textUpdater = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        speedText.setText("Ship Speed = " + (++shipSpeed) + "     ");
                    }
                };

                speedText.postDelayed(textUpdater, 0);
            }
        });
        userShip.setOnFiringRateChangedListener(new SpaceShip.OnFiringRateChangedListener()
        {
            @Override
            public void onFiringRateChanged()
            {
                Runnable textUpdater = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        firingRateText.setText("Firing Rate = " + (++firingRate) + "     ");
                    }
                };

                firingRateText.postDelayed(textUpdater, 0);
            }
        });

        // Initialize Sprite Engine
        spriteEngine = new StardroidSpriteEngine();
        spriteEngine.setOnEnemyShipPassedListener(new StardroidSpriteEngine.OnEnemyShipPassedListener()
        {
            @Override
            public void onEnemyShipPassed(boolean isSpecial, SpaceShip enemy)
            {
                if (isSpecial)
                    stardroidModel.specialPassed(enemy);
                else
                    stardroidModel.enemyPassed(enemy);
            }
        });
        spriteEngine.setOnEnemyShipDestroyedListener(new StardroidSpriteEngine.OnEnemyShipDestroyedListener()
        {
            @Override
            public void onEnemyShipDestroyed(boolean isSpecial, SpaceShip enemy)
            {

                if (isSpecial)
                    stardroidModel.specialKilled(enemy);
                else
                    stardroidModel.enemyKilled(enemy);

                Runnable textUpdater = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int score = stardroidModel.getScore();
                        scoreText.setText("Score = " + score);
                    }
                };

                scoreText.postDelayed(textUpdater, 0);
            }
        });

        // Handle touches
        glView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    isActive = true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_POINTER_UP)
                    isActive = false;

                if (isActive && !isPaused)
                {
                    shipX = 2 * (event.getRawX() - screenWidth /2)/ screenWidth + 1f * stardroidModel.getUserShip().getShipWidth();
                    shipY = -2 * (event.getRawY() - screenHeight /2)/ screenHeight + 0.5f * stardroidModel.getUserShip().getShipHeight();

                    stardroidModel.moveUserShip(shipX, shipY, 0.0f);
                }
                return true;
            }
        });

        initializeVariables();
    }

    private void initializeVariables()
    {
        Bundle extras = getIntent().getExtras();

        if (extras == null)
            return;

        playerName = extras.getString("PlayerName");

        isPaused = false;
        inGame = true;
        stardroidModel.resetModel();
        stardroidModel.addUserSpaceship(startX, startY).setOnShipDestroyedListener(new SpaceShip.OnShipDestroyedListener()
        {
            @Override
            public void onShipDestroyed()
            {
                isPaused = true;
                inGame = false;

                Intent intentExtras = new Intent();
                setResult(RESULT_OK, intentExtras);

                intentExtras.putExtra("PlayerName", playerName);
                intentExtras.putExtra("ShipSpeed", shipSpeed);
                intentExtras.putExtra("FiringRate", firingRate);
                intentExtras.putExtra("EnemiesKilled", stardroidModel.getScore());

                finish();
            }
        });

        shipSpeed = 1;
        firingRate = 1;
        scoreText.setText("Score = 0");
        speedText.setText("Ship Speed = " + shipSpeed);
        firingRateText.setText("Firing Rate = "  + firingRate);

        SpaceShip userShip = stardroidModel.getUserShip();
        userShip.setOnShipSpeedChangedListener(new SpaceShip.OnShipSpeedChangedListener()
        {
            @Override
            public void onShipSpeedChanged()
            {
                Runnable textUpdater = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        speedText.setText("Ship Speed = " + (++shipSpeed) + "     ");
                    }
                };

                speedText.postDelayed(textUpdater, 0);
            }
        });
        userShip.setOnFiringRateChangedListener(new SpaceShip.OnFiringRateChangedListener()
        {
            @Override
            public void onFiringRateChanged()
            {
                Runnable textUpdater = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        firingRateText.setText("Firing Rate = " + (++firingRate) + "     ");
                    }
                };

                firingRateText.postDelayed(textUpdater, 0);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (inGame && !isPaused)
        {
            pauseButton.performClick();
        }
        else
            finish();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // Clear the screen
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Enable textures
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Texture variables
        int userShipTexture = importTexture(R.drawable.usership);
        int enemyShipTexture = importTexture(R.drawable.enemyship);
        int specialEnemyShipTexture = importTexture(R.drawable.specialenemyship);
        int bulletTexture = importTexture(R.drawable.bullet);
        int starTexture = importTexture(R.drawable.star);
        int fullHealthTexture = importTexture(R.drawable.green);
        int halfHealthTexture = importTexture(R.drawable.yellow);
        int lowHealthTexture = importTexture(R.drawable.red);
        int pauseTexture = importTexture(R.drawable.pausescreen);

        // Import into Sprite Engine
        spriteEngine.setTextures(userShipTexture,
                enemyShipTexture,
                specialEnemyShipTexture,
                bulletTexture,
                starTexture,
                fullHealthTexture,      // healthRestoreTexture
                starTexture,            // enginePowerTexture
                bulletTexture,          // bulletStreamsTexture
                halfHealthTexture,      // bulletSpeedTexture
                fullHealthTexture,
                halfHealthTexture,
                lowHealthTexture,
                pauseTexture);
    }

    /**
     *
     * @param id The resource ID to pull the texture from
     * @return The int value corresponding to the texture in GLES20
     */
    private int importTexture(int id)
    {
        // Get the image and store the buffer
        Bitmap textureImage = BitmapFactory.decodeResource(getResources(), id);
        IntBuffer textures = IntBuffer.allocate(1);
        GLES20.glGenTextures(1, textures);
        int texture = textures.get(0);

        // Bind the texture to GLES20
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureImage, 0);

        return texture;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0,height/10, width, height);
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        SpaceShip userShip = stardroidModel.getUserShip();

        if (isActive)
            stardroidModel.addUserBullet(userShip.getShipX() + stardroidModel.getUserShip().getShipWidth(), userShip.getShipY());

        if (!isPaused)
            spriteEngine.draw(stardroidModel.getUserShip(), stardroidModel.getUserBullets(), stardroidModel.getCollidingBullets(), stardroidModel.generateEnemies(), stardroidModel.generateSpecialEnemies(), stardroidModel.getPowerUps()); // DOESN'T REQUIRE COLLIDING BULLETS
        else if (inGame)
            spriteEngine.drawPause();
    }
}


