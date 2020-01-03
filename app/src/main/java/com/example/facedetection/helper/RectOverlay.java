package com.example.facedetection.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic {
    private int mrectcolor = Color.GREEN;
    private float mstrockwidth = 4.0f;
    private Paint mrectpaint;
    private GraphicOverlay graphicoverlay;
    private Rect rect;


    public RectOverlay(GraphicOverlay graphicoverlay, Rect rect) {
        super(graphicoverlay);
        mrectpaint = new Paint();
        mrectpaint.setColor(mrectcolor);
        mrectpaint.setStyle(Paint.Style.STROKE);
        mrectpaint.setStrokeWidth(mstrockwidth);

        this.graphicoverlay = graphicoverlay;
        this.rect = rect;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.bottom = translateX(rectF.bottom);
        rectF.top = translateX(rectF.top);
        canvas.drawRect(rectF,mrectpaint);


    }
}
