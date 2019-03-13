package com.example.george.bookmarker.activity.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by GEORGE on 2017/04/23.
 */

public class CameraOverlayView extends View {

    private static final int ALPHA = 128;

    private static final Paint coverPaint = new Paint();
    private static final Paint targetPaint = new Paint();

    private int width = 0;
    private int height = 0;

    private Rect rect;

    public CameraOverlayView(Context context) {
        super(context);

        coverPaint.setStyle(Paint.Style.FILL);
        coverPaint.setColor(Color.GREEN);
        coverPaint.setAlpha(ALPHA);

        targetPaint.setStyle(Paint.Style.FILL);
        targetPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);

        rect = new Rect(0,0,width,height * 2/5);
        canvas.drawRect(rect,coverPaint);

        rect.offset(0, height * 1/5);
        canvas.drawRect(rect,targetPaint);

        rect.offset(0, height * 2/5);
        canvas.drawRect(rect,coverPaint);
    }
}
