package com.example.snakeysnake.java;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;

public class SoundManager {
    private SoundPool mSoundPool;
    private int mEatSoundId;
    private int mLevelUpSoundId;  // ID for the level-up sound
    private int mSmallerId;
    private int mCrashSoundId;
    private int mSpeedUpId;

    private int mGrowId;

    public SoundManager(Context context) {
        initializeSoundPool();
        loadSounds(context);
    }

    private void initializeSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)  // Appropriate for game sound effects
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
    }

    private void loadSounds(Context context) {
        AssetManager assetManager = context.getAssets();
        loadSound(assetManager, "bump_wall.ogg", id -> mCrashSoundId = id);
        loadSound(assetManager, "good.ogg", id -> mLevelUpSoundId = id);
        loadSound(assetManager, "agility.ogg", id -> mSpeedUpId = id);
        loadSound(assetManager, "grow.ogg", id -> mGrowId = id);
    }

    private void loadSound(AssetManager assetManager, String fileName, SoundIdUpdater updater) {
        try (AssetFileDescriptor descriptor = assetManager.openFd(fileName)) {
            int soundId = mSoundPool.load(descriptor, 1);
            updater.updateSoundId(soundId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playEatSound() {
        playSound(mEatSoundId);
    }

    public void playCrashSound() {
        playSound(mCrashSoundId);
    }

    public void playSmallerSound() {
        playSound(mSmallerId);
    }

    public void playLevelUp() {
        playSound(mLevelUpSoundId);
    }

    public void playSpeedUp() {
        playSound(mSpeedUpId);
    }

    public void playGrow(){
        playSound(mGrowId);
    }


    private void playSound(int soundId) {
        if (soundId != 0) {
            mSoundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        } else {
            System.err.println("Sound ID not loaded");
        }
    }

    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    @FunctionalInterface
    interface SoundIdUpdater {
        void updateSoundId(int soundId);
    }
}

