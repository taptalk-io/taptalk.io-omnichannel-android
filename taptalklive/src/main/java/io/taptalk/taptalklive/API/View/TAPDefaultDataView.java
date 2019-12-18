package io.taptalk.taptalklive.API.View;

import io.taptalk.taptalklive.API.Model.ResponseModel.TAPErrorModel;

public abstract class TAPDefaultDataView<T> implements TapView<T> {
    @Override
    public void startLoading() {}
    @Override
    public void startLoading(String localID) {}
    @Override
    public void endLoading() {}
    @Override
    public void endLoading(String localID) {}
    @Override
    public void onEmpty(String message) {}
    @Override
    public void onSuccess(T response) {}
    @Override
    public void onSuccess(T t, String localID) {}
    @Override
    public void onSuccessMessage(String message) {}
    @Override
    public void onError(TAPErrorModel error) {}
    @Override
    public void onError(TAPErrorModel error, String localID) {}
    @Override
    public void onError(String errorMessage) {}
    @Override
    public void onError(String errorMessage, String localID) {}
    @Override
    public void onError(Throwable throwable) {}
    @Override
    public void onError(Throwable throwable, String localID) {}
}

