package com.seakernel.stardroid.utilities;

import android.util.Log;

import com.seakernel.stardroid.BuildConfig;
import com.seakernel.stardroid.StardroidEngine;

import java.util.Stack;

/**
 * A profiler used to track time for components.
 *
 * The primary use of this class is general frames per second logging, which requires the user
 * to call {@link #trackFrame(StardroidEngine)} at the beginning of each frame. This will
 * automatically log out the current FPS based on an internal interval.
 *
 * The secondary use of this class is to track time for a specific section while updating a frame.
 *
 * While there exists a public constructor to manage your own objects (timing on different threads)
 * there also exists a singleton object to use for general tracking of drawing.
 *
 * Created by Calvin on 7/13/2017.
 */

public class Profiler {
    private static final String TAG = "PROFILER";
    private static final long ONE_SECOND_IN_NANOSECONDS = 1000000000L;
    private static final long ONE_MILLISECOND_IN_NANOSECONDS = 1000000L;
    private static final String FRAME_LOG_MESSAGE = "FPS = %d\nObjects = %d";
    private static final String SECTION_LOG_MESSAGE = "%s took %d milliseconds";

    private static final Profiler PROFILER = new Profiler();

    private int mFrameCount = 0;
    private long mCurrentFps = 0;
    private final Stack<Long> mNanoTimes = new Stack<>();

    public static Profiler getInstance() {
        return PROFILER;
    }

    public Profiler() {
        pushNanoTime(); // Initialize the time stack
    }

    // Add a time stamp
    private synchronized void pushNanoTime() {
        mNanoTimes.push(System.nanoTime());
    }

    // Alias to look at the current top time stamp as a peek
    private synchronized long peekNanoTime() {
        return lookNanoTime(false);
    }

    // Alias to look at the current top time stamp and pop it off
    private synchronized long popNanoTime() {
        return lookNanoTime(true);
    }

    /**
     * @param pop true if the last value in the stack should be popped, false if a peek
     * @return the difference between now and the last stack time, or 0 if there's no stack
     */
    private synchronized long lookNanoTime(boolean pop) {
        if (mNanoTimes.size() == 0) {

            return 0;
        }
        return System.nanoTime() - (pop ? mNanoTimes.pop() : mNanoTimes.peek());
    }

    // Adds a new frame to the time tracking
    private void newFrame() {
        mFrameCount++;
    }

    /**
     * Track the start time of this frame
     *
     * @return the current delta time in milliseconds since the last frame
     */
    public float trackFrame(StardroidEngine engine) {
        newFrame();

        float dt = popNanoTime();
        pushNanoTime(); // Push a new time onto the stack

        mCurrentFps = (long) (mFrameCount / (dt / ONE_SECOND_IN_NANOSECONDS));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format(FRAME_LOG_MESSAGE, mCurrentFps, engine.getObjectCount()));
        }
        mFrameCount = 0;

        return dt / ONE_MILLISECOND_IN_NANOSECONDS;
    }

    public long getCurrentFramesPerSecond() {
        return mCurrentFps;
    }

    /**
     * Start tracking the current section to log out how long it takes
     */
    public void startTrackingSection() {
        pushNanoTime();
    }

    /**
     *  Stop tracking the current section and log out how many milliseconds it took
     *
     * @param sectionName the name of the section to use when logging
     */
    public void stopTrackingSection(String sectionName) {
        long timeStamp = popNanoTime();
        if (BuildConfig.DEBUG) {
            // TODO: Somehow make this log in a way that isn't obnoxious?
            Log.d(TAG, String.format(SECTION_LOG_MESSAGE, sectionName, timeStamp / ONE_MILLISECOND_IN_NANOSECONDS));
        }
    }
}
