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

public class Snake extends GameObject implements Drawable {

    private final ArrayList<Point> segmentLocations;
    private final int mSegmentSize;
    private final Point mMoveRange;
    private final int halfWayPoint;
    private enum Heading { UP, RIGHT, DOWN, LEFT }
    private Heading heading = Heading.RIGHT;
    private final Bitmap[] mBitmapHeads = new Bitmap[4];
    private Bitmap mBitmapBody;
    private float speed = 1.0f; // Default speed of the snake

    public float getSpeed() {
        return getSpeed();
    }

    Snake(Context context, Point moveRange, int segmentSize) {
        this.segmentLocations = new ArrayList<>();
        this.mSegmentSize = segmentSize;
        this.mMoveRange = moveRange;

        this.mBitmapHeads[Heading.RIGHT.ordinal()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.helmet);
        this.mBitmapHeads[Heading.LEFT.ordinal()] = flipBitmap(mBitmapHeads[Heading.RIGHT.ordinal()]);
        this.mBitmapHeads[Heading.UP.ordinal()] = rotateBitmap(mBitmapHeads[Heading.RIGHT.ordinal()], -90);
        this.mBitmapHeads[Heading.DOWN.ordinal()] = rotateBitmap(mBitmapHeads[Heading.RIGHT.ordinal()], 180);

        this.mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.cloud);

        this.halfWayPoint = moveRange.x * segmentSize / 2;
    }

    @Override
    public void spawn() {
        reset(mMoveRange.x, mMoveRange.y);
    }

    @Override
    public Point getLocation() {
        return segmentLocations.isEmpty() ? new Point(-1, -1) : new Point(segmentLocations.get(0));
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
    public void draw() {

    }

    void reset(int width, int height) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(width / 2, height / 2));
        speed = 1.0f; // Reset speed when game is reset
    }

   void move() {
       // Move the body
       // Start at the back and move it
       // to the position of the segment in front of it
       for (int i = segmentLocations.size() - 1; i > 0; i--) {

           // Make it the same value as the next segment
           // going forwards towards the head
           segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
           segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
       }

       // Move the head in the appropriate heading
       // Get the existing head position
       Point p = segmentLocations.get(0);

       // Move it appropriately
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
    void increaseSize() {
        if (!segmentLocations.isEmpty()) {
            Point tail = segmentLocations.get(segmentLocations.size() - 1);
            segmentLocations.add(new Point(tail.x, tail.y));
        }
    }
    void increaseSpeed() {
        final int maxSpeed = 10;
        if (speed < maxSpeed) {
            speed++;
        }
    }

   boolean detectDeath() {
       // Has the snake died?
       boolean dead = false;

       // Hit any of the screen edges
       if (segmentLocations.get(0).x == -1 ||
               segmentLocations.get(0).x > mMoveRange.x ||
               segmentLocations.get(0).y == -1 ||
               segmentLocations.get(0).y > mMoveRange.y) {

           dead = true;
       }
       // Eaten itself?
       for (int i = segmentLocations.size() - 1; i > 0; i--) {
           // Have any of the sections collided with the head
           if (segmentLocations.get(0).x == segmentLocations.get(i).x &&
                   segmentLocations.get(0).y == segmentLocations.get(i).y) {

               dead = true;
           }
       }
       return dead;
   }


    boolean checkDinner(Point l) {
        //if (snakeXs[0] == l.x && snakeYs[0] == l.y) {
        if (segmentLocations.get(0).x == l.x &&
                segmentLocations.get(0).y == l.y) {

            // Add a new Point to the list
            // located off-screen.
            // This is OK because on the next call to
            // move it will take the position of
            // the segment in front of it
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    void switchHeading(MotionEvent motionEvent) {
        heading = (motionEvent.getX() >= halfWayPoint) ? getNextClockwiseHeading() : getNextCounterClockwiseHeading();
    }


    private Heading getNextClockwiseHeading() {
        switch (heading) {
            case UP:
                return Heading.RIGHT;
            case RIGHT:
                return Heading.DOWN;
            case DOWN:
                return Heading.LEFT;
            case LEFT:
                return Heading.UP;
            default:
                return heading;
        }
    }

    private Heading getNextCounterClockwiseHeading() {
        switch (heading) {
            case UP:
                return Heading.LEFT;
            case LEFT:
                return Heading.DOWN;
            case DOWN:
                return Heading.RIGHT;
            case RIGHT:
                return Heading.UP;
            default:
                return heading;
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