package io.taptalk.taptalklive;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.CustomBubble.TTLReviewChatBubbleClass;

public class TapTalkLive {
    public static TapTalkLive tapLive;
    public static Context context;

    private TapTalkLive(@NonNull final Context appContext,
                        @NonNull String tapLiveKey,
                        int clientAppIcon,
                        String clientAppName) {
        context = appContext;
        TapTalk.setLoggingEnabled(true);
        TapTalk.init(appContext, BuildConfig.TAPTALK_SDK_APP_KEY_ID,
                BuildConfig.TAPTALK_SDK_APP_KEY_SECRET,
                clientAppIcon, clientAppName, BuildConfig.TAPTALK_SDK_BASE_URL,
                TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine,
                tapListener);
        TapTalk.initializeGooglePlacesApiKey(BuildConfig.GOOGLE_MAPS_API_KEY);
        TapUI.getInstance().setLogoutButtonVisible(true);

        TapUI.getInstance().addCustomBubble(new TTLReviewChatBubbleClass(
                R.layout.ttl_cell_chat_bubble_review,
                3001, (context, sender) -> {
            // TODO: 7 Jan 2020 OPEN REVIEW LAYOUT
        }));
    }

    public static TapTalkLive init(Context context, int clientAppIcon, String clientAppName) {
        return tapLive == null ? (tapLive = new TapTalkLive(context, "TAP_LIVE_KEY", clientAppIcon, clientAppName)) : tapLive;
    }

    public static void openChatRoomList(Context activityContext) {
        TapUI.getInstance().openRoomList(activityContext);
    }

    TapListener tapListener = new TapListener() {
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
}
