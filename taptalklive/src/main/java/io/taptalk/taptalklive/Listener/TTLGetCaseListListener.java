package io.taptalk.taptalklive.Listener;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLCaseModel;
import io.taptalk.taptalklive.Interface.TTLGetCaseListInterface;

public abstract class TTLGetCaseListListener implements TTLGetCaseListInterface {
    @Override
    public void onSuccess(List<TTLCaseModel> cases) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}
