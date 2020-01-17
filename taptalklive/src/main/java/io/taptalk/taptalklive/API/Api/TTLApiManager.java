package io.taptalk.taptalklive.API.Api;

import android.util.Log;

import androidx.annotation.NonNull;

import io.taptalk.taptalklive.API.Model.RequestModel.TTLCreateUserRequest;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import io.taptalk.taptalklive.API.Model.TTLUserModel;
import io.taptalk.taptalklive.API.Service.TTLApiService;
import io.taptalk.taptalklive.API.Service.TTLRefreshTokenApiService;
import io.taptalk.taptalklive.BuildConfig;
import io.taptalk.taptalklive.Exception.TTLApiRefreshTokenRunningException;
import io.taptalk.taptalklive.Exception.TTLApiSessionExpiredException;
import io.taptalk.taptalklive.Exception.TTLAuthException;
import io.taptalk.taptalklive.TTLDataManager;
import io.taptalk.taptalklive.TapTalkLive;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.HttpResponseStatusCode.RESPONSE_SUCCESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.HttpResponseStatusCode.UNAUTHORIZED;
import static io.taptalk.taptalklive.BuildConfig.TAPLIVE_SDK_API_VERSION;
import static io.taptalk.taptalklive.BuildConfig.TAPLIVE_SDK_BASE_URL;

public class TTLApiManager {
    private static final String TAG = TTLApiManager.class.getSimpleName();

    @NonNull private static String BaseUrlApi = TAPLIVE_SDK_BASE_URL + TAPLIVE_SDK_API_VERSION;
    //@NonNull private static String BaseUrlSocket = "https://hp.moselo.com:8080/";

    private TTLApiService ttlApiService;
    private TTLRefreshTokenApiService ttlRefreshTokenApiService;
    private static TTLApiManager instance;
    private int isShouldRefreshToken = 0;
    private boolean isLoggedOut = false;

    public static TTLApiManager getInstance() {
        return instance == null ? instance = new TTLApiManager() : instance;
    }

    private TTLApiManager() {
        TTLApiConnection connection = TTLApiConnection.getInstance();
        this.ttlApiService = connection.getTtlApiService();
        this.ttlRefreshTokenApiService = connection.getTtlRefreshTokenApiService();
    }

    public boolean isLoggedOut() {
        return isLoggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        isLoggedOut = loggedOut;
    }

    @NonNull
    public static String getBaseUrlApi() {
        return BaseUrlApi;
    }

    public static void setBaseUrlApi(@NonNull String baseUrlApi) {
        BaseUrlApi = baseUrlApi;
    }

    private Observable.Transformer ioToMainThreadSchedulerTransformer
            = createIOMainThreadScheduler();

