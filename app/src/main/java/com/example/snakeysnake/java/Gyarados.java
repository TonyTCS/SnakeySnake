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


class Gyarados extends GameObject implements  Drawable {

    private final ArrayList<Point> segmentLocations;
    private int mSegmentSize;
    private final Point mMoveRange;
    private final int halfWayPoint;
    private enum Heading { UP, RIGHT, DOWN, LEFT }
    private Heading heading = Heading.RIGHT;
    private final Bitmap[] mBitmapHeads = new Bitmap[4];
    private Bitmap mBitmapBody;
    private boolean doubleSize;

    Gyarados(Context context, Point moveRange, int segmentSize) {
        this.segmentLocations = new ArrayList<>();
        this.mSegmentSize = segmentSize;
        this.mMoveRange = moveRange;

        this.mBitmapHeads[Heading.RIGHT.ordinal()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.gyarados);
        this.mBitmapHeads[Heading.LEFT.ordinal()] = flipBitmap(mBitmapHeads[Heading.RIGHT.ordinal()]);
        this.mBitmapHeads[Heading.UP.ordinal()] = rotateBitmap(mBitmapHeads[Heading.RIGHT.ordinal()], -90);
        this.mBitmapHeads[Heading.DOWN.ordinal()] = rotateBitmap(mBitmapHeads[Heading.RIGHT.ordinal()], 180);

        this.mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluebody
        );

        this.halfWayPoint = moveRange.x * segmentSize / 2;
        this.doubleSize = false;
    }

    public void doubleSize() {
        if (!doubleSize) {  // Only double size if not already doubled
            for (int i = 0; i < mBitmapHeads.length; i++) {
                mBitmapHeads[i] = Bitmap.createScaledBitmap(mBitmapHeads[i],
                        mBitmapHeads[i].getWidth() * 2,
                        mBitmapHeads[i].getHeight() * 2,
                        false);
            }
            mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody,
                    mBitmapBody.getWidth() * 2,
                    mBitmapBody.getHeight() * 2,
                    false);
            doubleSize = true;
        }
    }

    public void halfSize() {
        if (doubleSize) {  // Only reduce size if it is currently doubled
            for (int i = 0; i < mBitmapHeads.length; i++) {
                mBitmapHeads[i] = Bitmap.createScaledBitmap(mBitmapHeads[i],
                        mBitmapHeads[i].getWidth() / 2,
                        mBitmapHeads[i].getHeight() / 2,
                        false);
            }
            mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody,
                    mBitmapBody.getWidth() / 2,
                    mBitmapBody.getHeight() / 2,
                    false);
            doubleSize = false;
        }
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
    public void draw(){
        // Implement if necessary
    }

    void reset(int width, int height) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(width / 2, height / 2));

        // Reset size to normal if doubled
        if (doubleSize) {
            halfSize();  // Adjust size back if it was doubled
        }
    }

    void move() {
        if (!segmentLocations.isEmpty()) {
            for (int i = segmentLocations.size() - 1; i > 0; i--) {
                Point current = segmentLocations.get(i);
                Point next = segmentLocations.get(i - 1);
                current.x = next.x;
                current.y = next.y;
            }
            Point head = segmentLocations.get(0);
            switch (heading) {
                case UP:
                    head.y--;
                    break;
                case RIGHT:
                    head.x++;
                    break;
                case DOWN:
                    head.y++;
                    break;
                case LEFT:
                    head.x--;
                    break;
            }
        }
    }

    boolean detectDeath() {
        if (segmentLocations.isEmpty()) return true;

        Point head = segmentLocations.get(0);
        return head.x == -1 || head.x > mMoveRange.x || head.y == -1 || head.y > mMoveRange.y || checkSelfCollision();
    }

    boolean checkDinner(Point location) {
        if (!segmentLocations.isEmpty() && segmentLocations.get(0).equals(location)) {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    boolean checkCollision(Point location) {
        if (!segmentLocations.isEmpty() && segmentLocations.get(0).equals(location)) {
            return true;
        }
        return false;
    }

    void switchHeading(MotionEvent motionEvent) {
        heading = (motionEvent.getX() >= halfWayPoint) ? getNextClockwiseHeading() : getNextCounterClockwiseHeading();
    }

    private boolean checkSelfCollision() {
        Point head = segmentLocations.get(0);
        for (int i = 1; i < segmentLocations.size(); i++) {
            if (head.equals(segmentLocations.get(i))) return true;
        }
        return false;
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

    public int getSegmentSize() {
        return mSegmentSize;
    }

    public void setmSegmentSize(int mSegmentSize) {
        this.mSegmentSize = mSegmentSize;
    }

}