package com.seakernel.stardroid.utilities;

import android.util.Log;

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
    private static final long FRAMES_PER_SECOND = 1;
    private static final long ONE_SECOND_IN_NANOSECONDS = 1000000000L;
    private static final long ONE_MILLISECOND_IN_NANOSECONDS = 1000000L;
    private static final String FRAME_LOG_MESSAGE = "FPS = %d (average of %d second(s))\nObjects = %d";
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

    /**
     * Get the latest nano time difference and log the current state
     *
     * @param engine used to log out objects to the console
     * @return the current running time in nanoseconds since the last frame/log
     */
    private synchronized long getCurrentTime(StardroidEngine engine) {
        long timeStamp = peekNanoTime();
        if (timeStamp >= FRAMES_PER_SECOND * ONE_SECOND_IN_NANOSECONDS) {
            mCurrentFps = mFrameCount / FRAMES_PER_SECOND;
            Log.d(TAG, String.format(FRAME_LOG_MESSAGE, mCurrentFps, FRAMES_PER_SECOND, engine.getObjectCount()));
            mFrameCount = 0;

            // Reset the time
            popNanoTime();
            pushNanoTime();
        }
        return timeStamp;
    }

    // Adds a new frame to the time tracking
    private void newFrame() {
        mFrameCount++;
    }

    /**
     * Track the start time of this frame
     */
    public void trackFrame(StardroidEngine engine) {
        getCurrentTime(engine);
        newFrame();
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
        // TODO: Somehow make this log in a way that isn't obnoxious?
        long timeStamp = popNanoTime();
        Log.d(TAG, String.format(SECTION_LOG_MESSAGE, sectionName, timeStamp / ONE_MILLISECOND_IN_NANOSECONDS));
    }
}
