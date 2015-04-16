package com.yixun.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;


public class MyImageButton extends ImageButton {
    private String text = null;  //Ҫ��ʾ������
    private int color;               //���ֵ���ɫ
    public MyImageButton(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
     
    public void setText(String text){
        this.text = text;       //��������
    }
     
    public void setColor(int color){
        this.color = color;    //����������ɫ
    }
     
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(color);
        canvas.drawText(text, 15, 20, paint);  //��������
    }
 
}