package io.taptalk.taptalklive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.API.Api.TTLApiManager;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetProjectConfigsRespone;
import io.taptalk.taptalklive.API.Model.TTLTapTalkProjectConfigsModel;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.Activity.TTLReviewActivity;
import io.taptalk.taptalklive.CustomBubble.TTLReviewChatBubbleClass;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.Manager.TTLNetworkStateManager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW;
import static io.taptalk.taptalklive.Const.TTLConstant.RequestCode.REVIEW;

public class TapTalkLive {

    public static TapTalkLive tapLive;
    public static Context context;

    private static String clientAppName;
    private static int clientAppIcon;
    private static boolean isNeedToGetProjectConfigs;

    private TapTalkLive(@NonNull final Context appContext,
                        @NonNull String tapLiveKey,
                        int clientAppIcon,
                        String clientAppName) {
        context = appContext;

        // Init Hawk for preference
        if (io.taptalk.Taptalk.BuildConfig.BUILD_TYPE.equals("dev")) {
            // No encryption for dev build
            Hawk.init(appContext).setEncryption(new NoEncryption()).build();
        } else {
            Hawk.init(appContext).build();
        }

        TapTalkLive.clientAppIcon = clientAppIcon;
        TapTalkLive.clientAppName = clientAppName;

        TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);

//        TapTalk.setLoggingEnabled(true);
//        TapTalk.init(appContext, BuildConfig.TAPTALK_SDK_APP_KEY_ID,
//                BuildConfig.TAPTALK_SDK_APP_KEY_SECRET,
//                clientAppIcon, clientAppName, BuildConfig.TAPTALK_SDK_BASE_URL,
//                TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine,
//                tapListener);
//        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);
//        TapUI.getInstance().setLogoutButtonVisible(true);
    }

    public static TapTalkLive init(Context context, int clientAppIcon, String clientAppName) {
        return tapLive == null ? (tapLive = new TapTalkLive(context, "TAP_LIVE_KEY", clientAppIcon, clientAppName)) : tapLive;
    }

    private TTLDefaultDataView<TTLGetProjectConfigsRespone> projectConfigsDataView = new TTLDefaultDataView<TTLGetProjectConfigsRespone>() {
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

    private static void initializeTapTalkSDK(String tapTalkAppKeyID, String tapTalkAppKeySecret, String tapTalkApiUrl) {
        TapTalk.setLoggingEnabled(true);
        TapTalk.init(context,
                tapTalkAppKeyID,
                tapTalkAppKeySecret,
                clientAppIcon,
                clientAppName,
                tapTalkApiUrl,
                TapTalkImplementationTypeCombine,
                tapListener);
        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);

        TapUI.getInstance().setLogoutButtonVisible(true);
        TapUI.getInstance().addCustomBubble(new TTLReviewChatBubbleClass(
                R.layout.ttl_cell_chat_bubble_review,
                TYPE_REVIEW, (context, sender) -> {
            Intent intent = new Intent(context, TTLReviewActivity.class);
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, REVIEW);
                ((Activity) context).overridePendingTransition(R.anim.tap_fade_in, R.anim.tap_stay);
            } else {
                context.startActivity(intent);
            }
        }));
    }

    private static TapListener tapListener = new TapListener() {
        @Override
        public void onTapTalkRefreshTokenExpired() {

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

    private TapTalkNetworkInterface networkListener = new TapTalkNetworkInterface() {
        @Override
        public void onNetworkAvailable() {
            if (isNeedToGetProjectConfigs) {
                TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);
                TTLNetworkStateManager.getInstance().unregisterCallback(context);
                isNeedToGetProjectConfigs = false;
            }
        }
    };

    public static void openChatRoomList(Context activityContext) {
        TapUI.getInstance().openRoomList(activityContext);
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
