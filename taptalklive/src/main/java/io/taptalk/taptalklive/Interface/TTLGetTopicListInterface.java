package io.taptalk.taptalklive.Interface;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLTopicModel;

public interface TTLGetTopicListInterface {

    void onSuccess(List<TTLTopicModel> topics);

    void onError(String errorCode, String errorMessage);
}
