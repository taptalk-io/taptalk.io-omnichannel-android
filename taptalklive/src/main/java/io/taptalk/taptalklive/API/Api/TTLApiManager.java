package io.taptalk.taptalklive.API.Api;

import android.util.Log;

import androidx.annotation.NonNull;

import io.taptalk.taptalklive.API.Model.RequestModel.TTLCreateCaseRequest;
import io.taptalk.taptalklive.API.Model.RequestModel.TTLCreateUserRequest;
import io.taptalk.taptalklive.API.Model.RequestModel.TTLGetCaseListRequest;
import io.taptalk.taptalklive.API.Model.RequestModel.TTLIdRequest;
import io.taptalk.taptalklive.API.Model.RequestModel.TTLRateConversationRequest;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateCaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetProjectConfigsResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetScfPathResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetUserProfileResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestTicketResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLSendMessageRequest;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLSendMessageResponse;
import io.taptalk.taptalklive.API.Service.TTLApiService;
import io.taptalk.taptalklive.API.Service.TTLRefreshTokenApiService;
import io.taptalk.taptalklive.BuildConfig;
import io.taptalk.taptalklive.Exception.TTLApiRefreshTokenRunningException;
import io.taptalk.taptalklive.Exception.TTLApiSessionExpiredException;
import io.taptalk.taptalklive.Exception.TTLAuthException;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.TapTalkLive;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.HttpResponseStatusCode.RESPONSE_SUCCESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.HttpResponseStatusCode.UNAUTHORIZED;

import java.util.concurrent.TimeUnit;

public class TTLApiManager {
    private static final String TAG = TTLApiManager.class.getSimpleName();

    @NonNull private static String apiBaseUrl = "";

    private TTLApiService ttlApiService;
    private TTLRefreshTokenApiService ttlRefreshTokenApiService;
    private static TTLApiManager instance;
    private boolean isRefreshTokenRunning = false;

    public static TTLApiManager getInstance() {
        return instance == null ? instance = new TTLApiManager() : instance;
    }

    private TTLApiManager() {
        TTLApiConnection connection = TTLApiConnection.getInstance();
        this.ttlApiService = connection.getTtlApiService();
        this.ttlRefreshTokenApiService = connection.getTtlRefreshTokenApiService();
    }

    public boolean isLoggedOut() {
        return !TTLDataManager.getInstance().checkAccessTokenAvailable();
    }

