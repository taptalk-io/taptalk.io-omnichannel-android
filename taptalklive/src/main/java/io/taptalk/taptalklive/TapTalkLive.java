package io.taptalk.taptalklive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
import static io.taptalk.taptalklive.Const.TTLConstant.CustomKeyboard.MARK_AS_SOLVED;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_CLOSE_CASE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REOPEN_CASE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW_SUBMITTED;
import static io.taptalk.taptalklive.Const.TTLConstant.RequestCode.REVIEW;

public class TapTalkLive {

    public static TapTalkLive tapLive;
    public static Context context;

    private static final String TAG = TapTalkLive.class.getSimpleName();
    private static String clientAppName;
    private static int clientAppIcon;
    private static boolean isNeedToGetProjectConfigs;
    private static boolean isTapTalkInitialized; // TODO TEMPORARY
    private static TapTalkLiveListener tapTalkLiveListener;

    private TapTalkLive(@NonNull final Context appContext,
                        @NonNull String appKeySecret,
                        int clientAppIcon,
                        String clientAppName,
                        @NonNull TapTalkLiveListener tapTalkLiveListener) {

        context = appContext;

        // Init Hawk for preference
        if (io.taptalk.Taptalk.BuildConfig.BUILD_TYPE.equals("dev")) {
            // No encryption for dev build
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        } else {
            Hawk.init(appContext).build();
        }

        TTLDataManager.getInstance().saveAppKeySecret(appKeySecret);
        TTLApiManager.setApiBaseUrl(generateApiBaseUrl(TAPLIVE_SDK_BASE_URL));

        TapTalkLive.clientAppIcon = clientAppIcon;
        TapTalkLive.clientAppName = clientAppName;
        TapTalkLive.tapTalkLiveListener = tapTalkLiveListener;

        TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);
        if (TTLDataManager.getInstance().checkActiveUserExists()) {
            TTLDataManager.getInstance().getCaseList(caseListDataView);

            // TODO: 023, 23 Jan 2020 TESTING
//            if (TTLDataManager.getInstance().checkTapTalkAppKeyIDAvailable() &&
//                    TTLDataManager.getInstance().checkTapTalkAppKeySecretAvailable() &&
//                    TTLDataManager.getInstance().checkTapTalkApiUrlAvailable()) {
//                initializeTapTalkSDK(
//                        TTLDataManager.getInstance().getTapTalkAppKeyID(),
//                        TTLDataManager.getInstance().getTapTalkAppKeySecret(),
//                        TTLDataManager.getInstance().getTapTalkApiUrl());
//            } else {
//                isNeedToGetProjectConfigs = true;
//                TTLNetworkStateManager.getInstance().registerCallback(context);
//                TTLNetworkStateManager.getInstance().addNetworkListener(networkListener);
//            }
        }
    }

    public static TapTalkLive init(Context context,
                                   String appKeySecret,
                                   int clientAppIcon,
                                   String clientAppName,
                                   TapTalkLiveListener tapTalkLiveListener) {
        isTapTalkInitialized = false; // TODO TEMPORARY
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

    private TTLDefaultDataView<TTLGetCaseListResponse> caseListDataView = new TTLDefaultDataView<TTLGetCaseListResponse>() {
        @Override
        public void onSuccess(TTLGetCaseListResponse response) {
            TTLDataManager.getInstance().saveActiveUserHasExistingCase(
                    null != response &&
                            null != response.getCases() &&
                            !response.getCases().isEmpty());
        }
    };

    private static void initializeTapTalkSDK(String tapTalkAppKeyID, String tapTalkAppKeySecret, String tapTalkApiUrl) {
        if (isTapTalkInitialized) { // TODO TEMPORARY
            return;
        }
        TapTalk.setLoggingEnabled(BuildConfig.DEBUG);
        TapTalk.init(
                context,
                tapTalkAppKeyID,
                tapTalkAppKeySecret,
                clientAppIcon,
                clientAppName,
                tapTalkApiUrl,
                TapTalkImplementationTypeCombine,
                tapListener);
        isTapTalkInitialized = true; // TODO TEMPORARY

        TapUI.getInstance().setCloseButtonInRoomListVisible(true);
        TapUI.getInstance().setProfileButtonInChatRoomVisible(false);
        TapUI.getInstance().setMyAccountButtonInRoomListVisible(false);
        TapUI.getInstance().removeRoomListListener(tapUIRoomListListener);
        TapUI.getInstance().addRoomListListener(tapUIRoomListListener);

        // TODO: 20 Feb 2020 TEMPORARILY DISABLED CASE LIST PAGE
//        TapUI.getInstance().setSearchChatBarInRoomListVisible(false);
//        TapUI.getInstance().setNewChatButtonInRoomListVisible(false);

        TapUI.getInstance().addCustomBubble(new TTLSystemMessageBubbleClass(
                R.layout.ttl_cell_chat_system_message,
                TYPE_CLOSE_CASE, (context, message) -> {}));
        TapUI.getInstance().addCustomBubble(new TTLSystemMessageBubbleClass(
                R.layout.ttl_cell_chat_system_message,
                TYPE_REOPEN_CASE, (context, message) -> {}));
        TapUI.getInstance().addCustomBubble(new TTLReviewChatBubbleClass(
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
        }));
        TapUI.getInstance().addCustomBubble(new TTLReviewChatBubbleClass(
                R.layout.ttl_cell_chat_bubble_review,
                TYPE_REVIEW_SUBMITTED, (context, message) -> {}));

        TapUI.getInstance().addCustomKeyboardListener(new TapUICustomKeyboardListener() {
            @Override
            public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
                List<TAPCustomKeyboardItemModel> keyboardItemModelList = new ArrayList<>();
                TAPCustomKeyboardItemModel markAsSolvedCustomKeyboard = new TAPCustomKeyboardItemModel(
                        MARK_AS_SOLVED,
                        ContextCompat.getDrawable(context, R.drawable.ttl_ic_checklist_black_19),
                        MARK_AS_SOLVED
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
        });

        tapTalkLiveListener.onInitializationCompleted();
    }

    public static void authenticateTapTalkSDK(String authTicket, TapCommonListener listener) {
        if (TapTalk.isAuthenticated()) {
            listener.onSuccess("TapTalk SDK is already authenticated");
            return;
        }
        TapTalk.authenticateWithAuthTicket(authTicket, true, listener);
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
            clearAllTapLiveData();
            // TODO: 30 Jan 2020 HANDLE UI FOR FORCED LOG OUT
        }

        @Override
        public void onTapTalkUnreadChatRoomBadgeCountUpdated(int unreadCount) {

        }

        @Override
        public void onNotificationReceived(TAPMessageModel message) {
            TapTalk.showTapTalkNotification(message);
        }

        @Override
        public void onUserLogout() {

        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {

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
        if (!isTapTalkInitialized) { // TODO CALL TapTalk.checkTapTalkInitialized
            return false;
        }
        TapUI.getInstance().openRoomList(activityContext);
//        openCaseList(activityContext); // TODO: 20 Feb 2020 TEMPORARILY DISABLED CASE LIST PAGE
        if (!TTLDataManager.getInstance().checkActiveUserExists() ||
                !TTLDataManager.getInstance().checkAccessTokenAvailable() ||
                !TTLDataManager.getInstance().activeUserHasExistingCase()) {
            openCreateCaseForm(activityContext, false);
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
