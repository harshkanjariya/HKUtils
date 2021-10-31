package com.harsh.hkutils;

import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpProcess {
    private final String url;
    private Map<String, String> headers;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HttpProcess(String url) {
        this.url = url;
    }

    public HttpProcess headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public void get(final Callback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build();
            Request.Builder builder = new Request.Builder().url(url);
            if (headers != null)
                builder.headers(Headers.of(headers));
            Request request = builder.build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (Objects.requireNonNull(e.getMessage()).equalsIgnoreCase("Chain validation failed"))
                        e = new IOException("Please set correct system date and try again!");
                    else if (!Utils.isInternetAvailable()) {
                        e = new IOException("You're offline. Check your connection");
                    }
                    callback.onError(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            try {
                                JSONObject object = new JSONObject(body.string());
                                callback.onResponse(object);
                            } catch (JSONException e) {
                                StackTraceElement[] s = e.getStackTrace();
                                for (StackTraceElement stackTraceElement : s) {
                                    String builder = "(" + stackTraceElement.getFileName() +
                                            ":" +
                                            stackTraceElement.getLineNumber() + ")" +
                                            "," +
                                            stackTraceElement.getMethodName();
                                    Log.e("Error", builder);
                                }
                                callback.onError(new IOException("Invalid response format! " + e.getMessage()));
                            }
                        } else {
                            callback.onError(new IOException("Null body"));
                        }
                    } else {
                        callback.onError(new IOException("Server Error! " + response.code()));
                    }
                }
            });
        }).start();
    }

    public void post(final Callback callback, @NotNull final String params, Map<String, Tuple<String, File, CountingFileRequestBody.ProgressListener>> files) {
        Request.Builder requestBuilder;
        if (files == null) {
            RequestBody body = RequestBody.create(params, JSON);
            requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);
        } else {
            final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Map<String, String> map = new Gson().fromJson(params, new TypeToken<HashMap<String, String>>() {}.getType());
            for (Map.Entry<String, String> e : map.entrySet()) {
                builder.addFormDataPart(e.getKey(), e.getValue());
            }
            for (Map.Entry<String, Tuple<String, File, CountingFileRequestBody.ProgressListener>> e : files.entrySet()) {
                builder.addFormDataPart(e.getKey(), e.getValue().a,
                        new CountingFileRequestBody(e.getValue().b, getMimeType(e.getValue().a), e.getValue().c));
            }
            requestBuilder = new Request.Builder().url(url).post(builder.build());
        }
        if (headers != null)
            requestBuilder.headers(Headers.of(headers));
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build();
            Request request = requestBuilder.build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (Objects.requireNonNull(e.getMessage()).equalsIgnoreCase("Chain validation failed"))
                        e = new IOException("Please set correct system date and try again!");
                    else if (!Utils.isInternetAvailable()) {
                        e = new IOException("You're offline. Check your connection");
                    }
                    callback.onError(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            String res = body.string();
                            try {
                                if (res.equals("unauthorized access!")) {
                                    callback.onError(new IOException("unauthorized access!"));
                                    return;
                                }
                                JSONObject object = new JSONObject(res);
                                callback.onResponse(object);
                            } catch (JSONException e) {
                                StackTraceElement[] s = e.getStackTrace();
                                for (StackTraceElement stackTraceElement : s) {
                                    String builder = "(" + stackTraceElement.getFileName() +
                                            ":" +
                                            stackTraceElement.getLineNumber() + ")" +
                                            "," +
                                            stackTraceElement.getMethodName();
                                    Log.e("Error", builder);
                                }
                                callback.onError(new IOException(e.getMessage() + " : " + res));
                            }
                        } else {
                            callback.onError(new IOException("Null body"));
                        }
                    } else {
                        callback.onError(new IOException("Server Error! " + response.code()));
                    }
                }
            });
        }).start();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (type == null) {
            if (url.contains(".")) {
                return url.substring(url.lastIndexOf(".") + 1);
            }
        }
        return type;
    }

    public void post(Callback callback, String body) {
        post(callback, body, null);
    }

    public interface Callback {
        void onError(IOException exception);

        void onResponse(JSONObject json) throws JSONException;
    }
}