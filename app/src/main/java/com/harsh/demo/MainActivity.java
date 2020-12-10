package com.harsh.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.harsh.hkutils.HKList;
import com.harsh.hkutils.HKViewHolder;

import java.util.ArrayList;
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
		hkList.init(R.layout.support_simple_spinner_dropdown_item, list, (holder, object) -> {
			holder.setText(android.R.id.text1,object.clientName);
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
		}).start();
	}
}