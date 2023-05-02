package io.taptalk.taptalklive.Listener;

import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import android.app.Activity;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.Interface.TapTalkLiveInterface;

public abstract class TapTalkLiveListener implements TapTalkLiveInterface {
    @Override
    public void onInitializationCompleted() {

    }

    @Override
    public void onInitializationFailed(TTLErrorModel error) {

    }

    @Override
    public void onNotificationReceived(TAPMessageModel message) {
        TapTalk.showTapTalkNotification(TAPTALK_INSTANCE_KEY, message);
    }

    @Override
    public void onCloseButtonInCreateCaseFormTapped() {

    }

    @Override
    public void onCloseButtonInCaseListTapped() {

    }

    @Override
    public void onTaskRootChatRoomClosed(Activity activity) {

    }
}
