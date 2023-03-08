package io.taptalk.taptalklive;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.OTHER_ERRORS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine;
import static io.taptalk.taptalklive.Const.TTLConstant.Api.API_VERSION;
import static io.taptalk.taptalklive.Const.TTLConstant.Broadcast.SCF_PATH_UPDATED;
import static io.taptalk.taptalklive.Const.TTLConstant.Extras.MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_BROADCAST_FILE_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_BROADCAST_IMAGE_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_BROADCAST_TEXT_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_BROADCAST_VIDEO_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_CLOSE_CASE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REOPEN_CASE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW_SUBMITTED;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_WABA_TEMPLATE_FILE_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_WABA_TEMPLATE_IMAGE_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_WABA_TEMPLATE_TEXT_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_WABA_TEMPLATE_VIDEO_MESSAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.RequestCode.REVIEW;
import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.stetho.Stetho;
import com.fasterxml.jackson.core.type.TypeReference;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TapLocaleManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.taptalklive.API.Api.TTLApiManager;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetProjectConfigsResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetScfPathResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestTicketResponse;
import io.taptalk.taptalklive.API.Model.TTLCaseModel;
import io.taptalk.taptalklive.API.Model.TTLScfPathModel;
import io.taptalk.taptalklive.API.Model.TTLTapTalkProjectConfigsModel;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.Activity.TTLHomeActivity;
import io.taptalk.taptalklive.Activity.TTLReviewActivity;
import io.taptalk.taptalklive.CustomBubble.TTLReviewChatBubbleClass;
import io.taptalk.taptalklive.CustomBubble.TTLSystemMessageBubbleClass;
import io.taptalk.taptalklive.Fragment.TTLCaseListFragment;
import io.taptalk.taptalklive.Listener.TTLCommonListener;
import io.taptalk.taptalklive.Listener.TapTalkLiveListener;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.helper.TTLUtil;

public class TapTalkLive {
    public static Context context;
    public static boolean isTapTalkLiveInitialized;
    private static TapTalkLive tapTalkLive;
    private static final String releaseBaseApiUrl = "https://taplive-cstd.taptalk.io/api/visitor";
    private static final String stagingBaseApiUrl = "https://taplive-cstd-stg.taptalk.io/api/visitor";
    private static final String devBaseApiUrl = "https://taplive-api-dev.taptalk.io/api/visitor";

    private static final String TAG = TapTalkLive.class.getSimpleName();
    private String clientAppName;
    //private String appKeySecret = "";
    private int clientAppIcon;
    private boolean isNeedToGetProjectConfigs;
    private boolean isTapTalkInitialized;
    private boolean isGetCaseListCompleted;
    private TTLCaseListFragment caseListFragment;
    private HashMap<String /*xcRoomID*/, TTLCaseModel> caseMap;
    public TapTalkLiveListener tapTalkLiveListener;

    /**
     * =============================================================================================
     * INITIALIZATION
     * =============================================================================================
     */

