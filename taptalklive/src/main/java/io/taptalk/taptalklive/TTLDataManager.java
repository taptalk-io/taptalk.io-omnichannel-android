package io.taptalk.taptalklive;

import com.orhanobut.hawk.Hawk;

import io.taptalk.taptalklive.API.Api.TTLApiManager;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateCaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetUserProfileResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import io.taptalk.taptalklive.API.Model.TTLUserModel;
import io.taptalk.taptalklive.API.Subscriber.TTLDefaultSubscriber;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;

import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.ACCESS_TOKEN;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.ACCESS_TOKEN_EXPIRY;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.ACTIVE_USER;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.AUTH_TICKET;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.REFRESH_TOKEN;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.REFRESH_TOKEN_EXPIRY;

public class TTLDataManager {
    private static final String TAG = TTLDataManager.class.getSimpleName();
    private static TTLDataManager instance;

    public static TTLDataManager getInstance() {
        return instance == null ? (instance = new TTLDataManager()) : instance;
    }

    /**
     * =========================================================================================== *
     * GENERIC METHODS FOR PREFERENCE
     * =========================================================================================== *
     */

    private void saveBooleanPreference(String key, boolean bool) {
        Hawk.put(key, bool);
    }

    private void saveStringPreference(String key, String string) {
        Hawk.put(key, string);
    }

    private void saveFloatPreference(String key, Float flt) {
        Hawk.put(key, flt);
    }

    private void saveLongTimestampPreference(String key, Long timestamp) {
        Hawk.put(key, timestamp);
    }

    private Boolean getBooleanPreference(String key) {
        return Hawk.get(key, false);
    }

    private String getStringPreference(String key) {
        return Hawk.get(key, "");
    }

    private Float getFloatPreference(String key) {
        return Hawk.get(key, null);
    }

    private Long getLongTimestampPreference(String key) {
        return Hawk.get(key, 0L);
    }

    private Boolean checkPreferenceKeyAvailable(String key) {
        return Hawk.contains(key);
    }

    private void removePreference(String key) {
        Hawk.delete(key);
    }

    /**
     * =========================================================================================== *
     * PUBLIC METHODS FOR PREFERENCE (CALLS GENERIC METHODS ABOVE)
     * PUBLIC METHODS MAY NOT HAVE KEY AS PARAMETER
     * =========================================================================================== *
     */

    public void deleteAllPreference() {
        removeAuthTicket();
        removeAccessToken();
        removeRefreshToken();
        removeActiveUser();
    }

    /**
     * AUTH TICKET
     */
    public Boolean checkAuthTicketAvailable() {
        return checkPreferenceKeyAvailable(AUTH_TICKET);
    }

    public String getAuthTicket() {
        return getStringPreference(AUTH_TICKET);
    }

    public void saveAuthTicket(String authTicket) {
        saveStringPreference(AUTH_TICKET, authTicket);
    }

    public void removeAuthTicket() {
        removePreference(AUTH_TICKET);
    }

    /**
     * ACCESS TOKEN
     */
    public Boolean checkAccessTokenAvailable() {
        return checkPreferenceKeyAvailable(ACCESS_TOKEN);
    }

    public String getAccessToken() {
        return getStringPreference(ACCESS_TOKEN);
    }

    public void saveAccessToken(String accessToken) {
        saveStringPreference(ACCESS_TOKEN, accessToken);
    }

    public void saveAccessTokenExpiry(Long accessTokenExpiry) {
        saveLongTimestampPreference(ACCESS_TOKEN_EXPIRY, accessTokenExpiry);
    }

    public long getAccessTokenExpiry() {
        return getLongTimestampPreference(ACCESS_TOKEN_EXPIRY);
    }

    public void removeAccessToken() {
        removePreference(ACCESS_TOKEN);
    }

    /**
     * REFRESH TOKEN
     */
    public Boolean checkRefreshTokenAvailable() {
        return checkPreferenceKeyAvailable(REFRESH_TOKEN);
    }

    public String getRefreshToken() {
        return getStringPreference(REFRESH_TOKEN);
    }

    public void saveRefreshToken(String refreshToken) {
        saveStringPreference(REFRESH_TOKEN, refreshToken);
    }

    public void saveRefreshTokenExpiry(Long refreshTokenExpiry) {
        saveLongTimestampPreference(REFRESH_TOKEN_EXPIRY, refreshTokenExpiry);
    }

    public void removeRefreshToken() {
        removePreference(REFRESH_TOKEN);
    }

    /**
     * ACTIVE USER
     */
    public TTLUserModel getActiveUser() {
        return Hawk.get(ACTIVE_USER, null);
    }

    public void saveActiveUser(TTLUserModel user) {
        Hawk.put(ACTIVE_USER, user);
    }

    public void removeActiveUser() {
        removePreference(ACTIVE_USER);
    }

    /**
     * =========================================================================================== *
     * API CALLS
     * =========================================================================================== *
     */

    public void requestAccessToken(TTLDefaultDataView<TTLRequestAccessTokenResponse> view) {
        TTLApiManager.getInstance().requestAccessToken(new TTLDefaultSubscriber<>(view));
    }

    public void getTopicList(TTLDefaultDataView<TTLGetTopicListResponse> view) {
        TTLApiManager.getInstance().getTopicList(new TTLDefaultSubscriber<>(view));
    }

    public void createUser(String fullName, String email, TTLDefaultDataView<TTLCreateUserResponse> view) {
        TTLApiManager.getInstance().createUser(fullName, email, new TTLDefaultSubscriber<>(view));
    }

    public void getUserProfile(TTLDefaultDataView<TTLGetUserProfileResponse> view) {
        TTLApiManager.getInstance().getUserProfile(new TTLDefaultSubscriber<>(view));
    }

    public void createCase(Integer topicID, String message, TTLDefaultDataView<TTLCreateCaseResponse> view) {
        TTLApiManager.getInstance().createCase(topicID, message, new TTLDefaultSubscriber<>(view));
    }

    public void rateConversation(Integer caseID, Integer rating, String note, TTLDefaultDataView<TTLCommonResponse> view) {
        TTLApiManager.getInstance().rateConversation(caseID, rating, note, new TTLDefaultSubscriber<>(view));
    }

    public void logout(TTLDefaultDataView<TTLCommonResponse> view) {
        TTLApiManager.getInstance().logout(new TTLDefaultSubscriber<>(view));
    }
}
