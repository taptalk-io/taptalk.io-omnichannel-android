package io.taptalk.taptalklive.API.View;


import io.taptalk.taptalklive.API.Model.TTLConfigs;

public interface TTLProjectConfigsInterface {
    void onSuccess(TTLConfigs configs);

    void onError(String errorCode, String errorMessage);
}

