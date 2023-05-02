package io.taptalk.taptalklive.Interface;

import android.app.Activity;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;

public interface TapTalkLiveInterface {

    void onInitializationCompleted();

    void onInitializationFailed(TTLErrorModel error);

    void onNotificationReceived(TAPMessageModel message);

    void onCloseButtonInCreateCaseFormTapped();

    void onCloseButtonInCaseListTapped();

    void onTaskRootChatRoomClosed(Activity activity);
}
