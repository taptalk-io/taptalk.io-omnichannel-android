package io.taptalk.taptalklive.API.View;


import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;

public interface TTLView<T> {
    void startLoading();

    void endLoading();

    void onEmpty(String message);

    void onSuccess(T t);

    void onError(TTLErrorModel error);

    void onError(String errorMessage);

    void onError(Throwable throwable);
}
