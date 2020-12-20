package com.harsh.hkutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DropDown extends AppCompatSpinner {
	private int item_layout;
	private HKAdapter<?> adapter;

	private final Activity activity;
	public DropDown(@NonNull Context context) {
		super(context);
		this.activity= (Activity) context;
		item_layout= android.R.layout.simple_spinner_dropdown_item;
	}
	public DropDown(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.activity= (Activity) context;

		TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.DropDown);
		item_layout=typedArray.getResourceId(R.styleable.DropDown_itemLayout, android.R.layout.simple_spinner_dropdown_item);

		typedArray.recycle();
	}

	// adapter setups
	public void setLayout(int layout){
		this.item_layout=layout;
	}
	public <D> void init(List<D>list,HKListHelper<D> helper){
		init(list,helper,null);
	}
	public <D> void init(int layout,List<D>list,HKListHelper<D> helper){
		this.item_layout=layout;
		init(list,helper,null);
	}
	public <D> void init(int layout,List<D>list,HKListHelper<D> helper,OnSelectListener<D> listener){
		this.item_layout=layout;
		init(list,helper,listener);
	}
	public <D> void init(List<D> list,HKListHelper<D> helper,OnSelectListener<D> listener){
		adapter = new HKAdapter<>(list,helper);
		setAdapter(adapter);
		if (listener!=null)
		this.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				D d = (D) getSelectedItem();
				listener.onSelect(d);
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				listener.onSelect(null);
			}
		});
	}
	public void update(){
		if (adapter!=null)
			adapter.notifyDataSetChanged();
	}
	class HKAdapter<D> extends BaseAdapter {
		HKListHelper<D> listHelper;

		LayoutInflater inflater;
		List<D>data;
		public HKAdapter(List<D>list, HKListHelper<D> listHelper){
			inflater = activity.getLayoutInflater();
			this.listHelper=listHelper;
			this.data=list;
			update();
		}
		@Override
		public int getCount() { return data.size(); }
		@Override
		public Object getItem(int i) { return data.get(i); }
		@Override
		public long getItemId(int i) { return i; }
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			view=inflater.inflate(item_layout,viewGroup,false);
			listHelper.bind(new HKViewHolder(view),data.get(i),i);
			return view;
		}
	}
	public interface OnSelectListener<D>{
		void onSelect(D obj);
	}
}