package com.test.zhangtao.activitytest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangtao on 17/1/3.
 */

public class TempControlView extends View
{
    //控件宽
    private int width;
    //控件高
    private int height;
    //刻度盘半径
    private int dialRadius;
    //圆弧半径
    private int arcRadius;
    //刻度高
    private int scaleHeight = DensityUtils.dp2Px(getContext() , 10);

    //刻度盘画笔
    private Paint dialPaint;
    //圆弧画笔
    private Paint arcPaint;
    //标题画笔
    private Paint titlePaint;
    //温度标识画笔
    private Paint tempFlagPaint;
    //旋转按钮画笔
    private Paint buttonPaint;
    //温度显示画笔
    private Paint tempPaint;

    //文本提示
    private String title = "最高温度设置";
    //温度
    private int temperature = 15;
    //最低温度
    private int minTemp = 15;
    //最高温度
    private int maxTemp = 30;
    //四格代表温度1度
    private int angleRate = 4;
    //按钮图片
    private Bitmap buttonImage = BitmapFactory.decodeResource(getResources() , R.mipmap.btn_rotate);
    //按钮阴影图片
    private Bitmap buttonImageShadow = BitmapFactory.decodeResource(getResources() , R.mipmap.btn_rotate_shadow);

    //抗锯齿
    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    //温度改变监听
    private OnTempChangeListener onTempChangeListener;

    //当前按钮旋转的角度
    private float rotateAngle;
    //当前的角度
    private float currentAngle;

    public TempControlView(Context context)
    {
        this(context , null);
    }

    public TempControlView(Context context, AttributeSet attrs)
    {
        this(context, attrs , 0);
    }

    public TempControlView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView()
    {
        dialPaint = new Paint();
        dialPaint.setAntiAlias(true);
        dialPaint.setStrokeWidth(DensityUtils.dp2Px(getContext() , 2));
        dialPaint.setStyle(Paint.Style.STROKE);

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(Color.parseColor("#3CB7EA"));
        arcPaint.setStrokeWidth(DensityUtils.dp2Px(getContext() , 2));
        arcPaint.setStyle(Paint.Style.STROKE);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(DensityUtils.sp2px(getContext() , 16));
        titlePaint.setColor(Color.parseColor("#3B434E"));
        titlePaint.setStyle(Paint.Style.STROKE);

        tempFlagPaint = new Paint();
        tempFlagPaint.setAntiAlias(true);
        tempFlagPaint.setTextSize(DensityUtils.sp2px(getContext() , 25));
        tempFlagPaint.setColor(Color.parseColor("#E4A07E"));
        tempFlagPaint.setStyle(Paint.Style.STROKE);
        
        buttonPaint = new Paint();
        buttonPaint.setAntiAlias(true);
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0 , Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        
        tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setTextSize(DensityUtils.sp2px(getContext() , 60));
        tempPaint.setColor(Color.parseColor("#E27A3F"));
        tempPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        //控件宽，高
        width = height = Math.min(h , w);
        //刻度盘半径
        dialRadius = width / 2 - DensityUtils.dp2Px(getContext() , 20);
        //圆弧半径
        arcRadius = dialRadius - DensityUtils.dp2Px(getContext() , 20);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawScale(canvas);
        drawArc(canvas);
        drawText(canvas);
        drawButton(canvas);
        drawTemp(canvas);
    }

    private void drawScale(Canvas canvas)
    {
        canvas.save();
        canvas.translate(getWidth() / 2 , getHeight() / 2);
        //逆时针旋转133度
        canvas.rotate(-133);
        dialPaint.setColor(Color.parseColor("#3CB7EA"));
        for (int i = 0 ; i < 60 ; i++)
        {
            canvas.drawLine(0 , -dialRadius , 0 , -dialRadius + scaleHeight , dialPaint);
            canvas.rotate(4.5f);
        }

        //4.5 ＊ 60 ＝ 270 加上90度的等于360，等于目前坐标还是－133
        canvas.rotate(90);
        dialPaint.setColor(Color.parseColor("#E37364"));
        //一度的大小相当于angleRate的大小，也就是四个格
        for (int i = 0 ; i < (temperature - minTemp) * angleRate ; i++)
        {
            canvas.drawLine(0 , -dialRadius , 0 , -dialRadius + scaleHeight , dialPaint);
            canvas.rotate(4.5f);
        }
        canvas.restore();
    }

    //绘制刻度盘下的圆弧
    private void drawArc(Canvas canvas)
    {
        canvas.save();
        canvas.translate(getWidth() / 2 , getHeight() / 2);
        canvas.rotate(135 + 2);
        RectF rectF = new RectF(-arcRadius , -arcRadius , arcRadius , arcRadius);
        canvas.drawArc(rectF , 0 , 265 , false , arcPaint);
        canvas.restore();
    }

