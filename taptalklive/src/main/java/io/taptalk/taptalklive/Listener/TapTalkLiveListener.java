package io.taptalk.taptalklive.Listener;

import static android.content.Context.CLIPBOARD_SERVICE;
import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;

import io.taptalk.TapTalk.Helper.TAPUtils;
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
import io.taptalk.taptalklive.R;

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
    public void onCaseListItemTapped(Activity activity, TAPMessageModel lastMessage) {
        if (lastMessage == null || lastMessage.getRoom() == null) {
            return;
        }
        TAPRoomModel room = lastMessage.getRoom();
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

    @Override
    public void onFaqContentUrlTapped(Activity activity, TTLScfPathModel scfPath, String url) {
        TAPUtils.openUrl(TAPTALK_INSTANCE_KEY, activity, url);
    }

    @Override
    public void onFaqContentUrlLongPressed(Activity activity, TTLScfPathModel scfPath, String url) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(url, url);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, activity.getString(R.string.ttl_url_copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFaqContentEmailAddressTapped(Activity activity, TTLScfPathModel scfPath, String email) {
        TAPUtils.composeEmail(activity, email);
    }

    @Override
    public void onFaqContentEmailAddressLongPressed(Activity activity, TTLScfPathModel scfPath, String email) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(email, email);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, activity.getString(R.string.ttl_email_copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFaqContentPhoneNumberTapped(Activity activity, TTLScfPathModel scfPath, String phone) {
        TAPUtils.openDialNumber(activity, phone);
    }

    @Override
    public void onFaqContentPhoneNumberLongPressed(Activity activity, TTLScfPathModel scfPath, String phone) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(phone, phone);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, activity.getString(R.string.ttl_phone_copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }
}
