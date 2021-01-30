package com.harsh.demo;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.harsh.hkutils.ExpandableLayout;
import com.harsh.hkutils.calendar.EventCalendarView;
import com.harsh.hkutils.calendar.GridViewSquareItem;
import com.harsh.hkutils.list.DropDown;
import com.harsh.hkutils.list.HKList;
import com.harsh.hkutils.list.SelectableEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarDemoFragment extends Fragment {

	private static final String TAG = "CalendarDemo";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar_demo, container, false);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		EventCalendarView calendarView = view.findViewById(R.id.calendar);
		calendarView.init(getParentFragmentManager());
		calendarView.setCallback(new EventCalendarView.Callback() {
			@Override
			public void onDateSelect(Calendar calendar) { }
			@Override
			public void onMonthSelect(int month) { }
			@Override
			public void onYearSelect(int year) { }
		});
		calendarView.setInflater(new EventCalendarView.DayInflater() {
			@Override
			public void inflate(Calendar calendar, View layout, boolean selected, int type) {
				ImageView dot = layout.findViewById(R.id.dot);
				if (type==EventCalendarView.DAY){
					if (selected){
						GridViewSquareItem squareItem = layout.findViewById(R.id.root_layout);
						squareItem.setBackgroundResource(R.drawable.ic_circle);
					}
					if (dateFormat.format(calendar.getTime()).equals("02/01/2021")){
						dot.setColorFilter(Color.BLUE);
					}
				}
			}
		});

		Button button = view.findViewById(R.id.dialog_button);
		button.setOnClickListener(v -> showDialog());

		return view;
	}

	private void showDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.show(requireActivity().getSupportFragmentManager(),"Calendar Dialog");
	}
}