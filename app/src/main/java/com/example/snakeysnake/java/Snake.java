package com.example.snakeysnake.java;

import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import com.example.snakeysnake.R;
import java.util.ArrayList;


class Snake implements GameObject, Drawable {

    private final ArrayList<Point> segmentLocations;
    private final int mSegmentSize;
    private final Point mMoveRange;
    private final int halfWayPoint;
    private enum Heading { UP, RIGHT, DOWN, LEFT }
    private Heading heading = Heading.RIGHT;
    private Bitmap[] mBitmapHeads = new Bitmap[4];
    private Bitmap mBitmapBody;

    Snake(Context context, Point mr, int ss) {
        segmentLocations = new ArrayList<>();
        mSegmentSize = ss;
        mMoveRange = mr;

        mBitmapHeads[Heading.RIGHT.ordinal()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.helmet);
        mBitmapHeads[Heading.LEFT.ordinal()] = flipBitmap(mBitmapHeads[Heading.RIGHT.ordinal()]);
        mBitmapHeads[Heading.UP.ordinal()] = rotateBitmap(mBitmapHeads[Heading.RIGHT.ordinal()], -90);
        mBitmapHeads[Heading.DOWN.ordinal()] = rotateBitmap(mBitmapHeads[Heading.RIGHT.ordinal()], 180);

        mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.cloud);

        halfWayPoint = mr.x * ss / 2;
    }

    @Override
    public void spawn() {
        reset(mMoveRange.x, mMoveRange.y);
    }

    @Override
    public Point getLocation() {
        return segmentLocations.isEmpty() ? new Point(-1, -1) : segmentLocations.get(0);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!segmentLocations.isEmpty()) {
            int headIndex = heading.ordinal();
            canvas.drawBitmap(mBitmapHeads[headIndex], segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);
            for (int i = 1; i < segmentLocations.size(); i++) {
                canvas.drawBitmap(mBitmapBody, segmentLocations.get(i).x * mSegmentSize, segmentLocations.get(i).y * mSegmentSize, paint);
            }
        }
    }

    @Override
    public void draw(){
    }

    void reset(int w, int h) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    void move() {
        if (!segmentLocations.isEmpty()) {
            for (int i = segmentLocations.size() - 1; i > 0; i--) {
                segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
                segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
            }
            Point p = segmentLocations.get(0);
            switch (heading) {
                case UP:
                    p.y--;
                    break;
                case RIGHT:
                    p.x++;
                    break;
                case DOWN:
                    p.y++;
                    break;
                case LEFT:
                    p.x--;
                    break;
            }
        }
    }

    boolean detectDeath() {
        boolean dead = segmentLocations.isEmpty() || segmentLocations.get(0).x == -1 || segmentLocations.get(0).x > mMoveRange.x || segmentLocations.get(0).y == -1 || segmentLocations.get(0).y > mMoveRange.y;
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (segmentLocations.get(0).x == segmentLocations.get(i).x && segmentLocations.get(0).y == segmentLocations.get(i).y) {
                dead = true;
                break;
            }
        }
        return dead;
    }

    boolean checkDinner(Point l) {
        if (!segmentLocations.isEmpty() && segmentLocations.get(0).equals(l)) {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    void switchHeading(MotionEvent motionEvent) {
        if (motionEvent.getX() >= halfWayPoint) {
            switch (heading) {
                case UP:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.UP;
                    break;
            }
        } else {
            switch (heading) {
                case UP:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.UP;
                    break;
            }
        }
    }

    private Bitmap flipBitmap(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private Bitmap rotateBitmap(Bitmap src, float angle) {
        Matrix matrix = new Matrix();
        matrix.preRotate(angle);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}

