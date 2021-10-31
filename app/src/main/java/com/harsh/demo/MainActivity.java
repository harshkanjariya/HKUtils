package com.harsh.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.harsh.hkutils.HttpProcess;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

	DrawerLayout drawerLayout;

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId()==android.R.id.home){
			if (drawerLayout.isDrawerOpen(GravityCompat.START))
				drawerLayout.closeDrawers();
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawerLayout = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.navigation_view);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				switch (item.getItemId()){
					case R.id.calendar:
						transaction.replace(R.id.main_frame,new CalendarDemoFragment());
						break;
					case R.id.list:
						transaction.replace(R.id.main_frame,new ListDemoFragment());
						break;
					case R.id.animations:
						transaction.replace(R.id.main_frame,new SlideAnimationDemoFragment());
						break;
				}
				transaction.commit();
				drawerLayout.closeDrawers();
				return true;
			}
		});
		test();
	}
	void test(){
		HttpProcess process = new HttpProcess("http://192.168.0.101:4000/health");
		process.post(new HttpProcess.Callback() {
			@Override
			public void onError(IOException exception) { }
			@Override
			public void onResponse(JSONObject json) throws JSONException {
				Log.e("jasjd", "onResponse: "+json.toString());
			}
		},"{\"ab\":\"cd\"}");
	}
}