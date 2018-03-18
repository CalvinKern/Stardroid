package com.seakernel.stardroid;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seakernel.stardroid.model.StardroidModel;
import com.seakernel.stardroid.utilities.Profiler;

import java.nio.IntBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Calvin on 2/27/16.
 */
public class GameFragment extends Fragment implements GLSurfaceView.Renderer {

    private static final int[] TEXTURE_DRAWABLE_IDS = new int[] {
            R.drawable.usership,
//            R.drawable.enemyship,
//            R.drawable.specialenemyship,
//            R.drawable.bullet,
            R.drawable.star,
//            R.drawable.green,
//            R.drawable.yellow,
//            R.drawable.red,
            R.drawable.pausescreen,
    };

    private Timer mTimer;
    private float mScreenWidth;
    private float mScreenHeight;
    private boolean mHasActiveTouch;
    private StardroidEngine mStardroidEngine = null;

    // View Matrices
    private final float[] mMvpMatrix = new float[16]; // Model View Projection Matrix
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];

    // FPS variables
    private TextView mScoreTextView;
    private TextView mFpsTextView;
    private TextView mEngineSpeedTextView;
    private TextView mPowerUpDurationTextView;

    private final StardroidModel.GameStateChangeWatcher mGameStateChangeWatcher = new StardroidModel.GameStateChangeWatcher() {
        @Override
        public void onStateChanged(int oldState, int newState) {
            // Reset the touch on state change so the user doesn't experience any overlapping state
            //  problems (such as moving the ship to where the pause button was when resuming play,
            //  if they keep their finger pressed down)
            mHasActiveTouch = false;
        }
    };

    public static GameFragment newInstance() {
        Bundle args = new Bundle();
        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Only schedule fps text updater if we're in debug
        if (BuildConfig.DEBUG) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateUiViews();
                }
            }, 1000, 500);
        }

        StardroidModel.getInstance().addGameStateChangeWatcher(mGameStateChangeWatcher);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); // TODO: Save game state
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        StardroidModel.getInstance().removeGameStateChangeWatcher(mGameStateChangeWatcher);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);

        mStardroidEngine = new StardroidEngine(); // TODO: Restore game state

        // Only waste time getting the frames per second label if we're in debug mode
        if (BuildConfig.DEBUG) {
            mFpsTextView = (TextView) root.findViewById(R.id.fps_label);
            mScoreTextView = (TextView) root.findViewById(R.id.score_label);
            mEngineSpeedTextView = (TextView) root.findViewById(R.id.engine_speed_label);
            mPowerUpDurationTextView = (TextView) root.findViewById(R.id.ship_power_up_duration_left_label);
        }

        GLSurfaceView glView = (GLSurfaceView) root.findViewById(R.id.game_surface);
        glView.setEGLContextClientVersion(2);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.setRenderer(this);

        // Handle touches
        glView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mHasActiveTouch = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_POINTER_UP) {
                    mHasActiveTouch = false;
                }

                if (mHasActiveTouch) {
                    mStardroidEngine.receiveTouch(event.getRawX() / mScreenWidth, event.getRawY() / mScreenHeight);
                }
                return true;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFpsTextView = null;
        mScoreTextView = null;
        mEngineSpeedTextView = null;
        mPowerUpDurationTextView = null;

        if (mStardroidEngine != null) {
            mStardroidEngine = null;
        }
    }

    private void updateUiViews() {
        final Activity activity = getActivity();
        if (activity == null || isDetached()) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateFpsTextView();
                updateScoreTextView();
//                updateSpeedTextView(); // Not used for now
                updatePowerUpDurationTextView();
            }
        });
    }

    private void updateFpsTextView() {
        if (mFpsTextView != null) {
            mFpsTextView.setText(getString(R.string.fps, Profiler.getInstance().getCurrentFramesPerSecond()));
        }
    }

    private void updateScoreTextView() {
        if (mScoreTextView != null) {
            StardroidModel model = StardroidModel.getInstance();
            if (model.isGameRunning()) {
                mScoreTextView.setText(getString(R.string.score, model.getScore()));
            } else {
                mScoreTextView.setText(null);
            }
        }
    }

    private void updateSpeedTextView() {
        if (mEngineSpeedTextView != null) {
//            if (!mStardroidEngine.incrementEngineSpeed()) {
//                mStardroidEngine.resetUserEngineSpeed();
//            }

            final float engineSpeed = mStardroidEngine.getUserEngineSpeed();
            mEngineSpeedTextView.setText(getString(R.string.engine_speed, engineSpeed));
        }
    }

    private void updatePowerUpDurationTextView() {
        if (mPowerUpDurationTextView == null) {
            return;
        }

        StardroidModel model = StardroidModel.getInstance();
        if (model.getPowerUpSecondsLeft() <= 0) {
            mPowerUpDurationTextView.setText("");
        } else {
            mPowerUpDurationTextView.setText(getString(R.string.power_up_duration, model.getPowerUpSecondsLeft()));
        }
    }

    // =============================================================================================
    // OpenGL Initialization Methods
    // =============================================================================================

    /**
     * This method will import the texture resources into GLES 2.0 and return an array of those
     * imported int ID's.
     *
     * @return an int[] of the ID's for the GLES 2.0 textures
     */
    private int[] importTextures() {
        int size = TEXTURE_DRAWABLE_IDS.length;
        int[] textures = new int[size];

        // Loop through all textures importing them and storing their ID's
        for (int i = 0; i < size; i++) {
            textures[i] = importTexture(TEXTURE_DRAWABLE_IDS[i]);
        }

        return textures;
    }

    /**
     *
     * @param id The resource ID to pull the texture from
     * @return The int value corresponding to the texture in GLES20
     */
    private int importTexture(int id) {
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
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Clear the screen
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Enable textures
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Texture variables
        int[] textures = importTextures();

        // When working on below, may be nice to pass in TEXTURE_DRAWABLE_IDS into setTextures()

        mStardroidEngine.setTextures();
        //  in order to determine the index into 'textures'
        //        // Import into Sprite Engine
        //        mStardroidEngine.setTextures(userShipTexture,
        //                enemyShipTexture,
        //                specialEnemyShipTexture,
        //                bulletTexture,
        //                starTexture,
        //                fullHealthTexture,      // healthRestoreTexture
        //                starTexture,            // enginePowerTexture
        //                bulletTexture,          // bulletStreamsTexture
        //                halfHealthTexture,      // bulletSpeedTexture
        //                fullHealthTexture,
        //                halfHealthTexture,
        //                lowHealthTexture,
        //                pauseTexture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // Store the size of the screen
        mScreenWidth = width;
        mScreenHeight = height;

        float aspectRatio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 3, 7);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        mStardroidEngine.initializeScreen(aspectRatio);
    }

    // =============================================================================================
    // Drawing Methods
    // =============================================================================================

    @Override
    public void onDrawFrame(GL10 gl) {
        Profiler profiler = Profiler.getInstance();
        float dt = profiler.trackFrame(mStardroidEngine);

        StardroidModel model = StardroidModel.getInstance(); // TODO: Definitely change the model to hold stars, and everything else
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // TODO: push mMvpMatrix; google effective storage methods
        profiler.startTrackingSection();
        if (mStardroidEngine != null) {
            mStardroidEngine.draw(mMvpMatrix, dt);
        }
        profiler.stopTrackingSection("Drawing");
    }
}