    private TapTalkLive(@NonNull final Context appContext,
                        @NonNull String appKeySecret,
                        int clientAppIcon,
                        String clientAppName,
                        @NonNull TapTalkLiveListener tapTalkLiveListener
    ) {
        tapTalkLive = this;
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
//        TTLApiManager.setApiBaseUrl(generateApiBaseUrl(TAPLIVE_SDK_BASE_URL));
        // FIXME: TAPLIVE_SDK_BASE_URL NOT FOUND IN BUILD CONFIG WHEN BUILDING JITPACK LIBRARY
        if (BuildConfig.BUILD_TYPE.equals("release")) {
            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(releaseBaseApiUrl));
        } else if (BuildConfig.BUILD_TYPE.equals("staging")) {
            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(stagingBaseApiUrl));
        } else {
            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(devBaseApiUrl));
        }

        //this.appKeySecret = appKeySecret;
        this.clientAppIcon = clientAppIcon;
        this.clientAppName = clientAppName;
        this.tapTalkLiveListener = tapTalkLiveListener;

        // Get project configs for TapTalk SDK
        TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);

        fetchScfPath();
        // TODO: TEST DUMMY DATA
        TTLScfPathModel dummyScf = TAPUtils.fromJSON(new TypeReference<>() {
        }, "{\"itemID\":35,\"pathID\":10,\"parentID\":0,\"sequence\":0,\"title\":\"Selamat datang di TapTalk.io! Apa itu OmniChannel? Selamat datang di TapTalk.io! Apa itu OmniChannel? Selamat datang di TapTalk.io! Apa itu OmniChannel? Selamat datang di TapTalk.io! Apa itu OmniChann\",\"content\":\"Omnichannel adalah platform integrasi berbagai saluran yang digabungkan menjadi satu sistem manajemen untuk meningkatkan kepuasan pelanggan dan pengalaman pelanggan.\\n\\nDalam hal customer support, omnichannel menyatukan berbagai saluran perpesanan dalam platform yang sama. Omnichannel fokus pada perjalanan pelanggan dalam berkomunikasi dengan perusahaan, sehingga kualitas pengalaman yang didapatkan menjadi lebih mulus.\\nOmnichannel adalah platform integrasi berbagai saluran yang digabungkan menjadi satu sistem manajemen untuk meningkatkan kepuasan pelanggan dan pengalaman pelanggan.\\n\\nDalam hal customer support, omnichannel menyatukan berbagai saluran perpesanan dalam platform yang sama. Omnichannel fokus pada perjalanan pelanggan dalam berkomunikasi dengan perusahaan, sehingga kualitas pengalaman yang didapatkan menjadi lebih mulus.\\nOmnichannel adalah platform integrasi berbagai saluran yang digabungkan menjadi satu sistem manajemen untuk meningkatkan kepuasan pelanggan dan pengalaman pel\",\"type\":\"qna\",\"createdTime\":1641534357255,\"updatedTime\":1669876981058,\"deletedTime\":0,\"childItems\":[{\"itemID\":50,\"pathID\":10,\"parentID\":35,\"sequence\":2,\"title\":\"How to know what's the best subscription plan for your business?\",\"content\":\"How to know what's the best subscription plan for your business?\\n\\ndi atas ini kosong\\nHow to know what's the best subscription plan for your business\\n\",\"type\":\"talk_to_agent\",\"createdTime\":1641792012566,\"updatedTime\":1642671098853,\"deletedTime\":0,\"topics\":[{\"id\":1,\"name\":\"Billing kepotong gak??\"},{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"},{\"id\":362,\"name\":\"new topic 2\"},{\"id\":361,\"name\":\"tes topic namanya panjang tes topic namanya\"}]},{\"itemID\":57,\"pathID\":10,\"parentID\":35,\"sequence\":3,\"title\":\"Why our company is the best choice for you?\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae interdum sem, et congue turpis. Donec efficitur porttitor mauris, tempor dapibus ligula commodo tempus. Suspendisse congue nibh sit amet leo ultrices dictum. Proin pretium ornare sagittis. Donec vitae neque a lectus euismod rutrum. Proin a porta risus. Nullam semper velit sapien, sed venenatis metus pulvinar et. Etiam mollis tristique erat, nec vestibulum eros molestie nec. Fusce pulvinar est non quam iaculis gravida. Nulla tincidunt vulputate diam, varius vulputate lacus lobortis vel. Sed volutpat enim in iaculis imperdiet. Nulla semper sagittis porttitor. Nam rutrum lacus ligula, eu accumsan urna aliquam et.\\n.\\n.\\n.\\nullamcorper mollis metus, volutpat porta augue sodales fermentum. Cras vel elit ac urna ultrices tempor at nec nibh. Curabitur convallis augue non accumsan mollis. In quis elit ipsum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed tristique enim nec mauris\",\"type\":\"qna\",\"createdTime\":1641803736932,\"updatedTime\":0,\"deletedTime\":0,\"childItems\":[{\"itemID\":58,\"pathID\":10,\"parentID\":57,\"sequence\":1,\"title\":\"More About Our Company\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae interdum sem, et congue turpis. Donec efficitur porttitor mauris, tempor dapibus ligula commodo tempus. Suspendisse congue nibh sit amet leo ultrices dictum. Proin pretium ornare sagittis. Donec vitae neque a lectus euismod rutrum. Proin a porta risus. Nullam semper velit sapien, sed venenatis metus pulvinar et. Etiam mollis tristique erat, nec vestibulum eros molestie nec. Fusce pulvinar est non quam iaculis gravida. Nulla tincidunt vulputate diam, varius vulputate lacus lobortis vel. Sed volutpat enim in iaculis imperdiet. Nulla semper sagittis porttitor. Nam rutrum lacus ligula, eu accumsan urna aliquam\",\"type\":\"qna\",\"createdTime\":1641803770519,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":91,\"pathID\":10,\"parentID\":57,\"sequence\":2,\"title\":\"q\",\"content\":\"b\",\"type\":\"qna\",\"createdTime\":1642138351639,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":92,\"pathID\":10,\"parentID\":57,\"sequence\":3,\"title\":\"b\",\"content\":\"b\",\"type\":\"qna\",\"createdTime\":1642138359112,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":93,\"pathID\":10,\"parentID\":57,\"sequence\":4,\"title\":\"What is OmniChannel? \",\"content\":\"What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? \",\"type\":\"qna\",\"createdTime\":1642138365487,\"updatedTime\":1655196511440,\"deletedTime\":0},{\"itemID\":94,\"pathID\":10,\"parentID\":57,\"sequence\":5,\"title\":\"asd\",\"content\":\"123\",\"type\":\"qna\",\"createdTime\":1642138378117,\"updatedTime\":0,\"deletedTime\":0}]},{\"itemID\":59,\"pathID\":10,\"parentID\":35,\"sequence\":4,\"title\":\"Services we provide:\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae interdum sem, et congue turpis. Donec efficitur porttitor mauris, tempor dapibus ligula commodo tempus. Suspendisse congue nibh sit amet leo ultrices dictum. Proin pretium ornare sagittis. Donec vitae neque a lectus euismod rutrum. Proin a porta risus. Nullam semper velit sapien, sed venenatis metus pulvinar et. Etiam mollis tristique erat, nec vestibulum eros molestie nec. Fusce pulvinar est non quam iaculis gravida. Nulla tincidunt vulputate diam, varius vulputate lacus lobortis vel. Sed volutpat enim in iaculis imperdiet. Nulla semper sagittis porttitor. Nam rutrum lacus ligula, eu accumsan urna aliquam et.\\n.\\n.\\n.\\nullamcorper mollis metus, volutpat porta augue sodales fermentum. Cras vel elit ac urna ultrices tempor at nec nibh. Curabitur convallis augue non accumsan mollis. In quis elit ipsum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed tristique enim nec mauris\",\"type\":\"talk_to_agent\",\"createdTime\":1641803843242,\"updatedTime\":0,\"deletedTime\":0,\"topics\":[{\"id\":1,\"name\":\"Billing kepotong gak??\"},{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"}]},{\"itemID\":86,\"pathID\":10,\"parentID\":35,\"sequence\":5,\"title\":\"Apa itu Chatbot? Fitur apa saja yang tersedia? \uD83C\uDD94 Apa itu Chatbot? Fitur apa saja yang tersedia? Apa itu Chatbot? Fitur apa saja yang tersedia? Apa itu Chatbot? Fitur apa saja yang tersedia? \uD83D\uDC4D\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\\n\\n!\\\"#$%\\u0026'()*+,-./:;\\u003c=\\u003e?@[\\\\]^_`{|}~ ❌\uD83C\uDFC1\uD83C\uDD94\uD83C\uDF80\uD83D\uDC4D❤️ !\\\"#$%\\u0026'()*+,-./:;\\u003c=\\u003e?@[\\\\]^_`{|}~ ä § ‡ © Æ ¥\\n\\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\",\"type\":\"talk_to_agent\",\"createdTime\":1641963693002,\"updatedTime\":1669877155294,\"deletedTime\":0,\"topics\":[{\"id\":361,\"name\":\"tes topic namanya panjang tes topic namanya\"},{\"id\":362,\"name\":\"new topic 2\"}]},{\"itemID\":89,\"pathID\":10,\"parentID\":35,\"sequence\":6,\"title\":\"title\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\",\"type\":\"qna\",\"createdTime\":1642138135467,\"updatedTime\":1669877212847,\"deletedTime\":0},{\"itemID\":101,\"pathID\":10,\"parentID\":35,\"sequence\":7,\"title\":\"child 1 talk to agent\",\"content\":\"child 1 talk to agent\",\"type\":\"qna\",\"createdTime\":1642579154210,\"updatedTime\":0,\"deletedTime\":0,\"childItems\":[{\"itemID\":102,\"pathID\":10,\"parentID\":101,\"sequence\":1,\"title\":\"child 1a talk to agent\",\"content\":\"child 1a talk to agent\",\"type\":\"qna\",\"createdTime\":1642579161459,\"updatedTime\":0,\"deletedTime\":0,\"childItems\":[{\"itemID\":103,\"pathID\":10,\"parentID\":102,\"sequence\":1,\"title\":\"child 1a-1 talk to agent\",\"content\":\"child 1a-1 talk to agent\",\"type\":\"talk_to_agent\",\"createdTime\":1642579192033,\"updatedTime\":0,\"deletedTime\":0,\"topics\":[{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"}]}]},{\"itemID\":111,\"pathID\":10,\"parentID\":101,\"sequence\":2,\"title\":\"child 1b \",\"content\":\"child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b \",\"type\":\"qna\",\"createdTime\":1655196597467,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":112,\"pathID\":10,\"parentID\":101,\"sequence\":3,\"title\":\"child 1c  \",\"content\":\"child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  \",\"type\":\"talk_to_agent\",\"createdTime\":1655196621531,\"updatedTime\":0,\"deletedTime\":0,\"topics\":[{\"id\":1,\"name\":\"Billing kepotong gak??\"},{\"id\":3,\"name\":\"Career\"},{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"},{\"id\":378,\"name\":\"Instagram\"},{\"id\":104,\"name\":\"Logistic\"},{\"id\":362,\"name\":\"new topic 2\"},{\"id\":361,\"name\":\"tes topic namanya panjang tes topic namanya\"}]},{\"itemID\":113,\"pathID\":10,\"parentID\":101,\"sequence\":4,\"title\":\"child 1d \",\"content\":\"child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d \",\"type\":\"qna\",\"createdTime\":1655196642144,\"updatedTime\":0,\"deletedTime\":0}]}]}\n");
        TTLDataManager.getInstance().saveScfPath(dummyScf);
        Intent intent = new Intent(SCF_PATH_UPDATED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static String generateApiBaseUrl(String apiBaseUrl) {
        return apiBaseUrl + "/" + API_VERSION + "/";
    }

    private final TTLDefaultDataView<TTLGetProjectConfigsResponse> projectConfigsDataView = new TTLDefaultDataView<>() {
        @Override
        public void onSuccess(TTLGetProjectConfigsResponse response) {
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
            if (null != response.getChannelLinks()) {
                TTLDataManager.getInstance().saveChannelLinks(response.getChannelLinks());
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
                TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).registerCallback(context);
                TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).addNetworkListener(networkListener);
            }
        }
    };

    private final TTLDefaultDataView<TTLGetCaseListResponse> accessTokenCaseListDataView = new TTLDefaultDataView<>() {
        @Override
        public void onSuccess(TTLGetCaseListResponse response) {
            isGetCaseListCompleted = true;
            TTLUtil.processGetCaseListResponse(response, null);
        }
    };

    private void initializeTapTalkSDK(String tapTalkAppKeyID, String tapTalkAppKeySecret, String tapTalkApiUrl) {
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

        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setReadStatusVisible(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setProfileButtonInChatRoomVisible(false);
//        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setCloseButtonInRoomListVisible(true);
//        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setMyAccountButtonInRoomListVisible(false);
//        TapUI.getInstance(TAPTALK_INSTANCE_KEY).removeRoomListListener(tapUIRoomListListener);
//        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addRoomListListener(tapUIRoomListListener);

        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(closeCaseCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reopenCaseCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reviewCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reviewSubmittedCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).removeCustomKeyboardListener(customKeyboardListener);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomKeyboardListener(customKeyboardListener);

        // Remove disabled features from chat room
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setForwardMessageMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setMentionUsernameEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setDeleteMessageMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setEditMessageMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLocationAttachmentEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setSendVoiceNoteMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setStarMessageMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setPinMessageMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setSavedMessagesMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setScheduledMessageFeatureEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setReportMessageMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setBlockUserMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setMessageInfoMenuEnabled(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_BROADCAST_TEXT_MESSAGE, TapUI.LongPressMenuType.TYPE_TEXT_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_BROADCAST_IMAGE_MESSAGE, TapUI.LongPressMenuType.TYPE_IMAGE_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_BROADCAST_VIDEO_MESSAGE, TapUI.LongPressMenuType.TYPE_VIDEO_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_BROADCAST_FILE_MESSAGE, TapUI.LongPressMenuType.TYPE_FILE_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_WABA_TEMPLATE_TEXT_MESSAGE, TapUI.LongPressMenuType.TYPE_TEXT_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_WABA_TEMPLATE_IMAGE_MESSAGE, TapUI.LongPressMenuType.TYPE_IMAGE_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_WABA_TEMPLATE_VIDEO_MESSAGE, TapUI.LongPressMenuType.TYPE_VIDEO_MESSAGE);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setLongPressMenuForMessageType(TYPE_WABA_TEMPLATE_FILE_MESSAGE, TapUI.LongPressMenuType.TYPE_FILE_MESSAGE);

        if (!TapTalk.isConnected(TAPTALK_INSTANCE_KEY)) {
            TapTalk.connect(TAPTALK_INSTANCE_KEY, new TapCommonListener() {
                @Override
                public void onSuccess(String s) {
                    isTapTalkInitialized = true;
                    if (isGetCaseListCompleted) {
                        tapTalkLiveListener.onInitializationCompleted();
                    } else {
                        getCaseList();
                    }
                }

                @Override
                public void onError(String s, String s1) {
                    isTapTalkInitialized = true;
                    if (isGetCaseListCompleted) {
                        tapTalkLiveListener.onInitializationCompleted();
                    } else {
                        getCaseList();
                    }
                }
            });
        } else {
            isTapTalkInitialized = true;
            if (isGetCaseListCompleted) {
                tapTalkLiveListener.onInitializationCompleted();
            } else {
                getCaseList();
            }
        }
    }

    private void getCaseList() {
        if (TTLDataManager.getInstance().checkActiveUserExists() && !isGetCaseListCompleted) {
            // Check if user has active case
            TTLDataManager.getInstance().getCaseList(new TTLDefaultDataView<>() {
                @Override
                public void onSuccess(TTLGetCaseListResponse response) {
                    TTLUtil.processGetCaseListResponse(response, new TapCommonListener() {
                        @Override
                        public void onSuccess(String successMessage) {
                            onFinish();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            onFinish();
                        }
                    });
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
                        isTapTalkLiveInitialized = true;
                        tapTalkLiveListener.onInitializationCompleted();
                    }
                }
            });
        } else {
            isGetCaseListCompleted = true;
            if (isTapTalkInitialized) {
                isTapTalkLiveInitialized = true;
                tapTalkLiveListener.onInitializationCompleted();
            }
        }
    }

    private void fetchScfPath() {
        TTLDataManager.getInstance().getScfPath(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLGetScfPathResponse response) {
                if (response != null && response.getItem() != null) {
                    TTLDataManager.getInstance().saveScfPath(response.getItem());
                    Intent intent = new Intent(SCF_PATH_UPDATED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        });
    }

    private final TapListener tapListener = new TapListener() {
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

    private final TapTalkNetworkInterface networkListener = new TapTalkNetworkInterface() {
        @Override
        public void onNetworkAvailable() {
            if (isNeedToGetProjectConfigs) {
                TTLDataManager.getInstance().getProjectConfigs(projectConfigsDataView);
                TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).unregisterCallback(context);
                isNeedToGetProjectConfigs = false;
            }
        }
    };

    /**
     * =============================================================================================
     * AUTHENTICATION
     * =============================================================================================
     */

    private void authenticateTapTalkSDK(String authTicket, TTLCommonListener listener) {
        if (TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
            listener.onSuccess("TapTalk SDK is already authenticated.");
            TTLDataManager.getInstance().removeTapTalkAuthTicket();
            return;
        }
        TapTalk.authenticateWithAuthTicket(TAPTALK_INSTANCE_KEY, authTicket, true, new TapCommonListener() {
            @Override
            public void onSuccess(String successMessage) {
                listener.onSuccess(successMessage);
                TTLDataManager.getInstance().removeTapTalkAuthTicket();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                listener.onError(errorCode, errorMessage);
            }
        });
    }

    // For expired refresh token
    private void requestTapTalkAuthTicket() {
        TTLDataManager.getInstance().requestTapTalkAuthTicket(requestTapTalkAuthTicketDataView);
    }

    private final TTLDefaultDataView<TTLRequestTicketResponse> requestTapTalkAuthTicketDataView = new TTLDefaultDataView<>() {
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

    private final TTLCommonListener authenticateTapTalkSDKListener = new TTLCommonListener() {
        @Override
        public void onSuccess(String s) {
            TTLDataManager.getInstance().removeTapTalkAuthTicket();
        }

        @Override
        public void onError(String s, String s1) {
            clearAllTapLiveData();
        }
    };

    public static void requestAccessToken(String authTicket, TTLCommonListener listener) {
        TTLDataManager.getInstance().saveAuthTicket(authTicket);
        requestAccessToken(listener);
    }

    private static void requestAccessToken(TTLCommonListener listener) {
        TTLDataManager.getInstance().requestAccessToken(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLRequestAccessTokenResponse response) {
                if (null != response) {
                    TTLDataManager.getInstance().removeAuthTicket();
                    TTLDataManager.getInstance().saveAccessToken(response.getAccessToken());
                    TTLDataManager.getInstance().saveRefreshToken(response.getRefreshToken());
                    TTLDataManager.getInstance().saveRefreshTokenExpiry(response.getRefreshTokenExpiry());
                    TTLDataManager.getInstance().saveAccessTokenExpiry(response.getAccessTokenExpiry());
                    TTLDataManager.getInstance().saveActiveUser(response.getUser());
                    if (tapTalkLive != null) {
                        TTLDataManager.getInstance().getCaseList(tapTalkLive.accessTokenCaseListDataView);
                    }
                    if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
                        requestTapTalkAuthTicket(listener);
                    } else {
                        listener.onSuccess(context.getString(R.string.ttl_successfully_logged_in));
                    }
                } else {
                    onError("No response when requesting access token.");
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(OTHER_ERRORS, errorMessage);
            }
        });
    }

    /**
     * =============================================================================================
     * TAP UI CUSTOMIZATION
     * =============================================================================================
     */

    private final TTLSystemMessageBubbleClass closeCaseCustomBubble = new TTLSystemMessageBubbleClass(
            R.layout.ttl_cell_chat_system_message,
            TYPE_CLOSE_CASE, (context, message) -> {}
    );

    private final TTLSystemMessageBubbleClass reopenCaseCustomBubble = new TTLSystemMessageBubbleClass(
            R.layout.ttl_cell_chat_system_message,
            TYPE_REOPEN_CASE, (context, message) -> {}
    );

    private final TTLReviewChatBubbleClass reviewCustomBubble = new TTLReviewChatBubbleClass(
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

    private final TTLReviewChatBubbleClass reviewSubmittedCustomBubble = new TTLReviewChatBubbleClass(
            R.layout.ttl_cell_chat_bubble_review,
            TYPE_REVIEW_SUBMITTED, (context, message) -> {}
    );

    private final TapUICustomKeyboardListener customKeyboardListener = new TapUICustomKeyboardListener() {
        @Override
        public List<TAPCustomKeyboardItemModel> setCustomKeyboardItems(TAPRoomModel room, TAPUserModel activeUser, @Nullable TAPUserModel recipientUser) {
            List<TAPCustomKeyboardItemModel> keyboardItemModelList = new ArrayList<>();
            TAPCustomKeyboardItemModel markAsSolvedCustomKeyboard = new TAPCustomKeyboardItemModel(
                    context.getString(R.string.ttl_mark_as_resolved),
                    ContextCompat.getDrawable(context, R.drawable.ttl_ic_checklist_black_19),
                    context.getString(R.string.ttl_mark_as_resolved)
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

//    private final TapUIRoomListListener tapUIRoomListListener = new TapUIRoomListListener() {
//        @Override
//        public void onNewChatButtonTapped(Activity activity) {
//            openCreateCaseForm(activity, true);
//        }
//
//        @Override
//        public void onCloseRoomListTapped(Activity activity) {
//            super.onCloseRoomListTapped(activity);
//            tapTalkLiveListener.onCloseButtonInCaseListTapped();
//        }
//    };

    private static void closeCase(String xcRoomID) {
        try {
            int caseID = Integer.parseInt(xcRoomID.replace("case:", ""));
            TTLDataManager.getInstance().closeCase(caseID, new TTLDefaultDataView<>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * =============================================================================================
     * PUBLIC METHODS
     * =============================================================================================
     */

    public static void init(Context context,
                            String appKeySecret,
                            int clientAppIcon,
                            String clientAppName,
                            TapTalkLiveListener tapTalkLiveListener
    ) {
        if (tapTalkLive != null) {
            tapTalkLive.isTapTalkInitialized = false;
        }
        if (!isTapTalkLiveInitialized) {
            tapTalkLive = new TapTalkLive(
                    context,
                    appKeySecret,
                    clientAppIcon,
                    clientAppName,
                    tapTalkLiveListener
            );
        } else if (tapTalkLive != null) {
            if (TTLDataManager.getInstance().checkTapTalkAppKeyIDAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkAppKeySecretAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkApiUrlAvailable()
            ) {
                tapTalkLive.initializeTapTalkSDK(
                        TTLDataManager.getInstance().getTapTalkAppKeyID(),
                        TTLDataManager.getInstance().getTapTalkAppKeySecret(),
                        TTLDataManager.getInstance().getTapTalkApiUrl()
                );
            } else {
                tapTalkLive.isNeedToGetProjectConfigs = true;
                TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).registerCallback(context);
                TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).addNetworkListener(tapTalkLive.networkListener);
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
    }

    @Nullable
    public static TapTalkLive getInstance() {
        return tapTalkLive;
    }

    public static void initializeGooglePlacesApiKey(String apiKey) {
        if (tapTalkLive == null || !tapTalkLive.isTapTalkInitialized) {
            return;
        }
        TapTalk.initializeGooglePlacesApiKey(apiKey);
    }

    public static void authenticateUser(String fullName, String email, TTLCommonListener listener) {
        TTLDataManager.getInstance().createUser(fullName, email, new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLCreateUserResponse response) {
                if (null != response) {
                    TTLDataManager.getInstance().saveActiveUser(response.getUser());
                    requestAccessToken(response.getTicket(), listener);
                } else {
                    onError("No response when registering user.");
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(OTHER_ERRORS, errorMessage);
            }
        });
    }

    // For register
    public static void requestTapTalkAuthTicket(TTLCommonListener listener) {
        TTLDataManager.getInstance().requestTapTalkAuthTicket(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLRequestTicketResponse response) {
                if (null != response) {
                    TTLDataManager.getInstance().saveTapTalkAuthTicket(response.getTicket());
                    if (tapTalkLive != null) {
                        tapTalkLive.authenticateTapTalkSDK(response.getTicket(), listener);
                    } else {
                        onError(context.getString(R.string.ttl_error_not_initialized));
                    }
                } else {
                    onError(context.getString(R.string.ttl_error_taptalk_auth_ticket_empty));
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(OTHER_ERRORS, errorMessage);
            }
        });
    }

    public static boolean openTapTalkLiveView(Context activityContext) {
        if (tapTalkLive == null || !tapTalkLive.isTapTalkInitialized) {
            return false;
        }
        TTLHomeActivity.Companion.start(activityContext);
//        if (TTLDataManager.getInstance().checkActiveUserExists() ||
//                TTLDataManager.getInstance().checkAccessTokenAvailable()
//        ) {
//            // Open case list
//            TTLCaseListActivity.Companion.start(activityContext);
//        }
//        if (!TTLDataManager.getInstance().activeUserHasExistingCase()) {
//            // Open create case form
//            TTLCreateCaseFormActivity.Companion.start(activityContext, true);
//        }
        return true;
    }

    @Nullable
    public static TTLCaseListFragment getCaseListFragment() {
        if (tapTalkLive == null) {
            return null;
        }
        if (tapTalkLive.caseListFragment == null) {
            tapTalkLive.caseListFragment = new TTLCaseListFragment();
        }
        return tapTalkLive.caseListFragment;
    }

    public static HashMap<String, TTLCaseModel> getCaseMap() {
        if (tapTalkLive == null) {
            return new HashMap<>();
        }
        if (tapTalkLive.caseMap == null) {
            tapTalkLive.caseMap = new HashMap<>();
        }
        return tapTalkLive.caseMap;
    }

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

    public static void logoutAndClearAllTapLiveData(TTLCommonListener listener) {
        //checkTapTalkInitialized();
        TTLDataManager.getInstance().logout(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLCommonResponse response) {
                clearAllTapLiveData();
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                clearAllTapLiveData();
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                clearAllTapLiveData();
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
        TapTalk.logoutAndClearAllTapTalkData(TAPTALK_INSTANCE_KEY);
    }
}
