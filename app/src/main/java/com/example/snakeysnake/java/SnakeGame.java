package com.example.snakeysnake.java;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import android.graphics.Rect;


import com.example.snakeysnake.R;


class SnakeGame extends SurfaceView implements Runnable{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;

    private Rect mPauseButtonRect;
    private boolean mPauseButtonClicked = false;

    private boolean mPlayerDead = true;

    // An image to represent the apple
    private Bitmap mBitmapBackground;



    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        int buttonSize = blockSize * 3; // Adjust the size as needed
        mPauseButtonRect = new Rect(0, size.y - buttonSize, buttonSize, size.y);

    }


    // Called to start a new game
// Called to start a new game
    public void newGame() {
        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can be triggered
        mNextFrameTime = System.currentTimeMillis();

        // Player is dead at the start of the game
        mPlayerDead = true;
    }



    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
// Update all the game objects
// Update all the game objects
    public void update() {
        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if (mSnake.checkDinner(mApple.getLocation())) {
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            mApple.spawn();

            // Add to mScore
            mScore = mScore + 1;

            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            mPaused = true;
            // Player is dead
            mPlayerDead = true;
        } else {
            // Player is not dead
            mPlayerDead = false;
        }
    }




    // Do all the drawing
// Do all the drawing
    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            //mCanvas.drawColor(Color.argb(255, 26, 128, 182));
            mCanvas.drawColor(Color.argb(205, 11, 8, 102));

            // Draw the background

            mBitmapBackground = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.hxh);

            //mBitmapBackground = Bitmap.createScaledBitmap(mBitmapBackground, 100, 100, false);

            mCanvas.drawBitmap(mBitmapBackground, 0, 0, mPaint);


            // Draw the score
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            // Draw the apple and the snake
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Draw the pause/resume button
            mPaint.setColor(Color.argb(128, 255, 255, 255)); // Semi-transparent white color
            int buttonSize = mPauseButtonRect.width() / 2; // Adjust the size as needed
            mCanvas.drawRect(mPauseButtonRect, mPaint);
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(40);
            String buttonText = mPaused ? "Resume" : "Pause";
            float textWidth = mPaint.measureText(buttonText);
            float textHeight = mPaint.ascent() + mPaint.descent();
            mCanvas.drawText(buttonText, mPauseButtonRect.centerX() - textWidth / 2, mPauseButtonRect.centerY() - textHeight / 2, mPaint);

            // Draw the "Tap to Play" message if the game is paused and the player is dead
            if (mPaused && mPlayerDead) {
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(250);
                mCanvas.drawText(getResources().getString(R.string.tap_to_play), 450, 500, mPaint);
            }

            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    if (mPauseButtonRect.contains(x, y)) {
                        // Resume the game
                        if (!mPlayerDead) {
                            mPaused = false;
                            mPauseButtonClicked = true;
                        }
                        return true;
                    }
                } else {
                    // Pause the game if playing and tapped inside the pause button
                    if (mPauseButtonRect.contains(x, y)) {
                        if (!mPlayerDead) {
                            mPaused = true;
                            mPauseButtonClicked = true;
                        }
                        return true;
                    } else {
                        // Let the Snake class handle the input if tapped outside the pause button
                        mSnake.switchHeading(motionEvent);
                    }
                }

                // Start a new game if the player is dead and tapped anywhere on the screen
                if (mPlayerDead) {
                    newGame();
                    mPaused = false; // Start the game immediately
                    return true;
                }
                break;
            default:
                break;
        }
        return true;
    }






    // Other existing methods...

    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}




