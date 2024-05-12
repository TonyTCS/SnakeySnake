package com.example.snakeysnake.java;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.example.snakeysnake.R;

import java.util.Random;

public class SlowpokeDebuff implements PowerUps{

    private Context context;
    private final Point spawnRange;
    private final int size;
    private Bitmap wailmerBitmap;
    private final Point location = new Point();

    public SlowpokeDebuff(Context context, Point spawnRange, int size){
        this.context = context;
        this.spawnRange = spawnRange;
        this.size = size;
        wailmerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.slowpoke);
        wailmerBitmap = Bitmap.createScaledBitmap(wailmerBitmap, size, size, false);
    }

    @Override
    public void applyPowerUps(SnakeGame sg) {
        int currentFPS = sg.getTargetFPS();
        // Check if the current FPS is greater than 5 before reducing it
        if (currentFPS >= 7) {
            sg.setTargetFPS(currentFPS - 3);
        } else {
            // Optionally, handle the case where FPS cannot be reduced further
            // For example, notify the user, log a message, or set it to a minimum value
            sg.setTargetFPS(4);  // This sets FPS to a minimum viable limit if it's too low to reduce
        }
    }


    public Point getLocation() { return new Point(location);}

    public void spawn(){
        Random random = new Random();
        int newX = random.nextInt(spawnRange.x)+1;
        int newY = random.nextInt(spawnRange.y-1)+1;
        setLocation(newX, newY);
    }

    private void setLocation(int x, int y){this.location.set(x,y);}

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(wailmerBitmap, getLocation().x * size, getLocation().y*size,paint);
    }


}