package com.example.snakeysnake.java;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Typeface;
import android.graphics.Rect;


import com.example.snakeysnake.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GyaradosGame extends SurfaceView implements Runnable, GameLifecycle, Drawable {

    private List<SharpedoPowerUp> sharpedoPowerUps;
    private List<SlowpokeDebuff> slowpokeDebuffs;

    private List<QwilfishDebuff> qwilfishDebuffs;
    private static final int DEFAULT_TARGET_FPS = 10;
    private static int TARGET_FPS = DEFAULT_TARGET_FPS;
    private static final long MILLIS_PER_SECOND = 1000;

    private Thread mThread = null;
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    private SoundManager mSoundManager;
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    private int mScore = 0;
    private Canvas mCanvas;
    private int mHighScore = 0;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Gyarados mGyarados;
    private Magikarp mMagikarp;
    private Rect mPauseButtonRect;
    private boolean mPlayerDead = true;
    private Bitmap mBitmapBackground;
    private long mNextFrameTime;
    private int x;
    private int y;
    private double mTimer;



    public void setTargetFPS(int fps) {
        TARGET_FPS = fps;
    }

    public int getTargetFPS() {
        return TARGET_FPS;
    }


    public GyaradosGame(Context context, Point size) {

        super(context);
        initGame(context, size);
    }

    private void initGame(Context context, Point size) {
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;

        mSoundManager = new SoundManager(context);
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mGyarados = new Gyarados(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mMagikarp = new Magikarp(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        this.x = size.x;
        this.y = size.y;
        this.setmTimer(0);
        // separate list of power ups
        sharpedoPowerUps = new ArrayList<>();
        slowpokeDebuffs = new ArrayList<>();
        qwilfishDebuffs = new ArrayList<>();


        //calls methods to spawn obtainable objects/power-ups in game
        spawnSharpedoPowerUp(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        spawnSlowpokePowerUp(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        spawnQwilfishDebuff(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);





        int buttonSize = blockSize * 3;
        mPauseButtonRect = new Rect(0, size.y - buttonSize, buttonSize, size.y);

        PowerUpDecoder.initializePowerUpDecoder(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);



    }
    private void spawnSlowpokePowerUp(Context context, Point spawnRange, int size){
        SlowpokeDebuff powerup = new SlowpokeDebuff(context, spawnRange, size);
        powerup.spawn();
        slowpokeDebuffs.add(powerup);
    }

    private void spawnSharpedoPowerUp(Context context, Point spawnRange, int size) {
        SharpedoPowerUp powerUp = new SharpedoPowerUp(context, spawnRange, size);
        powerUp.spawn();
        sharpedoPowerUps.add(powerUp);
    }

    private void spawnQwilfishDebuff(Context context, Point spawnRange, int size) {
        QwilfishDebuff powerUp = new QwilfishDebuff(context, spawnRange, size);
        powerUp.spawn();
        qwilfishDebuffs.add(powerUp);
    }
    @Override
    public void run() {
        while (mPlaying) {
            if (!mPaused && updateRequired()) {
                update();
            }
            draw();
        }
    }


    private boolean updateRequired() {
        return mNextFrameTime <= System.currentTimeMillis();
    }

    private void updateHighScore() {
        if (mScore > mHighScore) {
            mHighScore = mScore;
        }
    }

    private void update() {
        mGyarados.move();
        if (mGyarados.checkDinner(mMagikarp.getLocation())) {
            mMagikarp.spawn();
            mScore++;
            mSoundManager.playLevelUp();
        }


        if (mGyarados.detectDeath()) {
            mSoundManager.playCrashSound();
            mPaused = true;
            mPlayerDead = true;
            mSoundManager.stopSurf();
        } else {
            mPlayerDead = false;
        }

        Iterator<SharpedoPowerUp> iterator = sharpedoPowerUps.iterator();
        while (iterator.hasNext()) {
            SharpedoPowerUp sharpedoPowerUp = iterator.next();
            if (mGyarados.checkCollision(sharpedoPowerUp.getLocation())) {
                PowerUps powerUp = PowerUpDecoder.decodePowerUp("speed");
                powerUp.applyPowerUps(this);
                iterator.remove();
                spawnSharpedoPowerUp(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), mGyarados.getSegmentSize());
                mSoundManager.playSpeedUp();
                break;
            }
        }
        Iterator<QwilfishDebuff> Qiterator = qwilfishDebuffs.iterator();
        while (Qiterator.hasNext()) {
            QwilfishDebuff qwilfishDebuff = Qiterator.next();
            if (mGyarados.checkCollision(qwilfishDebuff.getLocation())) {
                PowerUps powerUp = PowerUpDecoder.decodePowerUp("poison");
                powerUp.applyPowerUps(this);
                Qiterator.remove();
                spawnQwilfishDebuff(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), mGyarados.getSegmentSize());
                mSoundManager.playStatsDown();
                break;
            }
        }

        Iterator<SlowpokeDebuff> mIterator = slowpokeDebuffs.iterator();

        if(!mIterator.hasNext() && checkTimer(mTimer) >= 5) {
            spawnSlowpokePowerUp(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), mGyarados.getSegmentSize());

        } else if(mIterator.hasNext()){
            while (mIterator.hasNext()) {
                SlowpokeDebuff slowpokeDebuff = mIterator.next();
                if (mGyarados.checkCollision(slowpokeDebuff.getLocation())) {
                    PowerUps mPowerUp = PowerUpDecoder.decodePowerUp("slow");
                    mPowerUp.applyPowerUps(this);
                    mIterator.remove();
                    this.setmTimer(System.nanoTime());
                    mSoundManager.playSlow();
                    break;
                }
            }
        }
        for (SharpedoPowerUp sharpedoPowerUp : sharpedoPowerUps) {
           if (mGyarados.checkDinner(sharpedoPowerUp.getLocation())) {
               PowerUps powerUps = PowerUpDecoder.decodePowerUp("speed");
                powerUps.applyPowerUps(this);
                sharpedoPowerUp.spawn();
               mScore++;
                mSoundManager.playLevelUp();
            }
        }

        mNextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;
    }


    @Override
    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            drawBackground();
            drawScore();
            drawGameObjects();
            drawPauseButton();
            drawTapToPlayMessage();
            drawGroupMembers(); // Call drawGroupMembers before unlocking and posting
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    private void drawBackground() {
        mCanvas.drawColor(Color.argb(205, 11, 8, 102));
        mBitmapBackground = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.pkmbackground);
        mBitmapBackground = Bitmap.createScaledBitmap(mBitmapBackground, x, y, false);
        mCanvas.drawBitmap(mBitmapBackground, 0, 0, mPaint);
    }

    private void drawScore() {
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(120);
        mPaint.setTypeface(Font.getCustomTypeface(getContext()));
        mCanvas.drawText(String.valueOf(mScore), 20, 120, mPaint);
    }

    private void drawHighScore() {
        mPaint.setColor(Color.argb(255, 255, 255, 255)); // White color
        mPaint.setTextSize(120);
        mPaint.setTypeface(Font.getCustomTypeface(getContext()));

        // Get the screen width
        int screenWidth = mCanvas.getWidth();

        // Calculate the width of the text
        String text = "High Score: " + mHighScore;
        float textWidth = mPaint.measureText(text);

        // Calculate the x coordinate to center the text
        float x = (screenWidth - textWidth) / 2;

        // Draw the text at the center of the screen
        mCanvas.drawText(text, x, 740, mPaint);
    }

    private void drawGameObjects() {
        mMagikarp.draw(mCanvas, mPaint);
        mGyarados.draw(mCanvas, mPaint);

        for (SharpedoPowerUp sharpedoPowerUp : sharpedoPowerUps) {
            sharpedoPowerUp.draw(mCanvas,mPaint);
        }
        for (SlowpokeDebuff slowpokeDebuff : slowpokeDebuffs) {
            slowpokeDebuff.draw(mCanvas,mPaint);
        }
        for (QwilfishDebuff qwilfishDebuff : qwilfishDebuffs) {
            qwilfishDebuff.draw(mCanvas, mPaint);
        }
    }

    private void drawPauseButton() {
        mPaint.setColor(Color.argb(128, 255, 255, 255));
        int buttonSize = mPauseButtonRect.width() / 2;
        mCanvas.drawRect(mPauseButtonRect, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(40);
        mPaint.setTypeface(Font.getCustomTypeface(getContext()));
        String buttonText = mPaused ? "Resume" : "Pause";
        float textWidth = mPaint.measureText(buttonText);
        float textHeight = mPaint.ascent() + mPaint.descent();
        mCanvas.drawText(buttonText, mPauseButtonRect.centerX() - textWidth / 2, mPauseButtonRect.centerY() - textHeight / 2, mPaint);
    }


    private void drawTapToPlayMessage() {
        if (mPaused && mPlayerDead) {

            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(250);
            mPaint.setTypeface(Font.getCustomTypeface(getContext()));
            mCanvas.drawText(getResources().getString(R.string.tap_to_play), 450, 500, mPaint);
            updateHighScore();
            drawHighScore();
        }
    }


    private void drawGroupMembers() {
        // Set the transparency level (alpha value)
        int alphaValue = 150; // Adjust this value to set the desired transparency level

        // Load custom font from assets
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "ChrustyRock-ORLA.ttf");

        // Draw the group members' names with transparency and custom font
        Paint paint = new Paint();
        paint.setColor(Color.argb(alphaValue, 255, 255, 255)); // Set color to white with alpha
        paint.setTextSize(60);
        paint.setTypeface(typeface); // Set custom font

        // Draw teammates names
        mCanvas.drawText("Tony Tran", mCanvas.getWidth() - 400, 60, paint);
        mCanvas.drawText("Maria Valencia", mCanvas.getWidth() - 470, 135, paint);
        mCanvas.drawText("Armaan Randhawa", mCanvas.getWidth() - 550, 205, paint);
        mCanvas.drawText("Brian Hert", mCanvas.getWidth() - 415, 280, paint);
    }


    @Override
    public void draw(Canvas canvas, Paint paint) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    if (mPauseButtonRect.contains(x, y)) {
                        if (!mPlayerDead) {
                            mSoundManager.playSurf();
                            mSoundManager.playMenu();
                            mPaused = false;
                        }
                        return true;
                    }
                } else {
                    if (mPauseButtonRect.contains(x, y)) {
                        if (!mPlayerDead) {
                            mSoundManager.stopSurf();
                            mSoundManager.playMenu();
                            mPaused = true;
                        }
                        return true;
                    } else {
                        mGyarados.switchHeading(motionEvent);
                    }
                }
                if (mPlayerDead) {
                    newGame();
                    mPaused = false;
                    return true;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void newGame() {
        mSoundManager.playSurf();
        mGyarados.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        updateHighScore();
        mGyarados.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mMagikarp.spawn();
        sharpedoPowerUps.clear();
        slowpokeDebuffs.clear();
        qwilfishDebuffs.clear();


        spawnQwilfishDebuff(getContext(), new Point(NUM_BLOCKS_WIDE,mNumBlocksHigh), mGyarados.getSegmentSize());
        spawnQwilfishDebuff(getContext(), new Point(NUM_BLOCKS_WIDE,mNumBlocksHigh), mGyarados.getSegmentSize());
        spawnSlowpokePowerUp(getContext(), new Point(NUM_BLOCKS_WIDE,mNumBlocksHigh), mGyarados.getSegmentSize());
        spawnSlowpokePowerUp(getContext(), new Point(NUM_BLOCKS_WIDE,mNumBlocksHigh), mGyarados.getSegmentSize());
        spawnSharpedoPowerUp(getContext(), new Point(NUM_BLOCKS_WIDE,mNumBlocksHigh), mGyarados.getSegmentSize());
        spawnSharpedoPowerUp(getContext(), new Point(NUM_BLOCKS_WIDE,mNumBlocksHigh), mGyarados.getSegmentSize());
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
        mPlayerDead = true;
        setTargetFPS(DEFAULT_TARGET_FPS);
    }

    @Override
    public void pause() {
        mSoundManager.stopSurf();
        mPlaying = false;
        mSoundManager.release();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
        mSoundManager = new SoundManager(getContext());
    }

    public double getmTimer() {
        return mTimer;
    }

    public void setmTimer(double mTimer) {
        this.mTimer = mTimer;
    }

    public double checkTimer(double startTime){
        double endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000_000.0; //Convert to Seconds
    }

    public void decrementScore() {
        if (mScore > 0) {  // Ensure the score does not go negative
            mScore -= 1;
        }
    }

}