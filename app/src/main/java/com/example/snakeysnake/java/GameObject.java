package com.example.snakeysnake.java;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

interface GameObject {
    void spawn();
    Point getLocation();
    void draw(Canvas canvas, Paint paint);
}
