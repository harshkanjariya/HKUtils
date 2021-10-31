package com.harsh.demo.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harsh.demo.R;
import com.harsh.hkutils.ui.treeview.TreeAdapter;
import com.harsh.hkutils.ui.treeview.TreeNode;
import com.harsh.hkutils.ui.treeview.TreeView;

public class TreeDemoFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tree_demo, container, false);

		TreeView tree = view.findViewById(R.id.tree);
		TreeDemoAdapter adapter = new TreeDemoAdapter(requireContext(), R.layout.tree_node);
		tree.setAdapter(adapter);

		TreeNode root = new TreeNode("hii");
		try {
			adapter.setRootNode(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TreeNode node1 = new TreeNode("hii");
		TreeNode node2 = new TreeNode("hii");
		TreeNode node3 = new TreeNode("hii");
		root.addChild(node1);
		root.addChild(node2);
		root.addChild(node3);

		return view;
	}
}