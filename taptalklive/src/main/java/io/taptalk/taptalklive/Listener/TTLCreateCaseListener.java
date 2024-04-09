package io.taptalk.taptalklive.Listener;

import io.taptalk.taptalklive.API.Model.TTLCaseModel;
import io.taptalk.taptalklive.Interface.TTLCreateCaseInterface;

public abstract class TTLCreateCaseListener implements TTLCreateCaseInterface {
    @Override
    public void onSuccess(TTLCaseModel caseModel) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}
