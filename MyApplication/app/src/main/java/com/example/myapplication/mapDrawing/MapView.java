package com.example.myapplication.mapDrawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

// 绘制地图 View
public class MapView extends View {

    public int eachStep = 100;
    private Paint mPaint = new Paint();
    public int directionX = 0, directionY = 0;
    public int nowX = -eachStep, nowY = -eachStep;
    public int WIDTH, HEIGHT;
    public boolean isOKToDraw = false;


    public int widthRectNumber, heightRectNumber;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WIDTH = getMeasuredWidth();
        HEIGHT = getMeasuredHeight();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        // 如果第一次定义就直接移动到目标位置
        if (nowX == -eachStep || nowY == -eachStep) {
            nowX = directionX * eachStep;
            nowY = directionY * eachStep;
        } else {

        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(nowX + 100,  nowY + 100 , 40, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawCircle(nowX + 100, nowY + 100 , 40, paint);

        eachMove();
        postInvalidateDelayed(eachStep);


    }

    private void eachMove() {
        float totalDistance = (float) Math.sqrt(Math.pow(nowX - directionX, 2) + Math.pow(nowY - directionY, 2));
        if (totalDistance < 0) {
            nowX = directionX * eachStep ;
            nowY = directionY * eachStep ;
        } else {
            nowX += (float)(directionX * eachStep - nowX) / 10.0;
            nowY += (float)(directionY * eachStep - nowY) / 10.0;

        }

    }


}