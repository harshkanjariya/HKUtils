package com.harsh.hkutils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HKViewHolder extends RecyclerView.ViewHolder{
	public HKViewHolder(@NonNull View itemView) {
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
	public void setImage(int res, Drawable drawable){
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