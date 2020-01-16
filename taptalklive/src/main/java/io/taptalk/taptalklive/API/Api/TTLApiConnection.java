package io.taptalk.taptalklive.API.Api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.taptalklive.API.Interceptor.TTLHeaderRequestInterceptor;
import io.taptalk.taptalklive.API.Service.TTLApiService;
import io.taptalk.taptalklive.API.Service.TTLRefreshTokenApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class TTLApiConnection {

    private static final String TAG = TTLApiConnection.class.getSimpleName();

    private TTLApiService ttlApiService;
    private TTLRefreshTokenApiService ttlRefreshTokenApiService;

    public ObjectMapper objectMapper;

    private static TTLApiConnection instance;

    public static TTLApiConnection getInstance() {
        return instance == null ? instance = new TTLApiConnection() : instance;
    }

    private TTLApiConnection() {
        this.objectMapper = createObjectMapper();

        Retrofit tapLiveAdapter = buildApiAdapter(buildHttpTtlClient(), TTLApiManager.getBaseUrlApi());
        Retrofit ttlRefreshTokenAdapter = buildApiAdapter(buildHttpTtlClient(), TTLApiManager.getBaseUrlApi());

        this.ttlApiService = tapLiveAdapter.create(TTLApiService.class);
        this.ttlRefreshTokenApiService = ttlRefreshTokenAdapter.create(TTLRefreshTokenApiService.class);
    }

    public TTLApiService getTtlApiService() {
        return ttlApiService;
    }

    public TTLRefreshTokenApiService getTtlRefreshTokenApiService() {
        return ttlRefreshTokenApiService;
    }

    public ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        return objectMapper;
    }

    private OkHttpClient buildHttpTtlClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(TapTalk.isLoggingEnabled ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new TTLHeaderRequestInterceptor())
                .build();
    }

    private Retrofit buildApiAdapter(OkHttpClient httpClient, String baseURL) {
        Executor executor = Executors.newCachedThreadPool();

        return new Retrofit.Builder()
                .callbackExecutor(executor)
                .baseUrl(baseURL)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}
