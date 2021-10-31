package com.harsh.hkutils.ui.treeview;

import android.graphics.Point;
import android.view.View;
import android.widget.Adapter;

import androidx.annotation.NonNull;

/**
 *
 */
public interface TreeAdapter<VH> extends Adapter, TreeNodeObserver {

	void notifySizeChanged();

	Algorithm getAlgorithm();

	void setAlgorithm(@NonNull Algorithm algorithm) throws Exception;

	void setRootNode(@NonNull TreeNode rootNode) throws Exception;

	TreeNode getNode(int position);

	Point getScreenPosition(int position);

	@NonNull
	VH onCreateViewHolder(View view);

	void onBindViewHolder(VH viewHolder, Object data, int position);
}
