package io.taptalk.taptalklive.API.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TTLTapTalkProjectConfigsModel {

    @JsonProperty("apiURL")
    private String apiURL;
    @JsonProperty("appKeyID")
    private String appKeyID;
    @JsonProperty("appKeySecret")
    private String appKeySecret;

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getAppKeyID() {
        return appKeyID;
    }

    public void setAppKeyID(String appKeyID) {
        this.appKeyID = appKeyID;
    }

    public String getAppKeySecret() {
        return appKeySecret;
    }

    public void setAppKeySecret(String appKeySecret) {
        this.appKeySecret = appKeySecret;
    }
}