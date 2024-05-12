package com.example.snakeysnake.java;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class GyaradosActivity extends Activity {

    private GyaradosGame mGyaradosGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSnakeGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGyaradosGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGyaradosGame.pause();
    }

    private void initializeSnakeGame() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mGyaradosGame = new GyaradosGame(this, size);
        setContentView(mGyaradosGame);
    }
}