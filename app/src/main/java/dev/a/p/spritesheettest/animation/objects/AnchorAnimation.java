package dev.a.p.spritesheettest.animation.objects;

import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.view.View;

/**
 * Animation with free position coordinates
 */
public class AnchorAnimation extends BaseAnimation {
    private View mAnchorView;
    private int[] mSurfaceLocation = new int[] {0, 0};

	@SuppressWarnings("unused")
    public AnchorAnimation(Bitmap bitmap, View anchorView, int frameCount) {
		this(bitmap, anchorView, frameCount, 1);
	}

	public AnchorAnimation(Bitmap bitmap, View anchorView, int frameCount, int rows) {
		super(bitmap, frameCount, rows);
        mAnchorView = anchorView;
	}

    public int getX() {
        int[] location = new int[2];
        mAnchorView.getLocationOnScreen(location);
        return location[0] - mSurfaceLocation[0];
    }

    public int getY() {
        int[] location = new int[2];
        mAnchorView.getLocationOnScreen(location);
        return location[1] - mSurfaceLocation[1];
    }

    @SuppressWarnings("unused")
    public void setAnchorView(View anchorView) {
        mAnchorView = anchorView;
    }

    public void setSurfaceOffset(SurfaceView surfaceView) {
        surfaceView.getLocationOnScreen(mSurfaceLocation);
    }
}
