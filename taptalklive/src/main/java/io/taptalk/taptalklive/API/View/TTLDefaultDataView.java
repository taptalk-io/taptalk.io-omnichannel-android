package io.taptalk.taptalklive.API.View;

import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;

public abstract class TTLDefaultDataView<T> implements TTLView<T> {
    @Override
    public void startLoading() {}
    @Override
    public void endLoading() {}
    @Override
    public void onEmpty(String message) {}
    @Override
    public void onSuccess(T response) {}
    @Override
    public void onError(TTLErrorModel error) {}
    @Override
    public void onError(String errorMessage) {}
    @Override
    public void onError(Throwable throwable) {}
}

