package dev.a.p.spritesheettest.animation.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Created by AP on 12/05/16.
 * Base animation class
 */
@SuppressWarnings("unused")
public abstract class BaseAnimation {
    private static final int DEFAULT_FPS = 60;

    private Bitmap mBitmap;		        // the animation sequence
    private Rect mSourceRect;	        // the rectangle to be drawn from the animation bitmap
    private int mFramesCount;	        // number of frames in animation
    private int mRowsCount, mColumnsCount;
    private int mCurrentFrame;	        // the current frame
    private int mFramePeriod;	        // milliseconds between each frame (1000/fps)
    private long mFrameTicker;	        // the time of the last frame update

    private int mSpriteWidth;	        // the width of the sprite to calculate the cut out rectangle
    private int mSpriteHeight;	        // the height of the sprite
    private int mFps = DEFAULT_FPS;     // animation frames per second

    private String mTouchId;
    private Rect mDestRect = new Rect();

    public BaseAnimation(Bitmap bitmap, int frameCount) {
        this(bitmap, frameCount, 1);
    }

    public BaseAnimation(Bitmap bitmap, int frameCount, int rows) {
        mBitmap = bitmap;
        mFramesCount = frameCount;
        mRowsCount = rows;
        mColumnsCount = frameCount / rows + (frameCount%rows > 0?1:0);
        reInitAnimation();
    }

    private void reInitAnimation() {
        mCurrentFrame = 0;
        mSpriteWidth = mBitmap.getWidth() / mColumnsCount;
        mSpriteHeight = mBitmap.getHeight() / mRowsCount;
        mSourceRect = new Rect(0, 0, mSpriteWidth, mSpriteHeight);
        mFramePeriod = 1000 / mFps;
        mFrameTicker = 0L;
    }

    // the update method for animation
    public void update(long animationTime) {
        if (animationTime > mFrameTicker + mFramePeriod) {
            mFrameTicker = animationTime;
            // increment the frame
            mCurrentFrame++;
            if (mCurrentFrame >= mFramesCount) {
                mCurrentFrame = 0;
            }
        }

        int currentColumn = mCurrentFrame % mColumnsCount;
        int currentRow = mCurrentFrame / mColumnsCount;
        // define the rectangle to cut out sprite
        mSourceRect.left = currentColumn * mSpriteWidth;
        mSourceRect.right = mSourceRect.left + mSpriteWidth;
        mSourceRect.top = currentRow * mSpriteHeight;
        mSourceRect.bottom = mSourceRect.top + mSpriteHeight;
    }

    // the draw method which draws the corresponding frame
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        // where to draw the sprite
        mDestRect = new Rect(getX(), getY(), getX() + mSpriteWidth, getY() + mSpriteHeight);
        canvas.drawBitmap(mBitmap, mSourceRect, mDestRect, null);
    }

    protected abstract int getY();
    protected abstract int getX();

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        reInitAnimation();
    }

    public int getFramesCount() {
        return mFramesCount;
    }

    public void setFramesCount(int framesCount) {
        mFramesCount = framesCount;
    }

    public int getFps() {
        return mFps;
    }

    public void setFps(int fps) {
        mFps = fps;
        reInitAnimation();
    }

    public String getTouchId() {
        return mTouchId;
    }

    public void setTouchId(String touchId) {
        mTouchId = touchId;
    }

    public boolean isTouched(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        return x >= mDestRect.left && x <= mDestRect.right
                && y >= mDestRect.top && y <= mDestRect.bottom;
    }
}