    //绘制标题和温度标示
    private void drawText(Canvas canvas)
    {
        canvas.save();
        //绘制标题
        float titleWidth = titlePaint.measureText(title);
        canvas.drawText(title , (width - titleWidth) / 2 , dialRadius * 2 + DensityUtils.dp2Px(getContext() , 15) , titlePaint);

        //绘制最小温度标识
        String minTempFlag = minTemp < 10 ? "0" + minTemp : minTemp + "";
        float tempFlagWidth = titlePaint.measureText(maxTemp + "");
        canvas.rotate(52 , width / 2 , height / 2);
        canvas.drawText(minTempFlag , (width - tempFlagWidth) / 2 , height + DensityUtils.dp2Px(getContext() , 5) , tempFlagPaint);

        //绘制最大温度标识
        canvas.rotate(-101 , width / 2 , height / 2);
        canvas.drawText(maxTemp + "" , (width - tempFlagWidth) / 2 , height + DensityUtils.dp2Px(getContext() , 5) , tempFlagPaint);
        canvas.restore();
    }


    private void drawButton(Canvas canvas)
    {
        //按钮宽高
        int buttonWidth = buttonImage.getWidth();
        int buttonHeight = buttonImage.getHeight();
        //按钮阴影宽高
        int buttonShadowWidth = buttonImageShadow.getWidth();
        int buttonShadowHeight = buttonImageShadow.getHeight();

        canvas.drawBitmap(buttonImageShadow , (width - buttonShadowWidth) / 2 , (height - buttonShadowHeight) / 2 , buttonPaint);

        Matrix matrix = new Matrix();
        //设置按钮位置
        matrix.setTranslate(buttonWidth / 2 , buttonHeight / 2);
        //设置旋转角度
        matrix.preRotate(45 + rotateAngle);
        //按钮位置还原，此时按钮位置在左上角
        matrix.preTranslate(-buttonWidth / 2 , -buttonHeight / 2);
        //将按钮中心位置
        matrix.postTranslate((width - buttonWidth) / 2 , (height - buttonHeight) / 2);

        canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.drawBitmap(buttonImage , matrix , buttonPaint);
    }

    private void drawTemp(Canvas canvas)
    {
        canvas.save();
        canvas.translate(getWidth() / 2 , getHeight() / 2);

        float tempWidth = tempPaint.measureText(temperature + "");
        float tempHeight = (tempPaint.ascent() + tempPaint.descent()) / 2;
        canvas.drawText(temperature + "°" , -tempWidth / 2 - DensityUtils.dp2Px(getContext() , 5) , -tempHeight , tempPaint);
    }

    private boolean isDown;
    private boolean isMove;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                float downX = event.getX();
                float downY = event.getY();
                currentAngle = calcAngle(downX , downY);
                break;

            case MotionEvent.ACTION_MOVE:
                isMove = true;
                float targetX;
                float targetY;

                targetX = event.getX();
                targetY = event.getY();
                float angle = calcAngle(targetX , targetY);

                //滑过角度增量
                float inCreasedAngle = angle - currentAngle;

                //防止越界
                if (inCreasedAngle < -270)
                {
                    inCreasedAngle = inCreasedAngle + 360;
                }
                else if (inCreasedAngle > 270)
                {
                    inCreasedAngle = inCreasedAngle - 360;
                }

                inAngleCrease(inCreasedAngle);
                currentAngle = angle;
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (isDown && isMove)
                {
                    //纠正指针位置
                    rotateAngle = (temperature - minTemp) * angleRate * 4.5f;
                    invalidate();
                    //回调温度改变接口
                    if (onTempChangeListener != null)
                        onTempChangeListener.change(temperature);
                    isMove = false;
                    isDown = false;
                }
                break;
        }

        return true;
    }

    //以按钮圆心为坐标原点，求出（targetX ， targetY）坐标与x轴的夹角
    private float calcAngle(float targetX, float targetY)
    {
        float x = targetX - width / 2;
        float y = targetY - height / 2;
        double radian;

        if (x != 0)
        {
            float tan = Math.abs(y / x);
            if (x > 0)
            {
                if (y >= 0)
                {
                    radian = Math.atan(tan);
                }
                else
                {
                    radian = 2 * Math.PI - Math.atan(tan);
                }
            }
            else
            {
                if (y >= 0)
                {
                    radian = Math.PI - Math.atan(tan);
                }
                else
                {
                    radian = Math.PI + Math.atan(tan);
                }
            }
        }
        else
        {
            if (y > 0)
            {
                radian = Math.PI / 2;
            }
            else
            {
                radian = - Math.PI / 2;
            }
        }

        return (float) ((radian * 180) / Math.PI);
    }

    //增加旋转角度
    private void inAngleCrease(float angle)
    {
        rotateAngle += angle;
        if (rotateAngle < 0)
        {
            rotateAngle = 0;
        }
        else if (rotateAngle > 270)
        {
            rotateAngle = 270;
        }

        temperature = (int) ((rotateAngle / 4.5f) / angleRate + minTemp);
    }

    public void setOnTempChangeListener(OnTempChangeListener onTempChangeListener)
    {
        this.onTempChangeListener = onTempChangeListener;
    }

    public void setTemp(int minTemp , int maxTemp , int temp)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.temperature = temp;
        this.angleRate = 60 / (maxTemp - minTemp);
        rotateAngle = (temp - minTemp) * angleRate * 4.5f;
        invalidate();
    }

    //温度改变监听接口
    public interface OnTempChangeListener
    {
        void change(int temp);
    }
}
