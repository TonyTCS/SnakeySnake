// SnakeActivity.java
package com.example.snakeysnake.java;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class SnakeActivity extends Activity {

    // Declare an instance of SnakeGame
    private SnakeGame mSnakeGame;

    // Set the game up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeGame class
        mSnakeGame = new SnakeGame(this, size);

        // Make SnakeGame the view of the Activity
        setContentView(mSnakeGame);
    }

    // Start the thread in SnakeGame
    @Override
    protected void onResume() {
        super.onResume();
        mSnakeGame.resume();
    }

    // Stop the thread in SnakeGame
    @Override
    protected void onPause() {
        super.onPause();
        mSnakeGame.pause();
    }
}
