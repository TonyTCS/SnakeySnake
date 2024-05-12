package com.example.snakeysnake.java;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.example.snakeysnake.R;

import java.util.Random;

public class QwilfishDebuff implements PowerUps {

    private Context context;
    private final Point spawnRange;
    private final int size;
    private Bitmap qwilfishBitmap;
    private final Point location = new Point();

    public QwilfishDebuff(Context context, Point spawnRange, int size) {
        this.context = context;
        this.spawnRange = spawnRange;
        this.size = size;
        qwilfishBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.qwilfish);
        qwilfishBitmap = Bitmap.createScaledBitmap(qwilfishBitmap, size, size, false);
    }

    @Override
    public void applyPowerUps(GyaradosGame sg) {
        sg.decrementScore(); // Method to decrement the score
    }

    public Point getLocation() {
        return new Point(location);
    }

    public void spawn() {
        Random random = new Random();
        int newX = random.nextInt(spawnRange.x) + 1;
        int newY = random.nextInt(spawnRange.y - 1) + 1;
        setLocation(newX, newY);
    }

    private void setLocation(int x, int y) {
        this.location.set(x, y);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(qwilfishBitmap, location.x * size, location.y * size, paint);
    }
}
