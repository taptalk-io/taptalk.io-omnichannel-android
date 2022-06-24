package io.taptalk.taptalklive.helper;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse;
import io.taptalk.taptalklive.API.Model.TTLCaseModel;
import io.taptalk.taptalklive.Manager.TTLDataManager;
import io.taptalk.taptalklive.TapTalkLive;

public class TTLUtil {
    public static void processGetCaseListResponse(TTLGetCaseListResponse response, TapCommonListener listener) {
        if (response == null) {
            if (listener != null) {
                listener.onError(ERROR_CODE_OTHERS, "Response is null");
            }
            return;
        }
        List<TTLCaseModel> cases = response.getCases();
        boolean hasActiveCase = null != cases && !cases.isEmpty();
        TTLDataManager.getInstance().saveActiveUserHasExistingCase(hasActiveCase);
        if (hasActiveCase) {
            ArrayList<TAPMessageEntity> entities = new ArrayList<>();
            for (TTLCaseModel caseModel : cases) {
                TapTalkLive.getCaseMap().put(caseModel.getTapTalkXCRoomID(), caseModel);
                TAPMessageModel lastMessage = TAPEncryptorManager.getInstance().decryptMessage(caseModel.getTapTalkRoom().getLastMessage());
                entities.add(TAPMessageEntity.fromMessageModel(lastMessage));
            }
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).insertToDatabase(entities, false, new TAPDatabaseListener() {
                @Override
                public void onInsertFinished() {
                    if (listener != null) {
                        listener.onSuccess("Successfully saved messages.");
                    }
                }

                @Override
                public void onInsertFailed(String errorMessage) {
                    if (listener != null) {
                        listener.onError(ERROR_CODE_OTHERS, "Failed to save messages.");
                    }
                }
            });
        } else {
            if (listener != null) {
                listener.onSuccess("Result is empty.");
            }
        }
    }
}
