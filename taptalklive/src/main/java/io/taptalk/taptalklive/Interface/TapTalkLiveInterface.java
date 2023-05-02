package io.taptalk.taptalklive.Interface;

import android.app.Activity;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.TTLScfPathModel;
import io.taptalk.taptalklive.model.TTLCaseListModel;

public interface TapTalkLiveInterface {

    void onInitializationCompleted();

    void onInitializationFailed(TTLErrorModel error);

    void onNotificationReceived(TAPMessageModel message);

    void onCloseButtonInHomePageTapped(Activity activity);

    void onCloseButtonInCreateCaseFormTapped(Activity activity);

    void onCloseButtonInCaseListTapped(Activity activity);

    void onTaskRootChatRoomClosed(Activity activity);

    void onSeeAllMessagesButtonTapped(Activity activity);

    void onCreateNewMessageButtonTapped(Activity activity);

    void onCaseListItemTapped(Activity activity, TTLCaseListModel caseListModel);

    void onFaqChildTapped(Activity activity, TTLScfPathModel scfPath);

    void onCloseButtonInFaqDetailsTapped(Activity activity, TTLScfPathModel scfPath);

    void onTalkToAgentButtonTapped (Activity activity, TTLScfPathModel scfPath);
}
