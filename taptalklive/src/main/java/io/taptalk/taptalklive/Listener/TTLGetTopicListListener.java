package io.taptalk.taptalklive.Listener;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLTopicModel;
import io.taptalk.taptalklive.Interface.TTLGetTopicListInterface;

public abstract class TTLGetTopicListListener implements TTLGetTopicListInterface {
    @Override
    public void onSuccess(List<TTLTopicModel> topics) {

    }

    @Override
    public void onError(String errorCode, String errorMessage) {

    }
}
