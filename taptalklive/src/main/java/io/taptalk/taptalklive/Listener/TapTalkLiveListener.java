package io.taptalk.taptalklive.Listener;

import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import android.app.Activity;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import io.taptalk.taptalklive.API.Model.TTLScfPathModel;
import io.taptalk.taptalklive.Activity.TTLCaseListActivity;
import io.taptalk.taptalklive.Activity.TTLCreateCaseFormActivity;
import io.taptalk.taptalklive.Activity.TTLFaqDetailsActivity;
import io.taptalk.taptalklive.Interface.TapTalkLiveInterface;
import io.taptalk.taptalklive.model.TTLCaseListModel;

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
    public void onCloseButtonInHomePageTapped(Activity activity) {

    }

    @Override
    public void onCloseButtonInCreateCaseFormTapped(Activity activity) {

    }

    @Override
    public void onCloseButtonInCaseListTapped(Activity activity) {

    }

    @Override
    public void onTaskRootChatRoomClosed(Activity activity) {

    }

    @Override
    public void onSeeAllMessagesButtonTapped(Activity activity) {
        TTLCaseListActivity.Companion.start(activity);
    }

    @Override
    public void onCreateNewMessageButtonTapped(Activity activity) {
        TTLCreateCaseFormActivity.Companion.start(activity, true);
    }

    @Override
    public void onCaseListItemTapped(Activity activity, TTLCaseListModel caseListModel) {
        if (caseListModel == null || caseListModel.getLastMessage() == null || caseListModel.getLastMessage().getRoom() == null) {
            return;
        }
        TAPRoomModel room = caseListModel.getLastMessage().getRoom();
        TapUIChatActivity.start(
            activity,
            TAPTALK_INSTANCE_KEY,
            room.getRoomID(),
            room.getName(),
            room.getImageURL(),
            room.getType(),
            room.getColor()
        );
    }

    @Override
    public void onFaqChildTapped(Activity activity, TTLScfPathModel scfPath) {
        TTLFaqDetailsActivity.Companion.start(activity, scfPath);
    }

    @Override
    public void onCloseButtonInFaqDetailsTapped(Activity activity, TTLScfPathModel scfPath) {

    }

    @Override
    public void onTalkToAgentButtonTapped(Activity activity, TTLScfPathModel scfPath) {
        TTLCreateCaseFormActivity.Companion.start(activity, true);
    }
}
