package com.harsh.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.harsh.hkutils.calendar.EventCalendarView;

public class CustomDialog extends DialogFragment{

	private static final String TAG = "CustomDialog";

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.test_calendar,container,false);

		EventCalendarView calendarView = view.findViewById(R.id.cal);
		calendarView.init(getChildFragmentManager());

		return view;
	}
}
