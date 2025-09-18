package io.taptalk.taptalklive.Manager;

import com.fasterxml.jackson.core.type.TypeReference;

import io.taptalk.TapTalk.Helper.TapPreferenceUtils;
import io.taptalk.taptalklive.API.Api.TTLApiManager;
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
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel;
import io.taptalk.taptalklive.API.Model.TTLScfPathModel;
import io.taptalk.taptalklive.API.Model.TTLTopicModel;
import io.taptalk.taptalklive.API.Model.TTLUserModel;
import io.taptalk.taptalklive.API.Subscriber.TTLDefaultSubscriber;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;

import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.ACCESS_TOKEN;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.ACCESS_TOKEN_EXPIRY;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.ACTIVE_USER;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.APP_KEY_SECRET;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.AUTH_TICKET;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.CASE_EXISTS;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.CHANNEL_LINKS;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.MAX_OPEN_CASES;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.OPEN_CASE_LIST_COUNT;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.REFRESH_TOKEN;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.REFRESH_TOKEN_EXPIRY;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.SCF_PATH;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.TAPTALK_API_URL;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.TAPTALK_APP_KEY_ID;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.TAPTALK_APP_KEY_SECRET;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.TAPTALK_AUTH_TICKET;
import static io.taptalk.taptalklive.Const.TTLConstant.PreferenceKey.TOPICS;

import java.util.List;

public class TTLDataManager {
    private static final String TAG = TTLDataManager.class.getSimpleName();
    private static TTLDataManager instance;
    private String appKeySecret;

    public static TTLDataManager getInstance() {
        return instance == null ? (instance = new TTLDataManager()) : instance;
    }

    /**
     * =========================================================================================== *
     * GENERIC METHODS FOR PREFERENCE
     * =========================================================================================== *
     */

    private void saveBooleanPreference(String key, boolean bool) {
        TapPreferenceUtils.saveBooleanPreference(key, bool);
    }

    private void saveStringPreference(String key, String string) {
        TapPreferenceUtils.saveStringPreference(key, string);
    }

    private void saveFloatPreference(String key, Float flt) {
        TapPreferenceUtils.saveFloatPreference(key, flt);
    }

    private void saveLongTimestampPreference(String key, Long timestamp) {
        TapPreferenceUtils.saveLongPreference(key, timestamp);
    }

    private Boolean getBooleanPreference(String key) {
        return TapPreferenceUtils.getBooleanPreference(key);
    }

    private String getStringPreference(String key) {
        return TapPreferenceUtils.getStringPreference(key);
    }

    private Float getFloatPreference(String key) {
        return TapPreferenceUtils.getFloatPreference(key);
    }

    private Long getLongTimestampPreference(String key) {
        return TapPreferenceUtils.getLongPreference(key);
    }

    private Boolean checkPreferenceKeyAvailable(String key) {
        return TapPreferenceUtils.checkPreferenceKeyAvailable(key);
    }

    private void removePreference(String key) {
        TapPreferenceUtils.removePreference(key);
    }

    /**
     * =========================================================================================== *
     * PUBLIC METHODS FOR PREFERENCE (CALLS GENERIC METHODS ABOVE)
     * PUBLIC METHODS MAY NOT HAVE KEY AS PARAMETER
     * =========================================================================================== *
     */

    public void deleteUserPreferences() {
        removeAuthTicket();
        removeAccessToken();
        removeAccessTokenExpiry();
        removeRefreshToken();
        removeRefreshTokenExpiry();
        removeActiveUser();
        removeTapTalkAuthTicket();
        removeActiveUserHasExistingCase();
        removeOpenCaseCount();
    }

    public void deleteAllPreference() {
        deleteUserPreferences();
        removeAppKeySecret();
        removeTapTalkApiUrl();
        removeTapTalkAppKeyID();
        removeTapTalkAppKeySecret();
        removeChannelLinks();
        removeMaxOpenCases();
        removeScfPath();
        removeTopics();
    }

    /**
     * APP KEY SECRET
     */
    public String getAppKeySecret() {
        return getStringPreference(APP_KEY_SECRET);
    }

    public void saveAppKeySecret(String appKeySecret) {
        saveStringPreference(APP_KEY_SECRET, appKeySecret);
    }

