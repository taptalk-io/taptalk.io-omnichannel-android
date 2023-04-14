package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.taptalklive.API.Model.TTLScfPathModel;

public class TTLGetScfPathResponse {
    @JsonProperty("item") private TTLScfPathModel item;

    public TTLScfPathModel getItem() {
        return item;
    }

    public void setItem(TTLScfPathModel item) {
        this.item = item;
    }
}
