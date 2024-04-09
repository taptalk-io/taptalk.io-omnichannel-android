package io.taptalk.taptalklive.Interface;

import io.taptalk.taptalklive.API.Model.TTLCaseModel;

public interface TTLCreateCaseInterface {

    void onSuccess(TTLCaseModel caseModel);

    void onError(String errorCode, String errorMessage);
}
