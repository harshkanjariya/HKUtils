package com.harsh.hkutils;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ExpandableLayout extends ConstraintLayout {
	public boolean isExpanded = true;
	private float height;
	private int duration = 500;
	private TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();

	public ExpandableLayout(@NonNull Context context) {
		super(context);
		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				height = getHeight();
			}
		});
	}
	public ExpandableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ExpandableLayout);
		isExpanded = typedArray.getBoolean(R.styleable.ExpandableLayout_expanded,true);
		duration = typedArray.getInteger(R.styleable.ExpandableLayout_duration,500);
		typedArray.recycle();

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				height = getHeight();
				if (!isExpanded){
					ViewGroup.LayoutParams layoutParams = getLayoutParams();
					layoutParams.height = 1;
					setLayoutParams(layoutParams);
				}
			}
		});
	}
	public void setInterpolator(TimeInterpolator interpolator){
		if (interpolator!=null)
			this.interpolator=interpolator;
	}
	public void toggle(){
		if (isExpanded)
			collapse();
		else
			expand();
	}
	public void expand() {
		if (!isExpanded) {
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, height);
			valueAnimator.setDuration(duration);
			valueAnimator.setInterpolator(interpolator);
			valueAnimator.addUpdateListener(animation -> {
				float val = (float) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				layoutParams.height = (int) val;
				setLayoutParams(layoutParams);
			});
			valueAnimator.start();
		}
		isExpanded=true;
	}
	public void collapse() {
		if (isExpanded) {
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(height, 1);
			valueAnimator.setDuration(duration);
			valueAnimator.setInterpolator(interpolator);
			valueAnimator.addUpdateListener(animation -> {
				float val = (float) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				layoutParams.height = (int) val;
				setLayoutParams(layoutParams);
			});
			valueAnimator.start();
		}
		isExpanded=false;
	}
}
