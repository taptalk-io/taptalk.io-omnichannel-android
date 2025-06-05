package io.taptalk.taptalklive.Interface;

import android.app.Activity;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.TTLScfPathModel;

public interface TapTalkLiveInterface {

    void onInitializationCompleted();

    void onInitializationFailed(TTLErrorModel error);

    void onTapTalkLiveRefreshTokenExpired();

    void onNotificationReceived(TAPMessageModel message);

    void onCloseButtonInHomePageTapped(Activity activity);

    void onCloseButtonInCreateCaseFormTapped(Activity activity);

    void onCloseButtonInCaseListTapped(Activity activity);

    void onTaskRootChatRoomClosed(Activity activity);

    void onSeeAllMessagesButtonTapped(Activity activity);

    void onCreateNewMessageButtonTapped(Activity activity);

    void onCaseListItemTapped(Activity activity, TAPMessageModel lastMessage);

    void onFaqChildTapped(Activity activity, TTLScfPathModel scfPath);

    void onCloseButtonInFaqDetailsTapped(Activity activity, TTLScfPathModel scfPath);

    void onTalkToAgentButtonTapped (Activity activity, TTLScfPathModel scfPath);

    void onFaqContentUrlTapped (Activity activity, TTLScfPathModel scfPath, String url);

    void onFaqContentUrlLongPressed (Activity activity, TTLScfPathModel scfPath, String url);

    void onFaqContentEmailAddressTapped(Activity activity, TTLScfPathModel scfPath, String email);

    void onFaqContentEmailAddressLongPressed(Activity activity, TTLScfPathModel scfPath, String email);

    void onFaqContentPhoneNumberTapped(Activity activity, TTLScfPathModel scfPath, String phone);

    void onFaqContentPhoneNumberLongPressed(Activity activity, TTLScfPathModel scfPath, String phone);
}
