package com.harsh.demo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harsh.hkutils.ExpandableLayout;
import com.harsh.hkutils.list.DropDown;
import com.harsh.hkutils.list.HKList;
import com.harsh.hkutils.list.SelectableEditText;

import java.util.ArrayList;
import java.util.List;

public class CalendarDemoFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar_demo, container, false);
		return view;
	}
}