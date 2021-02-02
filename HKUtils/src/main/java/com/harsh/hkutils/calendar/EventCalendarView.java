package com.harsh.hkutils.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.renderscript.RSInvalidStateException;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.harsh.hkutils.R;
import com.harsh.hkutils.list.HKList;
import com.harsh.hkutils.list.HKListHelper;
import com.harsh.hkutils.list.HKViewHolder;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Locale;

public class EventCalendarView extends LinearLayout{
	private static final String TAG = "EventCalendarView";
	private ViewPager viewPager;
	CalendarPagerAdapter adapter;
	private TextView monthTitle;
	private ImageView nextButton;
	private ImageView previousButton;

	Shared shared;
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);

	public boolean isMonthView = false;
	public boolean isYearView = false;

	private int selectedBackgroundResource;

	public static int DAY=0;
	public static int BLANK=-1;
	public static int WEEK=1;
	HKList monthList;

	public int getSelectedTextcolor() {
		return shared.selectedColor;
	}
	public void setSelectedTextcolor(int selectedTextcolor) {
		this.shared.selectedColor = selectedTextcolor;
	}
	public int getSelectedBackground() {
		return selectedBackgroundResource;
	}
	public void setSelectedBackground(int res) {
		this.selectedBackgroundResource = res;
	}
	public EventCalendarView(Context context) {
		super(context);
		shared = new Shared();
		shared.day_layout = R.layout.item_calendar_day;
		commonInit(context);
	}
	public EventCalendarView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		shared = new Shared();
		TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.EventCalendarView);
		shared.day_layout=typedArray.getResourceId(R.styleable.EventCalendarView_dayLayout, R.layout.item_calendar_day);
		typedArray.recycle();
		commonInit(context);
	}
	private void checkLayout(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(shared.day_layout,this,false);
		if (view instanceof GridViewSquareItem) {
			if (view.findViewById(R.id.number) == null) {
				throw new RSInvalidStateException("Day layout must contain atleast one TextView with id 'number'");
			}
			if (view.findViewById(R.id.dot) == null) {
				throw new RSInvalidStateException("Day layout must contain atleast one View with id 'dot'");
			}
		}else{
			throw new RSInvalidStateException("Day layout root element must be GridViewSquareItem");
		}
	}
	private void commonInit(Context context){
		inflate(context,R.layout.layout_event_calendar_view,this);

		checkLayout();

		shared.selectedColor = ContextCompat.getColor(context,R.color.colorPrimary);
		selectedBackgroundResource = R.drawable.calendar_selected_background;

		monthTitle = findViewById(R.id.month_title);
		monthList = findViewById(R.id.month_list);

		nextButton = findViewById(R.id.next_month_button);
		previousButton = findViewById(R.id.previous_month_button);

		viewPager=findViewById(R.id.calendar_pager);
	}
	public void init(FragmentManager fragmentManager){
		init(fragmentManager,null);
	}
	public void init(FragmentManager fragmentManager,Calendar initialDate){
		if (initialDate!=null)
			shared.selected.setTimeInMillis(initialDate.getTimeInMillis());

		adapter = new CalendarPagerAdapter(fragmentManager, shared);

		monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));

		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(1);

		findViewById(R.id.month_year_title).setOnClickListener(view -> {
			if (isMonthView){
				showDateSelector();
			}else {
				nextButton.setVisibility(GONE);
				previousButton.setVisibility(GONE);
				showMonthSelector();
			}
		});

		nextButton.setOnClickListener(v -> viewPager.setCurrentItem(2));
		previousButton.setOnClickListener(v -> viewPager.setCurrentItem(0));

		ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (position == 1 && positionOffset == 0){
					monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
				}else if (position == 1 && positionOffset > 0.5){
					monthTitle.setText(dateFormat.format(adapter.calendar[2].getTime()));
				}else if (position == 0 && positionOffset < 0.5){
					monthTitle.setText(dateFormat.format(adapter.calendar[0].getTime()));
				}else if (position!=2){
					monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
				}
			}
			@Override
			public void onPageSelected(int position) { }
			@Override
			public void onPageScrollStateChanged(int state) {
				if (state==ViewPager.SCROLL_STATE_IDLE && viewPager.getCurrentItem()!=1) {
					boolean notify = true;
					if (viewPager.getCurrentItem() == 2) {
						if (adapter.calendar[2].get(Calendar.MONTH) == Calendar.DECEMBER && adapter.calendar[2].get(Calendar.YEAR)==2099)
							notify = false;
						else
							adapter.nextMonth();
					} else {
						if (adapter.calendar[0].get(Calendar.MONTH) == Calendar.JANUARY && adapter.calendar[0].get(Calendar.YEAR)==1970)
							notify = false;
						else
							adapter.previousMonth();
					}
					if (notify) {
						viewPager.setCurrentItem(1, false);
						adapter.notifyDataSetChanged();
					}
				}
			}
		};
		viewPager.addOnPageChangeListener(pageChangeListener);
	}
	public void setOnTitleClick(View.OnClickListener onClickListener){
		findViewById(R.id.month_year_title).setOnClickListener(onClickListener);
	}
	public void update(){
		adapter.notifyDataSetChanged();
	}
	public Calendar getSelected(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(shared.selected.getTimeInMillis());
		return calendar;
	}
	public void setSelected(Calendar calendar){
		shared.selected.setTimeInMillis(calendar.getTimeInMillis());
		adapter.update();
	}
	private void showMonthSelector() {
		isYearView = false;
		isMonthView = true;
		viewPager.setVisibility(GONE);
		findViewById(R.id.year_list).setVisibility(GONE);
		monthList.setVisibility(VISIBLE);
		monthList.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,shared.height));
		monthTitle.setText(""+shared.selected.get(Calendar.YEAR));

		ArrayList<String> Months = new ArrayList<>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"));
		monthList
				.layoutManager(new GridLayoutManager(getContext(),4))
				.init(R.layout.layout_month, Months, (holder, object, position) -> {
					TextView tv = holder.textView(R.id.txt);
					tv.setText(""+object);
					if(shared.selected.get(Calendar.MONTH)==position) {
						tv.setBackgroundResource(selectedBackgroundResource);
						tv.setTextColor(shared.selectedColor);
						tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
					} else {
						tv.setTextColor(Color.BLACK);
						tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
					}
					holder.click(R.id.month_layout_particular, view -> {
						if (shared.callback!=null)
							shared.callback.onMonthSelect(position);
						else
							showDateSelector();
						shared.selected.set(Calendar.MONTH,position);
						adapter.update();
					});
				});
	}
	public void showDateSelector() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
		monthTitle.setText(dateFormat.format(adapter.calendar[1].getTime()));
		isMonthView = false;
		isYearView = false;
		findViewById(R.id.next_month_button).setVisibility(VISIBLE);
		findViewById(R.id.previous_month_button).setVisibility(VISIBLE);
		viewPager.setVisibility(VISIBLE);
		findViewById(R.id.year_list).setVisibility(GONE);
		monthList.setVisibility(GONE);
	}
	public void showYearSelector() {
		isMonthView = false;
		isYearView = true;
		viewPager.setVisibility(GONE);
		monthList.setVisibility(GONE);

		HKList listView = findViewById(R.id.year_list);
		listView.setVisibility(VISIBLE);
		listView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,shared.height));
		ArrayList<Integer> list = new ArrayList<>();
		for(int i = 1970;i<2100;i++) list.add(i);
		listView.init(R.layout.layout_year, list, (holder, object, position) -> {
			TextView tv = holder.textView(R.id.year_txt);
			tv.setText(""+object);
			if(object+1 == shared.selected.get(Calendar.YEAR))
				tv.setBackground(null);
			if(shared.selected.get(Calendar.YEAR)==object) {
				tv.setBackgroundResource(selectedBackgroundResource);
				tv.setTextColor(shared.selectedColor);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
			}else{
				tv.setTextColor(Color.BLACK);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
			}
			holder.click(R.id.year_layout, view -> {
				shared.selected.set(Calendar.YEAR,object);
				adapter.update();
				listView.update();
				if(shared.callback!=null)
					shared.callback.onYearSelect(object);
				else
					showMonthSelector();
			});
		});
		listView.recyclerView.getLayoutManager().scrollToPosition(shared.selected.get(Calendar.YEAR)-1973);
	}
	public void setDayResource(int day_resource){
		this.shared.day_layout=day_resource;
		checkLayout();
		adapter.notifyDataSetChanged();
	}
	public void setInflater(DayInflater inflater){
		shared.inflater = inflater;
		adapter.notifyDataSetChanged();
	}
	public void setCallback(Callback callback){
		shared.callback = callback;
		adapter.notifyDataSetChanged();
	}
	public interface Callback{
		void onDateSelect(Calendar calendar);
		void onMonthSelect(int month);
		void onYearSelect(int year);
	}
	public interface DayInflater{
		void inflate(Calendar calendar, View layout,boolean selected,int type);
	}
}
