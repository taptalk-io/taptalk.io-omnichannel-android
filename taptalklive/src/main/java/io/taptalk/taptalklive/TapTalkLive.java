package io.taptalk.taptalklive;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
import io.taptalk.TapTalk.Listener.TapUIRoomListListener;
import io.taptalk.TapTalk.Manager.TapLocaleManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.taptalklive.API.Api.TTLApiManager;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetProjectConfigsRespone;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestTicketResponse;
import io.taptalk.taptalklive.API.Model.TTLTapTalkProjectConfigsModel;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.Activity.TTLCaseListActivity;
import io.taptalk.taptalklive.Activity.TTLCreateCaseFormActivity;
import io.taptalk.taptalklive.Activity.TTLReviewActivity;
import io.taptalk.taptalklive.CustomBubble.TTLReviewChatBubbleClass;
import io.taptalk.taptalklive.CustomBubble.TTLSystemMessageBubbleClass;
import io.taptalk.taptalklive.Listener.TapTalkLiveListener;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.Manager.TTLNetworkStateManager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine;
import static io.taptalk.taptalklive.BuildConfig.TAPLIVE_SDK_BASE_URL;
import static io.taptalk.taptalklive.Const.TTLConstant.Api.API_VERSION;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_CLOSE_CASE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REOPEN_CASE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW_SUBMITTED;
import static io.taptalk.taptalklive.Const.TTLConstant.RequestCode.REVIEW;
import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

public class TapTalkLive {

    public static TapTalkLive tapLive;
    public static Context context;

    private static final String TAG = TapTalkLive.class.getSimpleName();
    private static String clientAppName;
    private static String appKeySecret = "";
    private static int clientAppIcon;
    private static boolean isNeedToGetProjectConfigs;
    private static boolean isTapTalkInitialized;
    private static boolean isGetCaseListCompleted;
    private static TapTalkLiveListener tapTalkLiveListener;

    private TapTalkLive(@NonNull final Context appContext,
                        @NonNull String appKeySecret,
                        int clientAppIcon,
                        String clientAppName,
                        @NonNull TapTalkLiveListener tapTalkLiveListener) {

        TapTalkLive.context = appContext;

        // Init Hawk for preference
        if (!Hawk.isBuilt()) {
            if (BuildConfig.BUILD_TYPE.equals("dev")) {
                // No encryption for dev build
                Hawk.init(appContext).setEncryption(new NoEncryption()).build();
            } else {
                Hawk.init(appContext).build();
            }
        }

        TTLDataManager.getInstance().saveAppKeySecret(appKeySecret);
        TTLApiManager.setApiBaseUrl(generateApiBaseUrl(TAPLIVE_SDK_BASE_URL));

        TapTalkLive.appKeySecret = appKeySecret; // FIXME APP KEY SECRET CURRENTLY SAVED AS STATIC
        TapTalkLive.clientAppIcon = clientAppIcon;
        TapTalkLive.clientAppName = clientAppName;
        TapTalkLive.tapTalkLiveListener = tapTalkLiveListener;

        // Get project configs for TapTalk SDK
        TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);

