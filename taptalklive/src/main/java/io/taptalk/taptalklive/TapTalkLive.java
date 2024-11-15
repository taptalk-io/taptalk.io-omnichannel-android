package io.taptalk.taptalklive;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.OTHER_ERRORS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_INIT_TAPTALK;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Helper.TapTalk.TapTalkImplementationType.TapTalkImplementationTypeCombine;
import static io.taptalk.taptalklive.Const.TTLConstant.Api.API_VERSION;
import static io.taptalk.taptalklive.Const.TTLConstant.Broadcast.SCF_PATH_UPDATED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorCode.ERROR_CODE_CONTEXT_REQUIRED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorCode.ERROR_CODE_INVALID_TOPIC_ID;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorCode.ERROR_CODE_MESSAGE_REQUIRED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorCode.ERROR_CODE_XC_ROOM_ID_REQUIRED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorMessage.ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorMessage.ERROR_MESSAGE_CONTEXT_REQUIRED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorMessage.ERROR_MESSAGE_INVALID_TOPIC_ID;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorMessage.ERROR_MESSAGE_MESSAGE_REQUIRED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorMessage.ERROR_MESSAGE_TAPTALKLIVE_NOT_INITIALIZED;
import static io.taptalk.taptalklive.Const.TTLConstant.ErrorMessage.ERROR_MESSAGE_XC_ROOM_ID_REQUIRED;
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
import static io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.FILE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.IMAGE;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.TEXT;
import static io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.VIDEO;
import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Listener.TapUICustomKeyboardListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager;
import io.taptalk.TapTalk.Manager.TapLocaleManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPCustomKeyboardItemModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.taptalklive.API.Api.TTLApiManager;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateCaseResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetProjectConfigsResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetScfPathResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestTicketResponse;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLSendMessageRequest;
import io.taptalk.taptalklive.API.Model.TTLCaseModel;
import io.taptalk.taptalklive.API.Model.TTLMessageMediaModel;
import io.taptalk.taptalklive.API.Model.TTLTapTalkProjectConfigsModel;
import io.taptalk.taptalklive.API.Model.TTLTopicModel;
import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.Activity.TTLCaseListActivity;
import io.taptalk.taptalklive.Activity.TTLCreateCaseFormActivity;
import io.taptalk.taptalklive.Activity.TTLHomeActivity;
import io.taptalk.taptalklive.Activity.TTLReviewActivity;
import io.taptalk.taptalklive.CustomBubble.TTLReviewChatBubbleClass;
import io.taptalk.taptalklive.CustomBubble.TTLSystemMessageBubbleClass;
import io.taptalk.taptalklive.Fragment.TTLCaseListFragment;
import io.taptalk.taptalklive.Listener.TTLCommonListener;
import io.taptalk.taptalklive.Listener.TTLCreateCaseListener;
import io.taptalk.taptalklive.Listener.TTLGetCaseListListener;
import io.taptalk.taptalklive.Listener.TTLGetTopicListListener;
import io.taptalk.taptalklive.Listener.TapTalkLiveListener;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.helper.TTLUtil;

public class TapTalkLive {
    public static Context context;
    public static boolean isTapTalkLiveInitialized;
    private static TapTalkLive tapTalkLive;
    private static String buildType;
    private static final String releaseBaseApiUrl = "https://taplive-cstd.taptalk.io/api/visitor";
    private static final String stagingBaseApiUrl = "https://taplive-cstd-stg.taptalk.io/api/visitor";
    private static final String devBaseApiUrl = "https://taplive-api-dev.taptalk.io/api/visitor";

    private String clientAppName;
    private int clientAppIcon;
    private boolean isTapTalkInitialized;
    private boolean isGetCaseListCompleted;
    private TTLCaseListFragment caseListFragment;
    private HashMap<String /*xcRoomID*/, TTLCaseModel> caseMap;
    private HashMap<String /*apiUrl*/, String /*contentResponse*/> contentResponseMap;
    private ArrayList<String> loadingContentResponseList;
    public TapTalkLiveListener tapTalkLiveListener;

    /**
     * =============================================================================================
     * INITIALIZATION
     * =============================================================================================
     */

