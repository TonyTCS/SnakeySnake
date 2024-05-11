package com.example.snakeysnake.java;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;


abstract class GameObject extends Drawable {
    // Abstract methods that subclasses will implement
    abstract void spawn();
    abstract Point getLocation();

    // Implementing Drawable interface's methods
    @Override
    public void draw(Canvas canvas) {
        // Default or example implementation, should be overridden in subclasses
    }

    @Override
    public int getOpacity() {
        // Return the opacity of the drawable
        return android.graphics.PixelFormat.TRANSLUCENT; // Example
    }

    @Override
    public void setAlpha(int alpha) {
        // Set the alpha value for the drawable
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        // Set a color filter for the drawable
    }
}