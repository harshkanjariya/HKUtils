package com.harsh.hkutils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

public class DropDown extends AppCompatSpinner {
	private int item_layout;
	public DropDown(@NonNull Context context) {
		super(context);
	}
	public DropDown(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}
}
