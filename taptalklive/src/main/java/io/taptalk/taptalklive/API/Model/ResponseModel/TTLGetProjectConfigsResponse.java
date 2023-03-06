package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel;
import io.taptalk.taptalklive.API.Model.TTLLauncherConfigsModel;
import io.taptalk.taptalklive.API.Model.TTLTapTalkProjectConfigsModel;

public class TTLGetProjectConfigsResponse {
    @JsonProperty("tapTalk") private TTLTapTalkProjectConfigsModel tapTalkProjectConfigs;
    @JsonProperty("launcherConfigs") private TTLLauncherConfigsModel launcherConfigs;
    @JsonProperty("channelLinks") private List<TTLChannelLinkModel> channelLinks;

    public TTLTapTalkProjectConfigsModel getTapTalkProjectConfigs() {
        return tapTalkProjectConfigs;
    }

    public void setTapTalkProjectConfigs(TTLTapTalkProjectConfigsModel tapTalkProjectConfigs) {
        this.tapTalkProjectConfigs = tapTalkProjectConfigs;
    }

    public TTLLauncherConfigsModel getLauncherConfigs() {
        return launcherConfigs;
    }

    public void setLauncherConfigs(TTLLauncherConfigsModel launcherConfigs) {
        this.launcherConfigs = launcherConfigs;
    }

    public List<TTLChannelLinkModel> getChannelLinks() {
        return channelLinks;
    }

    public void setChannelLinks(List<TTLChannelLinkModel> channelLinks) {
        this.channelLinks = channelLinks;
    }
}
