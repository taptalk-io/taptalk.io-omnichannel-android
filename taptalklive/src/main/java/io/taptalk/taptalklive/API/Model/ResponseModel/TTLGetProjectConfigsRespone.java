package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.taptalklive.API.Model.TTLTapTalkProjectConfigsModel;

public class TTLGetProjectConfigsRespone {
    @JsonProperty("tapTalk") private TTLTapTalkProjectConfigsModel tapTalkProjectConfigs;

    public TTLTapTalkProjectConfigsModel getTapTalkProjectConfigs() {
        return tapTalkProjectConfigs;
    }

    public void setTapTalkProjectConfigs(TTLTapTalkProjectConfigsModel tapTalkProjectConfigs) {
        this.tapTalkProjectConfigs = tapTalkProjectConfigs;
    }
}