    @NonNull
    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(@NonNull String apiBaseUrl) {
        TTLApiManager.apiBaseUrl = apiBaseUrl;
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

        if (code == RESPONSE_SUCCESS && isRefreshTokenRunning) {
            isRefreshTokenRunning = false;
        }

        if (code == RESPONSE_SUCCESS && BuildConfig.DEBUG) {
            Log.d(TAG, "√√ API CALL SUCCESS √√");
            return Observable.just(t);
        }
        else if (code == UNAUTHORIZED) {
            Log.e(TAG, String.format(String.format("[Err %s - %s] %s", br.getStatus(), br.getError().getCode(), br.getError().getMessage()), code));
            if (!isLoggedOut()) {
                if (isRefreshTokenRunning) {
                    Log.e(TAG, "validateResponse: raiseApiRefreshTokenRunningException");
                    return raiseApiRefreshTokenRunningException();
                }
                else {
                    Log.e(TAG, "validateResponse: raiseApiSessionExpiredException");
                    return raiseApiSessionExpiredException(br);
                }
            }
        }
        else {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, String.format(String.format("[%s - %s] %s", br.getStatus(), br.getError().getCode(), br.getError().getMessage()), code));
            }
            return Observable.just(t);
        }
        return Observable.just(t);
    }

    private Observable validateException(Throwable t) {
        if (t instanceof TTLApiSessionExpiredException && !isRefreshTokenRunning && !isLoggedOut()) {
            return refreshAccessToken();
        }
        else if (t instanceof TTLApiRefreshTokenRunningException || (t instanceof TTLApiSessionExpiredException && isRefreshTokenRunning) && !isLoggedOut()) {
            return Observable.just(Boolean.TRUE).delay(1000, TimeUnit.MILLISECONDS);
        }
        else {
            return Observable.error(t);
        }
    }

    private Observable<Throwable> raiseApiSessionExpiredException(TTLBaseResponse br) {
        return Observable.error(new TTLApiSessionExpiredException(br.getError().getMessage()));
    }

    private Observable<Throwable> raiseApiRefreshTokenRunningException() {
        return Observable.error(new TTLApiRefreshTokenRunningException());
    }

    public Observable<TTLBaseResponse<TTLRequestAccessTokenResponse>> refreshAccessToken() {
        final String lastRefreshToken = TTLDataManager.getInstance().getRefreshToken();
        isRefreshTokenRunning = true;
        if (BuildConfig.DEBUG) {
            Log.e("-->", "Refresh Token is Running");
        }
        return ttlRefreshTokenApiService.refreshAccessToken(String.format("Bearer %s", TTLDataManager.getInstance().getRefreshToken()))
            .compose(this.applyIOMainThreadSchedulers())
            .doOnNext(response -> {
                if (RESPONSE_SUCCESS == response.getStatus()) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "refreshAccessToken: RESPONSE_SUCCESS");
                    }
                    updateSession(response);
                    Observable.error(new TTLAuthException(response.getError().getMessage()));
                }
                else if (UNAUTHORIZED == response.getStatus() && lastRefreshToken.equals(TTLDataManager.getInstance().getRefreshToken())) {
                    if (null != TapTalkLive.getInstance()) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "refreshAccessToken: UNAUTHORIZED - onTapTalkLiveRefreshTokenExpired");
                        }
                        TapTalkLive.getInstance().tapTalkLiveListener.onTapTalkLiveRefreshTokenExpired();
                    }
                    TapTalkLive.clearUserData();
                }
                else {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "refreshAccessToken: AuthException");
                    }
                    Observable.error(new TTLAuthException(response.getError().getMessage()));
                }
            })
            .doOnError(throwable -> {

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

    public void getProjectConfigs(Subscriber<TTLBaseResponse<TTLGetProjectConfigsResponse>> subscriber) {
        execute(ttlApiService.getProjectConfigs(), subscriber);
    }

    public void requestTapTalkAuthTicket(Subscriber<TTLBaseResponse<TTLRequestTicketResponse>> subscriber) {
        execute(ttlApiService.requestTapTalkAuthTicket(), subscriber);
    }

    public void getScfPath(Subscriber<TTLBaseResponse<TTLGetScfPathResponse>> subscriber) {
        execute(ttlApiService.getScfPath(), subscriber);
    }

    public void getTopicList(Subscriber<TTLBaseResponse<TTLGetTopicListResponse>> subscriber) {
        execute(ttlApiService.getTopicList(), subscriber);
    }

    public void createUser(String fullName, String email, Subscriber<TTLBaseResponse<TTLCreateUserResponse>> subscriber) {
        TTLCreateUserRequest request = new TTLCreateUserRequest(fullName, email);
        execute(ttlApiService.createUser(request), subscriber);
    }

    public void getUserProfile(Subscriber<TTLBaseResponse<TTLGetUserProfileResponse>> subscriber) {
        execute(ttlApiService.getUserProfile(), subscriber);
    }

    public void createCase(Integer topicID, String message, Subscriber<TTLBaseResponse<TTLCreateCaseResponse>> subscriber) {
        TTLCreateCaseRequest request = new TTLCreateCaseRequest(topicID, message);
        execute(ttlApiService.createCase(request), subscriber);
    }

    public void getCaseList(Subscriber<TTLBaseResponse<TTLGetCaseListResponse>> subscriber) {
        TTLGetCaseListRequest request = new TTLGetCaseListRequest(true);
        execute(ttlApiService.getCaseList(request), subscriber);
    }

    public void getCaseDetailsByID(Integer caseID, Subscriber<TTLBaseResponse<TTLCreateCaseResponse>> subscriber) {
        TTLIdRequest request = new TTLIdRequest(caseID);
        execute(ttlApiService.getCaseDetailsByID(request), subscriber);
    }

    public void closeCase(Integer caseID, Subscriber<TTLBaseResponse<TTLCommonResponse>> subscriber) {
        TTLIdRequest request = new TTLIdRequest(caseID);
        execute(ttlApiService.closeCase(request), subscriber);
    }

    public void rateConversation(Integer caseID, Integer rating, String note, Subscriber<TTLBaseResponse<TTLCommonResponse>> subscriber) {
        TTLRateConversationRequest request = new TTLRateConversationRequest(caseID, rating, note);
        execute(ttlApiService.rateConversation(request), subscriber);
    }

    public void sendMessage(TTLSendMessageRequest request, Subscriber<TTLBaseResponse<TTLSendMessageResponse>> subscriber) {
        execute(ttlApiService.sendMessage(request), subscriber);
    }

    public void logout(Subscriber<TTLBaseResponse<TTLCommonResponse>> subscriber) {
        execute(ttlApiService.logout(), subscriber);
    }
}
