package dev.a.p.spritesheettest.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import dev.a.p.spritesheettest.animation.objects.AnchorAnimation;
import dev.a.p.spritesheettest.animation.objects.BaseAnimation;

/**
 * This is the main surface that handles the ontouch events and draws
 * animated images to the screen.
 */
public class AnimationSurface extends SurfaceView implements SurfaceHolder.Callback, AnimationThread.IThreadListener {

	private static final String TAG = AnimationSurface.class.getSimpleName();

    /* Stuff for stats */
    private DecimalFormat mDf = new DecimalFormat("0.##");  // 2 dp

	private AnimationThread mThread;
    private Canvas mCanvas;
    private ArrayList<BaseAnimation> mAnimList = new ArrayList<>();
    private IAnimationTouchListener mListener;

    public interface IAnimationTouchListener {
        void onAnimationTouch(String touchId);
    }

	public AnimationSurface(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
        setZOrderOnTop(true);    // necessary
        getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		// create the animation loop mThread
		mThread = new AnimationThread(this);
		
		// make the Animations focusable so it can handle events
		setFocusable(true);
	}

    public void setAnimationTouchListener(IAnimationTouchListener listener) {
        mListener = listener;
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // nothing to do here
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		mThread.setRunning(true);
		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the mThread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the mThread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (BaseAnimation animation : mAnimList) {
                if (animation.isTouched(event) && !TextUtils.isEmpty(animation.getTouchId())) {
                    if (mListener != null) mListener.onAnimationTouch(animation.getTouchId());
                }
            }
		}
		return true;
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 0, 0, 0);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

    /**
     * This is the animations update method. It iterates through all the objects
     * and calls their update method if they have one or calls specific
     * engine's update method.
     */
    @Override
    public void onUpdate() {
        long millis = System.currentTimeMillis();
        for (BaseAnimation animation : mAnimList) animation.update(millis);
    }

    /**
     * Makes updated and then render
     */
    @Override
    public void onUpdateWithRender() {
        // update animations state
        onUpdate();

        // render state to the screen, draws the canvas on the panel
        if (mCanvas == null) return;
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (BaseAnimation animation : mAnimList) {
            if (animation instanceof AnchorAnimation)
                ((AnchorAnimation) animation).setSurfaceOffset(this);
            animation.draw(mCanvas);
        }
        // display fps
        String averageFps = mDf.format(mThread.getAverageFps());
//		Log.d(TAG, String.format("Average FPS: %s", averageFps));
        displayFps(mCanvas, String.format("%s FPS", averageFps));
    }

    @Override
    public void onThreadStart() {
        // try locking the canvas for exclusive pixel editing
        // in the surface
        mCanvas = getHolder().lockCanvas();
    }

    @Override
    public void onThreadEnd() {
        // in case of an exception the surface is not left in
        // an inconsistent state
        if (mCanvas != null) {
            getHolder().unlockCanvasAndPost(mCanvas);
        }
        mCanvas = null;
    }

    public void addAnimation(BaseAnimation animation) {
        if (!mAnimList.contains(animation)) mAnimList.add(animation);
    }

    @SuppressWarnings("unused")
    public void removeAnimation(BaseAnimation animation) {
        if (mAnimList.contains(animation)) mAnimList.remove(animation);
    }
}
