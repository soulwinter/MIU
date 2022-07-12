package com.example.myapplication.mapDrawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.Trace;

import java.util.ArrayList;
import java.util.List;

// 绘制地图 View
public class MapView extends View {

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
        if (nowX == -eachStep || nowY == -eachStep) {
            nowX = directionX * eachStep;
            nowY = directionY * eachStep;
        } else {

        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawBitmap(bitmap,0,0,null);
        canvas.drawCircle(nowX,  nowY, 15, paint);
        paint.setColor(Color.BLACK);

        for (Tag tag : tagList) {
            if (nowTag != null){
                if (nowTag.getId() == tag.getId()){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tag_red);
                    canvas.drawBitmap(bitmap,tag.getX(),tag.getY(),null);
                    continue;
                }
            }
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tag);
            canvas.drawBitmap(bitmap,tag.getX(),tag.getY(),null);
        }


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
}
