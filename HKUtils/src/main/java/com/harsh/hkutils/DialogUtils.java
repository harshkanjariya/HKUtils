package com.harsh.hkutils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

public class DialogUtils {
	public static AlertDialog progressDialog(Context context){
		return progressDialog(context,"Please wait...").first;
	}
	public static Pair<AlertDialog, TextView> progressDialog(Context context, String message){
		LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.dialog_progress,null,false);
		TextView textView=layout.findViewById(R.id.progress_bar_text);
		textView.setText(message);
		AlertDialog.Builder builder=new AlertDialog.Builder(context)
				.setView(layout)
				.setCancelable(false);
		return new Pair<>(builder.create(),textView);
	}

	public static void imagePickerDialog(final Activity activity, final int requestCode, final Callback removeClickListener){
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.dialog_image_picker,null,false);

		final BottomSheetDialog dialog=new BottomSheetDialog(activity);
		dialog.setContentView(dialogLayout);
		dialog.setCancelable(true);
		dialog.show();

		TextView camera=dialogLayout.findViewById(R.id.camera);
		camera.setOnClickListener(view -> {
			dialog.dismiss();
			Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file=new File(Environment.getExternalStorageDirectory().getPath()+"/Emplitrack/");
			if (!file.exists())
				file.mkdirs();
			file=new File(file,"camera.jpg");
			Uri uri  = FileProvider.getUriForFile(activity,activity.getPackageName(),file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
			activity.startActivityForResult(intent,requestCode);
		});
		TextView gallery=dialogLayout.findViewById(R.id.gallery);
		gallery.setOnClickListener(view -> {
			dialog.dismiss();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"),requestCode);
		});
		TextView remove=dialogLayout.findViewById(R.id.remove);
		if (removeClickListener==null)
			remove.setVisibility(View.GONE);
		else
			remove.setOnClickListener(view -> removeClickListener.onClick(dialog));
	}
	public interface Callback{
		void onClick(DialogInterface dialog);
	}
}