        if (TTLDataManager.getInstance().checkActiveUserExists()) {
            // Check if user has active case
            TTLDataManager.getInstance().getCaseList(caseListDataView);
        } else {
            isGetCaseListCompleted = true;
            if (isTapTalkInitialized) {
                tapTalkLiveListener.onInitializationCompleted();
            }
        }
    }

    public static TapTalkLive init(Context context,
                                   String appKeySecret,
                                   int clientAppIcon,
                                   String clientAppName,
                                   TapTalkLiveListener tapTalkLiveListener) {
        isTapTalkInitialized = false;
        if (null == tapLive) {
            tapLive = new TapTalkLive(
                    context,
                    appKeySecret,
                    clientAppIcon,
                    clientAppName,
                    tapTalkLiveListener);
        } else {
            if (TTLDataManager.getInstance().checkTapTalkAppKeyIDAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkAppKeySecretAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkApiUrlAvailable()) {
                initializeTapTalkSDK(
                        TTLDataManager.getInstance().getTapTalkAppKeyID(),
                        TTLDataManager.getInstance().getTapTalkAppKeySecret(),
                        TTLDataManager.getInstance().getTapTalkApiUrl());
            } else {
                isNeedToGetProjectConfigs = true;
                TTLNetworkStateManager.getInstance().registerCallback(context);
                TTLNetworkStateManager.getInstance().addNetworkListener(networkListener);
            }
        }

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(context)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                            .build()
            );
        }

        return tapLive;
    }

    private static String generateApiBaseUrl(String apiBaseUrl) {
        return apiBaseUrl + "/" + API_VERSION + "/";
    }

    private static TTLDefaultDataView<TTLGetProjectConfigsRespone> projectConfigsDataView = new TTLDefaultDataView<TTLGetProjectConfigsRespone>() {
        @Override
        public void onSuccess(TTLGetProjectConfigsRespone response) {
            TTLTapTalkProjectConfigsModel tapTalk = response.getTapTalkProjectConfigs();
            if (null != tapTalk) {
                initializeTapTalkSDK(
                        tapTalk.getAppKeyID(),
                        tapTalk.getAppKeySecret(),
                        tapTalk.getApiURL());
                TTLDataManager.getInstance().saveTapTalkAppKeyID(tapTalk.getAppKeyID());
                TTLDataManager.getInstance().saveTapTalkAppKeySecret(tapTalk.getAppKeySecret());
                TTLDataManager.getInstance().saveTapTalkApiUrl(tapTalk.getApiURL());
            }
        }

        @Override
        public void onError(TTLErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            if (TTLDataManager.getInstance().checkTapTalkAppKeyIDAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkAppKeySecretAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkApiUrlAvailable()) {
                initializeTapTalkSDK(
                        TTLDataManager.getInstance().getTapTalkAppKeyID(),
                        TTLDataManager.getInstance().getTapTalkAppKeySecret(),
                        TTLDataManager.getInstance().getTapTalkApiUrl());
            } else {
                isNeedToGetProjectConfigs = true;
                TTLNetworkStateManager.getInstance().registerCallback(context);
                TTLNetworkStateManager.getInstance().addNetworkListener(networkListener);
            }
        }
    };

    private static TTLDefaultDataView<TTLGetCaseListResponse> caseListDataView = new TTLDefaultDataView<TTLGetCaseListResponse>() {
        @Override
        public void onSuccess(TTLGetCaseListResponse response) {
            TTLDataManager.getInstance().saveActiveUserHasExistingCase(
                    null != response &&
                            null != response.getCases() &&
                            !response.getCases().isEmpty());
            onFinish();
        }

        @Override
        public void onError(TTLErrorModel error) {
            onFinish();
        }

        @Override
        public void onError(String errorMessage) {
            onFinish();
        }

        private void onFinish() {
            isGetCaseListCompleted = true;
            if (isTapTalkInitialized) {
                tapTalkLiveListener.onInitializationCompleted();
            }
        }
    };

    private static void initializeTapTalkSDK(String tapTalkAppKeyID, String tapTalkAppKeySecret, String tapTalkApiUrl) {
        if (isTapTalkInitialized) {
            return;
        }
        TapTalk.setLoggingEnabled(BuildConfig.DEBUG);
        TapTalk.initNewInstance(
                TAPTALK_INSTANCE_KEY,
                context,
                tapTalkAppKeyID,
                tapTalkAppKeySecret,
                clientAppIcon,
                clientAppName,
                tapTalkApiUrl,
                TapTalkImplementationTypeCombine,
                tapListener);

        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setReadStatusHidden(true);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setCloseButtonInRoomListVisible(true);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setProfileButtonInChatRoomVisible(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setMyAccountButtonInRoomListVisible(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).removeRoomListListener(tapUIRoomListListener);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addRoomListListener(tapUIRoomListListener);

        // TODO: 20 Feb 2020 TEMPORARILY DISABLED CASE LIST PAGE
//        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setSearchChatBarInRoomListVisible(false);
//        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setNewChatButtonInRoomListVisible(false);

        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(closeCaseCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reopenCaseCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reviewCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reviewSubmittedCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).removeCustomKeyboardListener(customKeyboardListener);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomKeyboardListener(customKeyboardListener);

        if (!TapTalk.isConnected(TAPTALK_INSTANCE_KEY)) {
            TapTalk.connect(TAPTALK_INSTANCE_KEY, new TapCommonListener() {
                @Override
                public void onSuccess(String s) {
                    isTapTalkInitialized = true;
                    if (isGetCaseListCompleted) {
                        tapTalkLiveListener.onInitializationCompleted();
                    }
                }

                @Override
                public void onError(String s, String s1) {
                    isTapTalkInitialized = true;
                    if (isGetCaseListCompleted) {
                        tapTalkLiveListener.onInitializationCompleted();
                    }
                }
            });
        } else {
            isTapTalkInitialized = true;
            if (isGetCaseListCompleted) {
                tapTalkLiveListener.onInitializationCompleted();
            }
        }
    }

    private static TTLSystemMessageBubbleClass closeCaseCustomBubble = new TTLSystemMessageBubbleClass(
            R.layout.ttl_cell_chat_system_message,
            TYPE_CLOSE_CASE, (context, message) -> {}
    );

    private static TTLSystemMessageBubbleClass reopenCaseCustomBubble = new TTLSystemMessageBubbleClass(
            R.layout.ttl_cell_chat_system_message,
            TYPE_REOPEN_CASE, (context, message) -> {}
    );

    private static TTLReviewChatBubbleClass reviewCustomBubble = new TTLReviewChatBubbleClass(
            R.layout.ttl_cell_chat_bubble_review,
            TYPE_REVIEW, (context, message) -> {
        Intent intent = new Intent(context, TTLReviewActivity.class);
        intent.putExtra(MESSAGE, message);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, REVIEW);
            ((Activity) context).overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
        } else {
            context.startActivity(intent);
        }
    });

    private static TapUICustomKeyboardListener customKeyboardListener = new TapUICustomKeyboardListener() {
        @Override
        public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
            List<TAPCustomKeyboardItemModel> keyboardItemModelList = new ArrayList<>();
            TAPCustomKeyboardItemModel markAsSolvedCustomKeyboard = new TAPCustomKeyboardItemModel(
                    context.getString(R.string.ttl_mark_as_solved),
                    ContextCompat.getDrawable(context, R.drawable.ttl_ic_checklist_black_19),
                    context.getString(R.string.ttl_mark_as_solved)
            );
            keyboardItemModelList.add(markAsSolvedCustomKeyboard);
            return keyboardItemModelList;
        }

        @Override
        public void onCustomKeyboardItemTapped(Activity activity, TAPCustomKeyboardItemModel customKeyboardItem, TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
            new TapTalkDialog.Builder(activity)
                    .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                    .setTitle(activity.getString(R.string.ttl_close_case))
                    .setMessage(activity.getString(R.string.ttl_close_case_dialog_message))
                    .setPrimaryButtonTitle(activity.getString(R.string.ttl_ok))
                    .setPrimaryButtonListener(v -> closeCase(room.getXcRoomID()))
                    .setSecondaryButtonTitle(activity.getString(R.string.ttl_cancel))
                    .show();
        }
    };

    private static TTLReviewChatBubbleClass reviewSubmittedCustomBubble = new TTLReviewChatBubbleClass(
            R.layout.ttl_cell_chat_bubble_review,
            TYPE_REVIEW_SUBMITTED, (context, message) -> {}
    );

    public static void authenticateTapTalkSDK(String authTicket, TapCommonListener listener) {
        if (TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
            listener.onSuccess("TapTalk SDK is already authenticated");
            return;
        }
        TapTalk.authenticateWithAuthTicket(TAPTALK_INSTANCE_KEY, authTicket, true, listener);
    }

    public static void initializeGooglePlacesApiKey(String apiKey) {
        if (!isTapTalkInitialized) {
            return;
        }
        TapTalk.initializeGooglePlacesApiKey(apiKey);
    }

    private static TapListener tapListener = new TapListener() {
        @Override
        public void onTapTalkRefreshTokenExpired() {
            requestTapTalkAuthTicket();
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }

        @Override
        public void onNotificationReceived(TAPMessageModel message) {
            TapTalk.showTapTalkNotification(TAPTALK_INSTANCE_KEY, message);
        }

        @Override
        public void onUserLogout() {

        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {

        }
    };

    private static void requestTapTalkAuthTicket() {
        TTLDataManager.getInstance().requestTapTalkAuthTicket(requestTapTalkAuthTicketDataView);
    }

    private static TTLDefaultDataView<TTLRequestTicketResponse> requestTapTalkAuthTicketDataView = new TTLDefaultDataView<TTLRequestTicketResponse>() {
        @Override
        public void onSuccess(TTLRequestTicketResponse response) {
            if (null != response) {
                TTLDataManager.getInstance().saveTapTalkAuthTicket(response.getTicket());
                authenticateTapTalkSDK(response.getTicket(), authenticateTapTalkSDKListener);
            } else {
                clearAllTapLiveData();
            }
        }

        @Override
        public void onError(TTLErrorModel error) {
            clearAllTapLiveData();
        }

        @Override
        public void onError(String errorMessage) {
            clearAllTapLiveData();
        }
    };

    private static TapCommonListener authenticateTapTalkSDKListener = new TapCommonListener() {
        @Override
        public void onSuccess(String s) {
            TTLDataManager.getInstance().removeTapTalkAuthTicket();
        }

        @Override
        public void onError(String s, String s1) {
            clearAllTapLiveData();
        }
    };

    private static TapUIRoomListListener tapUIRoomListListener = new TapUIRoomListListener() {
        @Override
        public void onNewChatButtonTapped(Activity activity) {
            openCreateCaseForm(activity, true);
        }
    };

    private static TapTalkNetworkInterface networkListener = new TapTalkNetworkInterface() {
        @Override
        public void onNetworkAvailable() {
            if (isNeedToGetProjectConfigs) {
                TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);
                TTLNetworkStateManager.getInstance().unregisterCallback(context);
                isNeedToGetProjectConfigs = false;
            }
        }
    };

    private static void openCaseList(Context activityContext) {
        Intent intent = new Intent(activityContext, TTLCaseListActivity.class);
        activityContext.startActivity(intent);
        if (activityContext instanceof Activity) {
            ((Activity) activityContext).overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

    private static void openCreateCaseForm(Context activityContext, boolean showCloseButton) {
        Intent intent = new Intent(activityContext, TTLCreateCaseFormActivity.class);
        intent.putExtra(SHOW_CLOSE_BUTTON, showCloseButton);
        activityContext.startActivity(intent);
        if (activityContext instanceof Activity) {
            ((Activity) activityContext).overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

    public static boolean openTapTalkLiveView(Context activityContext) {
        if (!isTapTalkInitialized) {
            return false;
        }
        if (TTLDataManager.getInstance().checkActiveUserExists() ||
                TTLDataManager.getInstance().checkAccessTokenAvailable()) {
            TapUI.getInstance(TAPTALK_INSTANCE_KEY).openRoomList(activityContext);
//        openCaseList(activityContext); // TODO: 20 Feb 2020 TEMPORARILY DISABLED CASE LIST PAGE
        }
        if (!TTLDataManager.getInstance().activeUserHasExistingCase()) {
            openCreateCaseForm(activityContext, true);
        }
        return true;
    }

    private static void closeCase(String xcRoomID) {
        try {
            int caseID = Integer.valueOf(xcRoomID.replace("case:", ""));
            TTLDataManager.getInstance().closeCase(caseID, new TTLDefaultDataView<TTLCommonResponse>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppKeySecret() {
        return appKeySecret;
    }

    /**
     * =============================================================================================
     * LANGUAGE
     * =============================================================================================
     */

    public enum Language {ENGLISH, INDONESIAN}

    public static void setDefaultLanguage(Language language) {
        String defaultLanguage;
        switch (language) {
            case INDONESIAN:
                defaultLanguage = "in";
                break;
            default:
                defaultLanguage = "en";
                break;
        }
        TapLocaleManager.setLocale((Application) context, defaultLanguage);

        // TODO: 27 Feb 2020 RESTART OPEN ACTIVITIES TO APPLY CHANGED RESOURCES
    }

    public static void logoutAndClearAllTapLiveData(TapCommonListener listener) {
        //checkTapTalkInitialized();
        TTLDataManager.getInstance().logout(new TTLDefaultDataView<TTLCommonResponse>() {
            @Override
            public void onSuccess(TTLCommonResponse response) {
                clearAllTapLiveData();
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }

    public static void clearAllTapLiveData() {
        //checkTapTalkInitialized();
        TTLDataManager.getInstance().deleteAllPreference();
        TTLApiManager.getInstance().setLoggedOut(true);
    }
}
