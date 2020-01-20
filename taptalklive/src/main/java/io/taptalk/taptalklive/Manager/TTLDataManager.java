package io.taptalk.taptalklive.Manager;

import com.orhanobut.hawk.Hawk;

import io.taptalk.taptalklive.API.Api.TTLApiManager;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateCaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetProjectConfigsRespone;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetUserProfileResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import io.taptalk.taptalklive.API.Model.TTLUserModel;
import io.taptalk.taptalklive.API.Subscriber.TTLDefaultSubscriber;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.Const.TTLConstant;

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
        removeTapTalkApiUrl();
        removeTapTalkAppKeyID();
        removeTapTalkAppKeySecret();
        removeAuthTicket();
        removeAccessToken();
        removeRefreshToken();
        removeActiveUser();
    }

    /**
     * AUTH TICKET
     */
    public Boolean checkAuthTicketAvailable() {
        return checkPreferenceKeyAvailable(TTLConstant.PreferenceKey.AUTH_TICKET);
    }

    public String getAuthTicket() {
        return getStringPreference(TTLConstant.PreferenceKey.AUTH_TICKET);
    }

    public void saveAuthTicket(String authTicket) {
        saveStringPreference(TTLConstant.PreferenceKey.AUTH_TICKET, authTicket);
    }

    public void removeAuthTicket() {
        removePreference(TTLConstant.PreferenceKey.AUTH_TICKET);
    }

    /**
     * TAPTALK API URL
     */
    public Boolean checkTapTalkApiUrlAvailable() {
        return checkPreferenceKeyAvailable(TTLConstant.PreferenceKey.TAPTALK_API_URL);
    }

    public String getTapTalkApiUrl() {
        return getStringPreference(TTLConstant.PreferenceKey.TAPTALK_API_URL);
    }

    public void saveTapTalkApiUrl(String tapTalkApiUrl) {
        saveStringPreference(TTLConstant.PreferenceKey.TAPTALK_API_URL, tapTalkApiUrl);
    }

    public void removeTapTalkApiUrl() {
        removePreference(TTLConstant.PreferenceKey.TAPTALK_API_URL);
    }

    /**
     * TAPTALK API KEY ID
     */
    public Boolean checkTapTalkAppKeyIDAvailable() {
        return checkPreferenceKeyAvailable(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_ID);
    }

    public String getTapTalkAppKeyID() {
        return getStringPreference(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_ID);
    }

    public void saveTapTalkAppKeyID(String tapTalkAppKeyID) {
        saveStringPreference(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_ID, tapTalkAppKeyID);
    }

    public void removeTapTalkAppKeyID() {
        removePreference(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_ID);
    }

    /**
     * TAPTALK API KEY SECRET
     */
    public Boolean checkTapTalkAppKeySecretAvailable() {
        return checkPreferenceKeyAvailable(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_SECRET);
    }

    public String getTapTalkAppKeySecret() {
        return getStringPreference(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_SECRET);
    }

    public void saveTapTalkAppKeySecret(String tapTalkAppKeySecret) {
        saveStringPreference(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_SECRET, tapTalkAppKeySecret);
    }

    public void removeTapTalkAppKeySecret() {
        removePreference(TTLConstant.PreferenceKey.TAPTALK_APP_KEY_SECRET);
    }

    /**
     * ACCESS TOKEN
     */
    public Boolean checkAccessTokenAvailable() {
        return checkPreferenceKeyAvailable(TTLConstant.PreferenceKey.ACCESS_TOKEN);
    }

    public String getAccessToken() {
        return getStringPreference(TTLConstant.PreferenceKey.ACCESS_TOKEN);
    }

    public void saveAccessToken(String accessToken) {
        saveStringPreference(TTLConstant.PreferenceKey.ACCESS_TOKEN, accessToken);
    }

    public void saveAccessTokenExpiry(Long accessTokenExpiry) {
        saveLongTimestampPreference(TTLConstant.PreferenceKey.ACCESS_TOKEN_EXPIRY, accessTokenExpiry);
    }

    public long getAccessTokenExpiry() {
        return getLongTimestampPreference(TTLConstant.PreferenceKey.ACCESS_TOKEN_EXPIRY);
    }

    public void removeAccessToken() {
        removePreference(TTLConstant.PreferenceKey.ACCESS_TOKEN);
    }

    /**
     * REFRESH TOKEN
     */
    public Boolean checkRefreshTokenAvailable() {
        return checkPreferenceKeyAvailable(TTLConstant.PreferenceKey.REFRESH_TOKEN);
    }

    public String getRefreshToken() {
        return getStringPreference(TTLConstant.PreferenceKey.REFRESH_TOKEN);
    }

    public void saveRefreshToken(String refreshToken) {
        saveStringPreference(TTLConstant.PreferenceKey.REFRESH_TOKEN, refreshToken);
    }

    public void saveRefreshTokenExpiry(Long refreshTokenExpiry) {
        saveLongTimestampPreference(TTLConstant.PreferenceKey.REFRESH_TOKEN_EXPIRY, refreshTokenExpiry);
    }

    public void removeRefreshToken() {
        removePreference(TTLConstant.PreferenceKey.REFRESH_TOKEN);
    }

    /**
     * ACTIVE USER
     */
    public TTLUserModel getActiveUser() {
        return Hawk.get(TTLConstant.PreferenceKey.ACTIVE_USER, null);
    }

    public void saveActiveUser(TTLUserModel user) {
        Hawk.put(TTLConstant.PreferenceKey.ACTIVE_USER, user);
    }

    public void removeActiveUser() {
        removePreference(TTLConstant.PreferenceKey.ACTIVE_USER);
    }

    /**
     * =========================================================================================== *
     * API CALLS
     * =========================================================================================== *
     */

    public void requestAccessToken(TTLDefaultDataView<TTLRequestAccessTokenResponse> view) {
        TTLApiManager.getInstance().requestAccessToken(new TTLDefaultSubscriber<>(view));
    }

    public void getProjectConfigs(TTLDefaultDataView<TTLGetProjectConfigsRespone> view) {
        TTLApiManager.getInstance().getProjectConfigs(new TTLDefaultSubscriber<>(view));
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
