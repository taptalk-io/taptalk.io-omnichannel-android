package io.taptalk.taptalklive.API.Subscriber;

import io.taptalk.taptalklive.API.View.TTLDefaultDataView;
import io.taptalk.taptalklive.API.View.TTLView;
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLBaseResponse;
import rx.Subscriber;

public class TTLDefaultSubscriber<T extends TTLBaseResponse<D>, V extends TTLDefaultDataView<D> &
        TTLView<D>, D>
        extends Subscriber<T> {

    protected V view;

    public TTLDefaultSubscriber() {
        super();
    }

    public TTLDefaultSubscriber(V view) {
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
        e.printStackTrace();
        view.onError(e.getMessage());
        view.onError(e);
        view.endLoading();
    }

    @Override
    public void onNext(T t) {
        if (t.getError() != null && 200 != t.getStatus()) {
            view.onError(t.getError());
        } else {
            view.onSuccess(t.getData());
        }
        view.endLoading();
    }
}
