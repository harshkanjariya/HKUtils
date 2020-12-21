package com.harsh.hkutils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HKList extends RelativeLayout {
	private RecyclerView recyclerView;
	private HKAdapter<?> adapter;

	private LinearLayout emptyLayout;
	private ImageView emptyListIcon;
	private TextView emptyListMainMessage;
	private TextView emptyListExtraMessage;

	private int item_layout;
	private final int greyColor=Color.parseColor("#a0a0a0");

	private Activity activity;

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public HKList(@NonNull Context context) {
		super(context);
		initLayout(context);
	}
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void initLayout(Context context){
		this.activity = (Activity) context;

		recyclerView=new RecyclerView(context);
		recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		this.addView(recyclerView);

		emptyLayout=new LinearLayout(context);
		emptyLayout.setOrientation(LinearLayout.VERTICAL);
		emptyLayout.setId(View.generateViewId());
		LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(CENTER_IN_PARENT);
		emptyLayout.setLayoutParams(layoutParams);
		addView(emptyLayout);

		emptyListIcon=new ImageView(context);
		emptyLayout.addView(emptyListIcon);
		LinearLayout.LayoutParams iconParam=new LinearLayout.LayoutParams(230,230);
		iconParam.gravity = Gravity.CENTER;
		emptyListIcon.setLayoutParams(iconParam);
		emptyListIcon.setColorFilter(greyColor);

		LinearLayout.LayoutParams emptyLayoutParam=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		emptyLayoutParam.gravity = Gravity.CENTER;
		emptyLayoutParam.topMargin=40;
		emptyLayoutParam.leftMargin=100;
		emptyLayoutParam.rightMargin=100;

		emptyListMainMessage=new TextView(context);
		emptyListMainMessage.setTextSize(20);
		emptyListMainMessage.setLayoutParams(emptyLayoutParam);
		emptyListMainMessage.setPadding(0,0,0,0);
		emptyListMainMessage.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		emptyLayout.addView(emptyListMainMessage);

		emptyListExtraMessage=new TextView(context);
		emptyListExtraMessage.setLayoutParams(emptyLayoutParam);
		emptyListExtraMessage.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		emptyLayout.addView(emptyListExtraMessage);
	}
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public HKList(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context,attrs);
		initLayout(context);

		TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.HKList);

		CharSequence charSequence=typedArray.getString(R.styleable.HKList_emptyMessage);
		if (charSequence==null)
			emptyListMainMessage.setVisibility(GONE);
		else
			emptyListMainMessage.setText(charSequence);

		charSequence=typedArray.getString(R.styleable.HKList_emptyDescription);
		if (charSequence==null)
			emptyListExtraMessage.setVisibility(GONE);
		else
			emptyListExtraMessage.setText(charSequence);

		Drawable drawable=typedArray.getDrawable(R.styleable.HKList_emptyImage);
		if (drawable==null)
			emptyListIcon.setVisibility(GONE);
		else
			emptyListIcon.setImageDrawable(drawable);

		item_layout=typedArray.getResourceId(R.styleable.HKList_itemLayout, android.R.layout.simple_list_item_1);

		typedArray.recycle();
	}

	public <D> void init(List<D> list,HKListHelper<D> helper){
		init(item_layout,list,helper,null);
	}
	public <D> void init(List<D> list,HKListHelper<D> helper,HKFilterHelper<D> filterHelper){
		init(item_layout,list,helper,filterHelper);
	}
	public <D> void init(int item_layout, List<D> list,HKListHelper<D> helper){
		init(item_layout,list,helper,null);
	}
	public <D> void init(int item_layout, List<D> list,HKListHelper<D> helper,HKFilterHelper<D> filterHelper){
		if (list==null)
			throw new NullPointerException("data list cannot be null");
		else if(helper==null)
			throw new NullPointerException("helper cannot be null");

		this.item_layout=item_layout;
		adapter=new HKAdapter<>(list,helper,filterHelper);
		recyclerView.setAdapter(adapter);
		if (list.size()>0){
			emptyLayout.setVisibility(GONE);
		}
	}
	public void update(){
		if (adapter!=null){
			adapter.update();
		}
	}
	public HKList layoutManager(RecyclerView.LayoutManager layoutManager){
		recyclerView.setLayoutManager(layoutManager);
		return this;
	}
	public boolean isLast(int position){
		return adapter.isLast(position);
	}

	class HKAdapter<D> extends RecyclerView.Adapter<HKViewHolder>{
		HKListHelper<D> listHelper;
		HKFilterHelper<D> filterHelper;

		LayoutInflater inflater;
		List<D>data=new ArrayList<>();
		List<D>originalData;
		public HKAdapter(List<D>list,HKListHelper<D> listHelper,HKFilterHelper<D> filterHelper){
			inflater = activity.getLayoutInflater();
			this.listHelper=listHelper;
			this.filterHelper=filterHelper;
			this.originalData=list;
			update();
		}
		public void update(){
			data.clear();
			if (filterHelper!=null)
				data=filterHelper.filter(originalData);
			else{
				data.addAll(originalData);
			}
			if (Looper.getMainLooper()==Looper.myLooper()){
				if (getItemCount()==0){
					emptyLayout.setVisibility(VISIBLE);
				}else{
					emptyLayout.setVisibility(GONE);
				}
				notifyDataSetChanged();
			}else{
				activity.runOnUiThread(()->{
					if (getItemCount()==0){
						emptyLayout.setVisibility(VISIBLE);
					}else{
						emptyLayout.setVisibility(GONE);
					}
					notifyDataSetChanged();
				});
			}
		}
		@NonNull
		@Override
		public HKViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view=inflater.inflate(item_layout,parent,false);
			return new HKViewHolder(view);
		}
		@Override
		public void onBindViewHolder(@NonNull HKViewHolder holder, int position) {
			listHelper.bind(holder,data.get(position),position);
		}
		@Override
		public int getItemCount() {
			return data.size();
		}
		public boolean isLast(int position) {
			return data.size()==position+1;
		}
	}
}