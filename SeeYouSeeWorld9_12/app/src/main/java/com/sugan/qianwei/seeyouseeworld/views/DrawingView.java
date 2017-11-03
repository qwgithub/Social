package com.sugan.qianwei.seeyouseeworld.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.sugan.qianwei.seeyouseeworld.R;


public class DrawingView extends View {
    private boolean haveTouch = false;
    private Rect touchArea;
    private Paint paint;
    private Paint paint2;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.springgreen));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        paint2 = new Paint();
        paint2.setColor(getResources().getColor(R.color.white));
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(5);

        haveTouch = false;
    }
    
    public void setHaveTouch(boolean val, Rect rect) {
        haveTouch = val;
        touchArea = rect;
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        if(haveTouch){
//            canvas.drawRect(
//              touchArea.left, touchArea.top, touchArea.right, touchArea.bottom,
//              paint);
            int circleX = (touchArea.left+touchArea.right)/2;
            int circleY = (touchArea.top+touchArea.bottom)/2;
            //外圈
            canvas.drawCircle(circleX, circleY, 100, paint);
            //内圈
            canvas.drawCircle(circleX, circleY, 15, paint2);
        }
    }   
}