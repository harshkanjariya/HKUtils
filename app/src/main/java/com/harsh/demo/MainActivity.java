package com.harsh.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

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
	}
}