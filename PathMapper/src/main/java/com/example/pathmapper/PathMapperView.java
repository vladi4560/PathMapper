package com.example.pathmapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class PathMapperView extends View {

    private Paint pathPaint;
    private Paint startPaint;
    private Paint endPaint;
    private Path path;

    private double pathSize;
    private ArrayList<Coordinates> coordinates;
    private Bitmap pathBitmap;
    private int bitmapSize;
    private double proportion;
    private double latitudeOffset;
    private double longitudeOffset;
    private double scaleCoordinates;
    private double MinLatitude;
    private double MaxLatitude;
    private double MinLongitude;
    private double MaxLongitude;

    private boolean cancelFirstDraw;

    public PathMapperView(Context context) {
        super(context);
        init();
    }

    public PathMapperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PathMapperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        coordinates = new ArrayList<>();
        path = new Path();
        startPaint = new Paint();
        pathPaint = new Paint();
        endPaint = new Paint();
        pathSize = 8;
        bitmapSize = 300;
        proportion = 0;
        latitudeOffset = 0;
        longitudeOffset = 0;
        scaleCoordinates = 0;
        MinLatitude = 0;
        MaxLatitude = 0;
        MinLongitude = 0;
        MaxLongitude = 0;
        cancelFirstDraw = false;

        // Set the Colors of the path
        setColors();

    }

    public void setCoordinates(ArrayList<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    protected void onDraw(Canvas canvas) {
        if (!cancelFirstDraw) {
            cancelFirstDraw = true;
            return;
        }
        if (coordinates.size() > 1) {
            pathBitmap = createPathView(canvas);
            canvas.drawBitmap(pathBitmap, 200, 200, pathPaint);
        }
    }

    public Bitmap createPathView(Canvas canvas) {

        path.reset();

        // Set lowest and highest
        setMinMax();

        // Create bitmap to draw the path and give it to the canvas for showing it
        pathBitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(pathBitmap);

        // scale the coordinates to the bitmap
        setScale();

        // First point in the Path
        float x = (float) (longitudeOffset + (bitmapSize) * ((coordinates.get(0).getLong() - MinLongitude) * proportion/ scaleCoordinates));
        float y = (float) (-latitudeOffset + bitmapSize - ((bitmapSize) * ((coordinates.get(0).getLat() - MinLatitude) / scaleCoordinates)));
        canvas.drawPoint(x,y,startPaint);
        path.moveTo(x,y);
        canvas.drawPath(path, startPaint);


        // All other points in the path
        for (int i = 1; i < coordinates.size(); i++) {
            x= (float) (longitudeOffset + (bitmapSize) * ((coordinates.get(i).getLong() - MinLongitude) * proportion/ scaleCoordinates));
            y= (float) (-latitudeOffset + bitmapSize - ((bitmapSize) * ((coordinates.get(i).getLat() - MinLatitude) / scaleCoordinates)));
            path.lineTo(x,y);
        }
        canvas.drawPoint(x+1,y+1,endPaint);
        canvas.drawPath(path, pathPaint);

        return pathBitmap;
    }

    private void setScale(){
        double middle = (MaxLatitude + MinLatitude) / 2;
        double equator = Math.abs(middle);
        proportion = Math.cos(Math.toRadians(equator));


        scaleCoordinates = Math.max((MaxLongitude - MinLongitude)*proportion, MaxLatitude - MinLatitude);
        latitudeOffset = bitmapSize * (1 - ((MaxLatitude - MinLatitude) / scaleCoordinates)) / 2;
        longitudeOffset = bitmapSize * (1 - (proportion*(MaxLongitude - MinLongitude) / scaleCoordinates)) / 2;
        bitmapSize = bitmapSize - 25;
    }
    private void setColors(){
        // Set the Colors of the path
        startPaint.setColor(Color.RED);
        startPaint.setStyle(Paint.Style.STROKE);
        startPaint.setStrokeWidth((float) (pathSize*3));
        startPaint.setStrokeCap(Paint.Cap.ROUND);

        pathPaint.setColor(Color.BLUE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth((float) pathSize);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);

        endPaint.setColor(Color.BLACK);
        endPaint.setStyle(Paint.Style.STROKE);
        endPaint.setStrokeWidth((float) (pathSize*3));
        endPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void setMinMax(){
        MinLatitude = coordinates.get(0).getLat();
        MaxLatitude = coordinates.get(0).getLat();
        MinLongitude = coordinates.get(0).getLong();
        MaxLongitude = coordinates.get(0).getLong();

        for (int i = 1; i < coordinates.size(); i++) {
            MinLatitude = Math.min(MinLatitude, coordinates.get(i).getLat());
            MaxLatitude = Math.max(MaxLatitude, coordinates.get(i).getLat());
            MinLongitude = Math.min(MinLongitude, coordinates.get(i).getLong());
            MaxLongitude = Math.max(MaxLongitude, coordinates.get(i).getLong());
        }
    }
    public void endTask() {
        this.invalidate();
    }

    public void setPathPaint(Paint pathPaint) {
        this.pathPaint = pathPaint;
    }

    public void setStartPaint(Paint startPaint) {
        this.startPaint = startPaint;
    }

    public void setEndPaint(Paint endPaint) {
        this.endPaint = endPaint;
    }

    public void setPathSize(double pathSize) {
        this.pathSize = pathSize;
    }
}
