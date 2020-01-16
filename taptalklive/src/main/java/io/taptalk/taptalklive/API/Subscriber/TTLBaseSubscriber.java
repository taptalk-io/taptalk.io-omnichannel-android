package io.taptalk.taptalklive.API.Subscriber;

import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class TTLBaseSubscriber<V extends TTLDefaultDataView<ResponseBody>> extends Subscriber<ResponseBody> {

    protected V view;

    public TTLBaseSubscriber(V view) {
        this.view = view;
        if (view == null) throw new IllegalArgumentException("ERR: null view");
    }

    @Override
    public void onStart() {
        super.onStart();
        view.startLoading();
    }

    @Override
    public void onCompleted() {
        view.endLoading();
    }

    @Override
    public void onError(Throwable e) {
        view.onError(e.getMessage());
        view.onError(e);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (null == responseBody) {
            view.onError(new TTLErrorModel("999", "Unknown Error", ""));
        } else {
            view.onSuccess(responseBody);
        }
    }
}
