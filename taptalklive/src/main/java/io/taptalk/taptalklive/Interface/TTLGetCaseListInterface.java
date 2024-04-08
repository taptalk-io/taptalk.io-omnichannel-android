package io.taptalk.taptalklive.Interface;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLCaseModel;

public interface TTLGetCaseListInterface {

    void onSuccess(List<TTLCaseModel> cases);

    void onError(String errorCode, String errorMessage);
}
