package com.harsh.hkutils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HKList extends RelativeLayout {
	private RecyclerView recyclerView;

	private LinearLayout emptyLayout;
	private ImageView emptyListIcon;
	private TextView emptyListMainMessage;
	private TextView emptyListExtraMessage;

	private int item_layout;

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

		LayoutInflater inflater=activity.getLayoutInflater();
		emptyLayout= (LinearLayout) inflater.inflate(R.layout.empty_layout,this,false);
		addView(emptyLayout);

		emptyListIcon=emptyLayout.findViewById(R.id.empty_image);
		emptyListMainMessage=emptyLayout.findViewById(R.id.empty_main_message);
		emptyListExtraMessage=emptyLayout.findViewById(R.id.empty_description);
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

		typedArray.recycle();
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
		HKAdapter<D> adapter=new HKAdapter<>(list,helper,filterHelper);
		recyclerView.setAdapter(adapter);
		if (list.size()>0){
			emptyLayout.setVisibility(GONE);
		}
	}
	@SuppressWarnings("rawtypes")
	public void update(){
		if (recyclerView!=null && recyclerView.getAdapter()!=null){
			HKAdapter adapter= (HKAdapter) recyclerView.getAdapter();
			adapter.update();
		}
	}

	public HKList layoutManager(RecyclerView.LayoutManager layoutManager){
		recyclerView.setLayoutManager(layoutManager);
		return this;
	}

	class HKAdapter<D> extends RecyclerView.Adapter<HKViewHolder>{
		HKListHelper<D> listHelper;
		HKFilterHelper<D> filterHelper;

		LayoutInflater inflater;
		List<D>data;
		List<D>originalData;
		public HKAdapter(List<D>list,HKListHelper<D> listHelper,HKFilterHelper<D> filterHelper){
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.listHelper=listHelper;
			this.filterHelper=filterHelper;
			this.originalData=list;
			update();
		}
		public void update(){
			if (filterHelper!=null)
				filterHelper.filter(originalData,data);
			else{
				data=new ArrayList<>(originalData);
			}
			if (data==null)
				data=new ArrayList<>(originalData);
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
			listHelper.bind(holder,data.get(position));
		}
		@Override
		public int getItemCount() {
			return data.size();
		}
	}
	public interface HKFilterHelper<D>{
		void filter(List<D> all,List<D> filtered);
	}
	public interface HKListHelper<D>{
		void bind(HKViewHolder holder,D object);
	}
}