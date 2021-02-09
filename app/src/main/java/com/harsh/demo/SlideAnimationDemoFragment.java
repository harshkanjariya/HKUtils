package com.harsh.demo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.harsh.hkutils.ExpandableLayout;
import com.harsh.hkutils.ui.CircularProgressButton;

public class SlideAnimationDemoFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_slide_animation_demo, container, false);

		Button btn = view.findViewById(R.id.btn);
		CircularProgressButton button = view.findViewById(R.id.circular_btn);

		button.setReverse(true);
		btn.setOnClickListener(v -> {
			ExpandableLayout expandableLayout = view.findViewById(R.id.expandable);
			expandableLayout.toggle();
			if (button.isRunning())
				button.cancel();
			else
				button.start();
		});
		button.setCallback(new CircularProgressButton.Callback() {
			@Override
			public void onComplete() {
				if (button.isReverse()){
					button.setText("hello");
				}else{
					button.setText("olleh");
				}
				button.setReverse(!button.isReverse());
				if (button.isReverse()) {
					button.start();
					button.cancelable(false);
				}else{
					button.cancelable(true);
				}
			}
			@Override
			public void onProgress(float percentage) {
				Log.e("55", "SlideAnimationDemoFragment > onProgress: " + percentage);
			}
			@Override
			public void onCancel() {}
		});
		return view;
	}
}