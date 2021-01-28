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

public class ListDemoFragment extends Fragment {

	ArrayList<Meeting> list=new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_demo, container, false);

		HKList hkList=view.findViewById(R.id.list);
		hkList.init(list, (holder, object,i) -> {
			holder.setText(R.id.txt1, object.clientName);
			holder.setText(R.id.txt2, object.companyName);
			holder.click(R.id.layout,v -> { });
		});
		final int[] i = {0};
		new Thread(()->{
			while(true){
				if (i[0]<2) {
					i[0] = i[0] + 2;
					Meeting m = new Meeting();
					m.clientName = "ab " + i[0];
					m.companyName = "cd " + i[0];
					list.add(m);
				}else if (list.size()>0){
					list.remove(0);
				}else{
					i[0]=0;
					continue;
				}
				hkList.update();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		Meeting m=new Meeting();
		m.clientName="cl1";
		m.companyName="cm1";
		list.add(m);
		m=new Meeting();
		m.clientName="cl2";
		m.companyName="cm2";
		list.add(m);
		m=new Meeting();
		m.clientName="cl3";
		m.companyName="cm3";
		list.add(m);

		hkList.update();

		DropDown dropDown=view.findViewById(R.id.drop_down);
		dropDown.init(list, (holder, object,postition) -> {
			holder.setText(R.id.txt1,object.clientName);
			holder.setText(R.id.txt2,object.companyName);
		});

		SelectableEditText selectableEditText=view.findViewById(R.id.edit);
		selectableEditText.init(list, (holder, object, position) -> {
			holder.setText(R.id.txt1, object.clientName);
			holder.setText(R.id.txt2, object.companyName);
		}, new SelectableEditText.HKFilterHelper<Meeting>() {
			@Override
			public List<Meeting> filter(String prefix, List<Meeting> originalData) {
				List<Meeting> list=new ArrayList<>();
				for (Meeting mt:originalData){
					if (mt.companyName.contains(prefix) || mt.clientName.contains(prefix))
						list.add(mt);
				}
				return list;
			}
			@Override
			public String objectToString(Meeting object) {
				return object.clientName;
			}
		});

		return view;
	}
}