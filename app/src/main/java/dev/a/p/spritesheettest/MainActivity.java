package dev.a.p.spritesheettest;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import dev.a.p.spritesheettest.animation.objects.AnchorAnimation;
import dev.a.p.spritesheettest.animation.AnimationSurface;
import dev.a.p.spritesheettest.animation.objects.FreePosAnimation;

public class MainActivity extends AppCompatActivity implements AnimationSurface.IAnimationTouchListener {

    private static final String TOUCH_ID_CAT = "TOUCH_ID_CAT";
    private AnimationSurface mAnimationSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frame = (FrameLayout) findViewById(R.id.animation_holder);
        mAnimationSurface = new AnimationSurface(this);

        // create Test and load bitmap
        FreePosAnimation freePosAnimation = new FreePosAnimation(
                BitmapFactory.decodeResource(getResources(), R.drawable.walk_elaine), 10, 50, 5);
        freePosAnimation.setFps(5);

        // create Test and load bitmap
        FreePosAnimation freePosAnimation2 = new FreePosAnimation(
                BitmapFactory.decodeResource(getResources(), R.drawable.walk_elaine), 10, 200, 5);
        freePosAnimation2.setFps(5);

        // create Test and load bitmap
        AnchorAnimation anchorAnimation = new AnchorAnimation(
                BitmapFactory.decodeResource(getResources(), R.drawable.cat),
                findViewById(R.id.animation_anchor), 130, 11);
        anchorAnimation.setFps(25);
        anchorAnimation.setTouchId(TOUCH_ID_CAT);

        mAnimationSurface.addAnimation(freePosAnimation);
        mAnimationSurface.addAnimation(freePosAnimation2);
        mAnimationSurface.addAnimation(anchorAnimation);

        if (frame != null) frame.addView(mAnimationSurface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnimationSurface.setAnimationTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAnimationSurface.setAnimationTouchListener(null);
    }

    @Override
    public void onAnimationTouch(String touchId) {
        switch (touchId) {
            case TOUCH_ID_CAT:
                Log.d(MainActivity.class.getSimpleName(), "Cat touched. Mrrrr...");
                break;
        }
    }
}
