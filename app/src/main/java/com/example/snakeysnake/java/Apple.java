package com.example.snakeysnake.java;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

import com.example.snakeysnake.R;

class Apple implements GameObject {

    private final Point location = new Point();
    private final Point mSpawnRange;
    private final int mSize;
    private Bitmap mBitmapApple;

    Apple(Context context, Point sr, int s){
        mSpawnRange = sr;
        mSize = s;
        location.x = -10;
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.moon);
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, s, s, false);
    }

    @Override
    public void spawn(){
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    @Override
    public Point getLocation(){
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
    }
}
