package com.harsh.hkutils;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SelectableEditText extends AppCompatAutoCompleteTextView {
	private int item_layout;
	private HKAdapter<?> adapter;

	private Activity activity;
	public SelectableEditText(@NonNull Context context) {
		super(context);
		activity= (Activity) context;
		item_layout= android.R.layout.simple_spinner_dropdown_item;
		setThreshold(1);
	}
	public SelectableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		commonConstruct(context,attrs);
	}
	public SelectableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		commonConstruct(context,attrs);
	}
	private void commonConstruct(Context context,AttributeSet attrs){
		activity= (Activity) context;

		TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SelectableEditText);

		item_layout=typedArray.getResourceId(R.styleable.SelectableEditText_itemLayout, android.R.layout.simple_spinner_dropdown_item);

		typedArray.recycle();
		setThreshold(1);
	}

	public <D> void init(List<D> data,HKListHelper<D> helper,HKFilterHelper<D> filterHelper){
		adapter= new HKAdapter<>(activity,data,helper,filterHelper);
		setAdapter(adapter);
	}
	public void update(){
		adapter.notifyDataSetChanged();
	}
	class HKAdapter<D> extends BaseAdapter implements Filterable {
		List<D> filteredData;
		List<D> originalData;
		LayoutInflater inflater;
		HKListHelper<D> helper;
		HKFilterHelper<D> filterHelper;

		public HKAdapter(@NonNull Context context,List<D>data,HKListHelper<D> helper,HKFilterHelper<D> filterHelper) {
			this.originalData=data;
			this.helper=helper;
			this.filterHelper=filterHelper;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() { return filteredData.size(); }
		@Override
		public Object getItem(int i) { return filteredData.get(i); }
		@Override
		public long getItemId(int i) { return i; }
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			view=inflater.inflate(item_layout,viewGroup,false);
			helper.bind(new HKViewHolder(view),filteredData.get(i),i);
			return view;
		}
		@Override
		public Filter getFilter() {
			return new HKFilter();
		}
		class HKFilter extends Filter{
			private final Object lock = new Object();

			@SuppressWarnings("unchecked")
			@Override
			public CharSequence convertResultToString(Object resultValue) {
				return filterHelper==null?resultValue.toString():filterHelper.objectToString((D) resultValue);
			}
			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results=new FilterResults();
				if (originalData==null){
					synchronized (lock){
						originalData = new ArrayList<>(filteredData);
					}
				}
				if (prefix==null || prefix.length()==0){
					synchronized (lock){
						results.values=new ArrayList<>(originalData);
						results.count=originalData.size();
					}
				}else{
					if (filterHelper==null){
						results.values = new ArrayList<D>();
						results.count = 0;
					}else {
						List<D> newValues = filterHelper.filter(prefix.toString(), originalData);
						if (newValues == null) {
							results.values = new ArrayList<D>();
							results.count = 0;
						} else {
							results.values = newValues;
							results.count = newValues.size();
						}
					}
				}
				return results;
			}
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
				if(filterResults.values!=null){
					filteredData = (ArrayList<D>) filterResults.values;
				}else{
					filteredData = new ArrayList<>();
				}
				if (filterResults.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}
	}
	public interface HKFilterHelper<D>{
		List<D> filter(String prefix,List<D> originalData);
		String objectToString(D object);
	}
}
