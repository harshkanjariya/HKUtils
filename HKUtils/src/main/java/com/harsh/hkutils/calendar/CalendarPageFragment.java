package com.harsh.hkutils.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.harsh.hkutils.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CalendarPageFragment extends Fragment {

	private static final String TAG = "CalendarPageFragment";
	Calendar calendar;
	DayAdapter adapter;
	EventCalendarView.DayInflater dayInflater;
	int day_layout;
	Shared shared;
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

	public CalendarPageFragment(long millis,Shared shared) {
		this.calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		this.dayInflater = shared.inflater;
		this.day_layout = shared.day_layout;
		this.shared = shared;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar_page, container, false);

		GridView gridView = view.findViewById(R.id.calendar_grid);

		adapter=new DayAdapter(Objects.requireNonNull(getContext()),calendar);
		gridView.setAdapter(adapter);

		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

				int originalWidth=view.getMeasuredWidth();
				int originalHeight=originalWidth*6/7;

				if(shared.height<originalHeight) {
					ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
					layoutParams.height = originalHeight;
					shared.height = originalHeight;
					container.setLayoutParams(layoutParams);
				}
			}
		});
		return view;
	}
	class DayAdapter extends BaseAdapter {

		LayoutInflater inflater;
		int firstDayOfWeek, maxDay;
		String[] weeks=new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		ArrayList<Calendar>calendars;

		public DayAdapter(Context context,Calendar calendar) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;

			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			calendars = new ArrayList<>();
			for (int i=calendar.getActualMinimum(Calendar.DAY_OF_MONTH);i<=maxDay;i++){
				Calendar c = Calendar.getInstance();
				c.set(Calendar.MONTH,month);
				c.set(Calendar.YEAR,year);
				c.set(Calendar.DAY_OF_MONTH,i);
				calendars.add(c);
			}
		}
		@Override
		public int getCount() {
			return 42;
		}
		@Override
		public Object getItem(int position) { return position; }

		@Override
		public long getItemId(int position) { return position; }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(day_layout,parent,false);

			TextView number = convertView.findViewById(R.id.number);
			View dot = convertView.findViewById(R.id.dot);

			int day = position - 6 - firstDayOfWeek;
			if (29+position-firstDayOfWeek <= maxDay)
				day = 29+position-firstDayOfWeek;

			Calendar today = null;
			boolean selected = false;
			String selectedString = dateFormat.format(shared.selected.getTime());

			if (day>0 && day<=maxDay){
				today = calendars.get(day-1);
				selected = selectedString.equals(dateFormat.format(today.getTime()));
			}

			if (dayInflater!=null){
				if (position < 7) {
					dayInflater.inflate(null, convertView, false, EventCalendarView.WEEK);
				}else if (today!=null){
					dayInflater.inflate(today,convertView,selected,EventCalendarView.DAY);
				}else{
					dayInflater.inflate(null,convertView, false,EventCalendarView.BLANK);
				}
			}else{
				Drawable drawable = dot.getBackground();
				if(drawable!=null) {
					DrawableCompat.setTintList(drawable,
							new ColorStateList(
									new int[][]{
											new int[]{android.R.attr.state_selected},
											new int[]{-android.R.attr.state_selected}
									}, new int[]{shared.selectedColor, Color.WHITE}));
					dot.setBackground(drawable);
				}
			}

			if (position < 7) {
				number.setText(weeks[position]);
			}else if (today!=null){
				number.setText(""+(day));
				if (selected) {
					dot.setSelected(true);
					convertView.setSelected(true);
					number.setSelected(true);
				}
				Calendar finalToday = today;
				convertView.setOnClickListener(v -> {
					shared.selected.setTimeInMillis(finalToday.getTimeInMillis());
					if (shared.callback!=null) {
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(finalToday.getTimeInMillis());
						shared.callback.onDateSelect(c);
					}
					notifyDataSetChanged();
				});
			}else{
				number.setText("");
			}
			return convertView;
		}
	}
}