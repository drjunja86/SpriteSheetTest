package dev.a.p.spritesheettest.animation.objects;

import android.graphics.Bitmap;

/**
 * Animation with free position coordinates
 */
public class FreePosAnimation extends BaseAnimation {
    private int mX;				// the X coordinate of the object (top left of the image)
    private int mY;				// the Y coordinate of the object (top left of the image)

	public FreePosAnimation(Bitmap bitmap, int x, int y, int frameCount) {
		this(bitmap, x, y, frameCount, 1);
	}
	public FreePosAnimation(Bitmap bitmap, int x, int y, int frameCount, int rows) {
		super(bitmap, frameCount, rows);
        mX = x;
        mY = y;
	}

    public int getX() {
        return mX;
    }

    @SuppressWarnings("unused")
    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    @SuppressWarnings("unused")
    public void setY(int y) {
        mY = y;
    }
}
