package io.taptalk.taptalklive.API.Interceptor;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.taptalklive.BuildConfig;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.TapTalkLive;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TTLHeaderRequestInterceptor implements Interceptor {

    public TTLHeaderRequestInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Context context = TapTalkLive.context;

        String contentType = "application/json";

        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceOsVersion = "v" + android.os.Build.VERSION.RELEASE + "b" + android.os.Build.VERSION.SDK_INT;
        Request request = original
                .newBuilder()
                .addHeader("Content-Type", contentType)
                .addHeader("App-Identifier", TapTalkLive.context.getPackageName())
                .addHeader("Device-Identifier", deviceID)
                .addHeader("Device-Model", android.os.Build.MODEL)
                .addHeader("Device-OS-Version", deviceOsVersion)
                .addHeader("Device-Platform", "android")
                .addHeader("SDK-Version", "2.6.0") // FIXME: BuildConfig.VERSION_NAME NOT FOUND IN JITPACK BUILD
                .addHeader("Secret-Key", TTLDataManager.getInstance().getAppKeySecret())
                .addHeader("User-Agent", "android")
                .method(original.method(), original.body())
                .build();

        if (null == original.headers().get("Authorization") &&
            null != TTLDataManager.getInstance().getAccessToken() &&
            !TTLDataManager.getInstance().getAccessToken().isEmpty()
        ) {
            request = request
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + TTLDataManager.getInstance().getAccessToken())
                    .build();
        }
//        if (BuildConfig.DEBUG) {
//            Log.e(")))))) okhttp", "intercept: " + TAPUtils.toJsonString(request.headers()));
//        }

        return chain.proceed(request);
    }
}
