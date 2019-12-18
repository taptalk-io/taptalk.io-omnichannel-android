package io.taptalk.taptalklive.API.View;


import io.taptalk.taptalklive.API.Model.TapConfigs;

public interface TapProjectConfigsInterface {
    void onSuccess(TapConfigs configs);

    void onError(String errorCode, String errorMessage);
}

