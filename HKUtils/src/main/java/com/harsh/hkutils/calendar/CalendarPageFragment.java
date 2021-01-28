package com.harsh.hkutils.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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

		adapter=new DayAdapter(Objects.requireNonNull(getContext()));
		gridView.setAdapter(adapter);

		gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				int originalHeight=gridView.getMeasuredHeight();
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

		public DayAdapter(Context context) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
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
			calendar.set(Calendar.DAY_OF_MONTH,day);
			if (dayInflater!=null){
				if (position < 7) {
					dayInflater.inflate(null, convertView, EventCalendarView.WEEK);
				}else if (day>0 && day<=maxDay){
					dayInflater.inflate(calendar,convertView,EventCalendarView.DAY);
				}else{
					dayInflater.inflate(null,convertView,EventCalendarView.BLANK);
				}
			}else{
				Drawable drawable = dot.getBackground();
				DrawableCompat.setTintList(drawable,
						new ColorStateList(
								new int[][]{
										new int[]{android.R.attr.state_selected},
										new int[]{-android.R.attr.state_selected},
								},new int[]{shared.selectedColor,Color.WHITE}));
				dot.setBackground(drawable);
			}

			if (position < 7) {
				number.setText(weeks[position]);
			}else if (day>0 && day<=maxDay){
				number.setText(""+(day));
				if (dateFormat.format(shared.selected.getTime()).equals(dateFormat.format(calendar.getTime()))) {
					dot.setSelected(true);
				}

				int finalDay = day;
				convertView.setOnClickListener(v -> {
					calendar.set(Calendar.DAY_OF_MONTH, finalDay);
					shared.selected.setTimeInMillis(calendar.getTimeInMillis());
					notifyDataSetChanged();
				});
			}else{
				number.setText("");
			}
			return convertView;
		}
	}
}