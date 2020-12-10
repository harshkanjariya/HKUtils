package com.harsh.hkutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HKList extends RelativeLayout {
	public RecyclerView recyclerView;

	private LinearLayout emptyLayout;
	private ImageView emptyListIcon;
	private TextView emptyListMainMessage;
	private TextView emptyListExtraMessage;

	private final int greyColor=Color.parseColor("#a0a0a0");

	private int item_layout;

	private Context context;

	public HKList(@NonNull Context context) {
		super(context);
		initLayout(context);
	}
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void initLayout(Context context){
		this.context=context;

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
		emptyLayoutParam.topMargin=50;
		emptyLayoutParam.leftMargin=100;
		emptyLayoutParam.rightMargin=100;

		emptyListMainMessage=new TextView(context);
		emptyListMainMessage.setTextSize(20);
		emptyListMainMessage.setLayoutParams(emptyLayoutParam);
		emptyListMainMessage.setPadding(0,0,0,0);
		emptyLayout.addView(emptyListMainMessage);

		emptyListExtraMessage=new TextView(context);
		emptyListExtraMessage.setLayoutParams(emptyLayoutParam);
		emptyLayout.addView(emptyListExtraMessage);
	}
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public HKList(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context,attrs);
		initLayout(context);

		TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.HKList);

		CharSequence charSequence=typedArray.getString(R.styleable.HKList_emptyMessage);
		if (charSequence==null)
			charSequence="Empty List!";
		emptyListMainMessage.setText(charSequence);

		charSequence=typedArray.getString(R.styleable.HKList_emptyDescription);
		if (charSequence==null)
			charSequence="No data in list";
		emptyListExtraMessage.setText(charSequence);

		Drawable drawable=typedArray.getDrawable(R.styleable.HKList_emptyImage);
		if (drawable!=null)
			emptyListIcon.setImageDrawable(drawable);

		typedArray.recycle();
	}
	public <D> void init(int item_layout, List<D> list,HKListHelper<D> helper){
		this.item_layout=item_layout;
		MyAdapter<D> adapter=new MyAdapter<>(list,helper);
		recyclerView.setAdapter(adapter);
		if (list.size()>0){
			emptyLayout.setVisibility(GONE);
		}
	}

	public HKList layoutManager(RecyclerView.LayoutManager layoutManager){
		recyclerView.setLayoutManager(layoutManager);
		return this;
	}

	class MyAdapter<D> extends RecyclerView.Adapter<HKListViewHolder>{
		LayoutInflater inflater;
		HKListHelper<D> helper;
		List<D>data;
		public MyAdapter(List<D>list,HKListHelper<D> helper){
			inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.helper=helper;
			this.data=list;
		}
		@NonNull
		@Override
		public HKListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view=inflater.inflate(item_layout,parent,false);
			return new HKListViewHolder(view);
		}
		@Override
		public void onBindViewHolder(@NonNull HKListViewHolder holder, int position) {
			helper.bind(holder,data.get(position));
		}
		@Override
		public int getItemCount() {
			return data.size();
		}
	}
	static class HKListViewHolder extends RecyclerView.ViewHolder{
		public HKListViewHolder(@NonNull View itemView) {
			super(itemView);
		}
		public TextView textView(int res){
			return itemView.findViewById(res);
		}
		public void setText(int res,String text){
			textView(res).setText(text);
		}
		public ImageView imageView(int res){
			return itemView.findViewById(res);
		}
		public void setImage(int res,int drawable){
			imageView(res).setImageResource(drawable);
		}
		public void setImage(int res,Drawable drawable){
			imageView(res).setImageDrawable(drawable);
		}
		public View view(int res){
			return itemView.findViewById(res);
		}
		public LinearLayout linearLayout(int res){
			return itemView.findViewById(res);
		}
		public RelativeLayout relativeLayout(int res){
			return itemView.findViewById(res);
		}
		public TableLayout tableLayout(int res){
			return itemView.findViewById(res);
		}
	}
	interface HKListHelper<D>{
		void bind(HKListViewHolder holder,D object);
	}
}