    public void removeAppKeySecret() {
        removePreference(APP_KEY_SECRET);
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

    public void removeAccessToken() {
        removePreference(ACCESS_TOKEN);
    }

    public long getAccessTokenExpiry() {
        return getLongTimestampPreference(ACCESS_TOKEN_EXPIRY);
    }

    public void saveAccessTokenExpiry(Long accessTokenExpiry) {
        saveLongTimestampPreference(ACCESS_TOKEN_EXPIRY, accessTokenExpiry);
    }

    public void removeAccessTokenExpiry() {
        removePreference(ACCESS_TOKEN_EXPIRY);
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

    public void removeRefreshToken() {
        removePreference(REFRESH_TOKEN);
    }

    public String getRefreshTokenExpiry() {
        return getStringPreference(REFRESH_TOKEN_EXPIRY);
    }

    public void saveRefreshTokenExpiry(Long refreshTokenExpiry) {
        saveLongTimestampPreference(REFRESH_TOKEN_EXPIRY, refreshTokenExpiry);
    }

    public void removeRefreshTokenExpiry() {
        removePreference(REFRESH_TOKEN_EXPIRY);
    }

    /**
     * ACTIVE USER
     */
    public boolean checkActiveUserExists() {
        return checkPreferenceKeyAvailable(ACTIVE_USER) && null != getActiveUser();
    }

    public TTLUserModel getActiveUser() {
        return TapPreferenceUtils.getPreference(ACTIVE_USER, new TypeReference<>() {});
    }

    public void saveActiveUser(TTLUserModel user) {
        TapPreferenceUtils.savePreference(ACTIVE_USER, user);
    }

    public void removeActiveUser() {
        removePreference(ACTIVE_USER);
    }

    /**
     * TAPTALK API URL
     */
    public Boolean checkTapTalkApiUrlAvailable() {
        return checkPreferenceKeyAvailable(TAPTALK_API_URL);
    }

    public String getTapTalkApiUrl() {
        return getStringPreference(TAPTALK_API_URL);
    }

    public void saveTapTalkApiUrl(String tapTalkApiUrl) {
        saveStringPreference(TAPTALK_API_URL, tapTalkApiUrl);
    }

    public void removeTapTalkApiUrl() {
        removePreference(TAPTALK_API_URL);
    }

    /**
     * TAPTALK APP KEY ID
     */
    public Boolean checkTapTalkAppKeyIDAvailable() {
        return checkPreferenceKeyAvailable(TAPTALK_APP_KEY_ID);
    }

    public String getTapTalkAppKeyID() {
        return getStringPreference(TAPTALK_APP_KEY_ID);
    }

    public void saveTapTalkAppKeyID(String tapTalkAppKeyID) {
        saveStringPreference(TAPTALK_APP_KEY_ID, tapTalkAppKeyID);
    }

    public void removeTapTalkAppKeyID() {
        removePreference(TAPTALK_APP_KEY_ID);
    }

    /**
     * TAPTALK APP KEY SECRET
     */
    public Boolean checkTapTalkAppKeySecretAvailable() {
        return checkPreferenceKeyAvailable(TAPTALK_APP_KEY_SECRET);
    }

    public String getTapTalkAppKeySecret() {
        return getStringPreference(TAPTALK_APP_KEY_SECRET);
    }

    public void saveTapTalkAppKeySecret(String tapTalkAppKeySecret) {
        saveStringPreference(TAPTALK_APP_KEY_SECRET, tapTalkAppKeySecret);
    }

    public void removeTapTalkAppKeySecret() {
        removePreference(TAPTALK_APP_KEY_SECRET);
    }

    /**
     * TAPTALK AUTH TICKET
     */
    public String getTapTalkAuthTicket() {
        return getStringPreference(TAPTALK_AUTH_TICKET);
    }

    public void saveTapTalkAuthTicket(String tapTalkAuthTicket) {
        saveStringPreference(TAPTALK_AUTH_TICKET, tapTalkAuthTicket);
    }

    public void removeTapTalkAuthTicket() {
        removePreference(TAPTALK_AUTH_TICKET);
    }

    /**
     * CHANNEL LINKS
     */

    public List<TTLChannelLinkModel> getChannelLinks() {
        return TapPreferenceUtils.getPreference(CHANNEL_LINKS, new TypeReference<>() {});
    }

    public void saveChannelLinks(List<TTLChannelLinkModel> channelLinks) {
        TapPreferenceUtils.savePreference(CHANNEL_LINKS, channelLinks);
    }

    public void removeChannelLinks() {
        removePreference(CHANNEL_LINKS);
    }

    /**
     * MAX OPEN CASES
     */

    public int getMaxOpenCases() {
        return TapPreferenceUtils.getLongPreference(MAX_OPEN_CASES).intValue();
    }

    public void saveMaxOpenCases(int maxOpenCases) {
        TapPreferenceUtils.saveLongPreference(MAX_OPEN_CASES, (long) maxOpenCases);
    }

    public void removeMaxOpenCases() {
        removePreference(MAX_OPEN_CASES);
    }

    /**
     * SCF PATH
     */

    public TTLScfPathModel getScfPath() {
        return TapPreferenceUtils.getPreference(SCF_PATH, new TypeReference<>() {});
    }

    public void saveScfPath(TTLScfPathModel scfPath) {
        TapPreferenceUtils.savePreference(SCF_PATH, scfPath);
    }

    public void removeScfPath() {
        removePreference(SCF_PATH);
    }

    /**
     * TOPICS
     */

    public List<TTLTopicModel> getTopics() {
        return TapPreferenceUtils.getPreference(TOPICS, new TypeReference<>() {});
    }

    public void saveTopics(List<TTLTopicModel> topics) {
        TapPreferenceUtils.savePreference(TOPICS, topics);
    }

    public void removeTopics() {
        removePreference(TOPICS);
    }

    /**
     * USER HAS EXISTING CASE
     */

    public boolean activeUserHasExistingCase() {
        Boolean caseExists = getBooleanPreference(CASE_EXISTS);
        return null != caseExists ? caseExists : false;
    }

    public void saveActiveUserHasExistingCase(boolean hasExistingCase) {
        saveBooleanPreference(CASE_EXISTS, hasExistingCase);
    }

    public void removeActiveUserHasExistingCase() {
        removePreference(CASE_EXISTS);
    }

    /**
     * USER OPEN CASE COUNT
     */

    public int getOpenCaseCount() {
        return TapPreferenceUtils.getLongPreference(OPEN_CASE_LIST_COUNT).intValue();
    }

    public void saveOpenCaseCount(int openCaseCount) {
        TapPreferenceUtils.saveLongPreference(OPEN_CASE_LIST_COUNT, (long) openCaseCount);
    }

    public void removeOpenCaseCount() {
        removePreference(OPEN_CASE_LIST_COUNT);
    }

    /**
     * =========================================================================================== *
     * API CALLS
     * =========================================================================================== *
     */

    public void requestAccessToken(TTLDefaultDataView<TTLRequestAccessTokenResponse> view) {
        TTLApiManager.getInstance().requestAccessToken(new TTLDefaultSubscriber<>(view));
    }

    public void getProjectConfigs(TTLDefaultDataView<TTLGetProjectConfigsResponse> view) {
        TTLApiManager.getInstance().getProjectConfigs(new TTLDefaultSubscriber<>(view));
    }

    public void requestTapTalkAuthTicket(TTLDefaultDataView<TTLRequestTicketResponse> view) {
        TTLApiManager.getInstance().requestTapTalkAuthTicket(new TTLDefaultSubscriber<>(view));
    }

    public void getScfPath(TTLDefaultDataView<TTLGetScfPathResponse> view) {
        TTLApiManager.getInstance().getScfPath(new TTLDefaultSubscriber<>(view));
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

    public void getCaseList(TTLDefaultDataView<TTLGetCaseListResponse> view) {
        TTLApiManager.getInstance().getCaseList(new TTLDefaultSubscriber<>(view));
    }

    public void getCaseDetailsByID(Integer caseID, TTLDefaultDataView<TTLCreateCaseResponse> view) {
        TTLApiManager.getInstance().getCaseDetailsByID(caseID, new TTLDefaultSubscriber<>(view));
    }

    public void closeCase(Integer caseID, TTLDefaultDataView<TTLCommonResponse> view) {
        TTLApiManager.getInstance().closeCase(caseID, new TTLDefaultSubscriber<>(view));
    }

    public void rateConversation(Integer caseID, Integer rating, String note, TTLDefaultDataView<TTLCommonResponse> view) {
        TTLApiManager.getInstance().rateConversation(caseID, rating, note, new TTLDefaultSubscriber<>(view));
    }

    public void sendMessage(TTLSendMessageRequest request, TTLDefaultDataView<TTLSendMessageResponse> view) {
        TTLApiManager.getInstance().sendMessage(request, new TTLDefaultSubscriber<>(view));
    }

    public void logout(TTLDefaultDataView<TTLCommonResponse> view) {
        TTLApiManager.getInstance().logout(new TTLDefaultSubscriber<>(view));
    }
}
