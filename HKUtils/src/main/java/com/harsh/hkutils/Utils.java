package com.harsh.hkutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	public static int getColor(Context context,int res) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return ContextCompat.getColor(context,res);
		} else return Color.BLACK;
	}
	public static float dpToPixel(float dp, Context context){
		return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}
	public static float pixelsToDp(float px, Context context){
		return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}
	public static boolean isInternetAvailable(){
		try {
			InetAddress ipAddr = InetAddress.getByName("www.google.com");
			return !ipAddr.equals("");
		} catch (UnknownHostException e){
			e.printStackTrace();
		}
		return false;
	}

}