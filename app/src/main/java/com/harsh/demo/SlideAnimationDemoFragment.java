package com.harsh.demo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.harsh.hkutils.ExpandableLayout;

public class SlideAnimationDemoFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_slide_animation_demo, container, false);

		Button btn = view.findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandableLayout expandableLayout = view.findViewById(R.id.expandable);
				expandableLayout.toggle();
			}
		});

		return view;
	}
}