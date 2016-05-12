package dev.a.p.spritesheettest.animation;

import android.util.Log;


/**
 * The Main thread which contains the animation loop.
 */
public class AnimationThread extends Thread {
	
	private static final String TAG = AnimationThread.class.getSimpleName();
	
	// desired fps
	private final static int MAX_FPS = 60;
    // maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    // we'll be reading the stats every second
    private final static int STAT_INTERVAL = 1000; //ms
    // the average will be calculated by storing
    private final static int FPS_HISTORY_NR = 10;

	// the last n FPSs
	// the status time counter
	private long mStatusIntervalTimer = 0L;

	// number of rendered frames in an interval
	private int mFrameCountPerStatCycle = 0;
	// the last FPS values
	private double mFpsStore[];
	// the number of times the stat has been read
	private long mStatsCount = 0;

	// flag to hold animations state
	private boolean mRunning;
    // update listener
    private final IThreadListener mListener;
    private double mAverageFps = 0;

    public void setRunning(boolean running) {
		mRunning = running;
	}

	public AnimationThread(IThreadListener listener) {
		super();
        mListener = listener;
	}

    public interface IThreadListener {
        void onUpdate();
        void onUpdateWithRender();
        void onThreadStart();
        void onThreadEnd();
    }

	@Override
	public void run() {
		Log.d(TAG, "Starting animation loop");
		// initialise timing elements for stat gathering
		initTimingElements();
		
		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped
		
		while (mRunning) {
			try {
				synchronized (mListener) {
                    mListener.onThreadStart();
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;	// resetting the frames skipped
                    mListener.onUpdateWithRender();
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if (sleepTime > 0) {
						// if sleepTime > 0 we're OK
						try {
							// send the thread to sleep for a short period
							// very useful for battery saving
							Thread.sleep(sleepTime);	
						} catch (InterruptedException ignored) {}
					}
					
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // update without rendering
                        mListener.onUpdate();
						sleepTime += FRAME_PERIOD;	// add frame period to check if in next frame
						framesSkipped++;
					}

					// calling the routine to store the gathered statistics
					storeStats();
				}
			} finally {
                mListener.onThreadEnd();
			}	// end finally
		}
	}

	/**
	 * The statistics - it is called every cycle, it checks if time since last
	 * store is greater than the statistics gathering period (1 sec) and if so
	 * it calculates the FPS for the last period and stores it.
	 * 
	 *  It tracks the number of frames per period. The number of frames since 
	 *  the start of the period are summed up and the calculation takes part
	 *  only if the next period and the frame count is reset to 0.
	 */
	private void storeStats() {
		mFrameCountPerStatCycle++;
		// assuming that the sleep works each call to storeStats
		// happens at 1000/FPS so we just add it up
		mStatusIntervalTimer += FRAME_PERIOD;
		
		if (mStatusIntervalTimer >= STAT_INTERVAL) {
			// calculate the actual frames pers status check interval
            //noinspection PointlessArithmeticExpression
            double actualFps = (double)(mFrameCountPerStatCycle / (STAT_INTERVAL / 1000));
			
			//stores the latest fps in the array
			mFpsStore[(int) mStatsCount % FPS_HISTORY_NR] = actualFps;
			
			// increase the number of times statistics was calculated
			mStatsCount++;
			
			double totalFps = 0.0;
			// sum up the stored fps values
			for (int i = 0; i < FPS_HISTORY_NR; i++) {
				totalFps += mFpsStore[i];
			}
			
			// obtain the average
            if (mStatsCount < FPS_HISTORY_NR) {
				// in case of the first 10 triggers
				mAverageFps = totalFps / mStatsCount;
			} else {
				mAverageFps = totalFps / FPS_HISTORY_NR;
			}

			mStatusIntervalTimer = 0;
			mFrameCountPerStatCycle = 0;
		}
	}

	private void initTimingElements() {
		// initialise timing elements
		mFpsStore = new double[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			mFpsStore[i] = 0.0;
		}
		Log.d(TAG, "Timing elements for stats initialised");
	}

    public double getAverageFps() {
        return mAverageFps;
    }

}
