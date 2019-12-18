package io.taptalk.taptalklive.API.Api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.taptalk.taptalklive.API.Interceptor.TAPHeaderRequestInterceptor;
import io.taptalk.taptalklive.API.Service.TAPTalkApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class TAPApiConnection {

    private static final String TAG = TAPApiConnection.class.getSimpleName();

    private TAPTalkApiService homingPigeon;

    public ObjectMapper objectMapper;

    private static TAPApiConnection instance;

    public static TAPApiConnection getInstance() {
        return instance == null ? instance = new TAPApiConnection() : instance;
    }

    private TAPApiConnection() {
        this.objectMapper = createObjectMapper();
        OkHttpClient httpHpClientAccessToken = buildHttpTapClient();
        Retrofit homingPigeonAdapter = buildApiAdapter(httpHpClientAccessToken, TAPApiManager.getBaseUrlApi());
        this.homingPigeon = homingPigeonAdapter.create(TAPTalkApiService.class);
    }

    public TAPTalkApiService getHomingPigeon() {
        return homingPigeon;
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

    private OkHttpClient buildHttpTapClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new TAPHeaderRequestInterceptor())
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
