package com.harsh.demo.fragments;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.hkutils.ui.treeview.BaseTreeAdapter;
import com.harsh.hkutils.ui.treeview.TreeAdapter;

public class TreeDemoAdapter extends BaseTreeAdapter<TreeDemoAdapter.TreeViewHolder> {
	public TreeDemoAdapter(@NonNull Context context, int layoutRes) {
		super(context, layoutRes);
	}
	@NonNull
	@Override
	public TreeViewHolder onCreateViewHolder(View view) {
		return null;
	}

	@Override
	public void onBindViewHolder(TreeViewHolder viewHolder, Object data, int position) {

	}
	static class TreeViewHolder extends RecyclerView.ViewHolder{
		public TreeViewHolder(@NonNull View itemView) {
			super(itemView);
		}
	}
}