    private TapTalkLive(
        @NonNull final Context appContext,
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
//        if (BuildConfig.BUILD_TYPE.equals("release")) {
//            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(releaseBaseApiUrl));
//        } else if (BuildConfig.BUILD_TYPE.equals("staging")) {
//            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(stagingBaseApiUrl));
//        } else {
//            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(devBaseApiUrl));
//        }
        if (buildType.equals("dev")) {
            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(devBaseApiUrl));
        }
        else if (buildType.equals("staging")) {
            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(stagingBaseApiUrl));
        }
        else {
            TTLApiManager.setApiBaseUrl(generateApiBaseUrl(releaseBaseApiUrl));
        }

        //this.appKeySecret = appKeySecret;
        this.clientAppIcon = clientAppIcon;
        this.clientAppName = clientAppName;
        this.tapTalkLiveListener = tapTalkLiveListener;

        // Get project configs for TapTalk SDK
        getProjectConfigs();

        fetchScfPath();

        // TODO: TEST DUMMY DATA
//        if (BuildConfig.DEBUG) {
//            TTLScfPathModel dummyScf = TAPUtils.fromJSON(new TypeReference<>() {
//            }, "{\"itemID\":35,\"pathID\":10,\"parentID\":0,\"sequence\":0,\"title\":\"Selamat datang di TapTalk.io! Apa itu OmniChannel? Selamat datang di TapTalk.io! Apa itu OmniChannel? Selamat datang di TapTalk.io! Apa itu OmniChannel? Selamat datang di TapTalk.io! Apa itu OmniChann\",\"content\":\"Omnichannel adalah platform integrasi berbagai saluran yang digabungkan menjadi satu sistem manajemen untuk meningkatkan kepuasan pelanggan dan pengalaman pelanggan.\\n\\nDalam hal customer support, omnichannel menyatukan berbagai saluran perpesanan dalam platform yang sama. Omnichannel fokus pada perjalanan pelanggan dalam berkomunikasi dengan perusahaan, sehingga kualitas pengalaman yang didapatkan menjadi lebih mulus.\\nOmnichannel adalah platform integrasi berbagai saluran yang digabungkan menjadi satu sistem manajemen untuk meningkatkan kepuasan pelanggan dan pengalaman pelanggan.\\n\\nDalam hal customer support, omnichannel menyatukan berbagai saluran perpesanan dalam platform yang sama. Omnichannel fokus pada perjalanan pelanggan dalam berkomunikasi dengan perusahaan, sehingga kualitas pengalaman yang didapatkan menjadi lebih mulus.\\nOmnichannel adalah platform integrasi berbagai saluran yang digabungkan menjadi satu sistem manajemen untuk meningkatkan kepuasan pelanggan dan pengalaman pel\",\"type\":\"qna\",\"createdTime\":1641534357255,\"updatedTime\":1669876981058,\"deletedTime\":0,\"childItems\":[{\"itemID\":50,\"pathID\":10,\"parentID\":35,\"sequence\":2,\"title\":\"How to know what's the best subscription plan for your business?\",\"content\":\"How to know what's the best subscription plan for your business?\\n\\ndi atas ini kosong\\nHow to know what's the best subscription plan for your business\\n\",\"type\":\"talk_to_agent\",\"createdTime\":1641792012566,\"updatedTime\":1642671098853,\"deletedTime\":0,\"topics\":[{\"id\":1,\"name\":\"Billing kepotong gak??\"},{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"},{\"id\":362,\"name\":\"new topic 2\"},{\"id\":361,\"name\":\"tes topic namanya panjang tes topic namanya\"}]},{\"itemID\":57,\"pathID\":10,\"parentID\":35,\"sequence\":3,\"title\":\"Why our company is the best choice for you?\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae interdum sem, et congue turpis. Donec efficitur porttitor mauris, tempor dapibus ligula commodo tempus. Suspendisse congue nibh sit amet leo ultrices dictum. Proin pretium ornare sagittis. Donec vitae neque a lectus euismod rutrum. Proin a porta risus. Nullam semper velit sapien, sed venenatis metus pulvinar et. Etiam mollis tristique erat, nec vestibulum eros molestie nec. Fusce pulvinar est non quam iaculis gravida. Nulla tincidunt vulputate diam, varius vulputate lacus lobortis vel. Sed volutpat enim in iaculis imperdiet. Nulla semper sagittis porttitor. Nam rutrum lacus ligula, eu accumsan urna aliquam et.\\n.\\n.\\n.\\nullamcorper mollis metus, volutpat porta augue sodales fermentum. Cras vel elit ac urna ultrices tempor at nec nibh. Curabitur convallis augue non accumsan mollis. In quis elit ipsum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed tristique enim nec mauris\",\"type\":\"qna\",\"createdTime\":1641803736932,\"updatedTime\":0,\"deletedTime\":0,\"childItems\":[{\"itemID\":58,\"pathID\":10,\"parentID\":57,\"sequence\":1,\"title\":\"More About Our Company\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae interdum sem, et congue turpis. Donec efficitur porttitor mauris, tempor dapibus ligula commodo tempus. Suspendisse congue nibh sit amet leo ultrices dictum. Proin pretium ornare sagittis. Donec vitae neque a lectus euismod rutrum. Proin a porta risus. Nullam semper velit sapien, sed venenatis metus pulvinar et. Etiam mollis tristique erat, nec vestibulum eros molestie nec. Fusce pulvinar est non quam iaculis gravida. Nulla tincidunt vulputate diam, varius vulputate lacus lobortis vel. Sed volutpat enim in iaculis imperdiet. Nulla semper sagittis porttitor. Nam rutrum lacus ligula, eu accumsan urna aliquam\",\"type\":\"qna\",\"createdTime\":1641803770519,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":91,\"pathID\":10,\"parentID\":57,\"sequence\":2,\"title\":\"q\",\"content\":\"b\",\"type\":\"qna\",\"createdTime\":1642138351639,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":92,\"pathID\":10,\"parentID\":57,\"sequence\":3,\"title\":\"b\",\"content\":\"b\",\"type\":\"qna\",\"createdTime\":1642138359112,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":93,\"pathID\":10,\"parentID\":57,\"sequence\":4,\"title\":\"What is OmniChannel? \",\"content\":\"What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? What is OmniChannel? \",\"type\":\"qna\",\"createdTime\":1642138365487,\"updatedTime\":1655196511440,\"deletedTime\":0},{\"itemID\":94,\"pathID\":10,\"parentID\":57,\"sequence\":5,\"title\":\"asd\",\"content\":\"123\",\"type\":\"qna\",\"createdTime\":1642138378117,\"updatedTime\":0,\"deletedTime\":0}]},{\"itemID\":59,\"pathID\":10,\"parentID\":35,\"sequence\":4,\"title\":\"Services we provide:\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae interdum sem, et congue turpis. Donec efficitur porttitor mauris, tempor dapibus ligula commodo tempus. Suspendisse congue nibh sit amet leo ultrices dictum. Proin pretium ornare sagittis. Donec vitae neque a lectus euismod rutrum. Proin a porta risus. Nullam semper velit sapien, sed venenatis metus pulvinar et. Etiam mollis tristique erat, nec vestibulum eros molestie nec. Fusce pulvinar est non quam iaculis gravida. Nulla tincidunt vulputate diam, varius vulputate lacus lobortis vel. Sed volutpat enim in iaculis imperdiet. Nulla semper sagittis porttitor. Nam rutrum lacus ligula, eu accumsan urna aliquam et.\\n.\\n.\\n.\\nullamcorper mollis metus, volutpat porta augue sodales fermentum. Cras vel elit ac urna ultrices tempor at nec nibh. Curabitur convallis augue non accumsan mollis. In quis elit ipsum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed tristique enim nec mauris\",\"type\":\"talk_to_agent\",\"createdTime\":1641803843242,\"updatedTime\":0,\"deletedTime\":0,\"topics\":[{\"id\":1,\"name\":\"Billing kepotong gak??\"},{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"}]},{\"itemID\":86,\"pathID\":10,\"parentID\":35,\"sequence\":5,\"title\":\"Apa itu Chatbot? Fitur apa saja yang tersedia? \uD83C\uDD94 Apa itu Chatbot? Fitur apa saja yang tersedia? Apa itu Chatbot? Fitur apa saja yang tersedia? Apa itu Chatbot? Fitur apa saja yang tersedia? \uD83D\uDC4D\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\\n\\n!\\\"#$%\\u0026'()*+,-./:;\\u003c=\\u003e?@[\\\\]^_`{|}~ ❌\uD83C\uDFC1\uD83C\uDD94\uD83C\uDF80\uD83D\uDC4D❤️ !\\\"#$%\\u0026'()*+,-./:;\\u003c=\\u003e?@[\\\\]^_`{|}~ ä § ‡ © Æ ¥\\n\\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\",\"type\":\"talk_to_agent\",\"createdTime\":1641963693002,\"updatedTime\":1669877155294,\"deletedTime\":0,\"topics\":[{\"id\":361,\"name\":\"tes topic namanya panjang tes topic namanya\"},{\"id\":362,\"name\":\"new topic 2\"}]},{\"itemID\":89,\"pathID\":10,\"parentID\":35,\"sequence\":6,\"title\":\"title\",\"content\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\",\"type\":\"qna\",\"createdTime\":1642138135467,\"updatedTime\":1669877212847,\"deletedTime\":0},{\"itemID\":101,\"pathID\":10,\"parentID\":35,\"sequence\":7,\"title\":\"child 1 talk to agent\",\"content\":\"child 1 talk to agent\",\"type\":\"qna\",\"createdTime\":1642579154210,\"updatedTime\":0,\"deletedTime\":0,\"childItems\":[{\"itemID\":102,\"pathID\":10,\"parentID\":101,\"sequence\":1,\"title\":\"child 1a talk to agent\",\"content\":\"child 1a talk to agent\",\"type\":\"qna\",\"createdTime\":1642579161459,\"updatedTime\":0,\"deletedTime\":0,\"childItems\":[{\"itemID\":103,\"pathID\":10,\"parentID\":102,\"sequence\":1,\"title\":\"child 1a-1 talk to agent\",\"content\":\"child 1a-1 talk to agent\",\"type\":\"talk_to_agent\",\"createdTime\":1642579192033,\"updatedTime\":0,\"deletedTime\":0,\"topics\":[{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"}]}]},{\"itemID\":111,\"pathID\":10,\"parentID\":101,\"sequence\":2,\"title\":\"child 1b \",\"content\":\"child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b child 1b \",\"type\":\"qna\",\"createdTime\":1655196597467,\"updatedTime\":0,\"deletedTime\":0},{\"itemID\":112,\"pathID\":10,\"parentID\":101,\"sequence\":3,\"title\":\"child 1c  \",\"content\":\"child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  child 1c  \",\"type\":\"talk_to_agent\",\"createdTime\":1655196621531,\"updatedTime\":0,\"deletedTime\":0,\"topics\":[{\"id\":1,\"name\":\"Billing kepotong gak??\"},{\"id\":3,\"name\":\"Career\"},{\"id\":96,\"name\":\"General yang panjanggggg yaaa coba dehhhhhhh\"},{\"id\":378,\"name\":\"Instagram\"},{\"id\":104,\"name\":\"Logistic\"},{\"id\":362,\"name\":\"new topic 2\"},{\"id\":361,\"name\":\"tes topic namanya panjang tes topic namanya\"}]},{\"itemID\":113,\"pathID\":10,\"parentID\":101,\"sequence\":4,\"title\":\"child 1d \",\"content\":\"child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d child 1d \",\"type\":\"qna\",\"createdTime\":1655196642144,\"updatedTime\":0,\"deletedTime\":0}]}]}\n");
//            TTLDataManager.getInstance().saveScfPath(dummyScf);
//            Intent intent = new Intent(SCF_PATH_UPDATED);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//        }
    }

    private static String generateApiBaseUrl(String apiBaseUrl) {
        return apiBaseUrl + "/" + API_VERSION + "/";
    }

    private void getProjectConfigs() {
        TTLDataManager.getInstance().getProjectConfigs(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLGetProjectConfigsResponse response) {
                TTLTapTalkProjectConfigsModel tapTalk = response.getTapTalkProjectConfigs();
                if (null != tapTalk) {
                    initializeTapTalkSDK(
                        tapTalk.getAppKeyID(),
                        tapTalk.getAppKeySecret(),
                        tapTalk.getApiURL()
                    );
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
                if (TTLDataManager.getInstance().checkTapTalkAppKeyIDAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkAppKeySecretAvailable() &&
                    TTLDataManager.getInstance().checkTapTalkApiUrlAvailable()
                ) {
                    initializeTapTalkSDK(
                        TTLDataManager.getInstance().getTapTalkAppKeyID(),
                        TTLDataManager.getInstance().getTapTalkAppKeySecret(),
                        TTLDataManager.getInstance().getTapTalkApiUrl()
                    );
                }
                else {
                    tapTalkLiveListener.onInitializationFailed(error);
                }
            }

            @Override
            public void onError(String errorMessage) {
                onError(new TTLErrorModel(ERROR_CODE_OTHERS, errorMessage, ""));
            }
        });
    }

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
            tapListener
        );

        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(closeCaseCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reopenCaseCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reviewCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomBubble(reviewSubmittedCustomBubble);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).removeCustomKeyboardListener(customKeyboardListener);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).addCustomKeyboardListener(customKeyboardListener);

        // Remove disabled features from chat room
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setReadStatusVisible(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setProfileButtonInChatRoomVisible(false);
        TapUI.getInstance(TAPTALK_INSTANCE_KEY).setReplyMessageMenuEnabled(true);
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

        // Disable send message by default
        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).setSendMessageDisabled(true);
        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).removeChatListener(chatListener);
        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).addChatListener(chatListener);

        if (!TapTalk.isConnected(TAPTALK_INSTANCE_KEY)) {
            TapTalk.connect(TAPTALK_INSTANCE_KEY, new TapCommonListener() {
                @Override
                public void onSuccess(String s) {
                    onTapTalkInitializationCompleted();
                }

                @Override
                public void onError(String s, String s1) {
                    onTapTalkInitializationCompleted();
                }
            });
        }
        else {
            onTapTalkInitializationCompleted();
        }
    }

    private TAPChatListener chatListener = new TAPChatListener() {
        @Override
        public void onSendMessagePending(TAPMessageModel message) {
            if (message == null || message.getRoom() == null) {
                return;
            }
            TTLCaseModel caseModel = TapTalkLive.getCaseMap().get(message.getRoom().getXcRoomID());
            if (caseModel == null) {
                return;
            }
            if (TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(context)) {
                String replyToLocalID = null != message.getReplyTo() ? message.getReplyTo().getLocalID() : null;
                String messageType;
                if (message.getType() == TYPE_IMAGE) {
                    messageType = IMAGE;
                }
                else if (message.getType() == TYPE_VIDEO) {
                    messageType = VIDEO;
                }
                else if (message.getType() == TYPE_FILE) {
                    messageType = FILE;
                }
                else {
                    messageType = TEXT;
                }
                TTLMessageMediaModel media = null;
                if (!messageType.equals(TEXT) && message.getData() != null) {
                    String fileUrl = (String) message.getData().get(FILE_URL);
                    if (fileUrl == null || fileUrl.isEmpty()) {
                        fileUrl = (String) message.getData().get("fileURL");
                    }
                    String caption = (String) message.getData().get(CAPTION);
                    String fileName = (String) message.getData().get(FILE_NAME);
                    media = new TTLMessageMediaModel(
                        fileUrl,
                        caption,
                        fileName
                    );
                }
                TTLSendMessageRequest request = new TTLSendMessageRequest(
                    caseModel.getId(),
                    caseModel.getCreatedTime(),
                    message.getLocalID(),
                    replyToLocalID,
                    messageType,
                    message.getBody(),
                    messageType.equals(IMAGE) ? media : null,
                    messageType.equals(VIDEO) ? media : null,
                    messageType.equals(FILE) ? media : null
                );
                TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).putWaitingForResponseMessage(message);
                TTLDataManager.getInstance().sendMessage(request, new TTLDefaultDataView<>() {
                    @Override
                    public void onError(TTLErrorModel error) {
                        onError(error.getMessage());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        handleSendMessageError(message, errorMessage);
                    }
                });
            }
            else {
                handleSendMessageError(message, context.getString(R.string.ttl_error_message_offline));
            }
        }

        private void handleSendMessageError(TAPMessageModel message, String errorMessage) {
            TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).putPendingMessage(message);
            Toast.makeText(context, "Unable to send message: " + errorMessage, Toast.LENGTH_SHORT).show();
        }
    };

    private void onTapTalkInitializationCompleted() {
        isTapTalkInitialized = true;
        if (isGetCaseListCompleted) {
            tapTalkLiveListener.onInitializationCompleted();
        }
        else if (TTLDataManager.getInstance().checkActiveUserExists() && !isGetCaseListCompleted) {
            // Check if user has active case
            getCaseList(true, null);
        }
        else {
            onGetCaseListCompleted();
        }
    }

    private void getCaseList(boolean triggerCompletion, TTLGetCaseListListener listener) {
        TTLDataManager.getInstance().getCaseList(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLGetCaseListResponse response) {
                if (null != response) {
                    TTLUtil.processGetCaseListResponse(response, new TapCommonListener() {
                        @Override
                        public void onSuccess(String successMessage) {
                            onFinish();
                            if (null != listener) {
                                listener.onSuccess(response.getCases());
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            onFinish();
                            if (null != listener) {
                                listener.onSuccess(response.getCases());
                            }
                        }
                    });
                }
                else {
                    onError("No response when fetching cases");
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                onFinish();
                if (null != listener) {
                    String errorMessage;
                    if (TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(context)) {
                        errorMessage = error.getMessage();
                    }
                    else {
                        errorMessage = context.getString(io.taptalk.TapTalk.R.string.tap_no_internet_show_error);
                    }
                    listener.onError(error.getCode(), errorMessage);
                }
            }

            @Override
            public void onError(String errorMessage) {
                onError(new TTLErrorModel(ERROR_CODE_OTHERS, errorMessage, ""));
            }

            private void onFinish() {
                if (triggerCompletion) {
                    onGetCaseListCompleted();
                }
            }
        });
    }

    private void onGetCaseListCompleted() {
        isGetCaseListCompleted = true;
        if (isTapTalkInitialized) {
            isTapTalkLiveInitialized = true;
            tapTalkLiveListener.onInitializationCompleted();
        }
    }

    private void fetchScfPath() {
        TTLDataManager.getInstance().getScfPath(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLGetScfPathResponse response) {
                if (response != null/* && response.getItem() != null*/) {
                    TTLDataManager.getInstance().saveScfPath(response.getItem());
                    Intent intent = new Intent(SCF_PATH_UPDATED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                if (null != error.getCode() &&
                    null != error.getMessage() &&
                    error.getCode().equals("40401") &&
                    error.getMessage().contains("not set")
                ) {
                    // SCF Path is empty
                    TTLDataManager.getInstance().saveScfPath(null);
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
        public void onNotificationReceived(TAPMessageModel message) {
            tapTalkLiveListener.onNotificationReceived(message);
        }

        @Override
        public void onTaskRootChatRoomClosed(Activity activity) {
            tapTalkLiveListener.onTaskRootChatRoomClosed(activity);
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
                clearUserData();
            }
        }

        @Override
        public void onError(TTLErrorModel error) {
            clearUserData();
        }

        @Override
        public void onError(String errorMessage) {
            clearUserData();
        }
    };

    private final TTLCommonListener authenticateTapTalkSDKListener = new TTLCommonListener() {
        @Override
        public void onSuccess(String s) {
            TTLDataManager.getInstance().removeTapTalkAuthTicket();
        }

        @Override
        public void onError(String s, String s1) {
            clearUserData();
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
                        TTLDataManager.getInstance().getCaseList(new TTLDefaultDataView<>() {
                            @Override
                            public void onSuccess(TTLGetCaseListResponse response) {
                                TTLUtil.processGetCaseListResponse(response, null);
                                tapTalkLive.isGetCaseListCompleted = true;
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
                        });
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

            private void onFinish() {
                if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
                    requestTapTalkAuthTicket(listener);
                } else {
                    listener.onSuccess(context.getString(R.string.ttl_successfully_logged_in));
                }
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
            TYPE_REVIEW,
            TTLReviewActivity.Companion::start
    );

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
                    .setCancelable(true)
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

    public static void init(
        Context context,
        String appKeySecret,
        int clientAppIcon,
        String clientAppName,
        TapTalkLiveListener tapTalkLiveListener
    ) {
        init(context, appKeySecret, clientAppIcon, clientAppName, tapTalkLiveListener, "");
    }

    public static void init(
        Context context,
        String appKeySecret,
        int clientAppIcon,
        String clientAppName,
        TapTalkLiveListener tapTalkLiveListener,
        String buildTypeParameter
    ) {
        if (tapTalkLive != null) {
            tapTalkLive.isTapTalkInitialized = false;
        }
        if (buildTypeParameter == null || buildTypeParameter.isEmpty()) {
            buildType = BuildConfig.BUILD_TYPE;
        }
        else {
            buildType = buildTypeParameter;
        }
        if (!isTapTalkLiveInitialized) {
            tapTalkLive = new TapTalkLive(
                context,
                appKeySecret,
                clientAppIcon,
                clientAppName,
                tapTalkLiveListener
            );
        }
        else if (tapTalkLive != null) {
            if (TTLDataManager.getInstance().checkTapTalkAppKeyIDAvailable() &&
                TTLDataManager.getInstance().checkTapTalkAppKeySecretAvailable() &&
                TTLDataManager.getInstance().checkTapTalkApiUrlAvailable()
            ) {
                tapTalkLive.initializeTapTalkSDK(
                    TTLDataManager.getInstance().getTapTalkAppKeyID(),
                    TTLDataManager.getInstance().getTapTalkAppKeySecret(),
                    TTLDataManager.getInstance().getTapTalkApiUrl()
                );
            }
            else {
                tapTalkLive.getProjectConfigs();
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

    public static void getTopicList(TTLGetTopicListListener listener) {
        if (null == tapTalkLive || !tapTalkLive.isTapTalkInitialized) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_TAPTALKLIVE_NOT_INITIALIZED);
            }
            return;
        }
        TTLDataManager.getInstance().getTopicList(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLGetTopicListResponse response) {
                if (null != response) {
                    TTLDataManager.getInstance().saveTopics(response.getTopics());
                    if (null != listener) {
                        listener.onSuccess(response.getTopics());
                    }
                }
                else {
                    onError("No response when fetching topics");
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                List<TTLTopicModel> topics = TTLDataManager.getInstance().getTopics();
                if (null != listener) {
                    if (null != topics) {
                        listener.onSuccess(topics);
                    }
                    else {
                        String errorMessage;
                        if (TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(context)) {
                            errorMessage = error.getMessage();
                        }
                        else {
                            errorMessage = context.getString(io.taptalk.TapTalk.R.string.tap_no_internet_show_error);
                        }
                        listener.onError(error.getCode(), errorMessage);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                onError(new TTLErrorModel(ERROR_CODE_OTHERS, errorMessage, ""));
            }
        });
    }

    public static void getUserCaseList(TTLGetCaseListListener listener) {
        if (null == tapTalkLive || !tapTalkLive.isTapTalkInitialized) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_TAPTALKLIVE_NOT_INITIALIZED);
            }
            return;
        }
        if (!TTLDataManager.getInstance().checkActiveUserExists()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
            return;
        }
        tapTalkLive.getCaseList(false, listener);
    }

    public static void createNewCase(int topicID, String firstMessage, TTLCreateCaseListener listener) {
        if (topicID <= 0) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INVALID_TOPIC_ID, ERROR_MESSAGE_INVALID_TOPIC_ID);
            }
            return;
        }
        if (null == firstMessage || firstMessage.isEmpty()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_MESSAGE_REQUIRED, ERROR_MESSAGE_MESSAGE_REQUIRED);
            }
            return;
        }
        if (null == tapTalkLive || !tapTalkLive.isTapTalkInitialized) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_TAPTALKLIVE_NOT_INITIALIZED);
            }
            return;
        }
        if (!TTLDataManager.getInstance().checkActiveUserExists()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
            return;
        }
        if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
            TapTalkLive.requestTapTalkAuthTicket(new TTLCommonListener() {
                @Override
                public void onSuccess(String successMessage) {
                    createCase(topicID, firstMessage, listener);
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    if (null != listener) {
                        listener.onError(errorCode, errorMessage);
                    }
                }
            });
        }
        else {
            createCase(topicID, firstMessage, listener);
        }
    }

    private static void createCase(int topicID, String firstMessage, TTLCreateCaseListener listener) {
        TTLDataManager.getInstance().createCase(topicID, firstMessage, new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLCreateCaseResponse response) {
                if (null != response && null != response.getCaseResponse()) {
                    TTLCaseModel caseModel = response.getCaseResponse();
                    getCaseMap().put(caseModel.getTapTalkXCRoomID(), caseModel);
                    TTLDataManager.getInstance().saveActiveUserHasExistingCase(true);
                    if (null != listener) {
                        listener.onSuccess(caseModel);
                    }
                }
                else {
                    onError("No response when creating case");
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                if (null != listener) {
                    String errorMessage;
                    if (TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(context)) {
                        errorMessage = error.getMessage();
                    }
                    else {
                        errorMessage = context.getString(io.taptalk.TapTalk.R.string.tap_no_internet_show_error);
                    }
                    listener.onError(error.getCode(), errorMessage);
                }
            }

            @Override
            public void onError(String errorMessage) {
                onError(new TTLErrorModel(ERROR_CODE_OTHERS, errorMessage, ""));
            }
        });
    }

    public static void openCaseChatRoom(Context context, String tapTalkXCRoomID, TTLCommonListener listener) {
        if (null == context) {
            if (null != listener) {
                listener.onError(ERROR_CODE_CONTEXT_REQUIRED, ERROR_MESSAGE_CONTEXT_REQUIRED);
            }
            return;
        }
        if (null == tapTalkXCRoomID || tapTalkXCRoomID.isEmpty()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_XC_ROOM_ID_REQUIRED, ERROR_MESSAGE_XC_ROOM_ID_REQUIRED);
            }
            return;
        }
        if (null == tapTalkLive || !tapTalkLive.isTapTalkInitialized) {
            if (null != listener) {
                listener.onError(ERROR_CODE_INIT_TAPTALK, ERROR_MESSAGE_TAPTALKLIVE_NOT_INITIALIZED);
            }
            return;
        }
        if (!TTLDataManager.getInstance().checkActiveUserExists()) {
            if (null != listener) {
                listener.onError(ERROR_CODE_ACTIVE_USER_NOT_FOUND, ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND);
            }
            return;
        }
        if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
            TapTalkLive.requestTapTalkAuthTicket(new TTLCommonListener() {
                @Override
                public void onSuccess(String successMessage) {
                    openChatRoom(context, tapTalkXCRoomID, listener);
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    if (null != listener) {
                        listener.onError(errorCode, errorMessage);
                    }
                }
            });
        }
        else {
            openChatRoom(context, tapTalkXCRoomID, listener);
        }
    }

    private static void openChatRoom(Context context, String tapTalkXCRoomID, TTLCommonListener listener) {
        TapCoreChatRoomManager.getInstance(TAPTALK_INSTANCE_KEY).getChatRoomByXcRoomID(tapTalkXCRoomID, new TapCoreGetRoomListener() {
            @Override
            public void onSuccess(TAPRoomModel room) {
                if (TapTalk.isConnected(TAPTALK_INSTANCE_KEY)) {
                    openChatRoom(room);
                }
                else {
                    TapTalk.connect(TAPTALK_INSTANCE_KEY, new TapCommonListener() {
                        @Override
                        public void onSuccess(String successMessage) {
                            openChatRoom(room);
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            openChatRoom(room);
                        }
                    });
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                if (null != listener) {
                    String message;
                    if (TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(context)) {
                        message = errorMessage;
                    }
                    else {
                        message = context.getString(io.taptalk.TapTalk.R.string.tap_no_internet_show_error);
                    }
                    listener.onError(errorCode, message);
                }
            }

            private void openChatRoom(TAPRoomModel room) {
                TapUI.getInstance(TAPTALK_INSTANCE_KEY).openChatRoomWithRoomModel(context, room);
                if (null != listener) {
                    listener.onSuccess("Successfully opened chat room");
                }
            }
        });
    }

    public static boolean openTapTalkLiveView(Context activityContext) {
        if (tapTalkLive == null || !tapTalkLive.isTapTalkInitialized || activityContext == null) {
            return false;
        }
        TTLHomeActivity.Companion.start(activityContext);
        return true;
    }

    public static boolean openCaseListView(Context activityContext) {
        if (tapTalkLive == null || !tapTalkLive.isTapTalkInitialized || activityContext == null) {
            return false;
        }
        if (TTLDataManager.getInstance().checkActiveUserExists() ||
            TTLDataManager.getInstance().checkAccessTokenAvailable()
        ) {
            // Open case list
            TTLCaseListActivity.Companion.start(activityContext);
        }
        if (!TTLDataManager.getInstance().activeUserHasExistingCase()) {
            // Open create case form
            TTLCreateCaseFormActivity.Companion.start(activityContext, true);
        }
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

    public static HashMap<String, String> getContentResponseMap() {
        if (tapTalkLive == null) {
            return new HashMap<>();
        }
        if (tapTalkLive.contentResponseMap == null) {
            tapTalkLive.contentResponseMap = new HashMap<>();
        }
        return tapTalkLive.contentResponseMap;
    }

    public static ArrayList<String> getLoadingContentResponseList() {
        if (tapTalkLive == null) {
            return new ArrayList<>();
        }
        if (tapTalkLive.loadingContentResponseList == null) {
            tapTalkLive.loadingContentResponseList = new ArrayList<>();
        }
        return tapTalkLive.loadingContentResponseList;
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

    public static void logout(TTLCommonListener listener) {
        TTLDataManager.getInstance().logout(new TTLDefaultDataView<>() {
            @Override
            public void onSuccess(TTLCommonResponse response) {
                onFinish();
                if (null != listener) {
                    listener.onSuccess(response.getMessage());
                }
            }

            @Override
            public void onError(TTLErrorModel error) {
                onError(error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                onFinish();
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }

            private void onFinish() {
                clearUserData();
            }
        });
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
                onError(error.getMessage());
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

    public static void clearUserData() {
        TTLDataManager.getInstance().deleteUserPreferences();
        TTLApiManager.getInstance().setLoggedOut(true);
        TapTalk.logoutAndClearAllTapTalkData(TAPTALK_INSTANCE_KEY);
        if (tapTalkLive != null) {
            tapTalkLive.isTapTalkInitialized = false;
            tapTalkLive.isGetCaseListCompleted = false;
        }
    }

    public static void clearAllTapLiveData() {
        //checkTapTalkInitialized();
        tapTalkLive = null;
        isTapTalkLiveInitialized = false;
        TTLDataManager.getInstance().deleteAllPreference();
        TTLApiManager.getInstance().setLoggedOut(true);
        TapTalk.logoutAndClearAllTapTalkData(TAPTALK_INSTANCE_KEY);
    }
}
