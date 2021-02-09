package com.harsh.hkutils.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.harsh.hkutils.R;

public class CircularProgressButton
		extends androidx.appcompat.widget.AppCompatTextView
		implements View.OnTouchListener {

	float progress = 0;
	float maxScale;
	Callback callback;
	float progressBarWidth;
	int progressColor;
	int buttonColor;
	int shadowColor;
	int progressBackgroundColor;
	float elevation;
	private boolean reverse = false;
	int duration;

	public CircularProgressButton(Context context) {
		super(context);
		commonConstruct();
	}
	public CircularProgressButton(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressButton);
		elevation = array.getDimension(R.styleable.CircularProgressButton_elevation,40);
		progressBarWidth = array.getDimension(R.styleable.CircularProgressButton_progressBarWidth,50);
		progressColor = array.getColor(R.styleable.CircularProgressButton_progressColor, Color.parseColor("#6db538"));
		progressBackgroundColor = array.getColor(R.styleable.CircularProgressButton_progressBackgroundColor,Color.parseColor("#e2f0df"));
		buttonColor = array.getColor(R.styleable.CircularProgressButton_buttonColor,Color.parseColor("#8dd558"));
		shadowColor = array.getColor(R.styleable.CircularProgressButton_shadowColor,Color.parseColor("#a2a0af"));
		duration = array.getInt(R.styleable.CircularProgressButton_android_duration,1000);
		maxScale = array.getFloat(R.styleable.CircularProgressButton_maxScale,0.2f);
		array.recycle();
		commonConstruct();
	}
	public void setCallback(Callback callback){
		this.callback=callback;
	}
	Paint paint;
	BlurMaskFilter blurMaskFilter;
	private void commonConstruct(){
		paint = new Paint();
		blurMaskFilter = new BlurMaskFilter(elevation, BlurMaskFilter.Blur.NORMAL);
		setOnTouchListener(this);
	}
	ValueAnimator animator;
	public void start(){
		if(reverse)
			animator = ValueAnimator.ofFloat(progress,0);
		else
			animator = ValueAnimator.ofFloat(progress,360);
		animator.addUpdateListener(animation -> {
			float value = (float) animation.getAnimatedValue();
			progress = value;
			if (callback!=null)
				callback.onProgress(progress/360);
			setScaleX(1+value*maxScale/360);
			setScaleY(1+value*maxScale/360);
			invalidate();
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) { }
			@Override
			public void onAnimationEnd(Animator animation) {
				if ((!reverse && progress==360) || (reverse && progress==0)) {
					if (callback!=null)
						callback.onComplete();
				}
			}
			@Override
			public void onAnimationCancel(Animator animation) {
				ValueAnimator valueAnimator;
				if (reverse)
					valueAnimator = ValueAnimator.ofFloat(progress,360);
				else
					valueAnimator = ValueAnimator.ofFloat(progress,0);
				valueAnimator.addUpdateListener(animation1 -> {
					float value = (float) animation1.getAnimatedValue();
					progress = value;
					if (callback!=null)
						callback.onProgress(progress/360);
					setScaleX(1+value*maxScale/360);
					setScaleY(1+value*maxScale/360);
					invalidate();
				});
				valueAnimator.start();
				invalidate();
				if (callback!=null)
					callback.onCancel();
			}
			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		animator.setDuration(duration);
		animator.start();
	}
	private boolean cancelable = true;
	public void cancelable(boolean value){
		cancelable = value;
	}
	public boolean isRunning(){
		return animator != null && animator.isRunning();
	}
	public void cancel(){
		if (cancelable)animator.cancel();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				start();
				break;
			case MotionEvent.ACTION_UP:
				if (cancelable)animator.cancel();
				break;
		}
		return true;
	}
	@Override
	public boolean performClick() {
		return super.performClick();
	}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onDraw(Canvas canvas) {
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();

		int paddingMax = paddingTop;
		paddingMax = Math.max(paddingMax,paddingBottom);
		paddingMax = Math.max(paddingMax,paddingLeft);
		paddingMax = Math.max(paddingMax,paddingRight);
		paddingMax += progressBarWidth/2;

		int width = getWidth();
		int height = getHeight();

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(progressBarWidth);

		paint.setMaskFilter(blurMaskFilter);
		paint.setColor(shadowColor);
		canvas.drawCircle(width/2.0f,height/2.0f,width/2.0f-paddingMax,paint);

		paint.setMaskFilter(null);
		paint.setColor(progressBackgroundColor);
		canvas.drawArc(paddingMax,paddingMax,width-paddingMax,width-paddingMax,progress,360f,false,paint);

		paint.setColor(progressColor);
		canvas.drawArc(paddingMax,paddingMax,width-paddingMax,width-paddingMax,-90,progress,false,paint);

		paint.setStyle(Paint.Style.FILL);
		paint.setColor(buttonColor);
		canvas.drawCircle(width/2.0f,height/2.0f,width/2.0f-paddingMax,paint);

		paint.setColor(getCurrentTextColor());
		paint.setTextSize(getTextSize());
		String s = getText().toString();
		float textwidth = paint.measureText(s);
		canvas.drawText(s, width/2f-(textwidth/2), height/2f+getTextSize()/4f, paint);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	public interface Callback{
		void onComplete();
		void onProgress(float percentage);
		void onCancel();
	}
	public void setReverse(boolean reverse) {
		if(reverse) {
			setScaleX(1 + maxScale);
			setScaleY(1 + maxScale);
			progress = 360;
		} else {
			progress = 0;
			setScaleX(1);
			setScaleY(1);
		}
		this.reverse = reverse;
		invalidate();
	}
	public boolean isReverse() { return  reverse; }
}