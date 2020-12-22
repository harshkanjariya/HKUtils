package com.harsh.hkutils;

import android.os.Build;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpProcess{
	private final String url;
	public HttpProcess(String url){
		this.url=url;
	}
	public void get(final Callback callback, final Map<String,String>params){
		new Thread(new Runnable() {
			@RequiresApi(api = Build.VERSION_CODES.KITKAT)
			@Override
			public void run() {
				OkHttpClient client= new OkHttpClient.Builder()
						.connectTimeout(1, TimeUnit.MINUTES)
						.writeTimeout(1,TimeUnit.MINUTES)
						.readTimeout(1,TimeUnit.MINUTES)
						.build();
				HttpUrl.Builder urlBuilder= Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
				if(params!=null){
					for(Map.Entry<String, String> param : params.entrySet()) {
						urlBuilder.addQueryParameter(param.getKey(),param.getValue());
					}
				}
				Request request=new Request.Builder().url(urlBuilder.build()).build();
				client.newCall(request).enqueue(new okhttp3.Callback() {
					@Override
					public void onFailure(@NotNull Call call, @NotNull IOException e) {
						callback.onError(e);
					}
					@Override
					public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
						if (response.isSuccessful()){
							ResponseBody body=response.body();
							if (body!=null){
								try {
									JSONObject object=new JSONObject(body.string());
									callback.onResponse(object);
								} catch (JSONException e){
									callback.onError(new IOException("Invalid response format! "+e.getMessage()));
								}
							}else{
								callback.onError(new IOException("Null body"));
							}
						}else{
							callback.onError(new IOException("Server Error! "+response.code()));
						}
					}
				});
			}
		}).start();
	}
	public void get(final Callback callback){
		get(callback,null);
	}

	public void post(final Callback callback, final Map<String, String> params, Map<String, Tuple<String, File, CountingFileRequestBody.ProgressListener>> files){
		final MultipartBody.Builder builder=new MultipartBody.Builder().setType(MultipartBody.FORM);
		if (params!=null){
			for(Map.Entry<String,String> e:params.entrySet()){
				builder.addFormDataPart(e.getKey(),e.getValue());
			}
		}
		if (files!=null){
			for(Map.Entry<String, Tuple<String, File, CountingFileRequestBody.ProgressListener>> e:files.entrySet()){
				builder.addFormDataPart(e.getKey(),e.getValue().a,
						new CountingFileRequestBody(e.getValue().b, getMimeType(e.getValue().a),e.getValue().c));
			}
		}
		new Thread(new Runnable() {
			@RequiresApi(api = Build.VERSION_CODES.KITKAT)
			@Override
			public void run() {
				OkHttpClient client=new OkHttpClient.Builder()
						.connectTimeout(1, TimeUnit.MINUTES)
						.writeTimeout(1,TimeUnit.MINUTES)
						.readTimeout(1,TimeUnit.MINUTES)
						.build();
				Request request=new Request.Builder().url(url).post(builder.build()).build();
				client.newCall(request).enqueue(new okhttp3.Callback() {
					@Override
					public void onFailure(@NotNull Call call, @NotNull IOException e) {
						callback.onError(e);
					}
					@Override
					public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
						if (response.isSuccessful()){
							ResponseBody body=response.body();
							if (body!=null){
								String res=body.string();
								try {
									if (res.equals("unauthorized access!")){
										callback.onError(new IOException("unauthorized access!"));
										return;
									}
									JSONObject object=new JSONObject(res);
									callback.onResponse(object);
								} catch (JSONException e){
									callback.onError(new IOException("Invalid response format! "+e.getMessage()+" => "+res));
								}
							}else{
								callback.onError(new IOException("Null body"));
							}
						}else{
							callback.onError(new IOException("Server Error! "+response.code()));
						}
					}
				});
			}
		}).start();
	}
	public static String getMimeType(String url){
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		if (type==null){
			if (url.contains(".")) {
				return url.substring(url.lastIndexOf(".") + 1);
			}
		}
		return type;
	}
	public void post(Callback callback, Map<String, String> map) {
		post(callback,map,null);
	}
	public interface Callback{
		void onError(IOException exception);
		void onResponse(JSONObject json) throws JSONException;
	}
}