    private <T> Observable.Transformer<T, T> createIOMainThreadScheduler() {
        return tObservable -> tObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings("unchecked")
    private <T> Observable.Transformer<T, T> applyIOMainThreadSchedulers() {
        return ioToMainThreadSchedulerTransformer;
    }

    @SuppressWarnings("unchecked")
    private <T> void execute(Observable<? extends T> o, Subscriber<T> s) {
        o.compose((Observable.Transformer<T, T>) applyIOMainThreadSchedulers())
                .flatMap((Func1<T, Observable<T>>) this::validateResponse)
                .retryWhen(o1 -> o1.flatMap((Func1<Throwable, Observable<?>>) this::validateException))
                .subscribe(s);
    }

    @SuppressWarnings("unchecked")
    private <T> void executeWithoutBaseResponse(Observable<? extends T> o, Subscriber<T> s) {
        o.compose((Observable.Transformer<T, T>) applyIOMainThreadSchedulers()).subscribe(s);
    }

    private <T> Observable validateResponse(T t) {
        TTLBaseResponse br = (TTLBaseResponse) t;

        int code = br.getStatus();
        if (BuildConfig.DEBUG && code != 200)
            Log.d(TAG, "validateResponse: XX HAS ERROR XX: __error_code:" + code);

        if (code == 200 && BuildConfig.DEBUG) {
            Log.d(TAG, "validateResponse: √√ NO ERROR √√");
        } else if (code == 401 && 0 < isShouldRefreshToken && !isLoggedOut) {
            return raiseApiRefreshTokenRunningException();
        } else if (code == 401 && !isLoggedOut) {
            isShouldRefreshToken++;
            return raiseApiSessionExpiredException(br);
        }
        isShouldRefreshToken = 0;
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        Log.e(TAG, "call: retryWhen(), cause: " + t.getMessage());
        return (t instanceof TTLApiSessionExpiredException && 1 == isShouldRefreshToken && !isLoggedOut) ? refreshAccessToken() :
                ((t instanceof TTLApiRefreshTokenRunningException || (t instanceof TTLApiSessionExpiredException && 1 < isShouldRefreshToken)) && !isLoggedOut) ?
                        Observable.just(Boolean.TRUE) : Observable.error(t);
//        return Observable.just(true);
    }

    private Observable<Throwable> raiseApiSessionExpiredException(TTLBaseResponse br) {
        return Observable.error(new TTLApiSessionExpiredException(br.getError().getMessage()));
    }

    private Observable<Throwable> raiseApiRefreshTokenRunningException() {
        return Observable.error(new TTLApiRefreshTokenRunningException());
    }

    public Observable<TTLBaseResponse<TTLRequestAccessTokenResponse>> refreshAccessToken() {
        return ttlRefreshTokenApiService.refreshAccessToken("Bearer " + TTLDataManager.getInstance().getRefreshToken())
                .compose(this.applyIOMainThreadSchedulers())
                .doOnNext(response -> {
                    if (RESPONSE_SUCCESS == response.getStatus()) {
                        updateSession(response);
                        Observable.error(new TTLAuthException(response.getError().getMessage()));
                    } else if (UNAUTHORIZED == response.getStatus()) {
                        TapTalkLive.clearAllTapLiveData();
                    } else {
                        Observable.error(new TTLAuthException(response.getError().getMessage()));
                    }
                }).doOnError(throwable -> {

                });
    }

    private void updateSession(TTLBaseResponse<TTLRequestAccessTokenResponse> r) {
        TTLDataManager.getInstance().saveAccessToken(r.getData().getAccessToken());
        TTLDataManager.getInstance().saveAccessTokenExpiry(r.getData().getAccessTokenExpiry());
        TTLDataManager.getInstance().saveRefreshToken(r.getData().getRefreshToken());
        TTLDataManager.getInstance().saveRefreshTokenExpiry(r.getData().getRefreshTokenExpiry());
        TTLDataManager.getInstance().saveActiveUser(r.getData().getUser());
    }

    public void requestAccessToken(Subscriber<TTLBaseResponse<TTLRequestAccessTokenResponse>> subscriber) {
        execute(ttlApiService.requestAccessToken("Bearer " + TTLDataManager.getInstance().getAuthTicket()), subscriber);
    }

    public void getTopicList(Subscriber<TTLBaseResponse<TTLGetTopicListResponse>> subscriber) {
        execute(ttlApiService.getTopicList(), subscriber);
    }

    public void createUser(String fullName, String email, Subscriber<TTLBaseResponse<TTLCreateUserResponse>> subscriber) {
        TTLCreateUserRequest request = new TTLCreateUserRequest(fullName, email);
        execute(ttlApiService.createUser(request), subscriber);
    }

    public void getUserProfile(Subscriber<TTLBaseResponse<TTLUserModel>> subscriber) {
        execute(ttlApiService.getUserProfile(), subscriber);
    }

    public void logout(Subscriber<TTLBaseResponse<TTLCommonResponse>> subscriber) {
        execute(ttlApiService.logout(), subscriber);
    }
}
