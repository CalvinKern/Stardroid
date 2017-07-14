package com.seakernel.stardroid;

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

import com.seakernel.stardroid.model.StardroidModel;
import com.seakernel.stardroid.utilities.Profiler;

import java.nio.IntBuffer;

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

//    private int mScreenWidth;
//    private int mScreenHeight;
    private boolean mHasActiveTouch;
    private StardroidEngine mStardroidEngine = null;

    // View Matrices
    // mMvMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMvMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float mRatio;

    // FPS variables
    private int mFrameCount = 0;
    private long mFrameStartNano = System.nanoTime();
    private static final int FRAME_COLLECTION_SIZE = 1;

    public static GameFragment newInstance() {
        Bundle args = new Bundle();
        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStardroidEngine = new StardroidEngine(); // TODO: Make this retain state when put in the background
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);

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

                if (mHasActiveTouch && !StardroidModel.getInstance().isPaused()) {
//                    shipX = 2 * (event.getRawX() - screenWidth / 2) / screenWidth + 1f * stardroidModel.getUserShip().getShipWidth();
//                    shipY = -2 * (event.getRawY() - screenHeight / 2) / screenHeight + 0.5f * stardroidModel.getUserShip().getShipHeight();
//
//                    stardroidModel.moveUserShip(shipX, shipY, 0.0f);
                }
                return true;
            }
        });

        return root;
    }

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
//        mScreenWidth = width;
//        mScreenHeight = height;

        mRatio = (float) width / height;
        mStardroidEngine.initializeScreen(mRatio);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -mRatio, mRatio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Profiler profiler = Profiler.getInstance();
        profiler.trackFrame(mStardroidEngine);

        StardroidModel model = StardroidModel.getInstance(); // TODO: Definitely change the model to hold stars, and everything else
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // TODO: Can these matrices be set in onSurfaceChanged, so that it only happens once?
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMvMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

//        SpaceShip userShip = model.getUserShip();
//
//        if (isActive)
//            model.addUserBullet(userShip.getShipX() + userShip.getShipWidth(), userShip.getShipY());
//
        profiler.startTrackingSection();
        mStardroidEngine.draw(mMvMatrix, mRatio);
        profiler.stopTrackingSection("Drawing");

//        if (!model.isPaused()) {
//            mStardroidEngine.draw(userShip, model.getUserBullets(), model.getCollidingBullets(), model.generateEnemies(), model.generateSpecialEnemies(), model.getPowerUps()); // DOESN'T REQUIRE COLLIDING BULLETS
//        } else {
//            mStardroidEngine.drawPause();
//        }
    }
}
