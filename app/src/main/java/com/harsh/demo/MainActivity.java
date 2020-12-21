package com.harsh.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.harsh.hkutils.DropDown;
import com.harsh.hkutils.HKList;
import com.harsh.hkutils.HKListHelper;
import com.harsh.hkutils.HKViewHolder;
import com.harsh.hkutils.SelectableEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
	ArrayList<Meeting>list=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		HKList hkList=findViewById(R.id.list);
		hkList.init(list, (holder, object,i) -> {
			holder.setText(R.id.txt1, object.clientName);
			holder.setText(R.id.txt2, object.companyName);
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

		DropDown dropDown=findViewById(R.id.drop_down);
		dropDown.init(list, (holder, object,postition) -> {
			holder.setText(R.id.txt1,object.clientName);
			holder.setText(R.id.txt2,object.companyName);
		});

		SelectableEditText selectableEditText=findViewById(R.id.edit);
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
	}
}