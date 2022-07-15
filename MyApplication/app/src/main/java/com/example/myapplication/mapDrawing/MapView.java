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
public class MapView extends View {

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
    private List<Tag> tagList = new ArrayList<>(); //标记点
    private List<Trace> traceList = new ArrayList<>(); //轨迹点
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
//        if (nowX == -eachStep || nowY == -eachStep) {
//            nowX = directionX * eachStep;
//            nowY = directionY * eachStep;
//        } else {
//
//        }
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawBitmap(bitmap,0,0 + yOffset,null);





        for (Tag tag : tagList) {
            if (nowTag != null){
                if (nowTag.getId() == tag.getId()){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tag_red);
                    canvas.drawBitmap(bitmap,tag.getX(),tag.getY() + yOffset,null);
                    continue;
                }
            }
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tag);
            canvas.drawBitmap(bitmap,tag.getX(),tag.getY() + yOffset,null);
        }

        paint.setARGB(255, 30, 144, 255);
        paint.setStrokeWidth(7);
        for (int tracingPointNo = 0; tracingPointNo < tracingPointList.size(); tracingPointNo++)
        {
            if (tracingPointNo < tracingPointList.size() - 1) {
                canvas.drawLine(tracingPointList.get(tracingPointNo).getX(), tracingPointList.get(tracingPointNo).getY() + yOffset,
                        tracingPointList.get(tracingPointNo + 1).getX(), tracingPointList.get(tracingPointNo + 1).getY() + yOffset,
                        paint
                );
            }
        }

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);

            if (i == 0){
                directionX = user.getX().intValue();
                directionY = user.getY().intValue();
                if (nowX == -eachStep || nowY == -eachStep) {
                    nowX = directionX;
                    nowY = directionY;
                }
                //画用户自己
                paint.setStyle(Paint.Style.FILL);
                paint.setARGB(255, 0, 188, 255);

                canvas.drawCircle(nowX, nowY + yOffset, 30, paint);
                paint.setARGB(255, 240, 240, 240);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
               //  paint.setShadowLayer(5, 0, 0, Color.GRAY);
                canvas.drawCircle(nowX, nowY + yOffset, 30, paint);
                continue;
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setARGB(255, 224, 64, 64);
                canvas.drawCircle(user.getX(), user.getY() + yOffset, 30, paint);
                paint.setARGB(255, 240, 240, 240);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                paint.setShadowLayer(5, 0, 0, Color.GRAY);
                canvas.drawCircle(user.getX(), user.getY() + yOffset, 30, paint);
            }


        }

        eachMove();
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


    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public List<Trace> getTraceList() {
        return traceList;
    }

    public void setTraceList(List<Trace> traceList) {
        this.traceList = traceList;
    }

    public Tag getNowTag() {
        return nowTag;
    }

    public void setNowTag(Tag nowTag) {
        this.nowTag = nowTag;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
