package com.example.snakeysnake.java;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.example.snakeysnake.R;

import java.util.Random;

public class SharpedoPowerUp implements PowerUps {

    //    private static final int SPEED_UP_FPS = 10;
    private Context context;
    private final Point spawnRange;
    private final int size;
    private Bitmap sharpedoBitmap;
    private final Point location = new Point();


    public SharpedoPowerUp(Context context, Point spawnRange, int size) {
        this.context = context;
        this.spawnRange = spawnRange;
        this.size = size;
        sharpedoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sharpedo);
        sharpedoBitmap = Bitmap.createScaledBitmap(sharpedoBitmap, size,size,false);
    }

    @Override
    public void applyPowerUps(GyaradosGame sg) {
        int currentFPS = sg.getTargetFPS();
        // Increase FPS by 3 only if current FPS is between 4 and 18 inclusive
        if (currentFPS >= 4 && currentFPS <= 18) {
            sg.setTargetFPS(currentFPS + 3);
        } else {
            // If FPS is not within the range, leave it unchanged
            // Optionally, handle this case, e.g., log a message or notify the user
            sg.setTargetFPS(currentFPS);  // Keeps FPS unchanged if it's outside the range
        }
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
        canvas.drawBitmap(sharpedoBitmap, getLocation().x * size, getLocation().y * size, paint);
    }
}