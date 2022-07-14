package com.example.myapplication.mapDrawing;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.Trace;
import com.example.myapplication.entity.TracingPoint;
import com.example.myapplication.entity.User;

import java.util.ArrayList;
import java.util.List;

// 绘制地图 View
public class TraceView extends View {

    public TraceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int yOrigin = 0;
    public int yOffset = 0;
    public int yRealOffset = 0;

    public int eachStep = 1;
    private Paint mPaint = new Paint();
    public int directionX = 0, directionY = 0;
    public int nowX = -eachStep, nowY = -eachStep;
    public int WIDTH, HEIGHT;
    public boolean isOKToDraw = false;

    public int widthRectNumber, heightRectNumber;

    private Bitmap bitmap = null; //平面图
    private List<User> userList = new ArrayList<>(); //当前用户集合

    public List<TracingPoint> getTracingPointList() {
        return tracingPointList;
    }

    public void setTracingPointList(List<TracingPoint> tracingPointList) {
        this.tracingPointList = tracingPointList;
    }

    private List<TracingPoint> tracingPointList = new ArrayList<>(); // 轨迹记录点

    private Tag nowTag = null;

    public void setBitmap(Bitmap bitmap) {

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = 1000;

        bitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth, mScreenHeight, true);
        this.bitmap = bitmap;
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
//        if (nowX == -eachStep || nowY == -eachStep) {
//            nowX = directionX * eachStep;
//            nowY = directionY * eachStep;
//        } else {
//
//        }
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawBitmap(bitmap,0,0 + yOffset,null);



        paint.setARGB(255, 0, 0, 255);
        for (int tracingPointNo = 0; tracingPointNo < tracingPointList.size(); tracingPointNo++)
        {
            if (tracingPointNo < tracingPointList.size() - 1) {
                canvas.drawLine(tracingPointList.get(tracingPointNo).getX(), tracingPointList.get(tracingPointNo).getY() + yOffset,
                        tracingPointList.get(tracingPointNo + 1).getX(), tracingPointList.get(tracingPointNo + 1).getY() + yOffset,
                        paint
                );
            }
        }

        postInvalidateDelayed(eachStep);


    }

    // 上下移动图片
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取当前输入点的X、Y坐标（视图坐标）
        int thisY = 0;
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下时，获取初始 y
                yOrigin = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //处理移动事件
                thisY = y - yOrigin;
                yOffset = thisY + yRealOffset;
                break;
            case MotionEvent.ACTION_UP:
                //处理离开事件

                yRealOffset = yOffset;
                break;
            // originY = yOffset;
        }
        postInvalidate();
        return true;
    }


}
