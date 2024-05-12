package com.example.snakeysnake.java;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;

public class SoundManager {
    private SoundPool mSoundPool;
    private MediaPlayer mSurfPlayer;  // MediaPlayer for the surf sound

    private int mSlowId;
    private int mLevelUpSoundId;  // ID for the level-up sound
    private int mCrashSoundId;
    private int mSpeedUpId;
    private int mStatsDownId;
    private int mMenuId;

    public SoundManager(Context context) {
        initializeSoundPool();
        loadSounds(context);
        initializeSurfPlayer(context);
    }

    private void initializeSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    private void loadSounds(Context context) {
        AssetManager assetManager = context.getAssets();
        loadSound(assetManager, "flee.ogg", id -> mCrashSoundId = id);
        loadSound(assetManager, "level_up.ogg", id -> mLevelUpSoundId = id);
        loadSound(assetManager, "agility.ogg", id -> mSpeedUpId = id);
        loadSound(assetManager, "paralyze.ogg", id -> mSlowId = id);
        loadSound(assetManager, "stats_down.ogg", id -> mStatsDownId = id);
        loadSound(assetManager, "menu.ogg", id -> mMenuId = id);
    }

    private void initializeSurfPlayer(Context context) {
        mSurfPlayer = new MediaPlayer();
        AssetManager assetManager = context.getAssets();
        try {
            AssetFileDescriptor afd = assetManager.openFd("surf.ogg");
            mSurfPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mSurfPlayer.prepare();
            mSurfPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSound(AssetManager assetManager, String fileName, SoundIdUpdater updater) {
        try (AssetFileDescriptor descriptor = assetManager.openFd(fileName)) {
            int soundId = mSoundPool.load(descriptor, 1);
            updater.updateSoundId(soundId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playCrashSound() {
        playSound(mCrashSoundId, false);
    }

    public void playLevelUp() {
        playSound(mLevelUpSoundId, false);
    }

    public void playSpeedUp() {
        playSound(mSpeedUpId, false);
    }

    public void playSlow() {
        playSound(mSlowId, false);
    }

    public void playStatsDown() {
        float volume = 1.0f;  // Set volume to maximum (100%)
        playSound(mStatsDownId, false);
    }

    public void playMenu() {
        playSound(mMenuId, false);
    }

    public void playSurf() {
        if (mSurfPlayer != null && !mSurfPlayer.isPlaying()) {
            float volume = 0.5f;  // Set volume to 50%
            mSurfPlayer.setVolume(volume, volume);
            mSurfPlayer.start();
        }
    }


    public void stopSurf() {
        if (mSurfPlayer != null && mSurfPlayer.isPlaying()) {
            mSurfPlayer.stop();
            try {
                mSurfPlayer.prepare();  // Prepare the player to be started again later
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playSound(int soundId, boolean loop) {
        int loopFlag = loop ? -1 : 0;  // -1 for infinite looping, 0 for no loop
        if (soundId != 0) {
            mSoundPool.play(soundId, 1.0f, 1.0f, 0, loopFlag, 1.0f);
        } else {
            System.err.println("Sound ID not loaded");
        }
    }

    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        if (mSurfPlayer != null) {
            mSurfPlayer.release();
            mSurfPlayer = null;
        }
    }

    @FunctionalInterface
    interface SoundIdUpdater {
        void updateSoundId(int soundId);
    }
}
