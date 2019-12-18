package io.taptalk.taptalklive.API.Api;

import android.util.Log;

import androidx.annotation.NonNull;

import io.taptalk.taptalklive.API.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.taptalklive.API.Service.TAPTalkApiService;
import io.taptalk.taptalklive.BuildConfig;
import io.taptalk.taptalklive.Exception.TAPApiRefreshTokenRunningException;
import io.taptalk.taptalklive.Exception.TAPApiSessionExpiredException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class TAPApiManager {
    private static final String TAG = TAPApiManager.class.getSimpleName();

    @NonNull private static String BaseUrlApi = "https://hp.moselo.com:8080/api/v1/";
    @NonNull private static String BaseUrlSocket = "https://hp.moselo.com:8080/";

    private TAPTalkApiService homingPigeon;
    private static TAPApiManager instance;
    private int isShouldRefreshToken = 0;
    private boolean isLogout = false;

    public static TAPApiManager getInstance() {
        return instance == null ? instance = new TAPApiManager() : instance;
    }

    private TAPApiManager() {
        TAPApiConnection connection = TAPApiConnection.getInstance();
        this.homingPigeon = connection.getHomingPigeon();
    }

    public boolean isLogout() {
        return isLogout;
    }

    public void setLogout(boolean logout) {
        isLogout = logout;
    }

    @NonNull
    public static String getBaseUrlApi() {
        return BaseUrlApi;
    }

    public static void setBaseUrlApi(@NonNull String baseUrlApi) {
        BaseUrlApi = baseUrlApi;
    }

    @NonNull
    public static String getBaseUrlSocket() {
        return BaseUrlSocket;
    }

    public static void setBaseUrlSocket(@NonNull String baseUrlSocket) {
        BaseUrlSocket = baseUrlSocket;
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
        TAPBaseResponse br = (TAPBaseResponse) t;

        int code = br.getStatus();
        if (BuildConfig.DEBUG && code != 200)
            Log.d(TAG, "validateResponse: XX HAS ERROR XX: __error_code:" + code);

        if (code == 200 && BuildConfig.DEBUG) {
            Log.d(TAG, "validateResponse: √√ NO ERROR √√");
        } else if (code == 401 && 0 < isShouldRefreshToken && !isLogout) {
            return raiseApiRefreshTokenRunningException();
        } else if (code == 401 && !isLogout) {
            isShouldRefreshToken++;
            return raiseApiSessionExpiredException(br);
        }
        isShouldRefreshToken = 0;
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        Log.e(TAG, "call: retryWhen(), cause: " + t.getMessage());
//        return (t instanceof TAPApiSessionExpiredException && 1 == isShouldRefreshToken && !isLogout) ? refreshToken() :
//                ((t instanceof TAPApiRefreshTokenRunningException || (t instanceof TAPApiSessionExpiredException && 1 < isShouldRefreshToken)) && !isLogout) ?
//                        Observable.just(Boolean.TRUE) : Observable.error(t);
        return Observable.just(true);
    }

    private Observable<Throwable> raiseApiSessionExpiredException(TAPBaseResponse br) {
        return Observable.error(new TAPApiSessionExpiredException(br.getError().getMessage()));
    }

    private Observable<Throwable> raiseApiRefreshTokenRunningException() {
        return Observable.error(new TAPApiRefreshTokenRunningException());
    }

//    public Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> refreshToken() {
//        return hpRefresh.refreshAccessToken("Bearer " + TAPDataManager.getInstance().getRefreshToken())
//                .compose(this.applyIOMainThreadSchedulers())
//                .doOnNext(response -> {
//                    if (RESPONSE_SUCCESS == response.getStatus()) {
//                        updateSession(response);
//                        Observable.error(new TAPAuthException(response.getError().getMessage()));
//                    } else if (UNAUTHORIZED == response.getStatus()) {
//                        TapTalk.clearAllTapTalkData();
//                        for (TapListener listener : TapTalk.getTapTalkListeners()) {
//                            listener.onTapTalkRefreshTokenExpired();
//                        }
//                    } else {
//                        Observable.error(new TAPAuthException(response.getError().getMessage()));
//                    }
//                }).doOnError(throwable -> {
//
//                });
//    }
}
