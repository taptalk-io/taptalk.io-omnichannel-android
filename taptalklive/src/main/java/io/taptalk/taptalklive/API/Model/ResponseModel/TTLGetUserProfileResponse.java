package io.taptalk.taptalklive.API.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.taptalklive.API.Model.TTLUserModel;

public class TTLGetUserProfileResponse {
    @JsonProperty("user") private TTLUserModel user;

    public TTLUserModel getUser() {
        return user;
    }

    public void setUser(TTLUserModel user) {
        this.user = user;
    }
}
